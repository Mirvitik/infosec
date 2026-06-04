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
import kgu.game.project.screens.LevelOneScreen;

public class PasswordView extends View implements InputProcessor {
    private final MyGdxGame myGdxGame;
    private BitmapFont font;
    private BitmapFont titleFont;
    private GlyphLayout glyphLayout;
    private ShapeRenderer shapeRenderer;
    private ButtonView confirmButton;
    private TextView error;
    private TextView titleTextView;
    private TextView userLabel;
    private ImageView loginForm;
    private ImageView inputFieldBg;
    private StringBuilder inputText;
    private boolean isActive = false;
    private float blinkTimer = 0;
    private boolean showCursor = true;
    private float inputFieldX = 550;
    private float inputFieldY = 420;
    private float inputFieldWidth = 480;
    private float inputFieldHeight = 50;

    private float errorTimer = 0;
    private boolean showError = false;
    private boolean isVisible = false;

    private static final String CORRECT_PASSWORD = "password";

    public PasswordView(MyGdxGame myGdxGame) {
        super(0, 0, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        this.myGdxGame = myGdxGame;

        font = new BitmapFont();
        font.setColor(Color.BLACK);
        font.getData().setScale(1.2f);

        titleFont = new BitmapFont();
        titleFont.setColor(Color.WHITE);
        titleFont.getData().setScale(1.5f);

        glyphLayout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();
        inputText = new StringBuilder();

        loginForm = new ImageView(GameSettings.SCREEN_WIDTH / 2f - 200, 200, 420, 300, GameResources.LOGIN_FORM_PATH);
        inputFieldBg = new ImageView(GameSettings.SCREEN_WIDTH / 2f - 200, inputFieldY - 50, inputFieldWidth - 170, inputFieldHeight + 20, GameResources.INPUT_IMG_PATH);

        titleTextView = new TextView(titleFont, GameSettings.SCREEN_WIDTH / 2f - 80, 480, "LOGIN");
        userLabel = new TextView(font, inputFieldX, inputFieldY, "ENTER PASSWORD:");

        confirmButton = new ButtonView(
            GameSettings.SCREEN_WIDTH / 2f + 100,
            inputFieldY - 95,
            110,
            45,
            font,
            GameResources.PASSWORD_IMG_PATH,
            "LOGIN"
        );

        error = new TextView(font, GameSettings.SCREEN_WIDTH / 2f - 100, inputFieldY - 120, "INCORRECT PASSWORD!");
    }

    // Обновление логики (вызывается из экрана)
    public void update(float delta) {
        if (!isVisible) return;

        handleTouchInput();
        updateCursorBlink(delta);
        updateErrorTimer(delta);
    }

    // Отрисовка (вызывается из экрана)
    @Override
    public void draw(SpriteBatch batch) {
        if (!isVisible) return;

        loginForm.draw(batch);
        inputFieldBg.draw(batch);
        titleTextView.draw(batch);
        userLabel.draw(batch);
        confirmButton.draw(batch);

        if (showError) {
            error.draw(batch);
        }

        drawInputField(batch);
    }

    private void drawInputField(SpriteBatch batch) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.LIGHT_GRAY);
        shapeRenderer.rect(inputFieldX - 600, inputFieldY, inputFieldWidth, inputFieldHeight);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(inputFieldX + 2, inputFieldY + 2, inputFieldWidth - 4, inputFieldHeight - 4);

        if (isActive) {
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.rect(inputFieldX - 600, inputFieldY, inputFieldWidth, inputFieldHeight);
        }
        shapeRenderer.end();

        String displayText = getMaskedText();

        if (isActive && showCursor) {
            displayText += "|";
        }

        glyphLayout.setText(font, displayText);
        float textX = inputFieldX + 10;
        float textY = inputFieldY + inputFieldHeight / 2 + glyphLayout.height / 2;

