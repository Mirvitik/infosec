package kgu.game.project.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ImageView;

public class WallObject {
    public Body body;
    public ImageView imageView;

    public WallObject(MyGdxGame game, float x, float y, float width, float height, String texturePath) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        body = game.world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f, height / 2f);
        body.createFixture(shape, 0);
        shape.dispose();

        imageView = new ImageView(x - width / 2f, y - height / 2f, width, height, texturePath);
    }

    public void draw(SpriteBatch batch) {
        imageView.draw(batch);
    }

    public void dispose() {
        imageView.dispose();
    }
}
