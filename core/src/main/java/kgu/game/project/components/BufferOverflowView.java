package kgu.game.project.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

import kgu.game.project.FontBuilder;
import kgu.game.project.GameResources;
import kgu.game.project.GameSettings;
import kgu.game.project.MyGdxGame;
import kgu.game.project.managers.LocalizationManager;



public class BufferOverflowView extends View {
    private static final int SLOT_SIZE = 8;
    private static final int SAVED_RBP_SIZE = SLOT_SIZE;
    private static final float WINDOW_X = 60;
    private static final float WINDOW_Y = 40;
    private static final float WINDOW_WIDTH = 1160;
    private static final float WINDOW_HEIGHT = 640;
    private static final float TITLE_HEIGHT = 34;
    private static final float LEFT_X = 78;
    private static final float LEFT_WIDTH = 562;
    private static final float SOURCE_Y = 300;
    private static final float SOURCE_HEIGHT = 332;
    private static final float SYMBOLS_Y = 196;
    private static final float SYMBOLS_HEIGHT = 96;
    private static final float OUTPUT_Y = 58;
    private static final float OUTPUT_HEIGHT = 130;

    private static final float STACK_X = 656;
    private static final float STACK_Y = 196;
    private static final float STACK_WIDTH = 344;
    private static final float STACK_HEIGHT = 436;
    private static final float SLOT_HEIGHT = 46;

    private static final float BUTTONS_X = 1016;
    private static final float BUTTON_WIDTH = 186;
    private static final float BUTTON_HEIGHT = 56;

    private static final float CHIP_WIDTH = 170;
    private static final float CHIP_HEIGHT = 52;

    private static final float LINE_STEP = 22;
    private static final float ADVANCE_DELAY = 2.2f;

    private static final Color DIM = Color.valueOf("#05070AD0");
    private static final Color WINDOW_BG = Color.DARK_GRAY;
    private static final Color PANEL_BG = Color.valueOf("#141922");
    private static final Color BORDER = Color.valueOf("#2E3542");
    private static final Color TITLE_BG = Color.valueOf("#1C212B");

    private static final Color SLOT_BUFFER = Color.valueOf("#15301F");
    private static final Color SLOT_RBP = Color.valueOf("#2A2616");
    private static final Color SLOT_RETURN = Color.valueOf("#361A1C");
    private static final Color SLOT_FILLED = Color.valueOf("#1F4A6B");
    private static final Color SLOT_HIT = Color.valueOf("#1D5C34");
    private static final Color SLOT_MISS = Color.valueOf("#6B3A14");


    private static final Color TEXT = Color.valueOf("#D8DEE9");
    private static final Color TEXT_DIM = Color.valueOf("#7F8792");
    private static final Color CODE = Color.valueOf("#A7C7E7");
    private static final Color WARN = Color.YELLOW;
    private static final Color GOOD = Color.GREEN;
    private static final Color BAD = Color.RED;
    private static final int EMPTY = -1;
    private static final int FILLER = -2;

    private static final class Symbol {
        final String name;
        final long address;
        final String outputKey;

        Symbol(String name, long address, String outputKey) {
            this.name = name;
            this.address = address;
            this.outputKey = outputKey;
        }

        String hex() {
            return String.format("0x%06x", address);
        }
    }

    private static final Symbol WIN = new Symbol("win", 0x401196L, "bof.out.win");
    private static final Symbol LOSE = new Symbol("lose", 0x401172L, "bof.out.lose");
    private static final Symbol ADMIN = new Symbol("admin_panel", 0x4011d4L, "bof.out.admin");
    private static final Symbol FLAG = new Symbol("print_flag", 0x401245L, "bof.out.flag");

    private static final class Task {
        final String bufferName;
        final int bufferSize;
        final Symbol target;
        final String hintKey;
        final Symbol[] symbols;
        final String[] source;

        Task(String bufferName, int bufferSize, Symbol target, String hintKey,
             Symbol[] symbols, String[] source) {
            this.bufferName = bufferName;
            this.bufferSize = bufferSize;
            this.target = target;
            this.hintKey = hintKey;
            this.symbols = symbols;
            this.source = source;
        }

