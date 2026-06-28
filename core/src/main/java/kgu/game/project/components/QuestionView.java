package kgu.game.project.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import kgu.game.project.GameResources;
import kgu.game.project.MyGdxGame;
import kgu.game.project.managers.LocalizationManager;

public class QuestionView extends View {

    private Texture backgroundTexture;
    private final BitmapFont font;

    public ButtonView restartButton;
    public ButtonView continueButton;
    public ButtonView hintButton;       // Подсказка
    public ButtonView exitButton;
    public TextView text;

    public QuestionView(MyGdxGame myGdxGame, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.font = MyGdxGame.arialFont;
        initBackground();
        float buttonWidth = 160;
        float buttonHeight = 45;
        float spacing = 15;
        float totalWidth = buttonWidth * 4 + spacing * 3;
        float startX = x + (width - totalWidth) / 2 + 300;
        float startY = y + height / 2 - buttonHeight / 2 + 80;
        initButtons(x, y, width, height);
        text = new TextView(MyGdxGame.arialWhiteFont, startX,
            startY);
        text.setText("Чего вы хотите?");
    }

    private void initBackground() {
        backgroundTexture = new Texture(GameResources.DIALOG_FON_IMG_PATH);
    }

    private void initButtons(float x, float y, float width, float height) {
        float buttonWidth = 160;
        float buttonHeight = 45;
        float spacing = 15;
        float totalWidth = buttonWidth * 4 + spacing * 3;
        float startX = x + (width - totalWidth) / 2;
        float startY = y + height / 2 - buttonHeight / 2;

        restartButton = new ButtonView(
            startX,
            startY,
            buttonWidth,
            buttonHeight,
            font,
            GameResources.PASSWORD_IMG_PATH,
            LocalizationManager.get("question.restart"),
            0.8f
        );

        continueButton = new ButtonView(
            startX + buttonWidth + spacing,
            startY,
            buttonWidth,
            buttonHeight,
            font,
            GameResources.PASSWORD_IMG_PATH,
            LocalizationManager.get("question.continue"),
            0.8f
        );

        hintButton = new ButtonView(
            startX + (buttonWidth + spacing) * 2,
            startY,
            buttonWidth,
            buttonHeight,
            font,
            GameResources.PASSWORD_IMG_PATH,
            LocalizationManager.get("question.hint"),
            0.8f
        );

        exitButton = new ButtonView(
            startX + (buttonWidth + spacing) * 3,
            startY,
            buttonWidth,
            buttonHeight,
            font,
            GameResources.PASSWORD_IMG_PATH,
            LocalizationManager.get("dialog.exit"),
            0.8f
        );
    }

    @Override
    public void draw(SpriteBatch batch) {

        batch.draw(backgroundTexture, x, y, width, height);

        restartButton.draw(batch);
        continueButton.draw(batch);
        hintButton.draw(batch);
        exitButton.draw(batch);
        text.draw(batch);
    }

    @Override
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
        }
        if (restartButton != null) {
            restartButton.dispose();
            restartButton = null;
        }
        if (continueButton != null) {
            continueButton.dispose();
            continueButton = null;
        }
        if (hintButton != null) {
            hintButton.dispose();
            hintButton = null;
        }
        if (exitButton != null) {
            exitButton.dispose();
            exitButton = null;
        }
    }
}
