package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import kgu.game.project.GameResources;
import kgu.game.project.GameSettings;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ButtonView;

public class CutsceneScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;

    private final String[] images;
    private final String[] texts;
    private int currentScene = 0;

    private Texture currentTexture;

    private String fullText;
    private String displayedText = "";
    private float charTimer = 0f;
    private final float CHAR_DELAY = 0.045f;
    private int charIndex = 0;
    private boolean textComplete = false;

    private float lastTapTime = -1f;
    private final float DOUBLE_TAP_INTERVAL = 0.35f;

    private ButtonView nextButton;
    private final Runnable onFinished;

    public CutsceneScreen(MyGdxGame myGdxGame, String[] images, String[] texts, Runnable onFinished) {
        this.myGdxGame = myGdxGame;
        this.images = images;
        this.texts = texts;
        this.onFinished = onFinished;

        nextButton = new ButtonView(
            GameSettings.SCREEN_WIDTH - 280, 40,
            200, 60,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "Далее"
        );

        loadScene(0);
    }

    private void loadScene(int index) {
        if (currentTexture != null) {
            currentTexture.dispose();
        }
        currentTexture = new Texture(Gdx.files.internal(images[index]));
        fullText = texts[index];
        displayedText = "";
        charIndex = 0;
        charTimer = 0f;
        textComplete = false;
    }

    private void completeText() {
        displayedText = fullText;
        charIndex = fullText.length();
        textComplete = true;
    }

    private void goNext() {
        currentScene++;
        if (currentScene >= images.length) {
            if (onFinished != null) onFinished.run();
        } else {
            loadScene(currentScene);
        }
    }

    @Override
    public void render(float delta) {
        if (!textComplete) {
            charTimer += delta;
            while (charTimer >= CHAR_DELAY && charIndex < fullText.length()) {
                charTimer -= CHAR_DELAY;
                charIndex++;
                displayedText = fullText.substring(0, charIndex);
            }
            if (charIndex >= fullText.length()) {
                textComplete = true;
            }
        }

        handleInput();
        draw();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touch = myGdxGame.uiCamera.unproject(
                new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)
            );

            if (textComplete && nextButton.isHit(touch.x, touch.y)) {
                goNext();
                return;
            }

            float now = (float) (System.currentTimeMillis() / 1000.0);
            if (lastTapTime > 0 && (now - lastTapTime) <= DOUBLE_TAP_INTERVAL) {
                completeText();
                lastTapTime = -1f;
            } else {
                lastTapTime = now;
            }
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
            if (!textComplete) {
                completeText();
            } else {
                goNext();
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        myGdxGame.batch.setProjectionMatrix(myGdxGame.uiCamera.combined);
        myGdxGame.batch.begin();

        myGdxGame.batch.draw(
            currentTexture,
            0,
            1f * GameSettings.SCREEN_HEIGHT / 2f,
            GameSettings.SCREEN_WIDTH,
            GameSettings.SCREEN_HEIGHT * 1f / 2f
        );

        myGdxGame.arialFontBlue.draw(
            myGdxGame.batch,
            displayedText,
            60,
            GameSettings.SCREEN_HEIGHT / 2f - 30f
        );

        if (textComplete) {
            nextButton.draw(myGdxGame.batch);
        }

        myGdxGame.batch.end();
    }

    @Override
    public void dispose() {
        if (currentTexture != null) {
            currentTexture.dispose();
        }
    }
}
