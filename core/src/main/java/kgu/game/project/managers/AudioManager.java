package kgu.game.project.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import kgu.game.project.GameResources;

public class AudioManager {

    public boolean isSoundOn;
    public boolean isMusicOn;

    public Music backgroundMusic;
    public Sound shootSound;
    public Sound explosionSound;
    public Sound saveSound;
    public Music storyMusic;
    public Music hacked;
    public Music menuMusic;

    public AudioManager() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(GameResources.BACKGROUND2_MUSIC_PATH));
        shootSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.SCROLL_SOUND_PATH));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.DESTROY_SOUND_PATH));
        saveSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.SAVE_SOUND_PATH));
        storyMusic = Gdx.audio.newMusic(Gdx.files.internal(GameResources.STORY_SOUND_PATH));
        hacked = Gdx.audio.newMusic(Gdx.files.internal(GameResources.HACKED_PATH));
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal(GameResources.MENU_SOUND));

        storyMusic.setLooping(true);
        backgroundMusic.setLooping(true);

        updateSoundFlag();
        updateMusicFlag();
        applyVolumes();
    }

    public void applyVolumes() {
        float musicVolume = MemoryManager.loadMusicVolume();
        backgroundMusic.setVolume(musicVolume);
        storyMusic.setVolume(musicVolume);
        hacked.setVolume(musicVolume);
        menuMusic.setVolume(musicVolume);
    }


    public void dispose() {
        backgroundMusic.dispose();
        storyMusic.dispose();
        hacked.dispose();
        menuMusic.dispose();
        shootSound.dispose();
        explosionSound.dispose();
        saveSound.dispose();
    }

    public void updateSoundFlag() {
        isSoundOn = MemoryManager.loadIsSoundOn();
    }

    public void updateMusicFlag() {
        isMusicOn = MemoryManager.loadIsMusicOn();

        applyVolumes();
        if (isMusicOn) backgroundMusic.play();
        else backgroundMusic.stop();
    }
}
