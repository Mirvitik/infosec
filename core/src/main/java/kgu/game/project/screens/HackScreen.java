package kgu.game.project.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import kgu.game.project.FontBuilder;
import kgu.game.project.GameResources;
import kgu.game.project.MyGdxGame;

public class HackScreen extends ScreenAdapter {

    private static final float VIEW_W = 1280f;
    private static final float VIEW_H = 720f;

    private final MyGdxGame myGdxGame;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final ShapeRenderer shapes;
    private final BitmapFont font;
    private final BitmapFont fontSmall;

    private Texture screenshotTexture;
    private TextureRegion screenshotRegion;

    private float timer = 0f;
    private float hackProgress = 0f;
    private float hackFlicker = 0f;
    private float scanLine = 0f;

    private static class GlitchBar {
        float y, height, offsetX, alpha, life, maxLife;
        boolean invert;

        GlitchBar() {
            y = MathUtils.random(VIEW_H);
            height = 2f + MathUtils.random(30f);
            offsetX = (MathUtils.randomBoolean() ? 1 : -1) * (5f + MathUtils.random(140f));
            maxLife = 0.04f + MathUtils.random(0.18f);
            life = maxLife;
            alpha = 0.5f + MathUtils.random(0.5f);
            invert = MathUtils.random() > 0.7f;
        }
    }

    private final Array<GlitchBar> glitches = new Array<>();
    private float glitchTimer = 0f;

    private static class CodeLine {
        String text;
        float x, y, alpha, speed;
        Color color;

        CodeLine() {
            String[] pool = {
                "0xDEADBEEF", "OVERFLOW->0xFFFF", "root# ./exploit",
                "KERNEL PANIC", "decrypt(RSA)", "PAYLOAD INJECTED",
                "SIGSEGV", "fork() = -1", "RET 0x41414141",
                "iptables -F", "chmod 777 /etc", "SSH tunnel OK",
                "NOP SLED x90", "TRACE 0x1337", "core dumped"
            };
            text = pool[MathUtils.random(pool.length - 1)];
            x = MathUtils.random(VIEW_W * 0.65f);
            y = MathUtils.random(VIEW_H);
            alpha = 0.55f + MathUtils.random(0.45f);
            speed = 25f + MathUtils.random(90f);
            color = MathUtils.random() > 0.75f ? Color.CYAN
                : MathUtils.random() > 0.5f ? Color.LIME
                : Color.GREEN;
        }
    }

    private final Array<CodeLine> codeLines = new Array<>();
    private float codeTimer = 0f;

    private final float[] noiseX = new float[14];
    private final float[] noiseA = new float[14];
    private final float[] noiseW = new float[14];