        int offset() {
            return bufferSize + SAVED_RBP_SIZE;
        }

        int bufferSlots() {
            return (bufferSize + SLOT_SIZE - 1) / SLOT_SIZE;
        }

        int totalSlots() {
            return bufferSlots() + 2;
        }
    }

    private static final Task[] TASKS = {
        new Task("name", 8, WIN, "bof.hint.1",
            new Symbol[]{WIN, LOSE},
            new String[]{
                "void login(void) {",
                "    char name[8];",
                "    puts(\"name: \");",
                "    gets(name);           // длину никто не проверяет",
                "    printf(\"hi, %s\", name);",
                "}",
                "",
                "int main(void) { login(); return 0; }",
            }),
        new Task("buf", 24, ADMIN, "bof.hint.2",
            new Symbol[]{ADMIN, WIN, LOSE},
            new String[]{
                "void read_note(void) {",
                "    char buf[24];",
                "    read(0, buf, 128);    // 128 > 24",
                "}",
                "",
                "void win(void);           // печатает \"ok\"",
                "void admin_panel(void);   // печатает список доступа",
            }),
        new Task("dst", 32, FLAG, "bof.hint.3",
            new Symbol[]{FLAG, ADMIN, LOSE},
            new String[]{
                "void parse(char *src) {",
                "    char dst[32];",
                "    strcpy(dst, src);     // strcpy не знает длины",
                "}",
                "",
                "void print_flag(void);    // читает /flag от root",
            }),
    };

    // payload либо байты, либо адрес функции
    private static final class Item {
        final int fillerBytes;
        final Symbol symbol;

        Item(int fillerBytes) {
            this.fillerBytes = fillerBytes;
            this.symbol = null;
        }

        Item(Symbol symbol) {
            this.fillerBytes = 0;
            this.symbol = symbol;
        }

        int size() {
            return symbol != null ? SLOT_SIZE : fillerBytes;
        }
    }

    private final MyGdxGame myGdxGame;
    private final Runnable onSolved;

    private Texture pixel;
    private BitmapFont font;
    private BitmapFont titleFont;

    private final ArrayList<Item> payload = new ArrayList<>();
    private final StringBuilder builder = new StringBuilder();

    private final ButtonView runBtn;
    private final ButtonView add1Btn;
    private final ButtonView add8Btn;
    private final ButtonView eraseBtn;
    private final ButtonView resetBtn;
    private final ButtonView hintBtn;
    private final ButtonView closeBtn;
    private final ButtonView[] chips = new ButtonView[3];
    // фон выключенной кнопки: ButtonView не умеет менять текстуру на лету
    private Texture offTexture;

    private int taskIndex;
    private boolean visible;
    private boolean opened;
    private boolean hintShown;
    private boolean solved;
    private float advanceTimer;

    private String[] output = new String[0];
    private Color outputColor = TEXT_DIM;
    private String taskText;
    private String payloadText = "";
    private int[] stackBytes = new int[0];

