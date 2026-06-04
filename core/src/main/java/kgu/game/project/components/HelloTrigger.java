package kgu.game.project.components;

import static kgu.game.project.GameSettings.SCALE;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import kgu.game.project.GameSettings;
import kgu.game.project.objects.GameObject;

/**
 * Проходимый объект-триггер на Box2D (isSensor = true).
 * При столкновении с игроком выводит текст "Hello" и/или картинку.
 *
 * Интеграция в LevelTwoScreen:
 *
 *   1. Создать:
 *        helloTrigger = new HelloTrigger(tileX, tileY,
 *                GameSettings.TILE_SIZE, GameSettings.TILE_SIZE,
 *                "path/to/image.png",   // или null, если картинка не нужна
 *                myGdxGame.world);
 *
 *   2. ContactManager — добавить ветку:
 *        } else if (object.getClass().getSimpleName().equals("HelloTrigger")) {
 *            isNearHello = true;
 *        }
 *      и в end-контакте:
 *        isNearHello = false;
 *
 *   3. В draw():
 *        helloTrigger.draw(myGdxGame.batch);   // рисует спрайт объекта на карте
 *        if (isNearHello) {
 *            helloTrigger.drawOverlay(myGdxGame.batch); // рисует "Hello" / картинку поверх UI
 *        }
 *
 *   4. В dispose():
 *        helloTrigger.dispose();
 */
public class HelloTrigger extends GameObject {
    private Texture  overlayTexture;
    private String   overlayText;

    private float overlayX, overlayY;
    private float overlayW, overlayH;

    public HelloTrigger(int tileX, int tileY, int width, int height, World world) {
        this(tileX, tileY, width, height, null, world, GameSettings.SENSOR_MINUS_BIT);
    }

    public HelloTrigger(int tileX, int tileY, int width, int height,
                        String overlayImagePath, World world, short cBits) {
        super(null, tileX, tileY, width, height, cBits, world);

        this.overlayText = "Hello";

        if (overlayImagePath != null) {
            this.overlayTexture = new Texture(overlayImagePath);
        }
        overlayX = tileX;
        overlayY = tileY;
        overlayW = 32;
        overlayH = 32;
    }

    public HelloTrigger setOverlayBounds(float x, float y, float w, float h) {
        overlayX = x; overlayY = y; overlayW = w; overlayH = h;
        return this;
    }

    public HelloTrigger setOverlayText(String text) {
        this.overlayText = text;
        return this;
    }


    @Override
    public void draw(SpriteBatch batch) {
    }


    public void drawOverlay(SpriteBatch batch) {
        if (overlayTexture != null) {
            // Режим: картинка
            batch.draw(overlayTexture, overlayX, overlayY, overlayW, overlayH);
        } else {
            // Режим: просто текст — рисуем через BitmapFont снаружи
            // (font.draw(batch, overlayText, overlayX, overlayY))
            // Текст передаётся через getOverlayText() для удобства
        }
    }

    public String getOverlayText() { return overlayText; }

    public boolean hasImage() { return overlayTexture != null; }
    @Override
    public Body createBody(float x, float y, World world) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;   // статичный — не двигается
        Body body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(12 * SCALE / 2f, 12 * SCALE / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape  = shape;
        fixtureDef.isSensor = true;

        fixtureDef.filter.categoryBits = cBits;
        fixtureDef.filter.maskBits     = kgu.game.project.GameSettings.SHIP_BIT;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();
        body.setTransform((x + 26) * SCALE, (y + 24) * SCALE ,0);
        return body;
    }

    public void dispose() {
        if (overlayTexture != null) {
            overlayTexture.dispose();
            overlayTexture = null;
        }
    }
}
