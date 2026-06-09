package kgu.game.project.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import kgu.game.project.MyGdxGame;

public class AsciiTableView {
    private ShapeRenderer shapeRenderer;
    private boolean visible;
    private float x, y;
    private float width, height;
    private float startX, startY;

    // Константы
    private static final int START_CHAR = 32;  // Пробел
    private static final int END_CHAR = 126;   // '~'
    private static final int COLS = 4;         // 4 колонки для компактности

    public AsciiTableView() {
        this.shapeRenderer = new ShapeRenderer();
        this.visible = false;
        this.x = 50;
        this.y = 100;
        this.width = 700;
        this.height = 500;
        this.startX = x + 10;
        this.startY = y + height - 20;
    }

    /**
     * Главный метод отрисовки
     */
    public void draw(SpriteBatch batch) {
        if (!visible) return;
        BitmapFont font = MyGdxGame.arialFontGray;
        // Завершаем batch для рисования ShapeRenderer
        batch.end();

        // Рисуем фон
        drawBackground();

        // Начинаем batch для текста
        batch.begin();

        // Рисуем заголовки
        drawHeaders(batch, font);

        // Рисуем строки с ASCII символами
        drawAsciiRows(batch, font);

        // Рисуем инструкцию
        drawInstructions(batch, font);

        batch.end();
    }

    /**
     * Рисует фон таблицы
     */
    private void drawBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Основной фон
        shapeRenderer.setColor(new Color(0, 0, 0, 0.85f));
        shapeRenderer.rect(x, y, width, height);
        // Рамка
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
    }

    /**
     * Рисует заголовки колонок
     */
    private void drawHeaders(SpriteBatch batch, BitmapFont font) {
        font.setColor(Color.YELLOW);
        font.draw(batch, "DEC", startX + 50, startY);
        font.draw(batch, "BIN", startX + 150, startY);
        font.draw(batch, "CHAR", startX + 250, startY);
        font.draw(batch, "DESC", startX + 320, startY);

        // Разделитель
        font.setColor(Color.GRAY);
        font.draw(batch, "--------------------------------------------", startX, startY - 15);
    }

    /**
     * Рисует строки с ASCII символами
     */
    private void drawAsciiRows(SpriteBatch batch, BitmapFont font) {
        int row = 0;
        int col = 0;
        float currentX = startX;
        float currentY = startY - 40;

        for (int code = START_CHAR; code <= END_CHAR; code++) {
            // Переход на новую колонку
            if (col >= COLS) {
                col = 0;
                currentX = startX;
                currentY -= 120;
                row++;
            }

            // Если вышли за пределы видимой области, прекращаем рисовать
            if (currentY < y + 20) {
                break;
            }

            // Рисуем одну строку
            drawAsciiRow(batch, font, code, currentX, currentY);

            currentX += 400;
            col++;
        }
    }

    /**
     * Рисует одну строку таблицы
     */
    private void drawAsciiRow(SpriteBatch batch, BitmapFont font, int code, float x, float y) {
        char c = (char) code;

        // Десятичный код
        font.setColor(getColorForAscii(code));
        font.draw(batch, String.format("%3d", code), x + 50, y);

        // Бинарный код
        String binary = String.format("%8s", Integer.toBinaryString(code)).replace(' ', '0');
        font.draw(batch, binary, x + 150, y);

        // Символ
        String charStr = getCharDisplay(c);
        font.setColor(getColorForChar(code));
        font.draw(batch, charStr, x + 250, y);

        // Описание
        font.setColor(Color.WHITE);
        font.draw(batch, getDescription(code, c), x + 320, y);
    }

    /**
     * Рисует инструкцию по закрытию
     */
    private void drawInstructions(SpriteBatch batch, BitmapFont font) {
        font.setColor(Color.RED);
        font.draw(batch, "Press ESC to close", x + width - 150, y + 15);
        font.setColor(Color.WHITE);
    }

    /**
     * Возвращает отображаемый символ
     */
    private String getCharDisplay(char c) {
        if (c == ' ') return "[SPC]";
        if (c == '\t') return "[TAB]";
        if (c == '\n') return "[LF]";
        return String.valueOf(c);
    }

    /**
     * Возвращает цвет для десятичного кода
     */
    private Color getColorForAscii(int code) {
        if (code >= 48 && code <= 57) return Color.CYAN;      // Цифры
        if (code >= 65 && code <= 90) return Color.GREEN;     // Заглавные
        if (code >= 97 && code <= 122) return Color.ORANGE;   // Строчные
        if (code == 32) return Color.GRAY;                    // Пробел
        return Color.PINK;                                     // Символы
    }

    /**
     * Возвращает цвет для символа
     */
    private Color getColorForChar(int code) {
        if (code >= 65 && code <= 90) return Color.GREEN;
        if (code >= 97 && code <= 122) return Color.ORANGE;
        if (code >= 48 && code <= 57) return Color.CYAN;
        return Color.PINK;
    }

    /**
     * Возвращает описание символа
     */
    private String getDescription(int code, char c) {
        if (code == 32) return "Space";
        if (code == 33) return "Exclamation";
        if (code == 34) return "Quotation";
        if (code == 35) return "Number sign";
        if (code == 36) return "Dollar";
        if (code == 37) return "Percent";
        if (code == 38) return "Ampersand";
        if (code == 39) return "Apostrophe";
        if (code == 40) return "Left paren";
        if (code == 41) return "Right paren";
        if (code == 42) return "Asterisk";
        if (code == 43) return "Plus";
        if (code == 44) return "Comma";
        if (code == 45) return "Minus";
        if (code == 46) return "Period";
        if (code == 47) return "Slash";
        if (code >= 48 && code <= 57) return "Digit " + c;
        if (code == 58) return "Colon";
        if (code == 59) return "Semicolon";
        if (code == 60) return "Less than";
        if (code == 61) return "Equals";
        if (code == 62) return "Greater than";
        if (code == 63) return "Question";
        if (code == 64) return "At sign";
        if (code >= 65 && code <= 90) return "Uppercase " + c;
        if (code == 91) return "Left bracket";
        if (code == 92) return "Backslash";
        if (code == 93) return "Right bracket";
        if (code == 94) return "Caret";
        if (code == 95) return "Underscore";
        if (code == 96) return "Grave";
        if (code >= 97 && code <= 122) return "Lowercase " + c;
        if (code == 123) return "Left brace";
        if (code == 124) return "Vertical bar";
        if (code == 125) return "Right brace";
        if (code == 126) return "Tilde";
        return "ASCII " + code;
    }

    /**
     * Устанавливает позицию таблицы
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.startX = x + 10;
        this.startY = y + height - 20;
    }

    /**
     * Устанавливает размер таблицы
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        this.startX = x + 10;
        this.startY = y + height - 20;
    }

    /**
     * Показывает таблицу
     */
    public void show() {
        visible = true;
    }

    /**
     * Скрывает таблицу
     */
    public void hide() {
        visible = false;
    }

    /**
     * Переключает видимость таблицы
     */
    public void toggle() {
        visible = !visible;
    }

    /**
     * Проверяет, видима ли таблица
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Освобождает ресурсы
     */
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