    public HackScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEW_W, VIEW_H, camera);
        viewport.apply(true);
        shapes = new ShapeRenderer();
        font = FontBuilder.generate(40, Color.GREEN, GameResources.FONT_PATH_PIXEL);
        fontSmall = FontBuilder.generate(22, Color.GREEN, GameResources.FONT_PATH_PIXEL);

        for (int i = 0; i < noiseX.length; i++) randomizeNoise(i);
        myGdxGame.audioManager.backgroundMusic.stop();
        myGdxGame.audioManager.hacked.play();
    }


    @Override
    public void render(float delta) {
        timer += delta;
        hackProgress = Math.min(1f, hackProgress + delta * 0.035f);
        hackFlicker = MathUtils.sin(timer * 20f) * 0.5f + 0.5f;
        scanLine += delta * 220f;
        if (scanLine > VIEW_H) {
            myGdxGame.audioManager.hacked.stop();
            myGdxGame.audioManager.backgroundMusic.play();
            myGdxGame.setScreen(new LevelOneScreen(myGdxGame));
        }

        updateGlitches(delta);
        updateCodeLines(delta);
        updateNoise();


        camera.update();
        shapes.setProjectionMatrix(camera.combined);
        myGdxGame.batch.setProjectionMatrix(camera.combined);

        myGdxGame.batch.begin();
        if (screenshotRegion != null) {
            myGdxGame.batch.setColor(0.6f, 1f, 0.6f, 1f);
            myGdxGame.batch.draw(screenshotRegion, 0, 0, VIEW_W, VIEW_H);
            myGdxGame.batch.setColor(Color.WHITE);
        }
        myGdxGame.batch.end();

        drawGlitchBars();

        drawOverlays();

        myGdxGame.batch.begin();
        drawCodeLines();
        myGdxGame.batch.end();
    }

    private void updateGlitches(float delta) {
        glitchTimer += delta;
        float rate = 0.03f + (1f - hackProgress) * 0.05f;
        if (glitchTimer > rate) {
            glitchTimer = 0f;
            int count = 1 + (int) (hackProgress * 3);
            for (int i = 0; i < count; i++) glitches.add(new GlitchBar());
        }
        for (int i = glitches.size - 1; i >= 0; i--) {
            glitches.get(i).life -= delta;
            if (glitches.get(i).life <= 0) glitches.removeIndex(i);
        }
    }

    private void updateCodeLines(float delta) {
        codeTimer += delta;
        if (codeTimer > 0.10f) {
            codeTimer = 0f;
            codeLines.add(new CodeLine());
        }
        for (int i = codeLines.size - 1; i >= 0; i--) {
            CodeLine c = codeLines.get(i);
            c.y -= c.speed * delta;
            c.alpha -= delta * 0.8f;
            if (c.alpha <= 0 || c.y < -30f) codeLines.removeIndex(i);
        }
    }

    private void updateNoise() {
        for (int i = 0; i < noiseX.length; i++)
            if (MathUtils.random() < 0.015f) randomizeNoise(i);
    }

    private void randomizeNoise(int i) {
        noiseX[i] = MathUtils.random(VIEW_W);
        noiseA[i] = MathUtils.random(0.04f, 0.22f);
        noiseW[i] = 1f + MathUtils.random(5f);
    }

    private void drawGlitchBars() {
        if (screenshotRegion == null) return;

        for (GlitchBar g : glitches) {
            float t = g.life / g.maxLife;
            float srcY = VIEW_H - g.y - g.height;

            myGdxGame.batch.begin();
            myGdxGame.batch.setColor(0.5f, 1f, 0.5f, g.alpha * t);
            myGdxGame.batch.draw(screenshotTexture,
                g.offsetX, g.y, VIEW_W, g.height,
                0, (int) (VIEW_H - g.y - g.height),
                screenshotTexture.getWidth(), (int) g.height,
                false, true);
            myGdxGame.batch.setColor(1f, 0.2f, 0.2f, g.alpha * t * 0.5f);
            myGdxGame.batch.draw(screenshotTexture,
                g.offsetX + 6f, g.y, VIEW_W, g.height,
                0, (int) (VIEW_H - g.y - g.height),
                screenshotTexture.getWidth(), (int) g.height,
                false, true);
            myGdxGame.batch.setColor(0.2f, 0.4f, 1f, g.alpha * t * 0.5f);
            myGdxGame.batch.draw(screenshotTexture,
                g.offsetX - 6f, g.y, VIEW_W, g.height,
                0, (int) (VIEW_H - g.y - g.height),
                screenshotTexture.getWidth(), (int) g.height,
                false, true);
            myGdxGame.batch.setColor(Color.WHITE);
            myGdxGame.batch.end();
        }
    }

    private void drawOverlays() {
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        shapes.setColor(0f, 0.05f, 0f, 0.45f);
        shapes.rect(0, 0, VIEW_W, VIEW_H);


        for (int i = 0; i < noiseX.length; i++) {
            shapes.setColor(250f, 0f, 0.3f, noiseA[i]);
            shapes.rect(noiseX[i], 0, noiseW[i], VIEW_H);
        }

        shapes.setColor(250f, 0f, 0.3f, 0.07f);
        shapes.rect(0, scanLine, VIEW_W, 8f);
        shapes.setColor(250f, 0f, 0.3f, 0.03f);
        shapes.rect(0, scanLine + 8f, VIEW_W, 16f);

        shapes.end();
    }

    private void drawCodeLines() {
        for (CodeLine c : codeLines) {
            fontSmall.setColor(250f, 0f, 0.3f, 0.03f);
            fontSmall.draw(myGdxGame.batch, c.text, c.x, c.y);
        }
    }


    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }

    @Override
    public void dispose() {
        shapes.dispose();
        font.dispose();
        fontSmall.dispose();
        if (screenshotTexture != null) screenshotTexture.dispose();
    }
}
