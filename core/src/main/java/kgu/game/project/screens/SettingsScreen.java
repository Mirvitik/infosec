package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import kgu.game.project.FontBuilder;
import kgu.game.project.GameResources;
import kgu.game.project.GameSettings;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ButtonView;
import kgu.game.project.components.CyberFx;
import kgu.game.project.components.MovingBackgroundView;
import kgu.game.project.managers.LocalizationManager;
import kgu.game.project.managers.MemoryManager;

public class SettingsScreen extends ScreenAdapter {

    private static final float PANEL_X = 300f;
    private static final float PANEL_Y = 130f;
    private static final float PANEL_W = 680f;
    private static final float PANEL_H = 530f;

    private static final float TITLE_TOP = 632f;
    private static final float PROMPT_TOP = 578f;
    private static final float DIVIDER_Y = 548f;

    private static final float ROW_X = 340f;
    private static final float ROW_W = 600f;
    private static final float ROW_H = 44f;
    private static final float ROW_TOP = 480f;
    private static final float ROW_STEP = 56f;

    private static final float LABEL_X = 362f;
    private static final float STATE_RIGHT = 600f;
    private static final float VALUE_RIGHT = 918f;
    private static final float BAR_X = 632f;
    private static final float BAR_W = 140f;
    private static final float PCT_RIGHT = 855f;
    private static final float STEP_BUTTON = 32f;

    private static final float TOGGLE_W_VOLUME = 280f;

    private static final int ROW_DIFFICULTY = 0;
    private static final int ROW_MUSIC = 1;
    private static final int ROW_SOUND = 2;
    private static final int ROW_SUBTITLES = 3;
    private static final int ROW_LANGUAGE = 4;
    private static final int ROW_COUNT = 5;

    private static final String TITLE = "root@3xpl01T:~$ cat /etc/3xpl01T.conf";

    MyGdxGame myGdxGame;

    MovingBackgroundView backgroundView;
    ButtonView returnButton;
    ButtonView musicVolumeUpButton;
    ButtonView musicVolumeDownButton;
    ButtonView soundVolumeUpButton;
    ButtonView soundVolumeDownButton;

    private ButtonView[] widgets;
    private BitmapFont[] widgetFonts;
    private final float[] widgetHover = new float[5];
    private final float[] rowHover = new float[ROW_COUNT];

    private CyberFx fx;
    private BitmapFont hudFont;

    private final GlyphLayout layout = new GlyphLayout();
    private final Color savedColor = new Color();
    private final Vector3 pointer = new Vector3();
    private final StringBuilder promptBuffer = new StringBuilder(TITLE.length());

    private float intro;
    private float promptTimer;
    private float promptWidth;
    private int hoveredRow = -1;
    private int hoveredWidget = -1;

