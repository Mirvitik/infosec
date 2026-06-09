package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

import kgu.game.project.GameResources;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ButtonView;
import kgu.game.project.components.ImageView;
import kgu.game.project.components.TextView;
import kgu.game.project.managers.LocalizationManager;
import kgu.game.project.managers.MemoryManager;

public class LoadGameScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    TextView titleTextView;
    TextView subtitleTextView;
    ButtonView returnButton;
    ArrayList<SaveSlotCard> slotCards;

    static class SaveSlotCard {
        long timestamp;
        ImageView bg;
        TextView slotNumText;
        TextView levelText;
        TextView dateText;
        float x, y, w, h;

        SaveSlotCard(MyGdxGame g, float x, float y, float w, float h,
                     String slotNum, String levelName, String date, long timestamp) {
            this.timestamp = timestamp;
            this.x = x; this.y = y; this.w = w; this.h = h;
            bg          = new ImageView(x, y, w, h, GameResources.BUTTON_SHORT_BG_IMG_PATH);
            slotNumText = new TextView(MyGdxGame.arialFont,   x + 14, y + h - 16, slotNum);
            levelText   = new TextView(g.commonWhiteFont, x + 14, y + h - 50, levelName);
            dateText    = new TextView(MyGdxGame.arialFont,   x + 14, y + 14,     date);
        }

        void draw(SpriteBatch batch) {
            bg.draw(batch);
            slotNumText.draw(batch);
            levelText.draw(batch);
            dateText.draw(batch);
        }

        boolean isHit(float tx, float ty) {
            return tx >= x && tx <= x + w && ty >= y && ty <= y + h;
        }
    }

    public LoadGameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        titleTextView    = new TextView(myGdxGame.xanmonoFontBig, 380, 590, LocalizationManager.get("loadgame.title"));
        subtitleTextView = new TextView(MyGdxGame.arialFont, 430, 528, LocalizationManager.get("loadgame.subtitle"));

        returnButton = new ButtonView(
            480, 80, 160, 50,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "← RETURN"
        );

        slotCards = new ArrayList<>();

        float cardW = 260f, cardH = 90f;
        float startX = 250f, startY = 430f;
        float gapX = 280f, gapY = 110f;

        String[] levelNames = {"Level 1", "Level 2", "Level 3",
            "Level 4", "Level 5", "Final boss"};

        java.util.Map<Integer, Long> latestByLevel = new java.util.HashMap<>();
        for (long date : MemoryManager.getAllSaveDates()) {
            ArrayList<Object> save = MemoryManager.getSaveByDate(String.valueOf(date));
            int level = Integer.parseInt(save.get(0).toString());
            if (!latestByLevel.containsKey(level) || date > latestByLevel.get(level)) {
                latestByLevel.put(level, date);
            }
        }

        java.util.List<Integer> sortedLevels = new java.util.ArrayList<>(latestByLevel.keySet());
        java.util.Collections.sort(sortedLevels);

        int cnt = 0;
        for (int level : sortedLevels) {
            long date = latestByLevel.get(level);
            float cx = startX + (cnt % 2) * gapX;
            float cy = startY - ((float) cnt / 2) * gapY;

            String levelName = level <= levelNames.length
                ? levelNames[level - 1] : "Level " + level;
            @SuppressWarnings("DefaultLocale") String slotLabel = "SLOT " + String.format("%02d", cnt + 1);
            String dateStr   = timestampToDateString(date);

            slotCards.add(new SaveSlotCard(myGdxGame,
                cx, cy, cardW, cardH, slotLabel, levelName, dateStr, date));
            cnt++;
        }
    }

    public static String timestampToDateString(long timestamp) {
        @SuppressWarnings("SimpleDateFormat") java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return sdf.format(new java.util.Date(timestamp));
    }

    @Override
    public void render(float delta) {
        handleInput();

        myGdxGame.uiCamera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.uiCamera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();
        titleTextView.draw(myGdxGame.batch);
        subtitleTextView.draw(myGdxGame.batch);
        returnButton.draw(myGdxGame.batch);
        for (SaveSlotCard card : slotCards) {
            card.draw(myGdxGame.batch);
        }
        myGdxGame.batch.end();
    }

    void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.uiCamera.unproject(
                new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)
            );

            if (returnButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.menuScreen);
                return;
            }

            for (SaveSlotCard card : slotCards) {
                if (card.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                    ArrayList<Object> save = MemoryManager.getSaveByDate(
                        String.valueOf(card.timestamp)
                    );
                    int level = Integer.parseInt(save.get(0).toString());

                    switch (level) {
                        case 1: myGdxGame.setScreen(new LevelOneScreen(myGdxGame)); break;
                        case 2: myGdxGame.setScreen(new LevelTwoScreen(myGdxGame)); break;
                        case 3: myGdxGame.setScreen(new LevelThreeScreen(myGdxGame)); break;
                        case 4: myGdxGame.setScreen(new LevelFourScreen(myGdxGame)); break;
                        case 5: myGdxGame.setScreen(new LevelFiveScreen(myGdxGame)); break;
                        default: myGdxGame.setScreen(new LevelFiveScreen(myGdxGame)); break;
                    }
                    return;
                }
            }
        }
    }
    @Override
    public void show() {
        refreshSavesList();
    }

    private void refreshSavesList() {
        slotCards.clear();

        float cardW = 260f, cardH = 90f;
        float startX = 250f, startY = 430f;
        float gapX = 280f, gapY = 110f;

        String[] levelNames = {"Level 1", "Level 2", "Level 3",
            "Level 4", "Level 5", "Level 6"};

        java.util.Map<Integer, Long> latestByLevel = new java.util.HashMap<>();
        for (long date : MemoryManager.getAllSaveDates()) {
            ArrayList<Object> save = MemoryManager.getSaveByDate(String.valueOf(date));
            int level = Integer.parseInt(save.get(0).toString());
            if (!latestByLevel.containsKey(level) || date > latestByLevel.get(level)) {
                latestByLevel.put(level, date);
            }
        }

        java.util.List<Integer> sortedLevels = new java.util.ArrayList<>(latestByLevel.keySet());
        java.util.Collections.sort(sortedLevels);

        int cnt = 0;
        for (int level : sortedLevels) {
            long date = latestByLevel.get(level);
            float cx = startX + (cnt % 2) * gapX;
            float cy = startY - ((float) cnt / 2) * gapY;

            String levelName = level <= levelNames.length
                ? levelNames[level - 1] : "Level " + level;
            @SuppressWarnings("DefaultLocale") String slotLabel = "SLOT " + String.format("%02d", cnt + 1);
            String dateStr   = timestampToDateString(date);

            slotCards.add(new SaveSlotCard(myGdxGame,
                cx, cy, cardW, cardH, slotLabel, levelName, dateStr, date));
            cnt++;
        }
    }
}
