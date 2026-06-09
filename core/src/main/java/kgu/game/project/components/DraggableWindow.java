package kgu.game.project.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import kgu.game.project.GameResources;
import kgu.game.project.GameSettings;
import kgu.game.project.MyGdxGame;
import kgu.game.project.managers.LocalizationManager;
import kgu.game.project.screens.HackScreen;

public class DraggableWindow extends View {
    MyGdxGame myGdxGame;
    private Texture backgroundTexture;
    private Texture closeButtonTexture;
    private final BitmapFont font;

    private final String title;
    private final String content;

    private boolean isDragging = false;
    private float dragOffsetX;
    Screen gameScreen;
    private float dragOffsetY;

    private float closeButtonSize = 32;
    private float closeButtonX;
    private float closeButtonY;

    private boolean isVisible = false;
    private Runnable onCloseListener;
    private Runnable onOpenListener;

    private static final float WINDOW_WIDTH = 500;
    private static final float WINDOW_HEIGHT = 600;
    private static final float TITLE_BAR_HEIGHT = 40;
    private static final float PADDING = 80;
    private ButtonView clickButton;

    public DraggableWindow(MyGdxGame myGdxGame, float x, float y, String title, String content,
                           String backgroundPath, String closeButtonPath, BitmapFont font) {
        super(x, y, WINDOW_WIDTH, WINDOW_HEIGHT);
        this.myGdxGame = myGdxGame;

        this.title = title;
        this.content = content;
        this.font = font;

        backgroundTexture = new Texture(backgroundPath);
        closeButtonTexture = new Texture(closeButtonPath);
        if (LocalizationManager.getLanguage() == LocalizationManager.Language.EN) {
            clickButton = new ButtonView(x + 120, y + 200, GameResources.PRIZE_IMG_PATH);
        } else {
            clickButton = new ButtonView(x + 120, y + 200, GameResources.PRIZE_RUS_IMG_PATH);
        }

        updateCloseButtonPosition();
    }

    private void updateCloseButtonPosition() {
        closeButtonX = x + width - closeButtonSize - 10;
        closeButtonY = y + height - closeButtonSize - 5;
    }

    public void show() {
        isVisible = true;
        onOpenListener.run();
    }

    public void hide() {
        isVisible = false;
        isDragging = false;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setOnCloseListener(Runnable listener) {
        this.onCloseListener = listener;
    }

    public void setOnOpenListener(Runnable listener) {
        this.onOpenListener = listener;
    }

    public boolean handleTouch(Vector3 touch, boolean isTouched) {
        if (!isVisible) return false;

        if (isTouched && clickButton != null &&
            touch.x >= clickButton.getX() && touch.x <= clickButton.getX() + clickButton.getWidth() &&
            touch.y >= clickButton.getY() && touch.y <= clickButton.getY() + clickButton.getHeight()) {
            myGdxGame.camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
            Gdx.app.postRunnable(() -> {
                if (myGdxGame.computerScreen != null) {
                    myGdxGame.computerScreen.dispose();
                }
            });
            myGdxGame.setScreen(new HackScreen(myGdxGame));
            return true;
        }

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

        if (clickButton != null) {
            clickButton.draw(batch);
        }
    }

    @Override
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
        }
        if (closeButtonTexture != null) {
            closeButtonTexture.dispose();
            closeButtonTexture = null;
        }
        if (clickButton != null) {
            clickButton.dispose();
            clickButton = null;
        }
    }
}
