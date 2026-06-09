package kgu.game.project.objects;

import static kgu.game.project.GameSettings.SCALE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;


public class AnimatedHeroObject extends HeroObject {


    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> walkDownAnimation;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> walkUpLeftAnimation;
    private Animation<TextureRegion> walkUpRightAnimation;
    private Animation<TextureRegion> walkDownLeftAnimation;
    private Animation<TextureRegion> walkDownRightAnimation;

    private Animation<TextureRegion> currentAnimation;
    private float stateTime;

    // Текущее направление движения
    private Vector2 lastDirection;
    private boolean isMoving;

    public AnimatedHeroObject(int x, int y, int width, int height,
                              TextureRegion[][] spriteSheet, World world) {
        super(x, y, width, height, null, world);

        createAnimations(spriteSheet);

        currentAnimation = idleAnimation;
        stateTime = 0;
        lastDirection = new Vector2(0, -1);
        isMoving = false;
    }

    private void createAnimations(TextureRegion[][] spriteSheet) {
        float frameDuration = 0.1f;

        // Проверяем, что спрайт-лист загружен
        if (spriteSheet == null || spriteSheet.length == 0) {
            Gdx.app.error("AnimatedHero", "SpriteSheet is null or empty!");
            return;
        }

        Array<TextureRegion> walkFrames = new Array<>();

        // Ходьба вниз (ряд 0)
        if (spriteSheet.length > 0 && spriteSheet[0].length >= 4) {
            walkFrames.clear();
            for (int i = 0; i < 4; i++) {
                walkFrames.add(spriteSheet[2][i]);
            }
            walkDownAnimation = new Animation<>(frameDuration, walkFrames);
            walkDownAnimation.setPlayMode(Animation.PlayMode.LOOP);
        }

        if (spriteSheet.length > 1 && spriteSheet[1].length >= 4) {
            walkFrames.clear();
            for (int i = 0; i < 4; i++) {
                walkFrames.add(spriteSheet[3][24 + i]);
            }
            walkUpAnimation = new Animation<>(frameDuration, walkFrames);
            walkUpAnimation.setPlayMode(Animation.PlayMode.LOOP);
        }

        if (spriteSheet.length > 2 && spriteSheet[2].length >= 4) {
            walkFrames.clear();
            for (int i = 0; i < 4; i++) {
                TextureRegion frame = new TextureRegion(spriteSheet[2][4 + i]);
                frame.flip(true, false);
                walkFrames.add(frame);
            }
            walkLeftAnimation = new Animation<>(frameDuration, walkFrames);  // <-- ИСПРАВЛЕНО
            walkLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);
        }

        if (spriteSheet.length > 2 && spriteSheet[2].length >= 4) {
            walkFrames.clear();
            for (int i = 0; i < 4; i++) {
                walkFrames.add(new TextureRegion(spriteSheet[2][4 + i]));
            }
            walkRightAnimation = new Animation<>(frameDuration, walkFrames);
            walkRightAnimation.setPlayMode(Animation.PlayMode.LOOP);
        }

        if (spriteSheet.length > 8 && spriteSheet[8].length > 0) {
            walkFrames.clear();
            walkFrames.add(spriteSheet[8][0]);
            idleAnimation = new Animation<>(frameDuration, walkFrames);
            idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
        } else if (walkDownAnimation != null) {
            idleAnimation = walkDownAnimation;
        }
        currentAnimation = walkDownAnimation;
    }

    @Override
    public void moveWithTouchpad(Vector2 direction, float strength) {
        super.moveWithTouchpad(direction, strength);
        updateAnimation(direction, strength > 0.1f);
    }

    private void updateAnimation(Vector2 direction, boolean moving) {
        boolean wasMoving = this.isMoving;
        this.isMoving = moving;

        if (!moving) {
            if (wasMoving) {
                stateTime = 0f;
            }
            currentAnimation = idleAnimation;
            return;
        }

        if (direction.len() > 0.1f) {
            lastDirection.set(direction);
        }

        float angle = direction.angleDeg();

        if (angle >= 0 && angle < 22.5f) {
            currentAnimation = walkRightAnimation;
        } else if (angle >= 22.5f && angle < 67.5f) {
            currentAnimation = walkUpRightAnimation != null ? walkUpRightAnimation : walkRightAnimation;
        } else if (angle >= 67.5f && angle < 112.5f) {
            currentAnimation = walkUpAnimation;
        } else if (angle >= 112.5f && angle < 157.5f) {
            currentAnimation = walkUpLeftAnimation != null ? walkUpLeftAnimation : walkLeftAnimation;
        } else if (angle >= 157.5f && angle < 202.5f) {
            currentAnimation = walkLeftAnimation;
        } else if (angle >= 202.5f && angle < 247.5f) {
            currentAnimation = walkDownLeftAnimation != null ? walkDownLeftAnimation : walkLeftAnimation;
        } else if (angle >= 247.5f && angle < 292.5f) {
            currentAnimation = walkDownAnimation;
        } else if (angle >= 292.5f && angle < 337.5f) {
            currentAnimation = walkDownRightAnimation != null ? walkDownRightAnimation : walkRightAnimation;
        } else {
            currentAnimation = walkRightAnimation;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (currentAnimation == null) {
            return;
        }

        if (isMoving) {
            stateTime += Gdx.graphics.getDeltaTime();
        }
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        batch.draw(currentFrame,
            getX() - width / 2,
            getY() - height / 2,
            width, height);
    }


    @Override
    public void stop() {
        super.stop();
        stateTime = 0f;
        isMoving = false;
        currentAnimation = idleAnimation;
    }

    @Override
    public Body createBody(float x, float y, World world) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.fixedRotation = true;
        Body body = world.createBody(def);

        PolygonShape polygonShape = new PolygonShape();


        float hitboxWidth = width * SCALE * 0.7f;
        float hitboxHeight = height * SCALE * 0.85f;
        float hitboxOffsetY = height * SCALE * 0.075f;

        polygonShape.setAsBox(hitboxWidth / 2f, hitboxHeight / 2f,
            new Vector2(0, hitboxOffsetY), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.filter.categoryBits = cBits;
        fixtureDef.filter.maskBits = -1;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        polygonShape.dispose();

        body.setTransform(x * SCALE, y * SCALE, 0);

        addGroundSensor(body);

        return body;
    }

    private void addGroundSensor(Body body) {
        PolygonShape sensorShape = new PolygonShape();
        float sensorWidth = 64 * SCALE * 0.5f;
        float sensorHeight = height * SCALE * 0.2f;
        float sensorOffsetY = -height * SCALE * 0.45f; // At the bottom of feet

        sensorShape.setAsBox(sensorWidth / 2f, sensorHeight / 2f,
            new Vector2(0, sensorOffsetY), 0);

        FixtureDef sensorDef = new FixtureDef();
        sensorDef.shape = sensorShape;
        sensorDef.isSensor = true;
        sensorDef.filter.categoryBits = cBits;
        sensorDef.filter.maskBits = -1;

        Fixture sensorFixture = body.createFixture(sensorDef);

        sensorFixture.setUserData(this);

        sensorShape.dispose();
    }
}


