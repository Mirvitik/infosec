package kgu.game.project.objects;

import static kgu.game.project.GameSettings.SCALE;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Arrays;

import kgu.game.project.GameResources;

public class AntivirusObject extends GameObject {

    private static final int FRAME_SIZE = 32;
    private static final int RAISE_HAND_FRAMES = 5;
    private static final float RAISE_HAND_FRAME_DURATION = 0.1f;
    private static final float WAVE_FRAME_DURATION = 0.3f;

    private Texture helloSheet;
    private Animation<TextureRegion> raiseHandAnimation;
    private Animation<TextureRegion> waveAnimation;
    private TextureRegion currentFrame;

    public AntivirusObject(String texturePath, int x, int y, int width, int height, short cBits, World world) {
        super(texturePath, x, y, width, height, cBits, world);
    }

    public void setSheet(String sheet) {
        if (helloSheet != null){
            helloSheet.dispose();
        }
        helloSheet = new Texture(sheet);
        TextureRegion[] frames = TextureRegion.split(helloSheet, FRAME_SIZE, FRAME_SIZE)[0];
        if (frames.length <= RAISE_HAND_FRAMES) {
            throw new IllegalArgumentException("Antivirus sheet " + sheet + " must have at least "
                + (RAISE_HAND_FRAMES + 1) + " frames of " + FRAME_SIZE + "x" + FRAME_SIZE
                + " in a row, but has " + frames.length);
        }
        raiseHandAnimation = new Animation<>(RAISE_HAND_FRAME_DURATION,
            Arrays.copyOfRange(frames, 0, RAISE_HAND_FRAMES));
        waveAnimation = new Animation<>(WAVE_FRAME_DURATION,
            frames[RAISE_HAND_FRAMES - 1], frames[RAISE_HAND_FRAMES]);
    }

    public void changeSprite(float num, int gameLvl) {
        if (helloSheet == null) {
            setSheet(GameResources.ANTIVIRUS_SHEET);
        }

        float raiseHandDuration = raiseHandAnimation.getAnimationDuration();
        currentFrame = num < raiseHandDuration
            ? raiseHandAnimation.getKeyFrame(num)
            : waveAnimation.getKeyFrame(num - raiseHandDuration, true);
    }

    public void setDefaultTexture() {
        currentFrame = null;
    }

    @Override
    public void draw(SpriteBatch batch) {
        float drawX = getX() - (float) width / 2;
        float drawY = getY() - (float) height / 2;
        if (currentFrame != null) {
            batch.draw(currentFrame, drawX, drawY, width, height);
        } else if (texture != null) {
            batch.draw(texture, drawX, drawY, width, height);
        }
    }

    @Override
    public Body createBody(float x, float y, World world) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.fixedRotation = true;
        Body body = world.createBody(def);

        PolygonShape polygonShape = new PolygonShape();


        float hitboxWidth = 64 * SCALE * 0.7f;
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
        float sensorOffsetY = -height * SCALE * 0.45f;

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
