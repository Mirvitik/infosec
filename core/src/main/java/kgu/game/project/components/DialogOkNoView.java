package kgu.game.project.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import kgu.game.project.FontBuilder;
import kgu.game.project.GameResources;
import kgu.game.project.MyGdxGame;
import kgu.game.project.managers.LocalizationManager;

public class DialogOkNoView extends View {
    Texture fon;
    BitmapFont bitmapFont;
    public ButtonView okButton;
    public ButtonView noButton;
    MyGdxGame myGdxGame;
    String text;
    ImageView avatar;
    String image_path = GameResources.ANTIVIRUS_AVATAR_IMG_PATH;
    String name = "Antivirus";

    public DialogOkNoView(MyGdxGame myGdxGame, float x, float y, float width, float height, String text) {
        super(x, y, width, height);
        this.fon = new Texture(GameResources.DIALOG_FON_IMG_PATH);
        this.width = width;
        this.height = height;
        this.text = text;
        this.okButton = new ButtonView(width + 160, y + 110, 90, 40, myGdxGame.arialFont, GameResources.PASSWORD_IMG_PATH, LocalizationManager.get("dialog.yes"));
        this.noButton = new ButtonView(x + 20, y + 110, 90, 40, myGdxGame.arialFont, GameResources.PASSWORD_IMG_PATH, LocalizationManager.get("dialog.no"));
        this.bitmapFont = FontBuilder.generate(20, Color.BLACK, "fonts/arialmt.ttf");
        this.myGdxGame = myGdxGame;
        avatar = new ImageView(x + 50, y + 180, 64, 64, image_path);
    }

    public DialogOkNoView(MyGdxGame myGdxGame, float x, float y, float width, float height, String text, String image_path) {
        super(x, y, width, height);
        this.fon = new Texture(GameResources.DIALOG_FON_IMG_PATH);
        this.width = width;
        this.height = height;
        this.text = text;
        this.okButton = new ButtonView(width + 160, y + 110, 90, 40, myGdxGame.arialFont, GameResources.PASSWORD_IMG_PATH, LocalizationManager.get("dialog.yes"));
        this.noButton = new ButtonView(x + 20, y + 110, 90, 40, myGdxGame.arialFont, GameResources.PASSWORD_IMG_PATH, LocalizationManager.get("dialog.no"));
        this.bitmapFont = FontBuilder.generate(20, Color.BLACK, "fonts/arialmt.ttf");
        this.myGdxGame = myGdxGame;
        avatar = new ImageView(x + 50, y + 180, 64, 64, image_path);
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(fon, x, y + 90, width, height);
        float textWidth = bitmapFont.getRegion().getRegionWidth() * bitmapFont.getData().scaleX;
        float textX = x + (width - textWidth) / 2 + 20;
        float textY = y + height + 50;
        if (bitmapFont != null) {
            bitmapFont.draw(batch, text, textX, textY);
        }
        this.okButton.draw(myGdxGame.batch);
        this.noButton.draw(myGdxGame.batch);
        avatar.draw(batch);
        myGdxGame.arialFontGray.draw(myGdxGame.batch, name, x + 40, y + 170);
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
