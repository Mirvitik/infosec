package kgu.game.project.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;

public class MemoryManager {
    private static final String KEY_LANGUAGE = "language";
    private static final Preferences preferences = Gdx.app.getPreferences("User saves");
    private static final Preferences gameProgressPrefs = Gdx.app.getPreferences("GameProgress");
    private static final Preferences langPrefs = Gdx.app.getPreferences("Language");

    public static void saveLanguage(LocalizationManager.Language lang) {
        langPrefs.putString(KEY_LANGUAGE, lang.name());
        langPrefs.flush();
    }


    public static void saveSoundSettings(boolean isOn) {
        preferences.putBoolean("isSoundOn", isOn);
        preferences.flush();
    }

    public static boolean loadIsSoundOn() {
        return preferences.getBoolean("isSoundOn", true);
    }

    public static void saveSubtitlesSettings(boolean isOn) {
        preferences.putBoolean("areSubtitlesOn", isOn);
        preferences.flush();
    }

    public static boolean loadAreSubtitlesOn() {
        return preferences.getBoolean("areSubtitlesOn", true);
    }

    public static void saveMusicSettings(boolean isOn) {
        preferences.putBoolean("isMusicOn", isOn);
        preferences.flush();
    }

    public static boolean loadIsMusicOn() {
        return preferences.getBoolean("isMusicOn", true);
    }

    public static void saveTableOfRecords(ArrayList<Integer> table) {

        Json json = new Json();
        String tableInString = json.toJson(table);
        preferences.putString("recordTable", tableInString);
        preferences.flush();
    }

    public static ArrayList<Integer> loadRecordsTable() {
        if (!preferences.contains("recordTable"))
            return null;

        String scores = preferences.getString("recordTable");
        Json json = new Json();
        return json.fromJson(ArrayList.class, scores);
    }

    public static void changeDifficulty() {
        String st = preferences.getString("difficultyLevel", "normal");
        if (st.equals("normal")) {
            st = "hard";
        } else if (st.equals("easy")) {
            st = "normal";
        } else {
            st = "easy";
        }
        preferences.putString("difficultyLevel", st);
        preferences.flush();
    }

    public static String loadDifficulty() {
        return preferences.getString("difficultyLevel", "easy");
    }

    public static float loadDifficultyNums() {
        String st = preferences.getString("difficultyLevel", "easy");
        if (st.equals("easy")) {
            return 1f;
        } else if (st.equals("normal")) {
            return 2.5f;
        }
        return 3f;
    }

    public static void saveGameState(int currentLevel, float playerX, float playerY) {
        Json json = new Json();

        String savesListJson = gameProgressPrefs.getString("savesList", "[]");
        ArrayList savesDates = json.fromJson(ArrayList.class, savesListJson);

        if (savesDates == null) {
            savesDates = new ArrayList<>();
        }

        long currentTime = System.currentTimeMillis();
        savesDates.add(currentTime);


        Preferences saves = Gdx.app.getPreferences(String.valueOf(currentTime));
        gameProgressPrefs.putString("savesList", json.toJson(savesDates));

        saves.putInteger("currentLevel", currentLevel);
        saves.putFloat("playerX", playerX);
        saves.putFloat("playerY", playerY);
        saves.putLong("lastSaveTime", currentTime);
        gameProgressPrefs.flush();
        saves.flush();
    }

    public static ArrayList<Long> getAllSaveDates() {
        Json json = new Json();
        String savesListJson = gameProgressPrefs.getString("savesList", "[]");
        ArrayList savesDates = json.fromJson(ArrayList.class, savesListJson);

        if (savesDates == null) {
            return new ArrayList<>();
        }

        return savesDates;
    }

    public static ArrayList<Object> getSaveByDate(String time) {
        Preferences saves = Gdx.app.getPreferences(time);
        ArrayList<Object> gameData = new ArrayList<>();
        gameData.add(saves.getInteger("currentLevel"));
        gameData.add(saves.getFloat("playerX"));
        gameData.add(saves.getFloat("playerY"));
        gameData.add(saves.getLong("lastSaveTime"));
        saves.flush();
        return gameData;
    }


}
