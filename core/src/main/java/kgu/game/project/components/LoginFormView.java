package kgu.game.project.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import kgu.game.project.GameResources;
import kgu.game.project.GameSettings;
import kgu.game.project.MyGdxGame;

public class LoginFormView extends View implements InputProcessor {

    private MyGdxGame myGdxGame;

    private ImageView loginForm;
    private ImageView avatar;
    private ImageView inputImage;
    private ButtonView confirmButton;
    private ButtonView forgotButton;

    private StringBuilder inputText;
    private BitmapFont font;
    private BitmapFont fontRed;
    private GlyphLayout glyphLayout;
    private ShapeRenderer shapeRenderer;

    private float inputFieldX;
    private float inputFieldY;
    private float inputFieldWidth = 170;
    private float inputFieldHeight = 25;

    private boolean isActive = false;
    private float blinkTimer = 0;
    private boolean showCursor = true;
    private boolean showError = false;

    private boolean isVisible = false;

    // Callback when login succeeds
    public interface OnLoginSuccess {
        void onSuccess();
    }

    private OnLoginSuccess onLoginSuccess;

    public LoginFormView(MyGdxGame myGdxGame, OnLoginSuccess onLoginSuccess) {
        super(0, 0);
        this.myGdxGame = myGdxGame;
        this.onLoginSuccess = onLoginSuccess;

        float centerX = GameSettings.SCREEN_WIDTH / 2f;

        loginForm = new ImageView(centerX - 200, 210, 400, 500, GameResources.LOGIN_FORM_PATH);
        avatar = new ImageView(centerX - 32, 550, 64, 64, GameResources.AVATAR);

        inputFieldX = centerX - 170 / 2f;
        inputFieldY = 510;
        inputImage = new ImageView(inputFieldX, inputFieldY, 170, 25, GameResources.INPUT_IMG_PATH);

        confirmButton = new ButtonView(centerX + 90, 510, 50, 25, new BitmapFont(), GameResources.PASSWORD_IMG_PATH, "OK");
        forgotButton = new ButtonView(centerX - 230 / 2f, 250, 230, 25, new BitmapFont(), GameResources.PASSWORD_IMG_PATH, "Forgot your password?", 1f);

        inputText = new StringBuilder();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        fontRed = new BitmapFont();
        fontRed.setColor(Color.RED);
        glyphLayout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();
    }

    public void show() {
        isVisible = true;
        inputText.setLength(0);
        showError = false;
        Gdx.input.setInputProcessor(this);
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
        if (!isVisible) return;
        if (isActive) {
            blinkTimer += delta;
            if (blinkTimer >= 0.5f) {
                blinkTimer = 0;
                showCursor = !showCursor;
            }
        }
    }

    public void handleTouch() {
        if (!isVisible) return;
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = myGdxGame.uiCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            // Input field tap
            if (touchPos.x >= inputFieldX && touchPos.x <= inputFieldX + inputFieldWidth &&
                touchPos.y >= inputFieldY && touchPos.y <= inputFieldY + inputFieldHeight) {
                isActive = true;
                blinkTimer = 0;
                showCursor = true;
                Gdx.input.setOnscreenKeyboardVisible(true);
            } else if (isActive) {
                isActive = false;
                Gdx.input.setOnscreenKeyboardVisible(false);
            }

            // Confirm button
            if (confirmButton.isHit(touchPos.x, touchPos.y)) {
                if (inputText.toString().equals("password")) {
                    hide();
                    if (onLoginSuccess != null) onLoginSuccess.onSuccess();
                } else {
                    showError = true;
                }
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isVisible) return;

        loginForm.draw(batch);
        avatar.draw(batch);
        inputImage.draw(batch);
        confirmButton.draw(batch);
        forgotButton.draw(batch);

        font.draw(batch, "Log in", myGdxGame.uiCamera.position.x - 110, 670);
        font.draw(batch, "User", myGdxGame.uiCamera.position.x - 290, 750);

        if (showError) {
            fontRed.draw(batch, "Incorrect password", inputFieldX, inputFieldY + inputFieldHeight + 15);
        }

        // Draw input text (end batch temporarily for shapeRenderer, then restart)
        batch.end();
        drawInputField();
        batch.begin();
    }

    private void drawInputField() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.LIGHT_GRAY);
        shapeRenderer.rect(inputFieldX, inputFieldY, inputFieldWidth, inputFieldHeight);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(inputFieldX + 2, inputFieldY + 2, inputFieldWidth - 4, inputFieldHeight - 4);
        shapeRenderer.end();

        myGdxGame.batch.begin();
        String displayText = inputText.toString();
        if (isActive && showCursor) displayText += "|";

        glyphLayout.setText(font, displayText);
        float textX = inputFieldX + 10;
        float textY = inputFieldY + inputFieldHeight / 2 + glyphLayout.height / 2;
        font.draw(myGdxGame.batch, displayText, textX, textY);

        if (inputText.length() == 0 && !isActive) {
            font.setColor(Color.GRAY);
            font.draw(myGdxGame.batch, "Enter your password", textX, textY);
            font.setColor(Color.BLACK);
        }
        myGdxGame.batch.end();
    }

    @Override
    public void dispose() {
        loginForm.dispose();
        avatar.dispose();
        inputImage.dispose();
        confirmButton.dispose();
        forgotButton.dispose();
        font.dispose();
        fontRed.dispose();
        shapeRenderer.dispose();
    }

    // InputProcessor
    @Override
    public boolean keyDown(int keycode) {
        if (!isActive) return false;
        if (keycode == com.badlogic.gdx.Input.Keys.BACKSPACE) {
            if (inputText.length() > 0) inputText.deleteCharAt(inputText.length() - 1);
            return true;
        }
        if (keycode == com.badlogic.gdx.Input.Keys.ENTER) {
            isActive = false;
            Gdx.input.setOnscreenKeyboardVisible(false);
            return true;
        }
        return false;
    }

    @Override public boolean keyUp(int keycode) { return false; }

    @Override
    public boolean keyTyped(char character) {
        if (!isActive) return false;
        if (character >= 32 && character < 127 && inputText.length() < 15) {
            if (Character.isLetterOrDigit(character) || character == ' ' || character == '_' || character == '-') {
                inputText.append(character);
            }
            return true;
        }
        return false;
    }

    @Override public boolean touchDown(int x, int y, int pointer, int button) { return false; }
    @Override public boolean touchUp(int x, int y, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int x, int y, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int x, int y, int pointer) { return false; }
    @Override public boolean mouseMoved(int x, int y) { return false; }
    @Override public boolean scrolled(float ax, float ay) { return false; }
}
