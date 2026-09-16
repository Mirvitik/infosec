package kgu.game.project.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

import kgu.game.project.FontBuilder;
import kgu.game.project.GameResources;
import kgu.game.project.GameSettings;
import kgu.game.project.MyGdxGame;
import kgu.game.project.managers.LocalizationManager;
import kgu.game.project.terminal.KaliShell;

public class TerminalView extends View implements InputProcessor {

    private static final float WINDOW_X = 140;
    private static final float WINDOW_Y = 60;
    private static final float WINDOW_WIDTH = 1000;
    private static final float WINDOW_HEIGHT = 580;
    private static final float TITLE_BAR_HEIGHT = 32;
    private static final float MENU_BAR_HEIGHT = 26;
    private static final float CLOSE_BUTTON_SIZE = 24;
    private static final float CLOSE_BUTTON_TOUCH_MARGIN = 8;
    private static final float PADDING = 12;
    private static final int FONT_SIZE = 16;
    private static final int MAX_INPUT_LENGTH = 200;
    private static final int MAX_SCROLLBACK = 500;
    private static final String MENU_TEXT = "File   Actions   Edit   View   Help";

    private static final Color BORDER_COLOR = Color.valueOf("#3A3F4B");
    private static final Color WINDOW_COLOR = Color.valueOf("#101217F2");
    private static final Color TITLE_BAR_COLOR = Color.valueOf("#22252D");
    private static final Color MENU_BAR_COLOR = Color.valueOf("#1A1C22");
    private static final Color CLOSE_BUTTON_COLOR = Color.valueOf("#A93B3B");
    private static final Color TITLE_TEXT_COLOR = Color.valueOf("#C8CCD4");
    private static final Color MENU_TEXT_COLOR = Color.valueOf("#8B919C");
    private static final Color TEXT_COLOR = Color.valueOf("#D8DEE9");
    private static final String CURSOR_COLOR = "#D8DEE9";
    private static final String HINT_COLOR = "#7F8792";

    private final MyGdxGame myGdxGame;
    private final KaliShell shell = new KaliShell();
    private final BitmapFont font;
    private final Texture pixel;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    private final ArrayList<String> lines = new ArrayList<>();
    private final StringBuilder input = new StringBuilder();
    private int historyIndex;
    private int scrollOffset;

    private final float lineHeight;
    private final int columns;
    private final int rows;
    private final float closeButtonX;
    private final float closeButtonY;

    private boolean isVisible = false;
    private boolean suppressTyping = false;
    private boolean showCursor = true;
    private float blinkTimer = 0;
    private int lastDragY;
    private float dragAccumulator;

    public TerminalView(MyGdxGame myGdxGame) {
        super(WINDOW_X, WINDOW_Y, WINDOW_WIDTH, WINDOW_HEIGHT);
        this.myGdxGame = myGdxGame;
        // добавление в шрифт специальных символоы для командной строки
        font = FontBuilder.generateWithExtraChars(FONT_SIZE, Color.WHITE, GameResources.FONT_PATH_HACK_TEXT, "┌└─█×");
        // позволяем менять цвета отдельных символов, например, для команды grep в терминале
        font.getData().markupEnabled = true;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();
        float charWidth = font.getData().getGlyph('M').xadvance * font.getData().scaleX;
        lineHeight = font.getLineHeight();
        columns = (int) ((width - 2 * PADDING) / charWidth);
        rows = (int) ((height - TITLE_BAR_HEIGHT - MENU_BAR_HEIGHT - 2 * PADDING) / lineHeight);
        shell.setColumns(columns);

        closeButtonX = x + width - CLOSE_BUTTON_SIZE - 6;
        closeButtonY = y + height - TITLE_BAR_HEIGHT + (TITLE_BAR_HEIGHT - CLOSE_BUTTON_SIZE) / 2;

        resetScreen();
    }

