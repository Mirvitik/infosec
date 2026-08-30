package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

import java.util.Arrays;

import kgu.game.project.FontBuilder;
import kgu.game.project.GameResources;
import kgu.game.project.GameSettings;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ButtonView;
import kgu.game.project.components.CyberFx;
import kgu.game.project.components.ImageView;
import kgu.game.project.components.MovingBackgroundView;
import kgu.game.project.managers.LocalizationManager;
import kgu.game.project.managers.MemoryManager;

public class MenuScreen extends ScreenAdapter {

    private static final float COLUMN_X = 200f;
    private static final float COLUMN_W = 340f;
    private static final float BUTTON_H = 70f;
    private static final float BUTTON_STEP = 95f;
    private static final float BUTTON_BOTTOM = 56f;
    private static final float SPINE_X = 176f;
    private static final float TITLE_X = 200f;
    private static final float TITLE_TOP = 640f;
    private static final float DIVIDER_Y = 578f;
    private static final float PROMPT_TOP = 560f;
    private static final float MASK_X = 800f;
    // основные заголовки меню
    private static final String TITLE = "3xpl01T";
    private static final String PROMPT = "root@3xpl01T:~$ /bin/bash";

    private static final String MASK_CAPTION = "// SUBJECT: UNKNOWN";
    private static final String[] BOOT_LINES = {
        "[ ok ] core: kernel loaded",
        "[ ok ] net : tap0 up",
        "[warn] av  : db outdated",
        "[ ok ] tty : /bin/bash"
    };

    MyGdxGame myGdxGame;

    MovingBackgroundView backgroundView;
    ButtonView startButtonView;
    ButtonView loadGameButtonView;
    ButtonView settingsButtonView;
    ButtonView exitButtonView;
    ButtonView achievementsButtonView;
    ImageView image;

    private ButtonView[] buttons;
    private final float[] hoverAmount = new float[5];

    private CyberFx fx;
    private BitmapFont hudFont;

    private final GlyphLayout layout = new GlyphLayout();
    private final Color savedColor = new Color();
    private final Vector3 pointer = new Vector3();
    private final StringBuilder promptBuffer = new StringBuilder(PROMPT.length());

    private float intro;
    private float promptWidth;
    private float glitchDelay;
    private float glitchLife;
    private float glitchX;
    private float glitchY;
    private float promptTimer;
    private float bootTimer;
    private int bootLine;
    private int bootChars;
    private int hovered = -1;

