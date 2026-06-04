package kgu.game.project.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ImageView extends View {

    Texture texture;

    public ImageView(float x, float y, String imagePath) {
        super(x, y);
        texture = new Texture(imagePath);
        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }
    // Constructor with custom size
    public ImageView(float x, float y, float width, float height, String imagePath) {
        super(x, y);
        texture = new Texture(imagePath);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    // Size methods
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return (int) this.x;
    }

    public int getY() {
        return (int) this.y;
    }

    // Position methods
    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Set size maintaining aspect ratio
    public void setSizeMaintainAspect(float targetWidth, float targetHeight) {
        float textureAspect = (float) texture.getWidth() / (float) texture.getHeight();
        float targetAspect = targetWidth / targetHeight;

        if (targetAspect > textureAspect) {
            // Target is wider than texture, fit to height
            this.height = targetHeight;
            this.width = targetHeight * textureAspect;
        } else {
            // Target is taller than texture, fit to width
            this.width = targetWidth;
            this.height = targetWidth / textureAspect;
        }
    }

    // Get texture dimensions
    public int getTextureWidth() {
        return texture.getWidth();
    }

    public int getTextureHeight() {
        return texture.getHeight();
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
