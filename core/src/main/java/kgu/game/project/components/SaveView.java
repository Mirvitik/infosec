package kgu.game.project.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import kgu.game.project.GameResources;
import kgu.game.project.MyGdxGame;
import kgu.game.project.managers.LocalizationManager;

public class SaveView extends View {
    ImageView background;
    public ButtonView saveButton;
    TextView text;
    public ButtonView cancelButton;

    public SaveView(float x, float y, float width, float height) {
        super(x, y, width, height);
        background = new ImageView(x, y, GameResources.SAVE_BACKGROUND_IMG_PATH);
        cancelButton = new ButtonView(100 + x, 50 + y, 100, 50, MyGdxGame.arialFont, GameResources.PASSWORD_IMG_PATH, LocalizationManager.get("save.cancel"));
        saveButton = new ButtonView(400 + x, 50 + y, 120, 50, MyGdxGame.arialFont, GameResources.PASSWORD_IMG_PATH, LocalizationManager.get("save.save"));
        text = new TextView(MyGdxGame.arialWhiteFont, 250 + x, 200 + y, LocalizationManager.get("save.question"));
    }

    @Override
    public void draw(SpriteBatch batch) {
        background.draw(batch);
        cancelButton.draw(batch);
        saveButton.draw(batch);
        text.draw(batch);
    }
}
