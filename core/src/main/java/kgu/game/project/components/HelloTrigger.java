package kgu.game.project.components;

import static kgu.game.project.GameSettings.SCALE;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import kgu.game.project.objects.GameObject;


public class HelloTrigger extends GameObject {
    private Texture overlayTexture;

    private final float overlayX;
    private final float overlayY;
    private final float overlayW;
    private final float overlayH;

    public HelloTrigger(int tileX, int tileY, int width, int height,
                        String overlayImagePath, World world, short cBits) {
        super(null, tileX, tileY, width, height, cBits, world);


        if (overlayImagePath != null) {
            this.overlayTexture = new Texture(overlayImagePath);
        }
        overlayX = tileX;
        overlayY = tileY;
        overlayW = 64;
        overlayH = 64;
    }


    @Override
    public void draw(SpriteBatch batch) {
    }


    public void drawOverlay(SpriteBatch batch) {
        if (overlayTexture != null) {
            batch.draw(overlayTexture, overlayX, overlayY, overlayW, overlayH);
        }
    }

    @Override
    public Body createBody(float x, float y, World world) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(12 * SCALE / 2f, 18 * SCALE / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        fixtureDef.filter.categoryBits = cBits;
        fixtureDef.filter.maskBits = kgu.game.project.GameSettings.SHIP_BIT;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();
        body.setTransform((x + 10) * SCALE, (y + 50) * SCALE, 0);
        return body;
    }

    public void dispose() {
        if (overlayTexture != null) {
            overlayTexture.dispose();
            overlayTexture = null;
        }
    }
}
