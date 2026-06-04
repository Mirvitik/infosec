package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import kgu.game.project.GameResources;
import kgu.game.project.GameSettings;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ButtonView;
import kgu.game.project.components.ImageView;
import kgu.game.project.components.InfoWindow;
import kgu.game.project.components.TextView;
import kgu.game.project.managers.LocalizationManager;

public class LoginScreen extends ScreenAdapter implements InputProcessor {
    MyGdxGame myGdxGame;

    ImageView blackoutImageView;
    ImageView titleTextView;

    // Text input fields
    private StringBuilder inputText;

    private GlyphLayout glyphLayout;
    private ShapeRenderer shapeRenderer;

    // Input field position and size
    private float inputFieldX = 550;
    private float inputFieldY = 500;
    private float inputFieldWidth = 480;
    private float inputFieldHeight = 50;
    private ImageView loginForm;
    private ImageView avatar;
    ImageView image;
    ButtonView confirmButton;
    ButtonView forgotButton;
    InfoWindow infoWindow;
    TextView error;
    private boolean isActive = false;
    private ButtonView tornOffButton;
    private float blinkTimer = 0;
    private boolean showCursor = true;

    public LoginScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        blackoutImageView = new ImageView(0, 0, 1280, 1280, GameResources.LOGIN_IMG_PATH);
        titleTextView = new ImageView(GameSettings.SCREEN_WIDTH / 2f - 150, -50, 300, 300, GameResources.SHINDOWS_IMG_PATH);
        loginForm = new ImageView(GameSettings.SCREEN_WIDTH / 2f - 200, 210, 400, 500, GameResources.LOGIN_FORM_PATH);
        avatar = new ImageView(GameSettings.SCREEN_WIDTH / 2f - 32, 550, 64, 64, GameResources.AVATAR);

