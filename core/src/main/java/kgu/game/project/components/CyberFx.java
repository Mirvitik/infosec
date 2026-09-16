package kgu.game.project.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;

import kgu.game.project.FontBuilder;
import kgu.game.project.GameResources;
import kgu.game.project.GameSettings;

public class CyberFx implements Disposable {

    public static final Color ACCENT = Color.valueOf("#0bfb9c");
    public static final Color ACCENT_DIM = Color.valueOf("#89c7b2");

    private static final float W = GameSettings.SCREEN_WIDTH;
    private static final float H = GameSettings.SCREEN_HEIGHT;

    private static final float BACKDROP_R = 0.008f;
    private static final float BACKDROP_G = 0.020f;
    private static final float BACKDROP_B = 0.016f;

    private static final String GLYPHS = "0123456789ABCDEF#$%&*<>/|=+-";
    private static final int COLUMNS = 46;
    private static final int TRAIL = 16;
    private static final float STEP = 20f;

    private final ShapeRenderer shapes = new ShapeRenderer();
    private final BitmapFont rainFont =
        FontBuilder.generate(18, ACCENT, GameResources.XANMONO_FONT_PATH);

    private final float[] headY = new float[COLUMNS];
    private final float[] speed = new float[COLUMNS];
    private final float[] alpha = new float[COLUMNS];
    private final int[] length = new int[COLUMNS];
    private final char[][] glyph = new char[COLUMNS][TRAIL];

    private final StringBuilder glyphBuffer = new StringBuilder(1);
    private final GlyphLayout titleLayout = new GlyphLayout();
    private final Color gradA = new Color();
    private final Color gradB = new Color();
    private final Color backdrop = new Color();
    private final Color savedFontColor = new Color();

    private float time;
    private float fade;
    private float scanY = -120f;

    public CyberFx() {
        for (int i = 0; i < COLUMNS; i++) {
            respawn(i);
            headY[i] = MathUtils.random(-H, H);
        }
    }

    public float time() {
        return time;
    }

    public float fade() {
        return fade;
    }

    public ShapeRenderer shapes() {
        return shapes;
    }

    public Color backdrop(float a) {
        return backdrop.set(BACKDROP_R, BACKDROP_G, BACKDROP_B, a);
    }

    public void reset() {
        time = 0f;
        fade = 0f;
        scanY = -120f;
    }

    public void update(float delta) {
        time += delta;
        fade = MathUtils.clamp(time * 2.2f, 0f, 1f);

        scanY += delta * 90f;
        if (scanY > H + 120f) scanY = -120f;

        for (int i = 0; i < COLUMNS; i++) {
            headY[i] -= speed[i] * delta;
            if (headY[i] + length[i] * STEP < 0f) respawn(i);
            if (MathUtils.random() < 0.3f) glyph[i][MathUtils.random(TRAIL - 1)] = randomGlyph();
        }
    }

    public void clear() {
        ScreenUtils.clear(BACKDROP_R, BACKDROP_G, BACKDROP_B, 1f);
    }

    public void drawRain(SpriteBatch batch) {
        float columnWidth = W / COLUMNS;
        for (int i = 0; i < COLUMNS; i++) {
            float x = i * columnWidth + 5f;
            for (int j = 0; j < length[i]; j++) {
                float y = headY[i] + j * STEP;
                if (y < -STEP || y > H + STEP) continue;

                float trail = 1f - (float) j / length[i];
                float a = alpha[i] * trail * trail * fade;
                if (j == 0) rainFont.setColor(0.78f, 1f, 0.92f, Math.min(1f, a * 3.2f));
                else rainFont.setColor(ACCENT.r, ACCENT.g, ACCENT.b, a);

                glyphBuffer.setLength(0);
                glyphBuffer.append(glyph[i][j]);
                rainFont.draw(batch, glyphBuffer, x, y);
            }
        }
    }

