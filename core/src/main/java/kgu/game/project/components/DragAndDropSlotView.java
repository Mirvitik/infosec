package kgu.game.project.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DragAndDropSlotView extends View {
    Texture texture;
    BitmapFont bitmapFont;

    String text;

    float textX;
    float textY;
    private boolean isVisible = true;

    private DragAndDropCardView card;
    private DragAndDropCardView expectedCard;

    public DragAndDropSlotView(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    public DragAndDropSlotView(float x, float y, float width, float height, String texturePath) {
        super(x, y, width, height);

        texture = new Texture(texturePath);
    }

    public DragAndDropSlotView(float x, float y, float width, float height, BitmapFont font, String texturePath, String text) {
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

    public boolean isEmpty() {
        return card == null;
    }

    public DragAndDropCardView getCard() {
        return card;
    }

    public void setCard(DragAndDropCardView card) {
        this.card = card;
    }

    public void clearCard() {
        this.card = null;
    }

    public void setExpectedCard(DragAndDropCardView expectedCard) {
        this.expectedCard = expectedCard;
    }

    public boolean isFilledCorrectly() {
        return card != null && card == expectedCard;
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

    @Override
    public boolean isHit(float tx, float ty) {
        if (!isVisible) return false;
        return super.isHit(tx, ty);
    }

    public void show() {
        isVisible = true;
    }

    public void hide() {
        isVisible = false;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isVisible) return;

        if (texture != null) batch.draw(texture, x, y, width, height);
        if (bitmapFont != null && text != null) bitmapFont.draw(batch, text, textX, textY);
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }
}