    public BufferOverflowView(MyGdxGame myGdxGame, Runnable onSolved) {
        super(WINDOW_X, WINDOW_Y, WINDOW_WIDTH, WINDOW_HEIGHT);
        this.myGdxGame = myGdxGame;
        this.onSolved = onSolved;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();

        font = FontBuilder.generateWithExtraChars(16, Color.WHITE,
            GameResources.CONSOLAS_FONT_PATH, "·");
        titleFont = FontBuilder.generateWithExtraChars(20, Color.WHITE,
            GameResources.CONSOLAS_FONT_PATH, "·");

        // кнопки стоят одной колонкой справа, сверху вниз
        float row = STACK_Y + STACK_HEIGHT - BUTTON_HEIGHT;
        float step = BUTTON_HEIGHT + 10;
        offTexture = new Texture(GameResources.BOF_BTN_OFF_IMG_PATH);
        runBtn = new ButtonView(BUTTONS_X, row, BUTTON_WIDTH, BUTTON_HEIGHT,
            titleFont, GameResources.BOF_BTN_RUN_IMG_PATH, "");
        add1Btn = new ButtonView(BUTTONS_X, row - step, BUTTON_WIDTH, BUTTON_HEIGHT,
            font, GameResources.BOF_BTN_DEFAULT_IMG_PATH, "");
        add8Btn = new ButtonView(BUTTONS_X, row - 2 * step, BUTTON_WIDTH, BUTTON_HEIGHT,
            font, GameResources.BOF_BTN_DEFAULT_IMG_PATH, "");
        eraseBtn = new ButtonView(BUTTONS_X, row - 3 * step, BUTTON_WIDTH, BUTTON_HEIGHT,
            font, GameResources.BOF_BTN_DEFAULT_IMG_PATH, "");
        resetBtn = new ButtonView(BUTTONS_X, row - 4 * step, BUTTON_WIDTH, BUTTON_HEIGHT,
            font, GameResources.BOF_BTN_DEFAULT_IMG_PATH, "");
        hintBtn = new ButtonView(BUTTONS_X, row - 5 * step, BUTTON_WIDTH, BUTTON_HEIGHT,
            font, GameResources.BOF_BTN_DEFAULT_IMG_PATH, "");

        for (int i = 0; i < chips.length; i++) {
            chips[i] = new ButtonView(STACK_X + i * (CHIP_WIDTH + 12), OUTPUT_Y + 8,
                CHIP_WIDTH, CHIP_HEIGHT, font, GameResources.BOF_BTN_CHIP_IMG_PATH, "");
        }
        closeBtn = new ButtonView(WINDOW_X + WINDOW_WIDTH - 112,
            WINDOW_Y + WINDOW_HEIGHT - TITLE_HEIGHT + 3, 104, TITLE_HEIGHT - 6,
            font, GameResources.BOF_BTN_CLOSE_IMG_PATH, "");

        updateLabels();
        loadTask();
    }

    public void show() {
        if (visible) return;
        visible = true;
        // прогресс не сбрасываем: игрок может отойти от ноутбука и вернуться
        if (!opened) {
            opened = true;
            loadTask();
        }
        // язык могли переключить в настройках, пока окно было закрыто
        updateLabels();
        payloadText = payloadToText();
    }

    public void hide() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    // так же называется у TerminalView: уровень смотрит именно этот флаг
    public boolean isFlagRead() {
        return solved;
    }

    public void setMission(String missionText) {
        this.taskText = missionText;
    }

    public void update(float delta) {
        if (!visible || advanceTimer <= 0) return;

        advanceTimer -= delta;
        if (advanceTimer > 0) return;

        if (taskIndex + 1 < TASKS.length) {
            taskIndex++;
            loadTask();
            return;
        }
        solved = true;
        say(GOOD,
            LocalizationManager.get("bof.out.all_done"),
            String.format(LocalizationManager.get("bof.out.flag_line"), "0v3rfl0w"));
        if (onSolved != null) onSolved.run();
    }

    public void handleTouch() {
        if (!visible) return;

        Task task = TASKS[taskIndex];
        // пока играет пауза после верного ответа, кнопки не слушаем
        boolean locked = advanceTimer > 0 || solved;

        if (!locked && isDesktopShortcut(task)) return;
        if (!Gdx.input.justTouched()) return;

        float tx = myGdxGame.touch.x;
        float ty = myGdxGame.touch.y;

        if (closeBtn.isHit(tx, ty)) {
            hide();
            return;
        }
        if (locked) return;

        if (runBtn.isHit(tx, ty)) {
            run();
        } else if (add1Btn.isHit(tx, ty)) {
            append(new Item(1));
        } else if (add8Btn.isHit(tx, ty)) {
            append(new Item(SLOT_SIZE));
        } else if (eraseBtn.isHit(tx, ty)) {
            eraseLast();
        } else if (resetBtn.isHit(tx, ty)) {
            payload.clear();
            say(TEXT_DIM);
            payloadChanged();
        } else if (hintBtn.isHit(tx, ty)) {
            hintShown = true;
            say(WARN, LocalizationManager.get(task.hintKey));
        } else {
            for (int i = 0; i < task.symbols.length && i < chips.length; i++) {
                if (chips[i].isHit(tx, ty)) {
                    append(new Item(task.symbols[i]));
                    return;
                }
            }
        }
    }