    public MenuScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);

        image = new ImageView(MASK_X, 177f, GameResources.MASK_IMG_PATH);
        image.setPosition(MASK_X, 177f);

        startButtonView = menuButton(4, "menu.start");
        loadGameButtonView = menuButton(3, "menu.load");
        achievementsButtonView = menuButton(2, "menu.achievements");
        settingsButtonView = menuButton(1, "menu.settings");
        exitButtonView = menuButton(0, "menu.exit");
        buttons = new ButtonView[]{
            startButtonView, loadGameButtonView, achievementsButtonView, settingsButtonView, exitButtonView
        };

        fx = new CyberFx();
        hudFont = FontBuilder.generate(13, CyberFx.ACCENT_DIM, GameResources.XANMONO_FONT_PATH);

        refreshAllTexts();
    }

    private ButtonView menuButton(int slot, String key) {
        return new ButtonView(COLUMN_X, BUTTON_BOTTOM + slot * BUTTON_STEP, COLUMN_W, BUTTON_H,
            myGdxGame.commonGreenFont, GameResources.BUTTON_LONG_BG_IMG_PATH, LocalizationManager.get(key));
    }

    @Override
    public void render(float delta) {
        if (fx == null) return;

        fx.update(delta);
        intro = Math.min(2f, intro + delta);

        refreshAllTexts();
        updateGlitch(delta);
        updateTerminal(delta);
        updateHover(delta);
        handleInput();

        myGdxGame.uiCamera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.uiCamera.combined);
        fx.clear();

        myGdxGame.batch.begin();
        backgroundView.draw(myGdxGame.batch);
        fx.drawRain(myGdxGame.batch);
        myGdxGame.batch.end();

        ShapeRenderer shapes = fx.begin(myGdxGame.uiCamera.combined);
        drawMaskHalo(shapes);
        drawSpine(shapes);
        drawDivider(shapes);
        drawHoverAura(shapes);
        fx.end();

        myGdxGame.batch.begin();
        drawMask();
        drawTitle();
        drawTerminal();
        drawButtons();
        myGdxGame.batch.end();

        fx.begin(myGdxGame.uiCamera.combined);
        drawHoverHighlight(shapes);
        drawIntroWipe(shapes);
        drawMaskOverlay(shapes); // рисует рамку для маски
        drawCaret(shapes);
        fx.drawChrome();
        fx.end();
    }


    private void updateGlitch(float delta) {
        glitchDelay -= delta;
        if (glitchDelay <= 0f) {
            glitchDelay = 1.8f + MathUtils.random(3.6f);
            glitchLife = 0.10f + MathUtils.random(0.16f);
        }
        if (glitchLife > 0f) {
            glitchLife -= delta;
            glitchX = MathUtils.random(-9f, 9f);
            glitchY = MathUtils.random(-3f, 3f);
        } else {
            glitchX = 0f;
            glitchY = 0f;
        }
    }

    private void updateTerminal(float delta) {
        float time = fx.time();

        if (time > 0.9f && promptBuffer.length() < PROMPT.length()) {
            promptTimer += delta;
            while (promptTimer > 0.045f && promptBuffer.length() < PROMPT.length()) {
                promptTimer -= 0.045f;
                promptBuffer.append(PROMPT.charAt(promptBuffer.length()));
            }
            layout.setText(hudFont, promptBuffer);
            promptWidth = layout.width;
        }

        if (time > 1.4f && bootLine < BOOT_LINES.length) {
            bootTimer += delta;
            while (bootTimer > 0.018f && bootLine < BOOT_LINES.length) {
                bootTimer -= 0.018f;
                bootChars++;
                if (bootChars >= BOOT_LINES[bootLine].length()) {
                    bootLine++;
                    bootChars = 0;
                }
            }
        }
    }

    private void updateHover(float delta) {
        hovered = -1;
        if (buttons != null && intro > 1f) {
            pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            myGdxGame.uiCamera.unproject(pointer);
            for (int i = 0; i < buttons.length; i++) {
                ButtonView b = buttons[i];
                if (b != null && pointer.x >= b.getX() && pointer.x <= b.getX() + b.getWidth()
                    && pointer.y >= b.getY() && pointer.y <= b.getY() + b.getHeight()) {
                    hovered = i;
                    break;
                }
            }
        }
        for (int i = 0; i < hoverAmount.length; i++) {
            float target = i == hovered ? 1f : 0f;
            hoverAmount[i] = MathUtils.clamp(hoverAmount[i] + (target - hoverAmount[i]) * delta * 12f, 0f, 1f);
        }
    }

    private float reveal(int i) {
        return MathUtils.clamp((intro - (0.25f + i * 0.10f)) / 0.30f, 0f, 1f);
    }


    private void drawMaskHalo(ShapeRenderer shapes) {
        float pulse = 0.5f + 0.5f * MathUtils.sin(fx.time() * 1.1f);
        float cx = image.getX() + image.getTextureWidth() / 2f;
        float cy = image.getY() + image.getTextureHeight() / 2f;
        for (int k = 0; k < 9; k++) {
            shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b,
                0.030f * (1f - k / 9f) * fx.fade());
            shapes.circle(cx, cy, 145f + k * 24f + pulse * 12f, 48);
        }
    }

    private void drawMask() {
        image.setY(177f + MathUtils.sin(fx.time() * 0.8f) * 7f);
        float breathe = 0.88f + 0.12f * MathUtils.sin(fx.time() * 1.1f);
        myGdxGame.batch.setColor(0.80f * breathe, breathe, 0.92f * breathe, fx.fade());
        image.draw(myGdxGame.batch);
        myGdxGame.batch.setColor(Color.WHITE);
    }


    private void drawMaskOverlay(ShapeRenderer shapes) {
        float fade = fx.fade();
        float x = image.getX();
        float y = image.getY();
        float w = image.getTextureWidth();
        float h = image.getTextureHeight();

        for (int k = 0; k < 3; k++) {
            float offset = (fx.time() * 34f + k * (h / 3f)) % h;
            shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.09f * fade);
            shapes.rect(x, y + offset, w, 2f);
        }

        shapes.setColor(CyberFx.ACCENT_DIM.r, CyberFx.ACCENT_DIM.g, CyberFx.ACCENT_DIM.b, 0.45f * fade);
        fx.frame(x - 2f, y - 2f, w + 4f, h + 4f, 2f);
        shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.80f * fade);
        fx.corners(x - 5f, y - 5f, w + 10f, h + 10f, 24f, 3f);
    }

    private void drawTitle() {
        float split = 3f + MathUtils.sin(fx.time() * 1.7f) * 1.6f + (glitchLife > 0f ? 7f : 0f);
        float y = TITLE_TOP + glitchY;

        drawTitleLayer(myGdxGame.largeRedFont, TITLE_X - split + glitchX, y);
        drawTitleLayer(myGdxGame.largeBlueFont, TITLE_X + split - glitchX, y);
        drawTitleLayer(myGdxGame.largeWhiteFont, TITLE_X, TITLE_TOP);
    }

    private void drawTitleLayer(BitmapFont font, float x, float y) {
        savedColor.set(font.getColor());
        font.setColor(savedColor.r, savedColor.g, savedColor.b, fx.fade());
        font.draw(myGdxGame.batch, TITLE, x, y);
        font.setColor(savedColor);
    }

    private void drawDivider(ShapeRenderer shapes) {
        shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.55f * fx.fade());
        shapes.rect(TITLE_X, DIVIDER_Y, 26f, 3f);
        shapes.setColor(CyberFx.ACCENT_DIM.r, CyberFx.ACCENT_DIM.g, CyberFx.ACCENT_DIM.b, 0.20f * fx.fade());
        shapes.rect(TITLE_X + 32f, DIVIDER_Y + 1f, COLUMN_W - 32f, 1f);
    }


    private void drawTerminal() {
        float fade = fx.fade();
        savedColor.set(hudFont.getColor());

        hudFont.setColor(CyberFx.ACCENT_DIM.r, CyberFx.ACCENT_DIM.g, CyberFx.ACCENT_DIM.b, 0.85f * fade);
        hudFont.draw(myGdxGame.batch, promptBuffer, TITLE_X, PROMPT_TOP);

        hudFont.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.55f * fade);
        hudFont.draw(myGdxGame.batch, MASK_CAPTION, MASK_X, image.getY() + image.getTextureHeight() + 22f);

        for (int i = 0; i <= bootLine && i < BOOT_LINES.length; i++) {
            String line = BOOT_LINES[i];
            int visible = i < bootLine ? line.length() : bootChars;
            if (visible <= 0) continue;

            if (line.startsWith("[warn]")) hudFont.setColor(0.85f, 0.72f, 0.35f, 0.75f * fade);
            else
                hudFont.setColor(CyberFx.ACCENT_DIM.r, CyberFx.ACCENT_DIM.g, CyberFx.ACCENT_DIM.b, 0.45f * fade);
            hudFont.draw(myGdxGame.batch, line, MASK_X, 140f - i * 22f, 0, visible, 0f, Align.left, false);
        }

        hudFont.setColor(savedColor);
    }

    private void drawCaret(ShapeRenderer shapes) {
        if (promptBuffer.length() < PROMPT.length()) return;
        if (((int) (fx.time() * 1.7f)) % 2 != 0) return;
        shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.75f * fx.fade());
        shapes.rect(TITLE_X + promptWidth + 5f, PROMPT_TOP - 12f, 9f, 13f);
    }


    private void drawSpine(ShapeRenderer shapes) {
        float fade = fx.fade();
        shapes.setColor(CyberFx.ACCENT_DIM.r, CyberFx.ACCENT_DIM.g, CyberFx.ACCENT_DIM.b, 0.16f * fade);
        shapes.rect(SPINE_X, BUTTON_BOTTOM, 1.5f, 4 * BUTTON_STEP + BUTTON_H);

        for (int i = 0; i < buttons.length; i++) {
            ButtonView b = buttons[i];
            if (b == null) continue;
            float r = reveal(i);
            float t = hoverAmount[i] * r;
            float cy = b.getY() + b.getHeight() / 2f;
            shapes.setColor(
                CyberFx.ACCENT_DIM.r + (CyberFx.ACCENT.r - CyberFx.ACCENT_DIM.r) * t,
                CyberFx.ACCENT_DIM.g + (CyberFx.ACCENT.g - CyberFx.ACCENT_DIM.g) * t,
                CyberFx.ACCENT_DIM.b + (CyberFx.ACCENT.b - CyberFx.ACCENT_DIM.b) * t,
                (0.25f + 0.65f * t) * fade * r);
            shapes.rect(SPINE_X - 3f, cy - 1f, 8f + 8f * t, 2f);
        }
    }

    private void drawHoverAura(ShapeRenderer shapes) {
        for (int i = 0; i < buttons.length; i++) {
            ButtonView b = buttons[i];
            if (b == null) continue;
            float t = hoverAmount[i];
            if (t <= 0.01f) continue;

            for (int k = 4; k >= 1; k--) {
                float pad = k * 3.5f * t;
                shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.07f * t);
                fx.frame(b.getX() - pad, b.getY() - pad, b.getWidth() + pad * 2f, b.getHeight() + pad * 2f, 2.5f);
            }
            shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.9f * t);
            shapes.rect(b.getX() - 7f, b.getY() + 6f, 4f, b.getHeight() - 12f);
        }
    }

    private void drawButtons() {
        for (int i = 0; i < buttons.length; i++) {
            ButtonView b = buttons[i];
            if (b == null) continue;
            b.draw(myGdxGame.batch);

            float t = hoverAmount[i];
            if (t <= 0.01f || b.getText() == null) continue;

            BitmapFont font = myGdxGame.commonGreenFont;
            savedColor.set(font.getColor());
            layout.setText(font, b.getText());
            font.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, t);
            font.draw(myGdxGame.batch, b.getText(),
                b.getX() + (b.getWidth() - layout.width) / 2f,
                b.getY() + (b.getHeight() + layout.height) / 2f);
            font.setColor(savedColor);
        }
    }

    private void drawHoverHighlight(ShapeRenderer shapes) {
        for (int i = 0; i < buttons.length; i++) {
            ButtonView b = buttons[i];
            if (b == null) continue;
            float t = hoverAmount[i];
            if (t <= 0.01f) continue;

            shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.10f * t);
            shapes.rect(b.getX(), b.getY(), b.getWidth(), b.getHeight());
            shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.35f * t);
            shapes.rect(b.getX(), b.getY(), b.getWidth(), 2f);
            shapes.rect(b.getX(), b.getY() + b.getHeight() - 2f, b.getWidth(), 2f);
        }
    }

    private void drawIntroWipe(ShapeRenderer shapes) {
        for (int i = 0; i < buttons.length; i++) {
            ButtonView b = buttons[i];
            if (b == null) continue;
            float r = reveal(i);
            if (r >= 1f) continue;

            float coverH = b.getHeight() * (1f - r);
            float coverY = b.getY() + b.getHeight() - coverH;
            shapes.setColor(fx.backdrop(1f));
            shapes.rect(b.getX(), coverY, b.getWidth(), coverH);
            shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.85f);
            shapes.rect(b.getX(), coverY, b.getWidth(), 2f);
        }
    }


    private void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.uiCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (startButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                String[] images = {
                    GameResources.STORY_TELLING_IMG_PATH,
                    GameResources.STORY_TELLING_2_IMG_PATH,
                    GameResources.STORY_TELLING_3_IMG_PATH
                };
                String[] texts = {
                    LocalizationManager.get("story.intro.0"),
                    LocalizationManager.get("story.intro.1"),
                    LocalizationManager.get("story.intro.2")
                };
                myGdxGame.audioManager.menuMusic.stop();
                if (MemoryManager.loadIsMusicOn()) {
                    myGdxGame.audioManager.storyMusic.play();
                }

                myGdxGame.setScreen(new CutsceneScreen(myGdxGame, images, texts, () -> {
                    if (myGdxGame.gameScreen != null) myGdxGame.gameScreen.dispose();
                    myGdxGame.gameScreen = new GameScreen(myGdxGame);
                    myGdxGame.setScreen(myGdxGame.gameScreen);
                    myGdxGame.audioManager.storyMusic.stop();
                    if (MemoryManager.loadIsMusicOn()) {
                        myGdxGame.audioManager.backgroundMusic.play();
                    }
                }));
            }
            if (loadGameButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.loadScreen);
            }
            if (achievementsButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.achieveScreen = new AchieveScreen(myGdxGame);
                myGdxGame.setScreen(myGdxGame.achieveScreen);
            }
            if (exitButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                Gdx.app.exit();
            }
            if (settingsButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.settingsScreen);
            }
        }
    }

    @Override
    public void show() {
        myGdxGame.uiCamera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        myGdxGame.uiCamera.update();
        myGdxGame.audioManager.backgroundMusic.stop();
        if (MemoryManager.loadIsMusicOn()) {
            myGdxGame.audioManager.menuMusic.play();
        }

        if (fx != null) fx.reset();
        intro = 0f;
        hovered = -1;
        promptTimer = 0f;
        promptWidth = 0f;
        promptBuffer.setLength(0);
        bootTimer = 0f;
        bootLine = 0;
        bootChars = 0;
        Arrays.fill(hoverAmount, 0f);
    }

    private void refreshAllTexts() {
        startButtonView.setText(LocalizationManager.get("menu.start"));
        loadGameButtonView.setText(LocalizationManager.get("menu.load"));
        settingsButtonView.setText(LocalizationManager.get("menu.settings"));
        exitButtonView.setText(LocalizationManager.get("menu.exit"));
        achievementsButtonView.setText(LocalizationManager.get("menu.achievements"));
    }

    @Override
    public void dispose() {
        buttons = null;
        if (backgroundView != null) {
            backgroundView.dispose();
            backgroundView = null;
        }
        if (image != null) {
            image.dispose();
            image = null;
        }
        if (startButtonView != null) {
            startButtonView.dispose();
            startButtonView = null;
        }
        if (loadGameButtonView != null) {
            loadGameButtonView.dispose();
            loadGameButtonView = null;
        }
        if (achievementsButtonView != null) {
            achievementsButtonView.dispose();
            achievementsButtonView = null;
        }
        if (settingsButtonView != null) {
            settingsButtonView.dispose();
            settingsButtonView = null;
        }
        if (exitButtonView != null) {
            exitButtonView.dispose();
            exitButtonView = null;
        }
        if (fx != null) {
            fx.dispose();
            fx = null;
        }
        if (hudFont != null) {
            hudFont.dispose();
            hudFont = null;
        }
    }
}
