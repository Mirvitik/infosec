package kgu.game.project.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import kgu.game.project.GameResources;
import kgu.game.project.GameSettings;
import kgu.game.project.MyGdxGame;

public class PasswordInputView extends View implements InputProcessor {

    private MyGdxGame myGdxGame;

    private ImageView loginForm;
    private ButtonView confirmButton;
    String answer;

    private StringBuilder inputText = new StringBuilder();
    private BitmapFont font;
    private BitmapFont fontRed;
    private GlyphLayout glyphLayout;

    private float inputFieldX;
    private float inputFieldY;
    private float inputFieldWidth = 170;
    private float inputFieldHeight = 25;

    private boolean isActive = false;
    private float blinkTimer = 0;
    private boolean showCursor = true;
    private boolean showError = false;
    private boolean isVisible = false;
    ButtonView closeButton;

    public interface OnSuccess {
        void onSuccess();
    }

    private OnSuccess onSuccess;
    boolean isDisposed = false;
    boolean dismissedByUser;

    public PasswordInputView(MyGdxGame myGdxGame, OnSuccess onSuccess, String answer) {
        super(0, 0);
        this.myGdxGame = myGdxGame;
        this.onSuccess = onSuccess;
        this.answer = answer;

        float centerX = GameSettings.SCREEN_WIDTH / 2f;

        loginForm = new ImageView(centerX - 200, 210, 500, 400, GameResources.LOGIN_FORM_PATH);
        inputFieldX = centerX - 170 / 2f;
        inputFieldY = 510;

        confirmButton = new ButtonView(centerX + 90, inputFieldY, 50, 25,
            new BitmapFont(), GameResources.PASSWORD_IMG_PATH, "OK");

        font = new BitmapFont();
        font.setColor(Color.BLACK);
        fontRed = new BitmapFont();
        fontRed.setColor(Color.RED);
        glyphLayout = new GlyphLayout();
        closeButton = new ButtonView(centerX + 210, inputFieldY + 50, 32, 32, GameResources.CLOSE_BUTTON_PATH);
        dismissedByUser = false;
    }

    public void show() {
        isVisible = true;
        dismissedByUser = false;
        inputText.setLength(0);
        showError = false;
        Gdx.input.setInputProcessor(this);
    }

    public boolean isReallyDisposed() {
        return isDisposed;
    }

    public void hide() {
        isVisible = false;
        isActive = false;
        Gdx.input.setOnscreenKeyboardVisible(false);
        Gdx.input.setInputProcessor(null);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void update(float delta) {
        if (!isVisible || !isActive) return;
        blinkTimer += delta;
        if (blinkTimer >= 0.5f) {
            blinkTimer = 0;
            showCursor = !showCursor;
        }
    }

    public void handleTouch() {
        if (!isVisible || !Gdx.input.justTouched()) return;

        Vector3 touchPos = myGdxGame.uiCamera.unproject(
            new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        if (closeButton.isHit(touchPos.x, touchPos.y)) {
            this.hide();
            return;
        }
        boolean hitField = touchPos.x >= inputFieldX &&
            touchPos.x <= inputFieldX + inputFieldWidth &&
            touchPos.y >= inputFieldY &&
            touchPos.y <= inputFieldY + inputFieldHeight;

        if (hitField) {
            isActive = true;
            blinkTimer = 0;
            showCursor = true;
            Gdx.input.setOnscreenKeyboardVisible(true);
        } else if (isActive) {
            isActive = false;
            Gdx.input.setOnscreenKeyboardVisible(false);
        }

        if (confirmButton.isHit(touchPos.x, touchPos.y)) {
            checkPassword();
        }
    }

    private void checkPassword() {
        if (inputText.toString().equals(answer)) {
            hide();
            if (onSuccess != null) onSuccess.onSuccess();
        } else {
            showError = true;
            inputText.setLength(0);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isVisible) return;

        loginForm.draw(batch);
        confirmButton.draw(batch);

        // Input field background using a plain white pixel drawn via font
        // (no ShapeRenderer to avoid batch conflicts)
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "________________", inputFieldX, inputFieldY + inputFieldHeight);
        font.setColor(Color.BLACK);

        // Input text
        String display = inputText.toString();
        if (isActive && showCursor) display += "|";

        if (inputText.length() == 0 && !isActive) {
            font.setColor(Color.GRAY);
            font.draw(batch, "Enter answer", inputFieldX + 5, inputFieldY + inputFieldHeight - 5);
            font.setColor(Color.BLACK);
        } else {
            font.draw(batch, display, inputFieldX + 5, inputFieldY + inputFieldHeight - 5);
        }
        closeButton.draw(myGdxGame.batch);
        font.draw(batch, "DOOR TO THE NEXT LEVEL", GameSettings.SCREEN_WIDTH / 2f - 20, 580);

        if (showError) {
            fontRed.draw(batch, "Incorrect password",
                inputFieldX, inputFieldY + inputFieldHeight + 20);
        }
    }

    @Override
    public void dispose() {
        loginForm.dispose();
        confirmButton.dispose();
        font.dispose();
        fontRed.dispose();
        closeButton.dispose();
        isDisposed = true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!isActive) return false;
        if (keycode == com.badlogic.gdx.Input.Keys.BACKSPACE) {
            if (inputText.length() > 0)
                inputText.deleteCharAt(inputText.length() - 1);
            return true;
        }
        if (keycode == com.badlogic.gdx.Input.Keys.ENTER) {
            checkPassword();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (!isActive) return false;
        if (character >= 32 && character < 127 && inputText.length() < 15) {
            if (Character.isLetterOrDigit(character) || character == '_' || character == '-') {
                inputText.append(character);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int p, int b) {
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int p, int b) {
        return false;
    }

    @Override
    public boolean touchCancelled(int x, int y, int p, int b) {
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int p) {
        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        return false;
    }

    @Override
    public boolean scrolled(float ax, float ay) {
        return false;
    }
}
