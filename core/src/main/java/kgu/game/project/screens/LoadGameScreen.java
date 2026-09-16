package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

import kgu.game.project.FontBuilder;
import kgu.game.project.GameResources;
import kgu.game.project.GameSettings;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ButtonView;
import kgu.game.project.components.CyberFx;
import kgu.game.project.components.MovingBackgroundView;
import kgu.game.project.managers.LocalizationManager;
import kgu.game.project.managers.MemoryManager;

public class LoadGameScreen extends ScreenAdapter {

    private static final float PANEL_X = 300f;
    private static final float PANEL_Y = 120f;
    private static final float PANEL_W = 680f;
    private static final float PANEL_H = 545f;

    private static final float TITLE_TOP = 637f;
    private static final float PROMPT_TOP = 583f;
    private static final float DIVIDER_Y = 553f;

    private static final float GRID_X = 340f;
    private static final float GRID_W = 600f;
    private static final float CARD_W = 285f;
    private static final float CARD_H = 92f;
    private static final float COL_GAP = 30f;
    private static final float ROW_STEP = 106f;
    private static final float ROW_TOP = 430f;

    /** The grid is two columns by three rows, matching the six playable levels. */
    private static final int MAX_SLOTS = 6;

    MyGdxGame myGdxGame;

    MovingBackgroundView backgroundView;
    ButtonView returnButton;
    ArrayList<SaveSlot> slots = new ArrayList<>();

    private CyberFx fx;
    private Texture cardTexture;
    private BitmapFont hudFont;
    private BitmapFont cardFont;

    private final GlyphLayout layout = new GlyphLayout();
    private final Color savedColor = new Color();
    private final Vector3 pointer = new Vector3();
    private final StringBuilder promptBuffer = new StringBuilder(48);
    private final float[] slotHover = new float[MAX_SLOTS];

    private String prompt = "";
    private float promptTimer;
    private float promptWidth;
    private float intro;
    private int hovered = -1;
    private LocalizationManager.Language lastLanguage;

    /** Plain data: every card shares one texture and the screen's fonts. */
    static class SaveSlot {
        long timestamp;
        String slotLabel;
        String levelName;
        String dateText;
        float x;
        float y;

        boolean isHit(float tx, float ty) {
            return tx >= x && tx <= x + CARD_W && ty >= y && ty <= y + CARD_H;
        }
    }

