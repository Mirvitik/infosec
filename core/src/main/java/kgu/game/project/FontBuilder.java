package kgu.game.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontBuilder {

    // Строка со всеми необходимыми символами (русские + английские + цифры + знаки)
    private static final String DEFAULT_CHARACTERS =
        "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
            "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "0123456789" +
            " .,!?-;:\"\'()[]{}<>@#$%^&*+=/\\|~`";

    public static BitmapFont generate(int size, Color color, String fontPath) {
        return generate(size, color, fontPath, DEFAULT_CHARACTERS);
    }

    public static BitmapFont generate(int size, Color color, String fontPath, String characters) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = color;

        // КЛЮЧЕВОЙ МОМЕНТ: указываем все символы, которые должны быть в шрифте
        parameter.characters = characters;

        // Дополнительные полезные настройки
        parameter.borderWidth = 0;        // Обводка (0 - без обводки)
        parameter.borderColor = Color.BLACK;
        parameter.shadowOffsetX = 0;      // Тень (0 - без тени)
        parameter.shadowOffsetY = 0;
        parameter.shadowColor = Color.BLACK;
        parameter.genMipMaps = false;     // Mip-карты для масштабирования

        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    // Перегруженный метод с возможностью добавить дополнительные символы
    public static BitmapFont generateWithExtraChars(int size, Color color, String fontPath, String extraChars) {
        String allChars = DEFAULT_CHARACTERS + extraChars;
        return generate(size, color, fontPath, allChars);
    }
}
