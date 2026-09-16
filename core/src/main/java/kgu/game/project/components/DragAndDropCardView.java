package kgu.game.project.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

import kgu.game.project.GameSettings;

public class DragAndDropCardView extends View {
    Texture texture;
    BitmapFont bitmapFont;

    String text;

    float textX;
    float textY;
    private boolean isVisible = true;

    private final ArrayList<DragAndDropSlotView> slots = new ArrayList<>();
    private DragAndDropSlotView currentSlot;

    private boolean isDragging = false;
    private float dragOffsetX;
    private float dragOffsetY;

    private final float startX;
    private final float startY;

    public DragAndDropCardView(float x, float y, float width, float height) {
        super(x, y, width, height);

        startX = x;
        startY = y;
    }

    public DragAndDropCardView(float x, float y, float width, float height, String texturePath) {
        super(x, y, width, height);

        startX = x;
        startY = y;
        texture = new Texture(texturePath);
    }

    public DragAndDropCardView(float x, float y, float width, float height, BitmapFont font, String texturePath, String text) {
        super(x, y, width, height);

        this.text = text;
        this.bitmapFont = font;

        startX = x;
        startY = y;
        texture = new Texture(texturePath);

        GlyphLayout glyphLayout = new GlyphLayout(bitmapFont, text);
        float textWidth = glyphLayout.width;
        float textHeight = glyphLayout.height;

        textX = x + (width - textWidth) / 2;
        textY = y + (height + textHeight) / 2;
    }
    public void setText(String text) {
        this.text = text;
        if (bitmapFont != null && text != null) {
            GlyphLayout glyphLayout = new GlyphLayout(bitmapFont, text);
            float textWidth = glyphLayout.width;
            float textHeight = glyphLayout.height;

            textX = x + (width - textWidth) / 2;
            textY = y + (height + textHeight) / 2;
        }
    }

    public void addSlot(DragAndDropSlotView slot) {
        slots.add(slot);
    }

    public boolean isDragging() {
        return isDragging;
    }

    public DragAndDropSlotView getCurrentSlot() {
        return currentSlot;
    }

    public boolean handleTouch(Vector3 touch, boolean isTouched) {
        if (!isVisible) return false;

        if (!isTouched) {
            if (!isDragging) return false;

            isDragging = false;
            dropOnSlot();
            return true;
        }

        if (!isDragging) {
            if (!isHit(touch.x, touch.y)) return false;

            isDragging = true;
            dragOffsetX = touch.x - x;
            dragOffsetY = touch.y - y;
            leaveCurrentSlot();
            return true;
        }

        float newX = touch.x - dragOffsetX;
        float newY = touch.y - dragOffsetY;

        newX = Math.max(0, Math.min(newX, GameSettings.SCREEN_WIDTH - width));
        newY = Math.max(0, Math.min(newY, GameSettings.SCREEN_HEIGHT - height));

        setPosition(newX, newY);
        return true;
    }

    private void dropOnSlot() {
        for (DragAndDropSlotView slot : slots) {
            if (slot.isEmpty() && slot.isHit(getCenterX(), getCenterY())) {
                putInSlot(slot);
                System.out.println("Карточка в слоте");
                return;
            }
        }
    }

    public void putInSlot(DragAndDropSlotView slot) {
        leaveCurrentSlot();

        currentSlot = slot;
        slot.setCard(this);

        setPosition(slot.getX() + (slot.getWidth() - width) / 2,
            slot.getY() + (slot.getHeight() - height) / 2);
    }

    public void reset() {
        isDragging = false;
        leaveCurrentSlot();
        setPosition(startX, startY);
    }

    private void leaveCurrentSlot() {
        if (currentSlot == null) return;

        currentSlot.clearCard();
        currentSlot = null;
    }

    public void setPosition(float newX, float newY) {
        textX += newX - x;
        textY += newY - y;

        this.x = newX;
        this.y = newY;
    }

    public float getCenterX() {
        return x + width / 2;
    }

    public float getCenterY() {
        return y + height / 2;
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
        isDragging = false;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isVisible) return;

        batch.draw(texture, x, y, width, height);
        if (bitmapFont != null) bitmapFont.draw(batch, text, textX, textY);
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }
}
