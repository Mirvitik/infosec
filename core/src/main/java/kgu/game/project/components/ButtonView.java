package kgu.game.project.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ButtonView extends View {

    Texture texture;
    BitmapFont bitmapFont;

    String text;

    float textX;
    float textY;
    float scale;
    private boolean isVisible = true;

    public ButtonView(float x, float y, String texturePath) {
        super(x, y, 0, 0);
        this.texture = new Texture(texturePath);
        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }

    public ButtonView(float x, float y, int width, int height, String texturePath) {
        super(x, y, 0, 0);
        this.texture = new Texture(texturePath);
        this.width = width;
        this.height = height;
    }

    public ButtonView(float x, float y, float width, float height, BitmapFont font, String texturePath, String text) {
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

    public ButtonView(float x, float y, float width, float height, BitmapFont font, String texturePath, String text, float scale) {
        super(x, y, width, height);

        this.text = text;
        font.getData().setScale(scale);
        this.bitmapFont = font;


        texture = new Texture(texturePath);

        GlyphLayout glyphLayout = new GlyphLayout(bitmapFont, text);
        float textWidth = glyphLayout.width;
        float textHeight = glyphLayout.height;

        textX = x + (width - textWidth) / 2;
        textY = y + (height + textHeight) / 2;
    }

    public ButtonView(float x, float y, float width, float height, String texturePath) {
        super(x, y, width, height);

        texture = new Texture(texturePath);
    }

    public void setText(String text){
        this.text = text;
    }
    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
        if (bitmapFont != null) bitmapFont.draw(batch, text, textX, textY);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
    public String getText() {
        return text;
    }

    public boolean isHit(float tx, float ty) {
        if (!isVisible) return false;
        return super.isHit(tx, ty);
    }
    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void show() {
        isVisible = true;
    }

    public void hide() {
        isVisible = false;
    }
}
