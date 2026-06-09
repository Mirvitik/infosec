package kgu.game.project.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class IconView extends View {

    Texture texture;
    BitmapFont bitmapFont;

    String text;
    public String name;

    float textX;
    float textY;


    public IconView(float x, float y, int width, int height, String texturePath, String name) {
        super(x, y, 0, 0);
        this.texture = new Texture(texturePath);
        this.width = width;
        this.height = height;
        this.name = name;
    }


    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
        if (bitmapFont != null) bitmapFont.draw(batch, text, textX, textY);
    }

    @Override
    public void dispose() {
        texture.dispose();
        if (bitmapFont != null) bitmapFont.dispose();
    }

}
