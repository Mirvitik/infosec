package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;


import java.util.ArrayList;

import kgu.game.project.GameResources;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ButtonView;
import kgu.game.project.components.TextView;
import kgu.game.project.managers.LocalizationManager;
import kgu.game.project.managers.MemoryManager;

public class LoadGameScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    TextView titleTextView;
    ButtonView returnButton;
    ArrayList<ButtonView> buttonsArray;

    public LoadGameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        titleTextView = new TextView(myGdxGame.xanmonoFontBig, 380, 556, "load game");


        returnButton = new ButtonView(
            480, 100,
            160, 70,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            LocalizationManager.get("settings.return")
        );
        buttonsArray = new ArrayList<ButtonView>();
        int cnt = 0;
        ButtonView buttonAdd;

        System.out.println(MemoryManager.getAllSaveDates());
        for (long date : MemoryManager.getAllSaveDates()) {
            buttonAdd = new ButtonView(400 + (cnt % 2) * 300, ((float) cnt / 2 * 25) + 450 + (cnt % 2) * -13, 200, 20, new BitmapFont(), GameResources.PASSWORD_IMG_PATH, timestampToDateString(date));
            buttonsArray.add(buttonAdd);
            cnt += 1;
        }

    }

    public static String timestampToDateString(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        return sdf.format(new java.util.Date(timestamp));
    }

    public static long dateStringToTimestamp(String dateString) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
            java.util.Date date = sdf.parse(dateString);
            return date.getTime();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void render(float delta) {

        handleInput();

        myGdxGame.uiCamera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.uiCamera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();

        titleTextView.draw(myGdxGame.batch);
        returnButton.draw(myGdxGame.batch);
        for (ButtonView button : buttonsArray) {
            button.draw(myGdxGame.batch);
        }
        myGdxGame.batch.end();
    }

    //@SuppressWarnings("CheckResult")
    void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.uiCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (returnButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.menuScreen);
            }
            for (ButtonView button : buttonsArray) {
                if (button.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                    ArrayList<Object> array = MemoryManager.getSaveByDate(String.valueOf(dateStringToTimestamp(button.getText())));
                    int level = Integer.parseInt(array.get(0).toString());
                    float x = Float.parseFloat(array.get(1).toString());
                    float y = Float.parseFloat(array.get(2).toString());
                    if (level == 1) {
                        myGdxGame.setScreen(new LevelOneScreen(myGdxGame, x, y));
                    }
                }
            }
        }
    }
}