        // Initialize text input
        inputText = new StringBuilder();
        glyphLayout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();
        image = new ImageView(GameSettings.SCREEN_WIDTH / 2f - 170 / 2f, 510, 170, 25, GameResources.INPUT_IMG_PATH);
        confirmButton = new ButtonView(GameSettings.SCREEN_WIDTH / 2f + 90, 510, 50, 25, myGdxGame.arialFont, GameResources.PASSWORD_IMG_PATH, LocalizationManager.get("login.confirm"));
        forgotButton = new ButtonView(GameSettings.SCREEN_WIDTH / 2f - 230 / 2f, 250, 230, 25, myGdxGame.arialFont, GameResources.PASSWORD_IMG_PATH, LocalizationManager.get("login.forgot"), 1f);
        error = new TextView(myGdxGame.arialFont, GameSettings.SCREEN_WIDTH / 2f - 85, 538, LocalizationManager.get("login.error"));
        infoWindow = new InfoWindow(myGdxGame, 120, 120, LocalizationManager.get("login.incorrect"), LocalizationManager.get("login.hint"), GameResources.WINDOW_PATH, GameResources.CLOSE_BUTTON_PATH);
        tornOffButton = new ButtonView(0, 0, 64, 64, GameResources.TURN_OFF_IMG_PATH);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        int bodyCount = myGdxGame.world.getBodyCount();
        System.out.println(bodyCount);
        handleTouchInput();
        updateCursorBlink(delta);

        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.uiCamera.combined);
        ScreenUtils.clear(Color.CLEAR);

        // Draw images
        myGdxGame.batch.begin();
        blackoutImageView.draw(myGdxGame.batch);
        titleTextView.draw(myGdxGame.batch);
        loginForm.draw(myGdxGame.batch);
        avatar.draw(myGdxGame.batch);
        image.draw(myGdxGame.batch);
        confirmButton.draw(myGdxGame.batch);
        forgotButton.draw(myGdxGame.batch);
        myGdxGame.arialFont.draw(myGdxGame.batch, LocalizationManager.get("login.title"), 510, 670);
        myGdxGame.arialFont.draw(myGdxGame.batch, LocalizationManager.get("login.user"), 350, 750);
        error.draw(myGdxGame.batch);
        tornOffButton.draw(myGdxGame.batch);
        myGdxGame.batch.end();

        drawInputField();
        // Draw input field
        myGdxGame.batch.begin();
        infoWindow.draw(myGdxGame.batch);
        myGdxGame.batch.end();
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

    private void drawInputField() {
        // Draw background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.LIGHT_GRAY);
        shapeRenderer.rect(inputFieldX, inputFieldY, inputFieldWidth, inputFieldHeight);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(inputFieldX + 2, inputFieldY + 2, inputFieldWidth - 4, inputFieldHeight - 4);
        shapeRenderer.end();

        // Draw text
        myGdxGame.batch.begin();
        String displayText = inputText.toString();

        if (isActive && showCursor) {
            displayText += "|";
        }

        glyphLayout.setText(myGdxGame.arialFont, displayText);
        float textX = inputFieldX + 10;
        float textY = inputFieldY + inputFieldHeight / 2 + glyphLayout.height / 2;

        myGdxGame.arialFont.draw(myGdxGame.batch, displayText, textX, textY);

        // Draw hint text if empty
        if (inputText.length() == 0 && !isActive) {
            myGdxGame.arialFont.setColor(Color.GRAY);
            myGdxGame.arialFont.draw(myGdxGame.batch, LocalizationManager.get("login.password_hint"), textX, textY);
            myGdxGame.arialFont.setColor(Color.BLACK);
        }

        myGdxGame.batch.end();
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
            } else if (isActive) {
                isActive = false;
                Gdx.input.setOnscreenKeyboardVisible(false);
            }
            if (confirmButton.isHit(touchPos.x, touchPos.y)) {
                if (inputText.toString().equals("password")) {
                    if (myGdxGame.computerScreen != null){
                        myGdxGame.computerScreen = null;
                    }
                    myGdxGame.computerScreen = new ComputerScreen(myGdxGame);
                    myGdxGame.setScreen(myGdxGame.computerScreen);
                }
            }
            if (!infoWindow.isVisible()) {
                forgotButton.show();
            }
            if (forgotButton.isHit(touchPos.x, touchPos.y)) {
                infoWindow.show();
                forgotButton.hide();
            }
            if (tornOffButton.isHit(touchPos.x, touchPos.y)) {
                // удаляем все тела из игрового мира
                Array<Body> bodies = new Array<>();
                myGdxGame.world.getBodies(bodies);

                // Удаляем все тела
                for (Body body : bodies) {
                    myGdxGame.world.destroyBody(body);
                }

                bodies.clear();
                myGdxGame.gameScreen = new GameScreen(myGdxGame);
                myGdxGame.setScreen(myGdxGame.gameScreen);
            }
            infoWindow.handleTouch(touchPos, true);
        }
    }


    @Override
    public boolean keyDown(int keycode) {
        if (!isActive) return false;

        // Handle Backspace
        if (keycode == com.badlogic.gdx.Input.Keys.BACKSPACE) {
            if (inputText.length() > 0) {
                inputText.deleteCharAt(inputText.length() - 1);
            }
            return true;
        }

        // Handle Enter
        if (keycode == com.badlogic.gdx.Input.Keys.ENTER) {
            isActive = false;
            Gdx.input.setOnscreenKeyboardVisible(false);
            onLoginAttempt(inputText.toString());
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
        if (character >= 32 && character < 127) { // Printable ASCII
            if (inputText.length() < 15) {
                // Filter to alphanumeric and some special characters
                if (Character.isLetterOrDigit(character) || character == ' ' || character == '_' || character == '-') {
                    inputText.append(character);
                }
            }
            return true;
        }
        return false;
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

    private void onLoginAttempt(String username) {
        System.out.println("Login attempt with username: " + username);
        if (username.trim().length() > 0) {
            // Proceed to next screen
            // myGdxGame.setScreen(new GameScreen(myGdxGame, username));
        }
    }

    @Override
    public void dispose() {


        // Удаляем ShapeRenderer
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }

        // Удаляем все ImageView
        if (blackoutImageView != null) {
            blackoutImageView.dispose();
            blackoutImageView = null;
        }
        if (titleTextView != null) {
            titleTextView.dispose();
            titleTextView = null;
        }
        if (loginForm != null) {
            loginForm.dispose();
            loginForm = null;
        }
        if (avatar != null) {
            avatar.dispose();
            avatar = null;
        }
        if (image != null) {
            image.dispose();
            image = null;
        }

        // Удаляем ButtonView
        if (confirmButton != null) {
            confirmButton.dispose();
            confirmButton = null;
        }
        if (forgotButton != null) {
            forgotButton.dispose();
            forgotButton = null;
        }

        // Удаляем InfoWindow
        if (infoWindow != null) {
            infoWindow.dispose();
            infoWindow = null;
        }
        Gdx.input.setInputProcessor(null);

    }
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
