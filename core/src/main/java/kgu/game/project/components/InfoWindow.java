package kgu.game.project.components;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import kgu.game.project.MyGdxGame;
import kgu.game.project.screens.GameScreen;

public class InfoWindow extends View {
    Game myGdxGame;
    private final Texture backgroundTexture;
    private final Texture closeButtonTexture;
    private final BitmapFont font;

    private final String title;
    private final String content;

    private boolean isDragging = false;
    private float dragOffsetX;
    Screen gameScreen;
    private float dragOffsetY;

    private final float closeButtonSize = 32;
    private float closeButtonX;
    private float closeButtonY;

    private boolean isVisible = false;
    private Runnable onCloseListener;

    private static final float WINDOW_WIDTH = 500;
    private static final float WINDOW_HEIGHT = 600;
    private static final float TITLE_BAR_HEIGHT = 40;
    private static final float PADDING = 80;

    public InfoWindow(Game myGdxGame, float x, float y, String title, String content,
                      String backgroundPath, String closeButtonPath) {
        super(x, y, WINDOW_WIDTH, WINDOW_HEIGHT);
        this.myGdxGame = myGdxGame;
        this.gameScreen = new GameScreen((MyGdxGame) myGdxGame);

        this.title = title;
        this.content = content;
        this.font = MyGdxGame.arialFont;

        backgroundTexture = new Texture(backgroundPath);
        closeButtonTexture = new Texture(closeButtonPath);

        updateCloseButtonPosition();
    }

    private void updateCloseButtonPosition() {
        closeButtonX = x + width - closeButtonSize - 10;
        closeButtonY = y + height - closeButtonSize - 5;
    }

    public void show() {
        isVisible = true;
    }

    public void hide() {
        isVisible = false;
        isDragging = false;
    }

    public boolean isVisible() {
        return isVisible;
    }


    public boolean handleTouch(Vector3 touch, boolean isTouched) {
        if (!isVisible) return false;

        if (isTouched &&
            touch.x >= closeButtonX && touch.x <= closeButtonX + closeButtonSize &&
            touch.y >= closeButtonY && touch.y <= closeButtonY + closeButtonSize) {
            hide();
            if (onCloseListener != null) {
                onCloseListener.run();
            }
            return true;
        }

        if (isTouched &&
            touch.x >= x && touch.x <= x + width &&
            touch.y >= y && touch.y <= y + TITLE_BAR_HEIGHT) {
            isDragging = true;
            dragOffsetX = touch.x - x;
            dragOffsetY = touch.y - y;
            return true;
        }

        if (!isTouched) {
            isDragging = false;
        }

        if (isDragging) {
            float newX = touch.x - dragOffsetX;
            float newY = touch.y - dragOffsetY;

            newX = Math.max(0, Math.min(newX, Gdx.graphics.getWidth() - width));
            newY = Math.max(0, Math.min(newY, Gdx.graphics.getHeight() - height));

            setPosition(newX, newY);
            updateCloseButtonPosition();
            return true;
        }

        return false;
    }

    private void setPosition(float newX, float newY) {
        float deltaX = newX - x;
        float deltaY = newY - y;
        this.x = newX;
        this.y = newY;

        closeButtonX += deltaX;
        closeButtonY += deltaY;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isVisible) return;

        batch.draw(backgroundTexture, x, y, width, height);

        if (font != null && title != null) {
            font.draw(batch, title, x + PADDING, y + height - PADDING);
        }

        if (font != null && content != null) {
            String[] lines = content.split("\n");
            float currentY = y + height - TITLE_BAR_HEIGHT - PADDING;
            for (String line : lines) {
                font.draw(batch, line, x + PADDING, currentY);
                currentY -= font.getLineHeight();
            }
        }
        batch.draw(closeButtonTexture, closeButtonX, closeButtonY, closeButtonSize, closeButtonSize);

    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        closeButtonTexture.dispose();
    }
}
