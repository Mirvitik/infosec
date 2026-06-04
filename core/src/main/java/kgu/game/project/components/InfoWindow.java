package kgu.game.project.components;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import kgu.game.project.GameResources;
import kgu.game.project.MyGdxGame;
import kgu.game.project.screens.GameScreen;

public class InfoWindow extends View {
    Game myGdxGame;
    private Texture backgroundTexture;
    private Texture closeButtonTexture;
    private BitmapFont font;

    private String title;
    private String content;

    private boolean isDragging = false;
    private float dragOffsetX;
    Screen gameScreen;
    private float dragOffsetY;

    private float closeButtonSize = 32;
    private float closeButtonX;
    private float closeButtonY;

    private boolean isVisible = false;
    private Runnable onCloseListener;

    // Размеры окна
    private static final float WINDOW_WIDTH = 500;
    private static final float WINDOW_HEIGHT = 600;
    private static final float TITLE_BAR_HEIGHT = 40;
    private static final float PADDING = 80;
    private ButtonView clickButton;

    public InfoWindow(Game myGdxGame, float x, float y, String title, String content,
                           String backgroundPath, String closeButtonPath) {
        super(x, y, WINDOW_WIDTH, WINDOW_HEIGHT);
        this.myGdxGame = myGdxGame;
        this.gameScreen = new GameScreen((MyGdxGame) myGdxGame);

        this.title = title;
        this.content = content;
        this.font = new BitmapFont();

        backgroundTexture = new Texture(backgroundPath);
        closeButtonTexture = new Texture(closeButtonPath);
        clickButton = new ButtonView(x + 120, y + 200, GameResources.PRIZE_IMG_PATH);

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

    public void setOnCloseListener(Runnable listener) {
        this.onCloseListener = listener;
    }

    public boolean handleTouch(Vector3 touch, boolean isTouched) {
        if (!isVisible) return false;

        // Проверяем клик по clickButton (только в момент касания)
        if (isTouched && clickButton != null &&
            touch.x >= clickButton.getX() && touch.x <= clickButton.getX() + clickButton.getWidth() &&
            touch.y >= clickButton.getY() && touch.y <= clickButton.getY() + clickButton.getHeight()) {
            myGdxGame.setScreen(gameScreen);
            // Здесь можно добавить любую другую логику
            return true; // Возвращаем true, чтобы событие не ушло дальше
        }

        // Проверяем нажатие на кнопку закрытия
        if (isTouched &&
            touch.x >= closeButtonX && touch.x <= closeButtonX + closeButtonSize &&
            touch.y >= closeButtonY && touch.y <= closeButtonY + closeButtonSize) {
            hide();
            if (onCloseListener != null) {
                onCloseListener.run();
            }
            return true;
        }

        // Проверяем нажатие на заголовок окна (для перетаскивания)
        if (isTouched &&
            touch.x >= x && touch.x <= x + width &&
            touch.y >= y && touch.y <= y + TITLE_BAR_HEIGHT) {
            isDragging = true;
            dragOffsetX = touch.x - x;
            dragOffsetY = touch.y - y;
            return true;
        }

        // Отпускаем кнопку мыши/пальца
        if (!isTouched) {
            isDragging = false;
        }

        // Перетаскивание окна
        if (isDragging && isTouched) {
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

        // Обновляем позицию кнопки закрытия
        closeButtonX += deltaX;
        closeButtonY += deltaY;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isVisible) return;

        // Рисуем фон окна
        batch.draw(backgroundTexture, x, y, width, height);

        // Рисуем заголовок
        if (font != null && title != null) {
            font.draw(batch, title, x + PADDING, y + height - PADDING);
        }

        // Рисуем линию под заголовком
        // Можно добавить через ShapeRenderer, но для простоты используем текстуру

        // Рисуем содержимое
        if (font != null && content != null) {
            // Разбиваем текст на строки
            String[] lines = content.split("\n");
            float currentY = y + height - TITLE_BAR_HEIGHT - PADDING;
            for (String line : lines) {
                font.draw(batch, line, x + PADDING, currentY);
                currentY -= font.getLineHeight();
            }
        }

        // Рисуем кнопку закрытия
        batch.draw(closeButtonTexture, closeButtonX, closeButtonY, closeButtonSize, closeButtonSize);

        // Рисуем clickButton
        if (clickButton != null) {
            clickButton.draw(batch);
        }
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        closeButtonTexture.dispose();
    }
}
