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

public class AchieveScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;

    MovingBackgroundView backgroundView;
    TextView titleTextView;
    ButtonView returnButton;
    TextView difficultSettingView;
    TextView musicSettingView;
    TextView musicVolumeView;
    TextView soundSettingView;
    TextView soundVolumeView;
    TextView subtitlesSettingView;
    TextView languageSettingView;

    ButtonView musicVolumeUpButton;
    ButtonView musicVolumeDownButton;
    ButtonView soundVolumeUpButton;
    ButtonView soundVolumeDownButton;

    public AchieveScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);
        titleTextView = new TextView(myGdxGame.xanmonoFontBig, GameSettings.SCREEN_WIDTH / 2f - 150f, 555, "Settings");

        difficultSettingView = new TextView(
            myGdxGame.xanmonoFont,
            GameSettings.SCREEN_WIDTH / 2f - 120f, 490,
            "difficulty: " + MemoryManager.loadDifficulty()
        );

        musicSettingView = new TextView(
            myGdxGame.xanmonoFont,
            GameSettings.SCREEN_WIDTH / 2f - 120f, 440,
            "music: " + translateStateToText(MemoryManager.loadIsMusicOn())
        );

        musicVolumeView = new TextView(
            myGdxGame.xanmonoFont,
            GameSettings.SCREEN_WIDTH / 2f + 140f, 440,
            (int) (MemoryManager.loadMusicVolume() * 100) + "%"
        );

        musicVolumeDownButton = new ButtonView(
            GameSettings.SCREEN_WIDTH / 2f + 220f, 435,
            30, 30,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "-"
        );
        musicVolumeUpButton = new ButtonView(
            GameSettings.SCREEN_WIDTH / 2f + 260f, 435,
            30, 30,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "+"
        );

        soundSettingView = new TextView(
            myGdxGame.xanmonoFont,
            GameSettings.SCREEN_WIDTH / 2f - 120f, 380,
            "sound: " + translateStateToText(MemoryManager.loadIsSoundOn())
        );

        soundVolumeView = new TextView(
            myGdxGame.xanmonoFont,
            GameSettings.SCREEN_WIDTH / 2f + 140f, 380,
            (int) (MemoryManager.loadSoundVolume() * 100) + "%"
        );

        soundVolumeDownButton = new ButtonView(
            GameSettings.SCREEN_WIDTH / 2f + 220f, 375,
            30, 30,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "-"
        );
        soundVolumeUpButton = new ButtonView(
            GameSettings.SCREEN_WIDTH / 2f + 260f, 375,
            30, 30,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "+"
        );

        subtitlesSettingView = new TextView(
            myGdxGame.xanmonoFont,
            GameSettings.SCREEN_WIDTH / 2f - 120f, 320,
            "Subtitles: " + translateStateToText(MemoryManager.loadIsMusicOn())
        );

        languageSettingView = new TextView(
            myGdxGame.xanmonoFont,
            GameSettings.SCREEN_WIDTH / 2f - 120f, 260,
            LocalizationManager.get("settings.language") + LocalizationManager.getLanguage().name()
        );

        returnButton = new ButtonView(
            GameSettings.SCREEN_WIDTH / 2f - 80f, 100,
            160, 70,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "return"
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
        musicVolumeView.draw(myGdxGame.batch);
        musicVolumeDownButton.draw(myGdxGame.batch);
        musicVolumeUpButton.draw(myGdxGame.batch);
        soundSettingView.draw(myGdxGame.batch);
        soundVolumeView.draw(myGdxGame.batch);
        soundVolumeDownButton.draw(myGdxGame.batch);
        soundVolumeUpButton.draw(myGdxGame.batch);
        subtitlesSettingView.draw(myGdxGame.batch);
        languageSettingView.draw(myGdxGame.batch);

        myGdxGame.batch.end();

        if (!MemoryManager.loadIsMusicOn()) {
            myGdxGame.audioManager.menuMusic.stop();
        } else {
            myGdxGame.audioManager.backgroundMusic.stop();
            myGdxGame.audioManager.menuMusic.setVolume(MemoryManager.loadMusicVolume());
            myGdxGame.audioManager.menuMusic.play();
        }
    }

    void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.uiCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (returnButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.menuScreen);
            }

            if (musicSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                MemoryManager.saveMusicSettings(!MemoryManager.loadIsMusicOn());
                if (!MemoryManager.loadIsMusicOn()) {
                    MemoryManager.saveMusicVolume(0f);
                } else {
                    MemoryManager.saveMusicVolume(0.5f);
                }
                musicSettingView.setText("music: " + translateStateToText(MemoryManager.loadIsMusicOn()));
                myGdxGame.audioManager.updateMusicFlag();
                refreshAllTexts();
            }

            if (musicVolumeUpButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                float newVolume = Math.min(1.0f, MemoryManager.loadMusicVolume() + 0.05f);
                MemoryManager.saveMusicVolume(newVolume);
                if (newVolume > 0 && !MemoryManager.loadIsMusicOn()) {
                    MemoryManager.saveMusicSettings(true);
                    myGdxGame.audioManager.updateMusicFlag();
                }
                refreshAllTexts();
            }

            if (musicVolumeDownButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                float newVolume = Math.max(0f, MemoryManager.loadMusicVolume() - 0.05f);
                MemoryManager.saveMusicVolume(newVolume);
                if (newVolume == 0) {
                    MemoryManager.saveMusicSettings(false);
                    myGdxGame.audioManager.updateMusicFlag();
                }
                refreshAllTexts();
            }

            if (soundSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                MemoryManager.saveSoundSettings(!MemoryManager.loadIsSoundOn());
                if (!MemoryManager.loadIsSoundOn()) {
                    MemoryManager.saveSoundVolume(0f);
                } else {
                    MemoryManager.saveSoundVolume(0.5f);
                }
                soundSettingView.setText("sound: " + translateStateToText(MemoryManager.loadIsSoundOn()));
                myGdxGame.audioManager.updateSoundFlag();
                refreshAllTexts();
            }

            if (soundVolumeUpButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                float newVolume = Math.min(1.0f, MemoryManager.loadSoundVolume() + 0.05f);
                MemoryManager.saveSoundVolume(newVolume);
                if (newVolume > 0 && !MemoryManager.loadIsSoundOn()) {
                    MemoryManager.saveSoundSettings(true);
                    myGdxGame.audioManager.updateSoundFlag();
                }
                refreshAllTexts();
            }

            if (soundVolumeDownButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                float newVolume = Math.max(0f, MemoryManager.loadSoundVolume() - 0.05f);
                MemoryManager.saveSoundVolume(newVolume);
                if (newVolume == 0) {
                    MemoryManager.saveSoundSettings(false);
                    myGdxGame.audioManager.updateSoundFlag();
                }
                refreshAllTexts();
            }

            if (subtitlesSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                MemoryManager.saveSubtitlesSettings(!MemoryManager.loadAreSubtitlesOn());
                subtitlesSettingView.setText("Subtitles: " + translateStateToText(MemoryManager.loadAreSubtitlesOn()));
                refreshAllTexts();
            }

            if (difficultSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                MemoryManager.changeDifficulty();
                difficultSettingView.setText("difficulty: " + MemoryManager.loadDifficulty());
                myGdxGame.audioManager.updateSoundFlag();
                refreshAllTexts();
            }

            if (languageSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                LocalizationManager.toggleLanguage();
                MemoryManager.saveLanguage(LocalizationManager.getLanguage());
                refreshAllTexts();
            }
        }
    }

    private void refreshAllTexts() {
        titleTextView.setText(LocalizationManager.get("settings.title"));
        difficultSettingView.setText(LocalizationManager.get("settings.difficulty") + MemoryManager.loadDifficulty());

        musicSettingView.setText(LocalizationManager.get("settings.music") + LocalizationManager.get(MemoryManager.loadIsMusicOn() ? "state.on" : "state.off"));
        musicVolumeView.setText((int) (MemoryManager.loadMusicVolume() * 100) + "%");

        soundSettingView.setText(LocalizationManager.get("settings.sound") + LocalizationManager.get(MemoryManager.loadIsSoundOn() ? "state.on" : "state.off"));
        soundVolumeView.setText((int) (MemoryManager.loadSoundVolume() * 100) + "%");

        subtitlesSettingView.setText(LocalizationManager.get("settings.subtitles") + LocalizationManager.get(MemoryManager.loadAreSubtitlesOn() ? "state.on" : "state.off"));
        languageSettingView.setText(LocalizationManager.get("settings.language") + LocalizationManager.getLanguage().name());
        returnButton.setText(LocalizationManager.get("settings.return"));

        String langText = LocalizationManager.get("settings.language") + LocalizationManager.getLanguage().name();
        int targetLength = 25;
        while (langText.length() < targetLength) {
            langText += " ";
        }
        languageSettingView.setText(langText);
    }

    private String translateStateToText(boolean state) {
        return state ? "ON" : "OFF";
    }
}
