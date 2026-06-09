package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import kgu.game.project.GameResources;
import kgu.game.project.GameSettings;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ButtonView;
import kgu.game.project.components.ImageView;
import kgu.game.project.components.MovingBackgroundView;
import kgu.game.project.components.TextView;
import kgu.game.project.managers.LocalizationManager;
import kgu.game.project.managers.MemoryManager;

public class MenuScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;

    MovingBackgroundView backgroundView;
    TextView titleView;
    ButtonView startButtonView;
    ButtonView loadGameButtonView;
    ButtonView settingsButtonView;
    ButtonView exitButtonView;
    ImageView image;

    public MenuScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        float num = 250f;
        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);
        image = new ImageView(800, GameSettings.SCREEN_HEIGHT / 2 - 250f, GameResources.MASK_IMG_PATH);
        titleView = new TextView(myGdxGame.largeWhiteFont, 210, 560, "3xpl01T");
        startButtonView = new ButtonView(200, 396, 340, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_LONG_BG_IMG_PATH, LocalizationManager.get("menu.start"));
        loadGameButtonView = new ButtonView(200, 301, 340, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_LONG_BG_IMG_PATH, LocalizationManager.get("menu.load"));
        settingsButtonView = new ButtonView(200, 206, 340, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_LONG_BG_IMG_PATH, LocalizationManager.get("menu.settings"));
        exitButtonView = new ButtonView(200, 111, 340, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_LONG_BG_IMG_PATH, LocalizationManager.get("menu.exit"));
    }

    @Override
    public void render(float delta) {

        handleInput();

        myGdxGame.uiCamera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.uiCamera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();

        backgroundView.draw(myGdxGame.batch);
        image.draw(myGdxGame.batch);
        titleView.draw(myGdxGame.batch);
        exitButtonView.draw(myGdxGame.batch);
        settingsButtonView.draw(myGdxGame.batch);
        loadGameButtonView.draw(myGdxGame.batch);
        startButtonView.draw(myGdxGame.batch);

        myGdxGame.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.uiCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (startButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                String[] images = {
                    GameResources.STORY_TELLING_IMG_PATH,
                    GameResources.STORY_TELLING_2_IMG_PATH,
                    GameResources.STORY_TELLING_3_IMG_PATH
                };
                String[] texts = {
                    LocalizationManager.get("story.intro.0"),
                    LocalizationManager.get("story.intro.1"),
                    LocalizationManager.get("story.intro.2")
                };
                myGdxGame.audioManager.backgroundMusic.stop();
                if (MemoryManager.loadIsMusicOn()){
                    myGdxGame.audioManager.storyMusic.play();
                }

                myGdxGame.setScreen(new CutsceneScreen(myGdxGame, images, texts, () -> {
                    if (myGdxGame.gameScreen != null) myGdxGame.gameScreen.dispose();
                    myGdxGame.gameScreen = new GameScreen(myGdxGame);
                    myGdxGame.setScreen(myGdxGame.gameScreen);
                    myGdxGame.audioManager.storyMusic.stop();
                    if (MemoryManager.loadIsMusicOn()){
                        myGdxGame.audioManager.backgroundMusic.play();
                    }
                }));
            }
            if (loadGameButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.loadScreen);
            }
            if (exitButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                Gdx.app.exit();
            }
            if (settingsButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.settingsScreen);
            }
        }

        refreshAllTexts();
    }

    @Override
    public void dispose() {
        if (backgroundView != null) {
            backgroundView.dispose();
            backgroundView = null;
        }
        if (image != null) {
            image.dispose();
            image = null;
        }
        if (startButtonView != null) {
            startButtonView.dispose();
            startButtonView = null;
        }
        if (loadGameButtonView != null) {
            loadGameButtonView.dispose();
            loadGameButtonView = null;
        }
        if (settingsButtonView != null) {
            settingsButtonView.dispose();
            settingsButtonView = null;
        }
        if (exitButtonView != null) {
            exitButtonView.dispose();
            exitButtonView = null;
        }
    }

    @Override
    public void show() {
        myGdxGame.uiCamera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        myGdxGame.uiCamera.update();
    }

    private void refreshAllTexts() {
        startButtonView.setText(LocalizationManager.get("menu.start"));
        loadGameButtonView.setText(LocalizationManager.get("menu.load"));
        settingsButtonView.setText(LocalizationManager.get("menu.settings"));
        exitButtonView.setText(LocalizationManager.get("menu.exit"));
    }
}