        if (inputText.length() == 0 && !isActive) {
            font.setColor(Color.GRAY);
            font.draw(batch, "Enter your password", textX, textY);
            font.setColor(Color.BLACK);
        } else {
            font.draw(batch, displayText, textX, textY);
        }
    }

    private String getMaskedText() {
        if (inputText.length() == 0) return "";
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < inputText.length(); i++) {
            masked.append("•");
        }
        return masked.toString();
    }

    private void updateCursorBlink(float delta) {
        if (isActive) {
            blinkTimer += delta;
            if (blinkTimer >= 0.5f) {
                blinkTimer = 0;
                showCursor = !showCursor;
            }
        }
    }

    private void updateErrorTimer(float delta) {
        if (errorTimer > 0) {
            errorTimer -= delta;
            if (errorTimer <= 0) {
                showError = false;
            }
        }
    }

    private void handleTouchInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = myGdxGame.uiCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (touchPos.x >= inputFieldX && touchPos.x <= inputFieldX + inputFieldWidth &&
                touchPos.y >= inputFieldY && touchPos.y <= inputFieldY + inputFieldHeight) {
                isActive = true;
                blinkTimer = 0;
                showCursor = true;
                Gdx.input.setOnscreenKeyboardVisible(true);
                Gdx.input.setInputProcessor(this);
            } else if (isActive) {
                isActive = false;
                showCursor = false;
                Gdx.input.setOnscreenKeyboardVisible(false);
                Gdx.input.setInputProcessor(null);
            }

            if (confirmButton.isHit(touchPos.x, touchPos.y)) {
                checkPassword();
            }
        }
    }

    private void checkPassword() {
        if (inputText.toString().equals(CORRECT_PASSWORD)) {
            if (myGdxGame.levelOneScreen != null) {
                myGdxGame.levelOneScreen.dispose();
            }
            myGdxGame.levelOneScreen = new LevelOneScreen(myGdxGame);
            myGdxGame.setScreen(myGdxGame.levelOneScreen);
        } else {
            showError = true;
            errorTimer = 2.0f;
            inputText.setLength(0);

            if (Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.Android) {
                Gdx.input.vibrate(200);
            }
        }
    }

    // Управление видимостью
    public void show() {
        isVisible = true;
        reset();
        Gdx.input.setInputProcessor(this);
    }

    public void hide() {
        isVisible = false;
        Gdx.input.setInputProcessor(null);
    }

    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!isActive || !isVisible) return false;

        if (keycode == com.badlogic.gdx.Input.Keys.BACKSPACE) {
            if (inputText.length() > 0) {
                inputText.deleteCharAt(inputText.length() - 1);
            }
            return true;
        }

        if (keycode == com.badlogic.gdx.Input.Keys.ENTER || keycode == com.badlogic.gdx.Input.Keys.NUMPAD_ENTER) {
            isActive = false;
            Gdx.input.setOnscreenKeyboardVisible(false);
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
        if (!isActive || !isVisible) return false;

        if (character >= 32 && character < 127) {
            if (inputText.length() < 20) {
                if (Character.isLetterOrDigit(character) ||
                    character == '_' || character == '-' || character == '!' ||
                    character == '@' || character == '#' || character == '$') {
                    inputText.append(character);
                }
            }
            return true;
        }
        return false;
    }

    public void reset() {
        inputText.setLength(0);
        isActive = false;
        showError = false;
        showCursor = false;
        errorTimer = 0;
        Gdx.input.setOnscreenKeyboardVisible(false);
    }

    public String getEnteredPassword() {
        return inputText.toString();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void dispose() {
        hide();

        if (font != null) font.dispose();
        if (titleFont != null) titleFont.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (confirmButton != null) confirmButton.dispose();
        if (loginForm != null) loginForm.dispose();
        if (inputFieldBg != null) inputFieldBg.dispose();
        if (titleTextView != null) titleTextView.dispose();
        if (userLabel != null) userLabel.dispose();
        if (error != null) error.dispose();
    }
}
