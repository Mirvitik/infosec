package kgu.game.project.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

import kgu.game.project.FontBuilder;
import kgu.game.project.GameResources;
import kgu.game.project.MyGdxGame;
import kgu.game.project.managers.LocalizationManager;

public class DialogView extends View {
    String text;
    Texture fon;
    BitmapFont bitmapFont;
    public ButtonView nextButton;
    MyGdxGame myGdxGame;
    int cnt = 0;
    Array<String> talks;

    public DialogView(MyGdxGame myGdxGame, float x, float y, float width, float height, Array<String> talks) {
        super(x, y, width, height);
        this.fon = new Texture(GameResources.DIALOG_FON_IMG_PATH);
        this.width = width;
        this.height = height;
        this.text = talks.get(cnt).toString();
        this.nextButton = new ButtonView(width + 160, y + 20, 80, 30, myGdxGame.arialFont, GameResources.PASSWORD_IMG_PATH, LocalizationManager.get("dialog.next"));

        // Инициализируем шрифт
        this.bitmapFont = myGdxGame.arialFont;
        this.myGdxGame = myGdxGame;
        this.talks = talks;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(fon, x, y, width, height);

        // Рассчитываем позицию для центрирования текста
        float textWidth = bitmapFont.getRegion().getRegionWidth() * bitmapFont.getData().scaleX;
        float textX = x + (width - textWidth) / 2;
        float textY = y + height - 50; // Отступ сверху
        if (bitmapFont != null) {
            bitmapFont.draw(batch, text, textX, textY);
        }
        this.nextButton.draw(batch);
        System.out.println(cnt);
        if (nextButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
            cnt += 1;
            if (nextButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && (cnt < talks.size - 1) && Gdx.input.justTouched() && cnt < talks.size) {
                this.text = talks.get(cnt).toString();
            }
        }
    }

    public boolean isToDispose() {
        return cnt >= talks.size;
    }

    @Override
    public void dispose() {
        if (fon != null) {
            fon.dispose();
            fon = null;
        }
        if (nextButton != null) {
            nextButton.dispose();
            nextButton = null;
        }
        myGdxGame = null;
        talks = null;
        text = null;
    }

    public int getCnt() {
        return cnt;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void addCnt() {
        cnt += 1;
    }
}
