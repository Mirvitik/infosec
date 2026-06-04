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

public class DialogOkNoView extends View {
    Texture fon;
    BitmapFont bitmapFont;
    public ButtonView okButton;
    public ButtonView noButton;
    MyGdxGame myGdxGame;
    String text;

    public DialogOkNoView(MyGdxGame myGdxGame, float x, float y, float width, float height, String text) {
        super(x, y, width, height);
        this.fon = new Texture(GameResources.DIALOG_FON_IMG_PATH);
        this.width = width;
        this.height = height;
        this.text = text;
        this.okButton = new ButtonView(width + 160, y + 20, 80, 30, new BitmapFont(), GameResources.PASSWORD_IMG_PATH, "Ok");
        this.noButton = new ButtonView(x + 20, y + 20, 80, 30, new BitmapFont(), GameResources.PASSWORD_IMG_PATH, "No");
        // Инициализируем шрифт
        this.bitmapFont = FontBuilder.generate(20, Color.GRAY, "fonts/arialmt.ttf");
        this.myGdxGame = myGdxGame;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(fon, x, y, width, height);
        float textWidth = bitmapFont.getRegion().getRegionWidth() * bitmapFont.getData().scaleX;
        float textX = x + (width - textWidth) / 2;
        float textY = y + height - 50;
        if (bitmapFont != null) {
            bitmapFont.draw(batch, text, textX, textY);
        }
        this.okButton.draw(myGdxGame.batch);
        this.noButton.draw(myGdxGame.batch);
    }

    @Override
    public void dispose() {
        fon.dispose();
        if (bitmapFont != null) bitmapFont.dispose();
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
}