    // Enter, Backspace и цифры 1-3 на десктопе, чтобы не целиться мышью
    private boolean isDesktopShortcut(Task task) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            run();
            return true;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            eraseLast();
            return true;
        }
        for (int i = 0; i < task.symbols.length && i < chips.length; i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                append(new Item(task.symbols[i]));
                return true;
            }
        }
        return false;
    }

    private void updateLabels() {
        runBtn.setText(LocalizationManager.get("bof.run"));
        add1Btn.setText(LocalizationManager.get("bof.add1"));
        add8Btn.setText(LocalizationManager.get("bof.add8"));
        eraseBtn.setText(LocalizationManager.get("bof.erase"));
        resetBtn.setText(LocalizationManager.get("bof.reset"));
        hintBtn.setText(LocalizationManager.get("bof.hint_btn"));
        closeBtn.setText(LocalizationManager.get("bof.close"));
    }

    private void loadTask() {
        payload.clear();
        hintShown = false;
        advanceTimer = 0;
        say(TEXT_DIM, LocalizationManager.get("bof.out.ready"));
        payloadChanged();
    }

    private void say(Color color, String... lines) {
        output = lines;
        outputColor = color;
    }

    private void append(Item item) {
        // за кадр стека пустить можно, но не бесконечно - иначе не видно перелёта
        if (payloadSize() + item.size() > capacity()) {
            say(WARN, LocalizationManager.get("bof.out.too_long"));
            return;
        }
        payload.add(item);
        payloadChanged();
    }

    private void eraseLast() {
        if (payload.isEmpty()) return;
        payload.remove(payload.size() - 1);
        payloadChanged();
    }

    private int payloadSize() {
        return paddingBefore(payload.size());
    }

    private int capacity() {
        return TASKS[taskIndex].totalSlots() * SLOT_SIZE + SLOT_SIZE;
    }

    // сколько байт стоит до элемента payload с этим индексом
    private int paddingBefore(int itemIndex) {
        int size = 0;
        for (int i = 0; i < itemIndex; i++) {
            size += payload.get(i).size();
        }
        return size;
    }

    private int firstAddressIndex() {
        for (int i = 0; i < payload.size(); i++) {
            if (payload.get(i).symbol != null) return i;
        }
        return -1;
    }

    // запуск программы, вся соль уровня тут
    // адрес должен лечь ровно на ячейку адреса возврата, иначе segfault
    private void run() {
        Task task = TASKS[taskIndex];
        int addressIndex = firstAddressIndex();

        if (addressIndex < 0) {
            int total = payloadSize();
            if (total <= task.bufferSize) {
                say(TEXT_DIM,
                    String.format(LocalizationManager.get("bof.out.fits"), total, task.bufferSize),
                    LocalizationManager.get("bof.out.returned"));
            } else {
                say(BAD,
                    LocalizationManager.get("bof.out.segv"),
                    LocalizationManager.get("bof.out.segv_garbage"));
            }
            return;
        }

        Symbol symbol = payload.get(addressIndex).symbol;
        int padding = paddingBefore(addressIndex);
        int offset = task.offset();

        if (padding < offset) {
            say(BAD,
                LocalizationManager.get("bof.out.segv"),
                padding < task.bufferSize
                    ? String.format(LocalizationManager.get("bof.out.in_buffer"), task.bufferName)
                    : LocalizationManager.get("bof.out.in_rbp"),
                String.format(LocalizationManager.get("bof.out.need_more"), offset - padding));
            return;
        }
        if (padding > offset) {
            say(BAD,
                LocalizationManager.get("bof.out.segv"),
                String.format(LocalizationManager.get("bof.out.overshoot"), padding - offset));
            return;
        }

        String hijacked = String.format(LocalizationManager.get("bof.out.hijacked"), symbol.name);
        if (symbol == task.target) {
            say(GOOD, hijacked, LocalizationManager.get(symbol.outputKey),
                LocalizationManager.get("bof.out.solved"));
            advanceTimer = ADVANCE_DELAY;
        } else {
            // перехват удался, но прыгнули не туда, куда просили
            say(WARN, hijacked, LocalizationManager.get(symbol.outputKey),
                LocalizationManager.get("bof.out.wrong_target"));
        }
    }

    private void payloadChanged() {
        payloadText = payloadToText();

        Task task = TASKS[taskIndex];
        if (stackBytes.length != task.totalSlots() * SLOT_SIZE) {
            stackBytes = new int[task.totalSlots() * SLOT_SIZE];
        }
        for (int i = 0; i < stackBytes.length; i++) {
            stackBytes[i] = EMPTY;
        }
        // payload ложится снизу вверх, от начала буфера к адресу возврата
        int cursor = 0;
        for (int i = 0; i < payload.size(); i++) {
            Item item = payload.get(i);
            for (int b = 0; b < item.size() && cursor < stackBytes.length; b++) {
                stackBytes[cursor++] = item.symbol != null ? i : FILLER;
            }
        }
    }

    // payload как его написали бы в pwntools: b'A'*16 + p64(0x401196)
    private String payloadToText() {
        if (payload.isEmpty()) {
            return LocalizationManager.get("bof.payload_empty");
        }

        builder.setLength(0);
        int fillerRun = 0;
        for (int i = 0; i < payload.size(); i++) {
            Item item = payload.get(i);
            if (item.symbol == null) {
                fillerRun += item.fillerBytes;
                continue;
            }
            if (fillerRun > 0) {
                appendPlus();
                builder.append("b'A'*").append(fillerRun);
                fillerRun = 0;
            }
            appendPlus();
            builder.append("p64(").append(item.symbol.hex()).append(")");
        }
        if (fillerRun > 0) {
            appendPlus();
            builder.append("b'A'*").append(fillerRun);
        }
        return builder.toString();
    }

    private void appendPlus() {
        if (builder.length() > 0) builder.append(" + ");
    }

    public void draw(SpriteBatch batch) {
        if (!visible) return;

        Task task = TASKS[taskIndex];

        rect(batch, 0, 0, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT, DIM);
        rect(batch, WINDOW_X, WINDOW_Y, WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_BG);
        frame(batch, WINDOW_X, WINDOW_Y, WINDOW_WIDTH, WINDOW_HEIGHT, BORDER);

        drawTitle(batch);
        drawSource(batch, task);
        drawSymbols(batch, task);
        drawOutput(batch);
        drawStack(batch, task);
        drawButtons(batch);
        drawPayload(batch, task);

        batch.setColor(Color.WHITE);
    }

    private void drawTitle(SpriteBatch batch) {
        float titleY = WINDOW_Y + WINDOW_HEIGHT - TITLE_HEIGHT;
        rect(batch, WINDOW_X, titleY, WINDOW_WIDTH, TITLE_HEIGHT, TITLE_BG);
        text(batch, titleFont, TEXT, LocalizationManager.get("bof.title"),
            WINDOW_X + 14, titleY + 24);
        text(batch, font, TEXT_DIM,
            String.format(LocalizationManager.get("bof.task"), taskIndex + 1, TASKS.length),
            WINDOW_X + 420, titleY + 23);
        text(batch, font, GOOD, taskText, WINDOW_X + 580, titleY + 23);
        button(batch, closeBtn, true, font, TEXT);
    }

    private void drawSource(SpriteBatch batch, Task task) {
        panel(batch, LEFT_X, SOURCE_Y, LEFT_WIDTH, SOURCE_HEIGHT, "bof.source");

        float lineY = SOURCE_Y + SOURCE_HEIGHT - 40;
        for (int i = 0; i < task.source.length; i++) {
            String line = task.source[i];
            text(batch, font, TEXT_DIM, String.format("%2d", i + 1), LEFT_X + 12, lineY);
            // строки с комментарием подсвечиваем: там и спрятана уязвимость
            text(batch, font, line.contains("//") ? WARN : CODE, line, LEFT_X + 48, lineY);
            lineY -= LINE_STEP;
        }
    }

    private void drawSymbols(SpriteBatch batch, Task task) {
        panel(batch, LEFT_X, SYMBOLS_Y, LEFT_WIDTH, SYMBOLS_HEIGHT, "bof.symbols");

        float lineY = SYMBOLS_Y + SYMBOLS_HEIGHT - 36;
        for (Symbol symbol : task.symbols) {
            text(batch, font, CODE, symbol.hex() + "  " + symbol.name + "()", LEFT_X + 12, lineY);
            lineY -= LINE_STEP;
        }
    }

    private void drawOutput(SpriteBatch batch) {
        panel(batch, LEFT_X, OUTPUT_Y, LEFT_WIDTH, OUTPUT_HEIGHT, "bof.output");

        float lineY = OUTPUT_Y + OUTPUT_HEIGHT - 38;
        for (String line : output) {
            text(batch, font, outputColor, line, LEFT_X + 12, lineY);
            lineY -= LINE_STEP;
        }
    }

    // ячейки рисуем сверху вниз, наверху адрес возврата, внизу начало буфера.
    private void drawStack(SpriteBatch batch, Task task) {
        panel(batch, STACK_X, STACK_Y, STACK_WIDTH, STACK_HEIGHT, "bof.stack");

        float slotY = STACK_Y + STACK_HEIGHT - 36 - SLOT_HEIGHT;
        for (int slot = task.totalSlots() - 1; slot >= 0; slot--) {
            drawSlot(batch, task, slot, slotY);
            slotY -= SLOT_HEIGHT + 6;
        }

        int beyond = payloadSize() - task.totalSlots() * SLOT_SIZE;
        if (beyond > 0) {
            text(batch, font, BAD,
                String.format(LocalizationManager.get("bof.beyond"), beyond),
                STACK_X + 12, STACK_Y + 20);
        }
    }

    private void drawSlot(SpriteBatch batch, Task task, int slot, float slotY) {
        boolean isRbp = slot == task.bufferSlots();
        boolean isReturn = slot == task.bufferSlots() + 1;

        int firstByte = slot * SLOT_SIZE;
        int addressIndex = -1;
        int usedBytes = 0;
        for (int b = 0; b < SLOT_SIZE; b++) {
            int owner = stackBytes[firstByte + b];
            if (owner >= 0) {
                addressIndex = owner;
            }
            if (owner != EMPTY) {
                usedBytes++;
            }
        }

        Color background;
        if (addressIndex >= 0) {
            // зелёный только если адрес лёг на адрес возврата
            boolean onTarget = isReturn && paddingBefore(addressIndex) == task.offset();
            background = onTarget ? SLOT_HIT : SLOT_MISS;
        } else if (usedBytes > 0) {
            background = SLOT_FILLED;
        } else if (isReturn) {
            background = SLOT_RETURN;
        } else if (isRbp) {
            background = SLOT_RBP;
        } else {
            background = SLOT_BUFFER;
        }

        float slotX = STACK_X + 12;
        float slotWidth = STACK_WIDTH - 24;
        rect(batch, slotX, slotY, slotWidth, SLOT_HEIGHT, background);
        frame(batch, slotX, slotY, slotWidth, SLOT_HEIGHT, isReturn ? BAD : BORDER);

        String name;
        if (isReturn) {
            name = LocalizationManager.get("bof.ret_addr");
        } else if (isRbp) {
            name = LocalizationManager.get("bof.saved_rbp");
        } else {
            int lastByte = Math.min(firstByte + SLOT_SIZE, task.bufferSize) - 1;
            name = task.bufferName + "[" + firstByte + ".." + lastByte + "]";
        }
        text(batch, font, isReturn ? BAD : TEXT_DIM, name, slotX + 10, slotY + 30);

        String content;
        Color contentColor;
        if (addressIndex >= 0) {
            content = payload.get(addressIndex).symbol.hex();
            contentColor = GOOD;
        } else {
            builder.setLength(0);
            for (int b = 0; b < SLOT_SIZE; b++) {
                builder.append(stackBytes[firstByte + b] == EMPTY ? '·' : 'A');
            }
            content = builder.toString();
            contentColor = usedBytes > 0 ? TEXT : TEXT_DIM;
        }
        text(batch, font, contentColor, content, STACK_X + STACK_WIDTH - 110, slotY + 30);
    }

    private void drawButtons(SpriteBatch batch) {
        boolean locked = advanceTimer > 0 || solved;
        boolean filled = !payload.isEmpty();

        button(batch, runBtn, !locked, titleFont, TEXT);
        button(batch, add1Btn, !locked, font, TEXT);
        button(batch, add8Btn, !locked, font, TEXT);
        button(batch, eraseBtn, !locked && filled, font, TEXT);
        button(batch, resetBtn, !locked && filled, font, TEXT);
        button(batch, hintBtn, !locked, font, hintShown ? WARN : TEXT);

        // счётчик байт под кнопками, его игрок и сверяет со смещением
        text(batch, font, TEXT_DIM,
            String.format(LocalizationManager.get("bof.bytes"), payloadSize()),
            BUTTONS_X, hintBtn.getY() - 14);
    }

    private void drawPayload(SpriteBatch batch, Task task) {
        text(batch, font, TEXT_DIM, LocalizationManager.get("bof.payload"),
            STACK_X, OUTPUT_Y + OUTPUT_HEIGHT - 10);
        text(batch, font, CODE, payloadText, STACK_X, OUTPUT_Y + OUTPUT_HEIGHT - 36);
        text(batch, font, TEXT_DIM, LocalizationManager.get("bof.tap_address"),
            STACK_X, OUTPUT_Y + CHIP_HEIGHT + 18);

        boolean locked = advanceTimer > 0 || solved;
        for (int i = 0; i < chips.length; i++) {
            if (i >= task.symbols.length) {
                chips[i].hide();
                continue;
            }
            String label = (i + 1) + ". " + task.symbols[i].name + "()";
            // setText пересчитывает GlyphLayout, поэтому зовём его только при смене задания
            if (!label.equals(chips[i].getText())) {
                chips[i].setText(label);
            }
            button(batch, chips[i], !locked, font, CODE);
        }
    }

    private void panel(SpriteBatch batch, float x, float y, float width, float height,
                       String titleKey) {
        rect(batch, x, y, width, height, PANEL_BG);
        frame(batch, x, y, width, height, BORDER);
        text(batch, font, TEXT_DIM, LocalizationManager.get(titleKey), x + 12, y + height - 10);
    }

    private void rect(SpriteBatch batch, float x, float y, float width, float height, Color color) {
        batch.setColor(color);
        batch.draw(pixel, x, y, width, height);
        batch.setColor(Color.WHITE);
    }

    private void frame(SpriteBatch batch, float x, float y, float width, float height, Color color) {
        batch.setColor(color);
        batch.draw(pixel, x, y, width, 1);
        batch.draw(pixel, x, y + height - 1, width, 1);
        batch.draw(pixel, x, y, 1, height);
        batch.draw(pixel, x + width - 1, y, 1, height);
        batch.setColor(Color.WHITE);
    }

    private void button(SpriteBatch batch, ButtonView btn, boolean enabled,
                        BitmapFont btnFont, Color textColor) {
        if (enabled) {
            btn.show();
            btnFont.setColor(textColor);
            btn.draw(batch);
            btnFont.setColor(Color.WHITE);
            return;
        }
        btn.hide();
        batch.draw(offTexture, btn.getX(), btn.getY(), btn.getWidth(), btn.getHeight());
        text(batch, btnFont, TEXT_DIM, btn.getText(), btn.textX, btn.textY);
    }

    private void text(SpriteBatch batch, BitmapFont textFont, Color color, String value,
                      float x, float y) {
        if (value == null){
            return;
        }
        textFont.setColor(color);
        textFont.draw(batch, value, x, y);
        textFont.setColor(Color.WHITE);
    }

    @Override
    public void dispose() {
        runBtn.dispose();
        add1Btn.dispose();
        add8Btn.dispose();
        eraseBtn.dispose();
        resetBtn.dispose();
        hintBtn.dispose();
        closeBtn.dispose();
        for (ButtonView chip : chips) {
            chip.dispose();
        }
        if (offTexture != null) {
            offTexture.dispose();
            offTexture = null;
        }
        if (pixel != null) {
            pixel.dispose();
            pixel = null;
        }
        if (font != null) {
            font.dispose();
            font = null;
        }
        if (titleFont != null) {
            titleFont.dispose();
            titleFont = null;
        }
    }
}
