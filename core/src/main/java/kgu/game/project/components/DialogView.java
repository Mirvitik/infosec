package kgu.game.project.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import kgu.game.project.GameResources;
import kgu.game.project.MyGdxGame;
import kgu.game.project.managers.LocalizationManager;

public class DialogView extends View {
    String text;
    Texture fon;
    BitmapFont bitmapFont;
    public ButtonView nextButton;
    public ButtonView exitButton;
    MyGdxGame myGdxGame;
    int cnt = 0;
    Array<String> talks;
    ImageView avatar;
    String image_path = GameResources.ANTIVIRUS_AVATAR_IMG_PATH;
    String name = "Antivirus";

    public DialogView(MyGdxGame myGdxGame, float x, float y, float width, float height, Array<String> talks) {
        super(x, y, width, height);
        this.fon = new Texture(GameResources.DIALOG_FON_IMG_PATH);
        this.width = width;
        this.height = height;
        this.text = talks.get(cnt);
        this.nextButton = new ButtonView(width + 160, y + 110, 90, 40, MyGdxGame.arialFont, GameResources.PASSWORD_IMG_PATH, LocalizationManager.get("dialog.next"));
        this.exitButton = new ButtonView(width + 65, y + 110, 90, 40, MyGdxGame.arialFont, GameResources.PASSWORD_IMG_PATH, LocalizationManager.get("dialog.exit"));

        this.bitmapFont = MyGdxGame.arialFont;
        this.myGdxGame = myGdxGame;
        this.talks = talks;
        avatar = new ImageView(x + 50, y + 180, 64, 64, image_path);
    }

    public DialogView(MyGdxGame myGdxGame, float x, float y, float width, float height, Array<String> talks, String image_path, String name) {
        super(x, y, width, height);
        this.fon = new Texture(GameResources.DIALOG_FON_IMG_PATH);
        this.width = width;
        this.height = height;
        this.text = talks.get(cnt);
        this.nextButton = new ButtonView(width + 160, y + 110, 90, 40, MyGdxGame.arialFont, GameResources.PASSWORD_IMG_PATH, LocalizationManager.get("dialog.next"));
        this.exitButton = new ButtonView(width + 65, y + 110, 90, 40, MyGdxGame.arialFont, GameResources.PASSWORD_IMG_PATH, LocalizationManager.get("dialog.exit"));

        this.bitmapFont = MyGdxGame.arialFont;
        this.myGdxGame = myGdxGame;
        this.talks = talks;
        avatar = new ImageView(x + 50, y + 180, 64, 64, image_path);
        this.name = name;
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
        this.nextButton.draw(batch);
        exitButton.draw(batch);
        avatar.draw(batch);
        MyGdxGame.arialFontGray.draw(myGdxGame.batch, name, x + 40, y + 170);
    }

    public void setText(String newText) {
        this.text = newText;
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
        addCntAndUpdate();
    }

    public void addCntAndUpdate() {
        cnt += 1;
        if (cnt < talks.size) {
            this.text = talks.get(cnt);
        }
    }

    public void exitCnt() {
        cnt = talks.size; // просто помечаем как завершённый, не talks.size + 1
    }
}
