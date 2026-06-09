package kgu.game.project.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import kgu.game.project.GameResources;
import kgu.game.project.MyGdxGame;


public class IpInputView {

    private static final int MAX_LENGTH = 15;

    private final MyGdxGame myGdxGame;
    private final String correctIp;
    private final Runnable onSuccess;
    private final Runnable onFailure;
    private final StringBuilder input = new StringBuilder();
    private boolean visible = false;
    private boolean wrongAttempt = false;
    private float wrongTimer = 0f;

    private final TextView titleText;
    private final TextView inputDisplay;
    private final TextView hintText;
    private final TextView errorText;
    private final ButtonView confirmButton;
    private final ButtonView clearButton;
    private final ButtonView backButton;

    private static final String[] KEY_LABELS = {
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        ".", "0", "⌫"
    };
    private final ButtonView[] keyButtons;

    private static final float PANEL_X = 240f;
    private static final float PANEL_Y = 150f;
    private static final float PANEL_W = 800f;
    private static final float PANEL_H = 500f;
    private static final float KEY_SIZE = 90f;
    private static final float KEY_GAP = 10f;

    public IpInputView(MyGdxGame myGdxGame, String correctIp,
                       Runnable onSuccess, Runnable onFailure) {
        this.myGdxGame = myGdxGame;
        this.correctIp = correctIp;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;

        // Заголовок
        titleText = new TextView(
            myGdxGame.largeWhiteFont,
            (int) (PANEL_X + PANEL_W / 2 - 250), (int) (PANEL_Y + PANEL_H - 40),
            "Введи IP злоумышленника из логов"
        );

        inputDisplay = new TextView(
            myGdxGame.largeWhiteFont,
            (int) (PANEL_X + 40), (int) (PANEL_Y + PANEL_H - 120),
            "_"
        );

        hintText = new TextView(
            myGdxGame.commonPixelFontText,
            (int) (PANEL_X + 40), (int) (PANEL_Y + PANEL_H - 160),
            "Формат: xxx.xxx.xxx.xxx  (например, 192.168.1.1)"
        );

        errorText = new TextView(
            myGdxGame.commonPixelFontText,
            (int) (PANEL_X + 40), (int) (PANEL_Y + PANEL_H - 195),
            ""
        );

        confirmButton = new ButtonView(
            (int) (PANEL_X + PANEL_W / 2 - 130), (int) (PANEL_Y + 20) - 150,
            260, 55,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "Подтвердить"
        );

        clearButton = new ButtonView(
            (int) (PANEL_X + PANEL_W - 140), (int) (PANEL_Y + 20) - 150,
            150, 55,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "Очистить"
        );

        backButton = new ButtonView(
            (int) (PANEL_X + 20), (int) (PANEL_Y + 20) - 150,
            120, 55,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "Назад"
        );

        keyButtons = new ButtonView[KEY_LABELS.length];
        float kbX = PANEL_X + (PANEL_W - (3 * KEY_SIZE + 2 * KEY_GAP)) / 2f;
        float kbY = PANEL_Y + 100f;
        int cols = 3;
        int rows = 4;
        float kbHeight = rows * KEY_SIZE + (rows - 1) * KEY_GAP;

        for (int i = 0; i < KEY_LABELS.length; i++) {
            int col = i % cols;
            int row = rows - 1 - (i / cols); // строки снизу вверх
            float bx = kbX + col * (KEY_SIZE + KEY_GAP);
            float by = kbY + row * (KEY_SIZE + KEY_GAP) - 150;
            keyButtons[i] = new ButtonView(
                (int) bx, (int) by,
                (int) KEY_SIZE, (int) KEY_SIZE,
                myGdxGame.commonBlackFont,
                GameResources.BUTTON_SHORT_BG_IMG_PATH,
                KEY_LABELS[i]
            );
        }
    }

    public void show() {
        visible = true;
        input.setLength(0);
        wrongAttempt = false;
        updateDisplay();
    }

    public void hide() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void update(float delta) {
        if (wrongAttempt) {
            wrongTimer -= delta;
            if (wrongTimer <= 0) {
                wrongAttempt = false;
                errorText.setText("");
            }
        }
    }

    public void handleTouch() {
        if (!visible) return;
        if (!Gdx.input.justTouched()) return;

        float tx = myGdxGame.touch.x;
        float ty = myGdxGame.touch.y;

        // Клавиатура
        for (int i = 0; i < keyButtons.length; i++) {
            if (keyButtons[i].isHit(tx, ty)) {
                onKeyPressed(KEY_LABELS[i]);
                return;
            }
        }


        if (confirmButton.isHit(tx, ty)) {
            checkAnswer();
            return;
        }


        if (clearButton.isHit(tx, ty)) {
            input.setLength(0);
            updateDisplay();
            return;
        }


        if (backButton.isHit(tx, ty)) {
            hide();
        }
    }

    public void draw(SpriteBatch batch) {
        if (!visible) return;

        titleText.draw(batch);
        inputDisplay.draw(batch);
        hintText.draw(batch);
        if (wrongAttempt) {
            errorText.draw(batch);
        }
        confirmButton.draw(batch);
        clearButton.draw(batch);
        backButton.draw(batch);
        for (ButtonView key : keyButtons) {
            key.draw(batch);
        }
    }

    private void onKeyPressed(String label) {
        if (label.equals("⌫")) {
            if (input.length() > 0) {
                input.deleteCharAt(input.length() - 1);
            }
        } else if (input.length() < MAX_LENGTH) {
            input.append(label);
        }
        updateDisplay();
    }

    private void updateDisplay() {
        String displayed = input.length() == 0 ? "_" : input.toString();
        inputDisplay.setText(displayed);
    }

    private void checkAnswer() {
        if (input.toString().trim().equals(correctIp)) {

            hide();
            onSuccess.run();
        } else {
            wrongAttempt = true;
            wrongTimer = 2.0f;
            errorText.setText("Неверный IP! Проверь логи ещё раз.");
            input.setLength(0);
            updateDisplay();
            if (onFailure != null) onFailure.run();
        }
    }
}
