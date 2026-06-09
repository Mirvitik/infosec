package kgu.game.project.objects;

import static kgu.game.project.GameSettings.SCALE;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class AntivirusObject extends GameObject {

    public AntivirusObject(String texturePath, int x, int y, int width, int height, short cBits, World world) {
        super(texturePath, x, y, width, height, cBits, world);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (texture == null) return;

        batch.draw(texture,
            getX() - width / 2,
            getY() - height / 2,
            width, height);
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
