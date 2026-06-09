package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import kgu.game.project.GameResources;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ButtonView;
import kgu.game.project.components.TextView;
import kgu.game.project.managers.LocalizationManager;

public class TheEndScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    TextView titleTextView;
    TextView subtitleTextView;
    ButtonView returnButton;

    public TheEndScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        titleTextView = new TextView(myGdxGame.xanmonoFontBig, 200, 500,
            LocalizationManager.get("theend.title"));
        subtitleTextView = new TextView(myGdxGame.xanmonoFont, 380, 380,
            LocalizationManager.get("theend.subtitle"));
        returnButton = new ButtonView(
            480, 100,
            160, 70,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            LocalizationManager.get("settings.return")
        );
    }

    @Override
    public void render(float delta) {
        handleInput();

        myGdxGame.uiCamera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.uiCamera.combined);
        ScreenUtils.clear(Color.BLACK);

        myGdxGame.batch.begin();
        titleTextView.draw(myGdxGame.batch);
        subtitleTextView.draw(myGdxGame.batch);
        returnButton.draw(myGdxGame.batch);
        myGdxGame.batch.end();
    }

    void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.uiCamera.unproject(
                new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (returnButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.menuScreen);
            }
        }
    }
}