    public void show() {
        if (isVisible) return;
        isVisible = true;
        scrollOffset = 0;
        historyIndex = shell.getHistory().size();
        suppressTyping = Gdx.input.isKeyPressed(Input.Keys.ANY_KEY);
        resetCursorBlink();
        Gdx.input.setInputProcessor(this);
    }

    public void hide() {
        if (!isVisible) return;
        isVisible = false;
        Gdx.input.setOnscreenKeyboardVisible(false);
        Gdx.input.setInputProcessor(null);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void update(float delta) {
        if (!isVisible) return;
        if (suppressTyping && !Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) suppressTyping = false;
        blinkTimer += delta;
        if (blinkTimer >= 0.5f) {
            blinkTimer = 0;
            showCursor = !showCursor;
        }
    }

    public void handleTouch() {
        if (!isVisible || !Gdx.input.justTouched()) return;

        Vector3 touch = myGdxGame.uiCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        if (isCloseButtonHit(touch.x, touch.y)) {
            hide();
        } else if (isHit(touch.x, touch.y)) {
            // даём пользователям на смартфонах возможность вводить текст в терминал через клавиатуру
            Gdx.input.setOnscreenKeyboardVisible(true);
        }
    }

    private boolean isCloseButtonHit(float tx, float ty) {
        return tx >= closeButtonX - CLOSE_BUTTON_TOUCH_MARGIN
            && tx <= closeButtonX + CLOSE_BUTTON_SIZE + CLOSE_BUTTON_TOUCH_MARGIN
            && ty >= closeButtonY - CLOSE_BUTTON_TOUCH_MARGIN
            && ty <= closeButtonY + CLOSE_BUTTON_SIZE + CLOSE_BUTTON_TOUCH_MARGIN;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isVisible) return;

        float top = y + height;
        float menuBarTop = top - TITLE_BAR_HEIGHT;
        fillRect(batch, BORDER_COLOR, x - 2, y - 2, width + 4, height + 4);
        fillRect(batch, WINDOW_COLOR, x, y, width, height);
        fillRect(batch, TITLE_BAR_COLOR, x, menuBarTop, width, TITLE_BAR_HEIGHT);
        fillRect(batch, MENU_BAR_COLOR, x, menuBarTop - MENU_BAR_HEIGHT, width, MENU_BAR_HEIGHT);
        fillRect(batch, CLOSE_BUTTON_COLOR, closeButtonX, closeButtonY, CLOSE_BUTTON_SIZE, CLOSE_BUTTON_SIZE);
        batch.setColor(Color.WHITE);

        float capHeight = font.getCapHeight();
        font.setColor(TITLE_TEXT_COLOR);
        String title = KaliShell.escape(shell.getWindowTitle());
        glyphLayout.setText(font, title);
        font.draw(batch, title, x + (width - glyphLayout.width) / 2, top - (TITLE_BAR_HEIGHT - capHeight) / 2);
        glyphLayout.setText(font, "×");
        font.draw(batch, "×", closeButtonX + (CLOSE_BUTTON_SIZE - glyphLayout.width) / 2,
            closeButtonY + (CLOSE_BUTTON_SIZE + capHeight) / 2);

        font.setColor(MENU_TEXT_COLOR);
        font.draw(batch, MENU_TEXT, x + PADDING, menuBarTop - (MENU_BAR_HEIGHT - capHeight) / 2);

        font.setColor(TEXT_COLOR);
        List<String> prompt = promptLines();
        int total = lines.size() + prompt.size();
        scrollOffset = MathUtils.clamp(scrollOffset, 0, Math.max(0, total - rows));
        int end = total - scrollOffset;
        int start = Math.max(0, end - rows);
        float lineY = menuBarTop - MENU_BAR_HEIGHT - PADDING;
        for (int i = start; i < end; i++) {
            String line = i < lines.size() ? lines.get(i) : prompt.get(i - lines.size());
            font.draw(batch, line, x + PADDING, lineY);
            lineY -= lineHeight;
        }
        font.setColor(Color.WHITE);
    }

