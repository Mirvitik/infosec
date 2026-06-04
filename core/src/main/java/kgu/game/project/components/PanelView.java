package kgu.game.project.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PanelView extends View {

    private Color backgroundColor;
    private TextView textView;
    private Texture backgroundTexture;
    private ShapeRenderer shapeRenderer;
    private boolean useTexture;

    // Конструктор с цветом фона
    public PanelView(float x, float y, float width, float height, Color backgroundColor) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.useTexture = false;
        shapeRenderer = new ShapeRenderer();
    }

    // Конструктор с текстурой фона
    public PanelView(float x, float y, float width, float height, String texturePath) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.backgroundTexture = new Texture(texturePath);
        this.useTexture = true;
    }

    public void setText(String text, BitmapFont font, Color textColor) {
        float textX = x + width / 2;
        float textY = y + height / 2;
        textView = new TextView(font, textX, textY, text);
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
        // Центрируем текст
        textView.x = x + width / 2 - textView.width / 2;
        textView.y = y + height / 2 + textView.height / 2;
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Рисуем фон
        batch.end();

        if (useTexture) {
            batch.begin();
            batch.draw(backgroundTexture, x, y, width, height);
            batch.end();
        } else {
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(backgroundColor);
            shapeRenderer.rect(x, y, width, height);
            shapeRenderer.end();
        }

        batch.begin();

        // Рисуем текст
        if (textView != null) {
            textView.draw(batch);
        }
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (textView != null) {
            textView.dispose();
        }
    }
}
