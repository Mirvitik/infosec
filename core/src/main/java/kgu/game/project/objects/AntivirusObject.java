package kgu.game.project.objects;

import static kgu.game.project.GameSettings.SCALE;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
        polygonShape.setAsBox(width * SCALE / 8f, height * SCALE / 8f);

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