    public SettingsScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);

        musicVolumeDownButton = stepButton(ROW_MUSIC, 860f, "-");
        musicVolumeUpButton = stepButton(ROW_MUSIC, 900f, "+");
        soundVolumeDownButton = stepButton(ROW_SOUND, 860f, "-");
        soundVolumeUpButton = stepButton(ROW_SOUND, 900f, "+");

        returnButton = new ButtonView(
            GameSettings.SCREEN_WIDTH / 2f - 90f, 160f,
            180f, 60f,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            LocalizationManager.get("settings.return")
        );

        widgets = new ButtonView[]{
            musicVolumeDownButton, musicVolumeUpButton,
            soundVolumeDownButton, soundVolumeUpButton,
            returnButton
        };
        widgetFonts = new BitmapFont[]{
            myGdxGame.xanmonoFont, myGdxGame.xanmonoFont,
            myGdxGame.xanmonoFont, myGdxGame.xanmonoFont,
            myGdxGame.commonBlackFont
        };

        fx = new CyberFx();
        hudFont = FontBuilder.generate(13, CyberFx.ACCENT_DIM, GameResources.XANMONO_FONT_PATH);
    }

    private ButtonView stepButton(int row, float x, String text) {
        return new ButtonView(x, rowY(row) + (ROW_H - STEP_BUTTON) / 2f, STEP_BUTTON, STEP_BUTTON,
            myGdxGame.xanmonoFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, text);
    }

    private static float rowY(int i) {
        return ROW_TOP - i * ROW_STEP;
    }

    private static float toggleWidth(int i) {
        return (i == ROW_MUSIC || i == ROW_SOUND) ? TOGGLE_W_VOLUME : ROW_W;
    }

    @Override
    public void render(float delta) {
        if (fx == null) return;
        // обновляем анимацию графических элементов
        fx.update(delta);
        intro = Math.min(2f, intro + delta);

        updatePrompt(delta);
        updateHover(delta);
        handleInput();

        myGdxGame.camera.update();
        myGdxGame.uiCamera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.uiCamera.combined);
        fx.clear();

        myGdxGame.batch.begin();
        backgroundView.draw(myGdxGame.batch);
        fx.drawRain(myGdxGame.batch);
        myGdxGame.batch.end();

        ShapeRenderer shapes = fx.begin(myGdxGame.uiCamera.combined);
        drawPanel(shapes);
        drawRowBackdrops(shapes);
        drawVolumeBar(shapes, ROW_MUSIC, MemoryManager.loadMusicVolume());
        drawVolumeBar(shapes, ROW_SOUND, MemoryManager.loadSoundVolume());
        drawWidgetAura(shapes);
        fx.end();

        myGdxGame.batch.begin();
        drawTitle();
        drawPrompt();
        drawRows();
        drawWidgets();
        myGdxGame.batch.end();

        fx.begin(myGdxGame.uiCamera.combined);
        drawWidgetHighlight(shapes);
        drawIntroWipe(shapes);
        drawCaret(shapes);
        fx.drawChrome();
        fx.end();

        if (!MemoryManager.loadIsMusicOn()) {
            myGdxGame.audioManager.menuMusic.stop();
        } else {
            myGdxGame.audioManager.backgroundMusic.stop();
            myGdxGame.audioManager.menuMusic.setVolume(MemoryManager.loadMusicVolume());
            myGdxGame.audioManager.menuMusic.play();
        }
    }


    private void updatePrompt(float delta) {
        if (fx.time() < 0.7f || promptBuffer.length() >= TITLE.length()) return;
        promptTimer += delta;
        while (promptTimer > 0.035f && promptBuffer.length() < TITLE.length()) {
            promptTimer -= 0.035f;
            promptBuffer.append(TITLE.charAt(promptBuffer.length()));
        }
        layout.setText(hudFont, promptBuffer);
        promptWidth = layout.width;
    }

    private void updateHover(float delta) {
        hoveredRow = -1;
        hoveredWidget = -1;

        if (intro > 1f) {
            pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            myGdxGame.uiCamera.unproject(pointer);

            for (int i = 0; i < widgets.length; i++) {
                ButtonView b = widgets[i];
                if (b != null && pointer.x >= b.getX() && pointer.x <= b.getX() + b.getWidth()
                    && pointer.y >= b.getY() && pointer.y <= b.getY() + b.getHeight()) {
                    hoveredWidget = i;
                    break;
                }
            }
            if (hoveredWidget < 0) {
                for (int i = 0; i < ROW_COUNT; i++) {
                    if (isRowHit(i, pointer.x, pointer.y)) {
                        hoveredRow = i;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < ROW_COUNT; i++) {
            rowHover[i] = ease(rowHover[i], i == hoveredRow ? 1f : 0f, delta);
        }
        for (int i = 0; i < widgetHover.length; i++) {
            widgetHover[i] = ease(widgetHover[i], i == hoveredWidget ? 1f : 0f, delta);
        }
    }

    private static float ease(float current, float target, float delta) {
        return MathUtils.clamp(current + (target - current) * delta * 12f, 0f, 1f);
    }

    private boolean isRowHit(int i, float x, float y) {
        float bottom = rowY(i);
        return x >= ROW_X && x <= ROW_X + toggleWidth(i) && y >= bottom && y <= bottom + ROW_H;
    }

    private float reveal(int i) {
        return MathUtils.clamp((intro - (0.25f + i * 0.09f)) / 0.30f, 0f, 1f);
    }

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
        shapes.rect(ROW_X, DIVIDER_Y, 26f, 3f);
        shapes.setColor(CyberFx.ACCENT_DIM.r, CyberFx.ACCENT_DIM.g, CyberFx.ACCENT_DIM.b, 0.20f * fade);
        shapes.rect(ROW_X + 32f, DIVIDER_Y + 1f, ROW_W - 32f, 1f);
    }

    private void drawRowBackdrops(ShapeRenderer shapes) {
        float fade = fx.fade();
        for (int i = 0; i < ROW_COUNT; i++) {
            float r = reveal(i);
            float t = rowHover[i] * r;
            float y = rowY(i);

            shapes.setColor(CyberFx.ACCENT_DIM.r, CyberFx.ACCENT_DIM.g, CyberFx.ACCENT_DIM.b, 0.05f * fade * r);
            shapes.rect(ROW_X, y, ROW_W, ROW_H);

            float toggleW = toggleWidth(i);
            if (toggleW < ROW_W) {
                shapes.setColor(CyberFx.ACCENT_DIM.r, CyberFx.ACCENT_DIM.g, CyberFx.ACCENT_DIM.b, 0.18f * fade * r);
                shapes.rect(ROW_X + toggleW, y + 6f, 1f, ROW_H - 12f);
            }

            if (t > 0.01f) {
                shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.10f * t);
                shapes.rect(ROW_X, y, toggleW, ROW_H);
                for (int k = 3; k >= 1; k--) {
                    shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.07f * t);
                    float pad = k * 3f * t;
                    fx.frame(ROW_X - pad, y - pad, toggleW + pad * 2f, ROW_H + pad * 2f, 2f);
                }
            }

            shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b,
                (0.30f + 0.60f * t) * fade * r);
            shapes.rect(ROW_X - 10f, y + 4f, 4f, ROW_H - 8f);
        }
    }

    private void drawVolumeBar(ShapeRenderer shapes, int row, float volume) {
        float fade = fx.fade() * reveal(row);
        float cy = rowY(row) + ROW_H / 2f;

        shapes.setColor(CyberFx.ACCENT_DIM.r, CyberFx.ACCENT_DIM.g, CyberFx.ACCENT_DIM.b, 0.20f * fade);
        shapes.rect(BAR_X, cy - 3f, BAR_W, 6f);

        shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.75f * fade);
        shapes.rect(BAR_X, cy - 3f, BAR_W * MathUtils.clamp(volume, 0f, 1f), 6f);

        shapes.setColor(0f, 0f, 0f, 0.55f * fade);
        for (int s = 1; s < 20; s++) shapes.rect(BAR_X + BAR_W * s / 20f, cy - 3f, 1f, 6f);

        shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.95f * fade);
        shapes.rect(BAR_X + BAR_W * MathUtils.clamp(volume, 0f, 1f) - 1.5f, cy - 8f, 3f, 16f);
    }

    private void drawRows() {
        BitmapFont font = myGdxGame.xanmonoFont;
        savedColor.set(font.getColor());

        for (int i = 0; i < ROW_COUNT; i++) {
            float t = rowHover[i] * reveal(i);
            float y = rowY(i);

            String label = rowLabel(i);
            layout.setText(font, label);
            setMixedColor(font, t, 0.9f);
            font.draw(myGdxGame.batch, label, LABEL_X, y + (ROW_H + layout.height) / 2f);

            String value = rowValue(i);
            layout.setText(font, value);
            setMixedColor(font, Math.max(t, 0.55f), 1f);
            float right = (i == ROW_MUSIC || i == ROW_SOUND) ? STATE_RIGHT : VALUE_RIGHT;
            font.draw(myGdxGame.batch, value, right - layout.width, y + (ROW_H + layout.height) / 2f);
        }

        drawPercent(font, ROW_MUSIC, MemoryManager.loadMusicVolume());
        drawPercent(font, ROW_SOUND, MemoryManager.loadSoundVolume());

        font.setColor(savedColor);
    }

    private void drawPercent(BitmapFont font, int row, float volume) {
        String text = (int) (volume * 100) + "%";
        layout.setText(font, text);
        setMixedColor(font, 0.55f, 0.9f);
        font.draw(myGdxGame.batch, text, PCT_RIGHT - layout.width, rowY(row) + (ROW_H + layout.height) / 2f);
    }

    private void setMixedColor(BitmapFont font, float t, float alpha) {
        font.setColor(
            CyberFx.ACCENT_DIM.r + (CyberFx.ACCENT.r - CyberFx.ACCENT_DIM.r) * t,
            CyberFx.ACCENT_DIM.g + (CyberFx.ACCENT.g - CyberFx.ACCENT_DIM.g) * t,
            CyberFx.ACCENT_DIM.b + (CyberFx.ACCENT.b - CyberFx.ACCENT_DIM.b) * t,
            alpha * fx.fade());
    }

    private String rowLabel(int i) {
        String key;
        switch (i) {
            case ROW_DIFFICULTY:
                key = "settings.difficulty";
                break;
            case ROW_MUSIC:
                key = "settings.music";
                break;
            case ROW_SOUND:
                key = "settings.sound";
                break;
            case ROW_SUBTITLES:
                key = "settings.subtitles";
                break;
            default:
                key = "settings.language";
                break;
        }
        String text = LocalizationManager.get(key);
        int colon = text.indexOf(':');
        return colon >= 0 ? text.substring(0, colon + 1) : text.trim();
    }

    private String rowValue(int i) {
        switch (i) {
            case ROW_DIFFICULTY:
                return String.valueOf(MemoryManager.loadDifficulty());
            case ROW_MUSIC:
                return state(MemoryManager.loadIsMusicOn());
            case ROW_SOUND:
                return state(MemoryManager.loadIsSoundOn());
            case ROW_SUBTITLES:
                return state(MemoryManager.loadAreSubtitlesOn());
            default:
                return LocalizationManager.getLanguage().name();
        }
    }

    private String state(boolean on) {
        return LocalizationManager.get(on ? "state.on" : "state.off");
    }

    private void drawWidgetAura(ShapeRenderer shapes) {
        for (int i = 0; i < widgets.length; i++) {
            ButtonView b = widgets[i];
            float t = widgetHover[i];
            if (b == null || t <= 0.01f) continue;

            for (int k = 4; k >= 1; k--) {
                float pad = k * 3f * t;
                shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.07f * t);
                fx.frame(b.getX() - pad, b.getY() - pad, b.getWidth() + pad * 2f, b.getHeight() + pad * 2f, 2.5f);
            }
        }
    }

    private void drawWidgets() {
        for (int i = 0; i < widgets.length; i++) {
            ButtonView b = widgets[i];
            if (b == null) continue;
            b.draw(myGdxGame.batch);

            float t = widgetHover[i];
            if (t <= 0.01f || b.getText() == null) continue;

            BitmapFont font = widgetFonts[i];
            savedColor.set(font.getColor());
            layout.setText(font, b.getText());
            font.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, t);
            font.draw(myGdxGame.batch, b.getText(),
                b.getX() + (b.getWidth() - layout.width) / 2f,
                b.getY() + (b.getHeight() + layout.height) / 2f);
            font.setColor(savedColor);
        }
    }

    private void drawWidgetHighlight(ShapeRenderer shapes) {
        for (int i = 0; i < widgets.length; i++) {
            ButtonView b = widgets[i];
            float t = widgetHover[i];
            if (b == null || t <= 0.01f) continue;

            shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.10f * t);
            shapes.rect(b.getX(), b.getY(), b.getWidth(), b.getHeight());
            shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.35f * t);
            shapes.rect(b.getX(), b.getY(), b.getWidth(), 2f);
            shapes.rect(b.getX(), b.getY() + b.getHeight() - 2f, b.getWidth(), 2f);
        }
    }


    private void drawTitle() {
        fx.drawTitle(myGdxGame.batch, myGdxGame.xanmonoFontBig,
            LocalizationManager.get("settings.title"),
            GameSettings.SCREEN_WIDTH / 2f, TITLE_TOP, ROW_W);
    }

    private void drawPrompt() {
        savedColor.set(hudFont.getColor());
        hudFont.setColor(CyberFx.ACCENT_DIM.r, CyberFx.ACCENT_DIM.g, CyberFx.ACCENT_DIM.b, 0.8f * fx.fade());
        hudFont.draw(myGdxGame.batch, promptBuffer, ROW_X, PROMPT_TOP);
        hudFont.setColor(savedColor);
    }

    private void drawCaret(ShapeRenderer shapes) {
        if (promptBuffer.length() < TITLE.length()) return;
        if (((int) (fx.time() * 1.7f)) % 2 != 0) return;
        shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.75f * fx.fade());
        shapes.rect(ROW_X + promptWidth + 5f, PROMPT_TOP - 12f, 9f, 13f);
    }

    private void drawIntroWipe(ShapeRenderer shapes) {
        for (int i = 0; i < ROW_COUNT; i++) {
            float r = reveal(i);
            if (r >= 1f) continue;

            float coverH = ROW_H * (1f - r);
            float coverY = rowY(i) + ROW_H - coverH;
            shapes.setColor(0.02f, 0.06f, 0.05f, 1f);
            shapes.rect(ROW_X - 12f, coverY, ROW_W + 14f, coverH);
            shapes.setColor(CyberFx.ACCENT.r, CyberFx.ACCENT.g, CyberFx.ACCENT.b, 0.85f);
            shapes.rect(ROW_X - 12f, coverY, ROW_W + 14f, 2f);
        }
    }

    void handleInput() {
        if (!Gdx.input.justTouched()) return;

        myGdxGame.touch = myGdxGame.uiCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        float x = myGdxGame.touch.x;
        float y = myGdxGame.touch.y;

        if (returnButton.isHit(x, y)) {
            myGdxGame.setScreen(myGdxGame.menuScreen);
            return;
        }

        if (musicVolumeUpButton.isHit(x, y)) {
            float newVolume = Math.min(1.0f, MemoryManager.loadMusicVolume() + 0.05f);
            MemoryManager.saveMusicVolume(newVolume);
            if (newVolume > 0 && !MemoryManager.loadIsMusicOn()) {
                MemoryManager.saveMusicSettings(true);
                myGdxGame.audioManager.updateMusicFlag();
            }
            return;
        }

        if (musicVolumeDownButton.isHit(x, y)) {
            float newVolume = Math.max(0f, MemoryManager.loadMusicVolume() - 0.05f);
            MemoryManager.saveMusicVolume(newVolume);
            if (newVolume == 0) {
                MemoryManager.saveMusicSettings(false);
                myGdxGame.audioManager.updateMusicFlag();
            }
            return;
        }

        if (soundVolumeUpButton.isHit(x, y)) {
            float newVolume = Math.min(1.0f, MemoryManager.loadSoundVolume() + 0.05f);
            MemoryManager.saveSoundVolume(newVolume);
            if (newVolume > 0 && !MemoryManager.loadIsSoundOn()) {
                MemoryManager.saveSoundSettings(true);
                myGdxGame.audioManager.updateSoundFlag();
            }
            return;
        }

        if (soundVolumeDownButton.isHit(x, y)) {
            float newVolume = Math.max(0f, MemoryManager.loadSoundVolume() - 0.05f);
            MemoryManager.saveSoundVolume(newVolume);
            if (newVolume == 0) {
                MemoryManager.saveSoundSettings(false);
                myGdxGame.audioManager.updateSoundFlag();
            }
            return;
        }

        if (isRowHit(ROW_MUSIC, x, y)) {
            MemoryManager.saveMusicSettings(!MemoryManager.loadIsMusicOn());
            MemoryManager.saveMusicVolume(MemoryManager.loadIsMusicOn() ? 0.5f : 0f);
            myGdxGame.audioManager.updateMusicFlag();
        }

        if (isRowHit(ROW_SOUND, x, y)) {
            MemoryManager.saveSoundSettings(!MemoryManager.loadIsSoundOn());
            MemoryManager.saveSoundVolume(MemoryManager.loadIsSoundOn() ? 0.5f : 0f);
            myGdxGame.audioManager.updateSoundFlag();
        }

        if (isRowHit(ROW_SUBTITLES, x, y)) {
            MemoryManager.saveSubtitlesSettings(!MemoryManager.loadAreSubtitlesOn());
        }

        if (isRowHit(ROW_DIFFICULTY, x, y)) {
            MemoryManager.changeDifficulty();
            myGdxGame.audioManager.updateSoundFlag();
        }

        if (isRowHit(ROW_LANGUAGE, x, y)) {
            LocalizationManager.toggleLanguage();
            MemoryManager.saveLanguage(LocalizationManager.getLanguage());
            returnButton.setText(LocalizationManager.get("settings.return"));
        }
    }

    @Override
    public void show() {
        myGdxGame.uiCamera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        myGdxGame.uiCamera.update();

        returnButton.setText(LocalizationManager.get("settings.return"));

        if (fx != null) fx.reset();
        intro = 0f;
        hoveredRow = -1;
        hoveredWidget = -1;
        promptTimer = 0f;
        promptWidth = 0f;
        promptBuffer.setLength(0);
        for (int i = 0; i < rowHover.length; i++) rowHover[i] = 0f;
        for (int i = 0; i < widgetHover.length; i++) widgetHover[i] = 0f;
    }

    @Override
    public void dispose() {
        if (backgroundView != null) {
            backgroundView.dispose();
            backgroundView = null;
        }
        if (widgets != null) {
            for (ButtonView b : widgets) {
                if (b != null) b.dispose();
            }
            widgets = null;
        }
        musicVolumeDownButton = null;
        musicVolumeUpButton = null;
        soundVolumeDownButton = null;
        soundVolumeUpButton = null;
        returnButton = null;
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
