package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import kgu.game.project.GameResources;
import kgu.game.project.GameSettings;
import kgu.game.project.managers.LocalizationManager;
import kgu.game.project.managers.MemoryManager;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ButtonView;
import kgu.game.project.components.ImageView;
import kgu.game.project.components.MovingBackgroundView;
import kgu.game.project.components.TextView;

import java.util.ArrayList;

public class SettingsScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;

    MovingBackgroundView backgroundView;
    TextView titleTextView;
    ButtonView returnButton;
    TextView difficultSettingView;
    TextView musicSettingView;
    TextView soundSettingView;
    TextView subtitlesSettingView;

    TextView languageSettingView;

    public SettingsScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);
        titleTextView = new TextView(myGdxGame.xanmonoFontBig, GameSettings.SCREEN_WIDTH / 2f - 150f, 555, "Settings");
        difficultSettingView = new TextView(
            myGdxGame.xanmonoFont,
            GameSettings.SCREEN_WIDTH / 2f - 120f, 437,
            "difficulty: " + MemoryManager.loadDifficulty()
        );

        musicSettingView = new TextView(
            myGdxGame.xanmonoFont,
            GameSettings.SCREEN_WIDTH / 2f - 120f, 378,
            "music: " + translateStateToText(MemoryManager.loadIsMusicOn())
        );
        subtitlesSettingView = new TextView(
            myGdxGame.xanmonoFont,
            GameSettings.SCREEN_WIDTH / 2f - 120f, 319,
            "Subtitles: " + translateStateToText(MemoryManager.loadIsMusicOn())
        );

        soundSettingView = new TextView(
            myGdxGame.xanmonoFont,
            GameSettings.SCREEN_WIDTH / 2f - 120f, 260,
            "sound: " + translateStateToText(MemoryManager.loadIsSoundOn())
        );

        returnButton = new ButtonView(
            GameSettings.SCREEN_WIDTH / 2f - 80f, 100,
            160, 70,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "return"
        );
        languageSettingView = new TextView(
            myGdxGame.xanmonoFont,
            GameSettings.SCREEN_WIDTH / 2f - 120f, 201,  // вставь между sound и return
            LocalizationManager.get("settings.language") + LocalizationManager.getLanguage().name()
        );

    }

    @Override
    public void render(float delta) {

        handleInput();
        refreshAllTexts();
        myGdxGame.camera.update();
        myGdxGame.uiCamera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.uiCamera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();

        backgroundView.draw(myGdxGame.batch);
        titleTextView.draw(myGdxGame.batch);
        returnButton.draw(myGdxGame.batch);
        musicSettingView.draw(myGdxGame.batch);
        soundSettingView.draw(myGdxGame.batch);
        subtitlesSettingView.draw(myGdxGame.batch);
        languageSettingView.draw(myGdxGame.batch);

        myGdxGame.batch.end();
    }

    void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.uiCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (returnButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.menuScreen);
            }
            if (musicSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                MemoryManager.saveMusicSettings(!MemoryManager.loadIsMusicOn());
                musicSettingView.setText("music: " + translateStateToText(MemoryManager.loadIsMusicOn()));
                myGdxGame.audioManager.updateMusicFlag();
            }
            if (soundSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                MemoryManager.saveSoundSettings(!MemoryManager.loadIsSoundOn());
                soundSettingView.setText("sound: " + translateStateToText(MemoryManager.loadIsSoundOn()));
                myGdxGame.audioManager.updateSoundFlag();
            }
            if (subtitlesSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                MemoryManager.saveSubtitlesSettings(!MemoryManager.loadAreSubtitlesOn());
                subtitlesSettingView.setText("Subtitles: " + translateStateToText(MemoryManager.loadAreSubtitlesOn()));
            }

            if (difficultSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                MemoryManager.changeDifficulty();
                difficultSettingView.setText("difficulty: " + MemoryManager.loadDifficulty());
                myGdxGame.audioManager.updateSoundFlag();
            }
            if (languageSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                LocalizationManager.toggleLanguage();
                MemoryManager.saveLanguage(LocalizationManager.getLanguage());
            }
            refreshAllTexts();
        }
    }
    private void refreshAllTexts() {
        titleTextView.setText(LocalizationManager.get("settings.title"));
        difficultSettingView.setText(LocalizationManager.get("settings.difficulty") + MemoryManager.loadDifficulty());
        musicSettingView.setText(LocalizationManager.get("settings.music") + LocalizationManager.get(MemoryManager.loadIsMusicOn() ? "state.on" : "state.off"));
        soundSettingView.setText(LocalizationManager.get("settings.sound") + LocalizationManager.get(MemoryManager.loadIsSoundOn() ? "state.on" : "state.off"));
        subtitlesSettingView.setText(LocalizationManager.get("settings.subtitles") + LocalizationManager.get(MemoryManager.loadAreSubtitlesOn() ? "state.on" : "state.off"));
        languageSettingView.setText(LocalizationManager.get("settings.language") + LocalizationManager.getLanguage().name());
        returnButton.setText(LocalizationManager.get("settings.return")); // если ButtonView поддерживает setText
    }

    private String translateStateToText(boolean state) {
        return state ? "ON" : "OFF";
    }
}