    private List<String> promptLines() {
        List<String> prompt = new ArrayList<>(KaliShell.wrap(shell.getPromptHeader(), columns));
        String cursor = showCursor ? KaliShell.color(CURSOR_COLOR, "█") : " ";
        prompt.addAll(KaliShell.wrap(shell.getPromptPrefix() + KaliShell.escape(input.toString()) + cursor, columns));
        return prompt;
    }

    private void fillRect(SpriteBatch batch, Color color, float rectX, float rectY, float rectWidth, float rectHeight) {
        batch.setColor(color);
        batch.draw(pixel, rectX, rectY, rectWidth, rectHeight);
    }

    private void submit() {
        String command = input.toString();
        addLine(shell.getPromptHeader());
        addLine(shell.getPromptPrefix() + KaliShell.escape(command));

        List<String> output = shell.execute(command);
        if (shell.consumeClearRequest()) {
            lines.clear();
        } else {
            for (String line : output) addLine(line);
            addLine("");
        }

        input.setLength(0);
        historyIndex = shell.getHistory().size();
        scrollOffset = 0;

        if (shell.consumeExitRequest()) {
            resetScreen();
            hide();
        }
    }

    private void addLine(String markup) {
        lines.addAll(KaliShell.wrap(markup, columns));
        if (lines.size() > MAX_SCROLLBACK) {
            lines.subList(0, lines.size() - MAX_SCROLLBACK).clear();
        }
    }

    private void resetScreen() {
        lines.clear();
        addLine(KaliShell.color(HINT_COLOR, LocalizationManager.get("level7.terminal_hint")));
        addLine("");
    }

    private void browseHistory(int direction) {
        List<String> history = shell.getHistory();
        historyIndex = MathUtils.clamp(historyIndex + direction, 0, history.size());
        replaceInput(historyIndex < history.size() ? history.get(historyIndex) : "");
    }

    private void replaceInput(String text) {
        input.setLength(0);
        input.append(text.length() > MAX_INPUT_LENGTH ? text.substring(0, MAX_INPUT_LENGTH) : text);
    }

    private void scroll(int deltaLines) {
        scrollOffset = Math.max(0, scrollOffset + deltaLines);
    }

    private void resetCursorBlink() {
        blinkTimer = 0;
        showCursor = true;
    }

    @Override
    public void dispose() {
        font.dispose();
        pixel.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!isVisible || suppressTyping) return false;
        switch (keycode) {
            case Input.Keys.BACKSPACE:
                if (input.length() > 0) input.deleteCharAt(input.length() - 1);
                break;
            case Input.Keys.ENTER:
            case Input.Keys.NUMPAD_ENTER:
                submit();
                break;
            case Input.Keys.TAB:
                replaceInput(shell.complete(input.toString()));
                break;
            case Input.Keys.UP:
                browseHistory(-1);
                break;
            case Input.Keys.DOWN:
                browseHistory(1);
                break;
            case Input.Keys.PAGE_UP:
                scroll(rows / 2);
                break;
            case Input.Keys.PAGE_DOWN:
                scroll(-rows / 2);
                break;
            default:
                return false;
        }
        resetCursorBlink();
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (!isVisible || suppressTyping) return false;
        if (character < 32 || character == 127) return false;
        if (input.length() < MAX_INPUT_LENGTH && font.getData().hasGlyph(character)) {
            input.append(character);
            scrollOffset = 0;
            resetCursorBlink();
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        lastDragY = screenY;
        dragAccumulator = 0;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!isVisible) return false;
        float linePixels = lineHeight * Gdx.graphics.getHeight() / GameSettings.SCREEN_HEIGHT;
        dragAccumulator += screenY - lastDragY;
        lastDragY = screenY;
        int deltaLines = (int) (dragAccumulator / linePixels);
        if (deltaLines != 0) {
            scroll(deltaLines);
            dragAccumulator -= deltaLines * linePixels;
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (!isVisible) return false;
        if (amountY > 0) scroll(-3);
        if (amountY < 0) scroll(3);
        return true;
    }
}
