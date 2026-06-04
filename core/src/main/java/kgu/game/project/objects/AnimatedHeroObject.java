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

    // Анимации для разных направлений
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
        super(x, y, width, height, null, world); // texturePath = null, т.к. используем spriteSheet

        // Инициализация анимаций
        createAnimations(spriteSheet);

        // Начальная анимация
        currentAnimation = idleAnimation;
        stateTime = 0;
        lastDirection = new Vector2(0, -1); // Смотрим вниз по умолчанию
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

        // Ходьба вверх (ряд 1)
        if (spriteSheet.length > 1 && spriteSheet[1].length >= 4) {
            walkFrames.clear();
            for (int i = 0; i < 4; i++) {
                walkFrames.add(spriteSheet[3][24 + i]);
            }
            walkUpAnimation = new Animation<>(frameDuration, walkFrames);
            walkUpAnimation.setPlayMode(Animation.PlayMode.LOOP);
        }

        // Ходьба влево (ряд 2) - ИСПРАВЛЕНО
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

        // Ходьба вправо (ряд 3)
        if (spriteSheet.length > 2 && spriteSheet[2].length >= 4) {
            walkFrames.clear();
            for (int i = 0; i < 4; i++) {
                walkFrames.add(new TextureRegion(spriteSheet[2][4 + i]));
            }
            walkRightAnimation = new Animation<>(frameDuration, walkFrames);
            walkRightAnimation.setPlayMode(Animation.PlayMode.LOOP);
        }

        // Idle анимация (ряд 8)
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
        // Обновляем анимацию в зависимости от направления
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

        // Сохраняем последнее направление для idle анимации
        if (direction.len() > 0.1f) {
            lastDirection.set(direction);
        }

        // Выбираем анимацию в зависимости от угла направления
        float angle = direction.angleDeg(); // 0° = вправо, 90° = вверх

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

        // Рисуем текущий кадр анимации
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
        polygonShape.setAsBox(width * SCALE / 2f, height * SCALE / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 1f;
        fixtureDef.filter.categoryBits = cBits;
        fixtureDef.filter.maskBits = -1;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        polygonShape.dispose();

        body.setTransform(x * SCALE, y * SCALE, 0);
        return body;
    }
}