    public void drawTitle(SpriteBatch batch, BitmapFont font, String text,
                          float centerX, float top, float maxWidth) {
        BitmapFont.BitmapFontData data = font.getData();
        float scaleX = data.scaleX;
        float scaleY = data.scaleY;

        titleLayout.setText(font, text);
        if (titleLayout.width > maxWidth) {
            float k = maxWidth / titleLayout.width;
            data.setScale(scaleX * k, scaleY * k);
            titleLayout.setText(font, text);
        }

        float x = centerX - titleLayout.width / 2f;
        float split = 2.5f + MathUtils.sin(time * 1.7f) * 1.3f;

        savedFontColor.set(font.getColor());
        font.setColor(0.51f, 0.10f, 0.18f, fade);
        font.draw(batch, text, x - split, top);
        font.setColor(0.10f, 0.43f, 0.51f, fade);
        font.draw(batch, text, x + split, top);
        font.setColor(ACCENT.r, ACCENT.g, ACCENT.b, fade);
        font.draw(batch, text, x, top);
        font.setColor(savedFontColor);

        data.setScale(scaleX, scaleY);
    }

    public ShapeRenderer begin(Matrix4 projection) {
        shapes.setProjectionMatrix(projection);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        return shapes;
    }

    public void end() {
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void drawChrome() {
        shapes.setColor(0f, 0f, 0f, 0.16f);
        for (float y = 0; y < H; y += 4f) shapes.rect(0, y, W, 2f);

        gradA.set(ACCENT.r, ACCENT.g, ACCENT.b, 0f);
        gradB.set(ACCENT.r, ACCENT.g, ACCENT.b, 0.05f);
        shapes.rect(0, scanY, W, 40f, gradA, gradA, gradB, gradB);
        shapes.rect(0, scanY + 40f, W, 40f, gradB, gradB, gradA, gradA);

        gradA.set(0f, 0f, 0f, 0.5f);
        gradB.set(0f, 0f, 0f, 0f);
        shapes.rect(0, 0, 200f, H, gradA, gradB, gradB, gradA);
        shapes.rect(W - 200f, 0, 200f, H, gradB, gradA, gradA, gradB);
        gradA.a = 0.45f;
        shapes.rect(0, 0, W, 90f, gradA, gradA, gradB, gradB);
        gradA.a = 0.5f;
        shapes.rect(0, H - 120f, W, 120f, gradB, gradB, gradA, gradA);

        drawCornerBrackets();

        if (fade < 1f) {
            shapes.setColor(backdrop(1f - fade));
            shapes.rect(0, 0, W, H);
        }
    }

    public void frame(float x, float y, float w, float h, float th) {
        shapes.rect(x, y, w, th);
        shapes.rect(x, y + h - th, w, th);
        shapes.rect(x, y, th, h);
        shapes.rect(x + w - th, y, th, h);
    }

    public void corners(float x, float y, float w, float h, float len, float th) {
        shapes.rect(x, y, len, th);
        shapes.rect(x, y, th, len);
        shapes.rect(x + w - len, y, len, th);
        shapes.rect(x + w - th, y, th, len);
        shapes.rect(x, y + h - th, len, th);
        shapes.rect(x, y + h - len, th, len);
        shapes.rect(x + w - len, y + h - th, len, th);
        shapes.rect(x + w - th, y + h - len, th, len);
    }

    private void drawCornerBrackets() {
        float m = 26f;
        float len = 46f;
        float th = 2f;
        shapes.setColor(ACCENT.r, ACCENT.g, ACCENT.b, 0.30f * fade);

        shapes.rect(m, m, len, th);
        shapes.rect(m, m, th, len);
        shapes.rect(W - m - len, m, len, th);
        shapes.rect(W - m - th, m, th, len);
        shapes.rect(m, H - m - th, len, th);
        shapes.rect(m, H - m - len, th, len);
        shapes.rect(W - m - len, H - m - th, len, th);
        shapes.rect(W - m - th, H - m - len, th, len);
    }

    private void respawn(int i) {
        headY[i] = H + MathUtils.random(0f, H * 0.9f);
        speed[i] = 55f + MathUtils.random(190f);
        alpha[i] = 0.10f + MathUtils.random(0.30f);
        length[i] = 6 + MathUtils.random(TRAIL - 7);
        for (int j = 0; j < TRAIL; j++) glyph[i][j] = randomGlyph();
    }

    private char randomGlyph() {
        return GLYPHS.charAt(MathUtils.random(GLYPHS.length() - 1));
    }

    @Override
    public void dispose() {
        shapes.dispose();
        rainFont.dispose();
    }
}
