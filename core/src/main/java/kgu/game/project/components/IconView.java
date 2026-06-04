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

    public IconView(float x, float y, String texturePath) {
        super(x, y, 0, 0);
        this.texture = new Texture(texturePath);
        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }
    public IconView(float x, float y, int width, int height, String texturePath, String name) {
        super(x, y, 0, 0);
        this.texture = new Texture(texturePath);
        this.width = width;
        this.height = height;
        this.name = name;
    }

    public IconView(float x, float y, float width, float height, BitmapFont font, String texturePath, String text) {
        super(x, y, width, height);

        this.text = text;
        this.bitmapFont = font;

        texture = new Texture(texturePath);

        GlyphLayout glyphLayout = new GlyphLayout(bitmapFont, text);
        float textWidth = glyphLayout.width;
        float textHeight = glyphLayout.height;

        textX = x + (width - textWidth) / 2;
        textY = y + (height + textHeight) / 2;
    }

    public IconView(float x, float y, float width, float height, String texturePath) {
        super(x, y, width, height);

        texture = new Texture(texturePath);
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