    public LoadGameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);
        cardTexture = new Texture(GameResources.BUTTON_SHORT_BG_IMG_PATH);

        returnButton = new ButtonView(
            GameSettings.SCREEN_WIDTH / 2f - 90f, 148f,
            180f, 58f,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            LocalizationManager.get("settings.return")
        );

        fx = new CyberFx();
        hudFont = FontBuilder.generate(13, CyberFx.ACCENT_DIM, GameResources.XANMONO_FONT_PATH);
        cardFont = FontBuilder.generate(26, CyberFx.ACCENT, GameResources.XANMONO_FONT_PATH);

        refreshTexts();
        refreshSavesList();
    }

    public static String timestampToDateString(long timestamp) {
        @SuppressWarnings("SimpleDateFormat") java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return sdf.format(new java.util.Date(timestamp));
    }

    @Override
    public void render(float delta) {
        if (fx == null) return;

        fx.update(delta);
        intro = Math.min(2f, intro + delta);

        refreshTextsIfNeeded();
        updatePrompt(delta);
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
        drawPanel(shapes);
        drawSlotAura(shapes);
        fx.end();

        myGdxGame.batch.begin();
        drawTitle();
        drawPrompt();
        drawSlots();
        drawReturnButton();
        myGdxGame.batch.end();

        fx.begin(myGdxGame.uiCamera.combined);
        drawSlotHighlight(shapes);
        drawIntroWipe(shapes);
        drawCaret(shapes);
        fx.drawChrome();
        fx.end();
    }

    // ---------------------------------------------------------------- update

    private void updatePrompt(float delta) {
        if (fx.time() < 0.7f || promptBuffer.length() >= prompt.length()) return;
        promptTimer += delta;
        while (promptTimer > 0.035f && promptBuffer.length() < prompt.length()) {
            promptTimer -= 0.035f;
            promptBuffer.append(prompt.charAt(promptBuffer.length()));
        }
        layout.setText(hudFont, promptBuffer);
        promptWidth = layout.width;
    }

    private void updateHover(float delta) {
        hovered = -1;
        if (intro > 1f) {
            pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            myGdxGame.uiCamera.unproject(pointer);
            for (int i = 0; i < slots.size(); i++) {
                if (slots.get(i).isHit(pointer.x, pointer.y)) {
                    hovered = i;
                    break;
                }
            }
        }
        for (int i = 0; i < slotHover.length; i++) {
            float target = i == hovered ? 1f : 0f;
            slotHover[i] = MathUtils.clamp(slotHover[i] + (target - slotHover[i]) * delta * 12f, 0f, 1f);
        }
    }

    /** 0 while the card is still hidden, 1 once its reveal wipe has finished. */
    private float reveal(int i) {
        return MathUtils.clamp((intro - (0.25f + i * 0.08f)) / 0.30f, 0f, 1f);
    }

    // ----------------------------------------------------------------- panel

    private void drawPanel(ShapeRenderer shapes) {
        float fade = fx.fade();

        shapes.setColor(0.02f, 0.06f, 0.05f, 0.62f * fade);
        shapes.rect(PANEL_X, PANEL_Y, PANEL_W, PANEL_H);

        shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.045f * fade);
        shapes.rect(PANEL_X, DIVIDER_Y, PANEL_W, PANEL_Y + PANEL_H - DIVIDER_Y);

        shapes.setColor(CyberFx.ACCENT_DIM.r, CyberFx.ACCENT_DIM.g, CyberFx.ACCENT_DIM.b, 0.35f * fade);
        fx.frame(PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 1.5f);

        shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.75f * fade);
        fx.corners(PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 22f, 3f);

        shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.55f * fade);
        shapes.rect(GRID_X, DIVIDER_Y, 26f, 3f);
        shapes.setColor(CyberFx.ACCENT_DIM.r, CyberFx.ACCENT_DIM.g, CyberFx.ACCENT_DIM.b, 0.20f * fade);
        shapes.rect(GRID_X + 32f, DIVIDER_Y + 1f, GRID_W - 32f, 1f);
    }

    // ----------------------------------------------------------------- slots

    private void drawSlotAura(ShapeRenderer shapes) {
        for (int i = 0; i < slots.size(); i++) {
            SaveSlot slot = slots.get(i);
            float t = slotHover[i];
            if (t <= 0.01f) continue;

            for (int k = 4; k >= 1; k--) {
                float pad = k * 3f * t;
                shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.07f * t);
                fx.frame(slot.x - pad, slot.y - pad, CARD_W + pad * 2f, CARD_H + pad * 2f, 2.5f);
            }
            shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.9f * t);
            shapes.rect(slot.x - 7f, slot.y + 8f, 4f, CARD_H - 16f);
        }
    }

    private void drawSlots() {
        float fade = fx.fade();

        if (slots.isEmpty()) {
            String text = LocalizationManager.get("loadgame.empty");
            savedColor.set(hudFont.getColor());
            layout.setText(hudFont, text);
            hudFont.setColor(CyberFx.ACCENT_DIM.r, CyberFx.ACCENT_DIM.g, CyberFx.ACCENT_DIM.b, 0.6f * fade);
            hudFont.draw(myGdxGame.batch, text,
                GameSettings.SCREEN_WIDTH / 2f - layout.width / 2f, 370f);
            hudFont.setColor(savedColor);
            return;
        }

        savedColor.set(hudFont.getColor());
        for (int i = 0; i < slots.size(); i++) {
            SaveSlot slot = slots.get(i);
            float t = slotHover[i];
            float a = fade * reveal(i);

            myGdxGame.batch.setColor(1f, 1f, 1f, a);
            myGdxGame.batch.draw(cardTexture, slot.x, slot.y, CARD_W, CARD_H);
            myGdxGame.batch.setColor(Color.WHITE);

            hudFont.setColor(CyberFx.ACCENT_DIM.r, CyberFx.ACCENT_DIM.g, CyberFx.ACCENT_DIM.b,
                (0.55f + 0.45f * t) * a);
            hudFont.draw(myGdxGame.batch, slot.slotLabel, slot.x + 16f, slot.y + 82f);
            hudFont.draw(myGdxGame.batch, slot.dateText, slot.x + 16f, slot.y + 22f);

            cardFont.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, (0.80f + 0.20f * t) * a);
            cardFont.draw(myGdxGame.batch, slot.levelName, slot.x + 16f, slot.y + 62f);

            if (t > 0.01f) {
                layout.setText(cardFont, ">");
                cardFont.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, t);
                cardFont.draw(myGdxGame.batch, ">",
                    slot.x + CARD_W - 22f - layout.width + (1f - t) * 10f,
                    slot.y + CARD_H / 2f + layout.height / 2f);
            }
        }
        hudFont.setColor(savedColor);
    }

    private void drawSlotHighlight(ShapeRenderer shapes) {
        for (int i = 0; i < slots.size(); i++) {
            SaveSlot slot = slots.get(i);
            float t = slotHover[i];
            if (t <= 0.01f) continue;

            shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.10f * t);
            shapes.rect(slot.x, slot.y, CARD_W, CARD_H);
            shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.35f * t);
            shapes.rect(slot.x, slot.y, CARD_W, 2f);
            shapes.rect(slot.x, slot.y + CARD_H - 2f, CARD_W, 2f);
        }
    }

    private void drawIntroWipe(ShapeRenderer shapes) {
        for (int i = 0; i < slots.size(); i++) {
            SaveSlot slot = slots.get(i);
            float r = reveal(i);
            if (r >= 1f) continue;

            float coverH = CARD_H * (1f - r);
            float coverY = slot.y + CARD_H - coverH;
            shapes.setColor(0.02f, 0.06f, 0.05f, 1f);
            shapes.rect(slot.x, coverY, CARD_W, coverH);
            shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.85f);
            shapes.rect(slot.x, coverY, CARD_W, 2f);
        }
    }

    // ------------------------------------------------------------ title & hud

    private void drawTitle() {
        fx.drawTitle(myGdxGame.batch, myGdxGame.xanmonoFontBig,
            LocalizationManager.get("loadgame.title"),
            GameSettings.SCREEN_WIDTH / 2f, TITLE_TOP, GRID_W);
    }

    private void drawPrompt() {
        savedColor.set(hudFont.getColor());
        hudFont.setColor(CyberFx.ACCENT_DIM.r, CyberFx.ACCENT_DIM.g, CyberFx.ACCENT_DIM.b, 0.8f * fx.fade());
        hudFont.draw(myGdxGame.batch, promptBuffer, GRID_X, PROMPT_TOP);
        hudFont.setColor(savedColor);
    }

    private void drawCaret(ShapeRenderer shapes) {
        if (promptBuffer.length() < prompt.length()) return;
        if (((int) (fx.time() * 1.7f)) % 2 != 0) return;
        shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.75f * fx.fade());
        shapes.rect(GRID_X + promptWidth + 5f, PROMPT_TOP - 12f, 9f, 13f);
    }

    private void drawReturnButton() {
        returnButton.draw(myGdxGame.batch);

        pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        myGdxGame.uiCamera.unproject(pointer);
        if (!returnButton.isHit(pointer.x, pointer.y) || returnButton.getText() == null) return;

        BitmapFont font = myGdxGame.commonBlackFont;
        savedColor.set(font.getColor());
        layout.setText(font, returnButton.getText());
        font.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, fx.fade());
        font.draw(myGdxGame.batch, returnButton.getText(),
            returnButton.getX() + (returnButton.getWidth() - layout.width) / 2f,
            returnButton.getY() + (returnButton.getHeight() + layout.height) / 2f);
        font.setColor(savedColor);
    }

    // ----------------------------------------------------------------- input

    void handleInput() {
        if (!Gdx.input.justTouched()) return;

        myGdxGame.touch = myGdxGame.uiCamera.unproject(
            new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)
        );

        if (returnButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
            myGdxGame.setScreen(myGdxGame.menuScreen);
            return;
        }

        for (SaveSlot slot : slots) {
            if (!slot.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) continue;

            ArrayList<Object> save = MemoryManager.getSaveByDate(String.valueOf(slot.timestamp));
            int level = Integer.parseInt(save.get(0).toString());
            myGdxGame.audioManager.menuMusic.stop();
            if (MemoryManager.loadIsMusicOn()) {
                myGdxGame.audioManager.backgroundMusic.play();
            }
            switch (level) {
                case 1:
                    myGdxGame.setScreen(new LevelOneScreen(myGdxGame));
                    break;
                case 2:
                    myGdxGame.setScreen(new LevelTwoScreen(myGdxGame));
                    break;
                case 3:
                    myGdxGame.setScreen(new LevelThreeScreen(myGdxGame));
                    break;
                case 4:
                    myGdxGame.setScreen(new LevelFourScreen(myGdxGame));
                    break;
                case 5:
                    myGdxGame.setScreen(new LevelFiveScreen(myGdxGame));
                    break;
                case 6:
                    myGdxGame.setScreen(new EndScreen(myGdxGame));
                    break;
                default:
                    myGdxGame.setScreen(new LevelFiveScreen(myGdxGame));
                    break;
            }
            return;
        }
    }

    @Override
    public void show() {
        myGdxGame.uiCamera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        myGdxGame.uiCamera.update();

        refreshTexts();
        refreshSavesList();

        if (fx != null) fx.reset();
        intro = 0f;
        hovered = -1;
        promptTimer = 0f;
        promptWidth = 0f;
        promptBuffer.setLength(0);
        for (int i = 0; i < slotHover.length; i++) slotHover[i] = 0f;
    }

    private void refreshTextsIfNeeded() {
        if (lastLanguage != LocalizationManager.getLanguage()) refreshTexts();
    }

    private void refreshTexts() {
        returnButton.setText(LocalizationManager.get("settings.return"));
        prompt = "// " + LocalizationManager.get("loadgame.subtitle");
        if (promptBuffer.length() > prompt.length()) promptBuffer.setLength(prompt.length());
        lastLanguage = LocalizationManager.getLanguage();
    }

    private void refreshSavesList() {
        slots.clear();

        String[] levelNames = {"Level 1", "Level 2", "Level 3",
            "Level 4", "Level 5", "Final boss"};

        java.util.Map<Integer, Long> latestByLevel = new java.util.HashMap<>();
        for (long date : MemoryManager.getAllSaveDates()) {
            ArrayList<Object> save = MemoryManager.getSaveByDate(String.valueOf(date));
            int level = Integer.parseInt(save.get(0).toString());
            if (!latestByLevel.containsKey(level) || date > latestByLevel.get(level)) {
                latestByLevel.put(level, date);
            }
        }

        java.util.List<Integer> sortedLevels = new java.util.ArrayList<>(latestByLevel.keySet());
        java.util.Collections.sort(sortedLevels);

        int cnt = 0;
        for (int level : sortedLevels) {
            if (cnt >= MAX_SLOTS) break;

            SaveSlot slot = new SaveSlot();
            slot.timestamp = latestByLevel.get(level);
            slot.x = GRID_X + (cnt % 2) * (CARD_W + COL_GAP);
            slot.y = ROW_TOP - (cnt / 2) * ROW_STEP;
            slot.levelName = level <= levelNames.length ? levelNames[level - 1] : "Level " + level;
            @SuppressWarnings("DefaultLocale") String slotLabel = "SLOT " + String.format("%02d", cnt + 1);
            slot.slotLabel = slotLabel;
            slot.dateText = timestampToDateString(slot.timestamp);

            slots.add(slot);
            cnt++;
        }
    }

    @Override
    public void dispose() {
        if (backgroundView != null) {
            backgroundView.dispose();
            backgroundView = null;
        }
        if (returnButton != null) {
            returnButton.dispose();
            returnButton = null;
        }
        if (cardTexture != null) {
            cardTexture.dispose();
            cardTexture = null;
        }
        if (fx != null) {
            fx.dispose();
            fx = null;
        }
        if (hudFont != null) {
            hudFont.dispose();
            hudFont = null;
        }
        if (cardFont != null) {
            cardFont.dispose();
            cardFont = null;
        }
    }
}
