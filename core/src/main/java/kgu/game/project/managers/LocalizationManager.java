package kgu.game.project.managers;

import java.util.HashMap;
import java.util.Map;

public class LocalizationManager {

    public enum Language {EN, RU}

    private static Language currentLanguage = Language.RU;

    // Словарь строк
    private static final Map<String, Map<Language, String>> strings = new HashMap<>();

    static {
        add("settings.title",      "Settings",                    "Настройки");
        add("settings.clear",      "clear records",               "очистить рекорды");
        add("settings.cleared",    "clear records (cleared)",     "очистить рекорды (очищено)");
        add("settings.difficulty", "difficulty: ",                "сложность: ");
        add("settings.music",      "music: ",                     "музыка: ");
        add("settings.sound",      "sound: ",                     "звук: ");
        add("settings.subtitles",  "Subtitles: ",                 "Субтитры: ");
        add("settings.language",   "language: EN",                "язык: RU");
        add("settings.return",     "return",                      "назад");
        add("state.on",            "ON",                          "ВКЛ");
        add("state.off",           "OFF",                         "ВЫКЛ");
        add("menu.exit",           "EXIT",                        "ВЫХОД");
        add("menu.start",          "START",                       "НАЧАТЬ");
        add("menu.load",           "LOAD GAME",                   "ЗАГРУЗИТЬ ИГРУ");
        add("menu.settings",       "SETTINGS",                    "НАСТРОЙКИ");

        add("talk.0",  "Greetings, most esteemed user!\nIt's quite dusty inside your computer case",
            "Здравствуйте, многоуважаемый пользователь!\nНу и пыльно в корпусе Вашего компьютера");
        add("talk.1",  "I am the antivirus you installed a very long time ago",
            "Я антивирус, который Вы установили очень давно");
        add("talk.2",  "It appears your computer has been hacked,\nthat's why you're here",
            "Видимо, Ваш компьютер взломали,\nпоэтому ты здесь");
        add("talk.3",  "You should have updated me,\nthe update would have taken only 5 days to download!",
            "Тебе надо было обновить меня,\nзагрузка обновлений длилась бы всего лишь 5 дней!");
        add("talk.4",  "Now you're stuck inside your own computer for a while,\ncongratulations",
            "Теперь ты застрял в своём компьютере надолго,\nпоздравляю тебя");
        add("talk.5",  "I can help you, but to get your device back\nyou need to learn the basics\nof information security",
            "Я могу тебе помочь, но для возвращения своего устройства\nсебе тебе надо обучиться основам\nинформационной безопасности");
        add("talk.6",  " ", " ");
        add("talk.7",  " ", " ");
        add("talk.8",  "You need to learn about the ASCII table",
            "Тебе надо узнать об ASCII таблице");
        add("talk.9",  "ASCII is a standard\nfor encoding Latin alphabet letters,\ndigits, some special characters\nand control symbols",
            "ASCII — это стандарт\nкодирования букв латинского\nалфавита, цифр, некоторых специальных\nзнаков и управляющих символов");
        add("talk.10", "You need to know it to understand\nwhat information your computer stores",
            "Её нужно знать, чтобы понимать,\nкакую информацию хранит компьютер");
        add("talk.11", "In ASCII, every character is encoded\nby a specific\nsequence of bits",
            "В ASCII все символы\nкодируются определённой\nпоследовательностью бит");
        add("talk.12", "A password was set in the BIOS,\nit's written on the board,\nbut it's encoded in ASCII",
            "В BIOS был установлен пароль,\nна плате написан пароль,\nно он закодирован в ASCII");
        add("talk.13", "There's an ASCII table in the corner\nshowing the correspondence\nbetween bits and characters",
            "В углу есть таблица ASCII\nс соответствием бит и символов");
        add("talk.14", "Find out the password\nto unlock the BIOS",
            "Узнай пароль,\nчтобы разблокировать BIOS");
        add("talk.15", " ", " ");

        add("talk2.0", "Then good luck choking in the dust",
            "Тогда удачи задыхаться в пыли");
        add("talk2.1", "I am the antivirus you installed a very long time ago",
            "Я антивирус, который Вы установили очень давно");
        add("talk2.2", "It appears your computer has been hacked,\nthat's why you're here",
            "Видимо, Ваш компьютер взломали,\nпоэтому ты здесь");
        add("talk2.3", "You should have updated me,\nthe update would have taken only 5 days to download!",
            "Тебе надо было обновить меня,\nзагрузка обновлений длилась бы всего лишь 5 дней!");
        add("talk2.4", "Now you're stuck inside your own computer for a while,\ncongratulations",
            "Теперь ты застрял в своём компьютере надолго,\nпоздравляю тебя");
        add("talk2.5", "I can help you, but to get your device back\nyou need to learn the basics\nof information security",
            "Я могу тебе помочь, но для возвращения своего устройства\nсебе тебе надо обучиться основам\nинформационной безопасности");
        add("talk2.6", "Do you want to start the training to return in your world?",
            "Ты хочешь начать обучение, чтобы вернуться в свой мир?");
        add("talk2.7", "?", "?");
        add("login.title",        "Log in",                     "Вход");
        add("login.user",         "User",                       "Пользователь");
        add("login.confirm",      "OK",                         "ОК");
        add("login.forgot",       "Forgot your password?",      "Забыли пароль?");
        add("login.error",        "Incorrect password",         "Неверный пароль");
        add("login.incorrect",    "Incorrect password",         "Неверный пароль");
        add("login.hint",         "Password is your name",      "Пароль — это ваше имя");
        add("login.password_hint","Enter your password",        "Введите пароль");
        add("game.pause",         "Pause",                      "Пауза");
        add("game.home",          "Home",                       "Домой");
        add("game.continue",      "Continue",                   "Продолжить");
        add("game.last_records",  "Last records",               "Последние рекорды");
        add("game.login_hint",    "Log in to your computer",    "Войдите в свой компьютер");
        add("computer.menu",        "Menu",                       "Меню");
        add("computer.mail_from",   "FROM: john@mail.www\nTO: you", "ОТ: john@mail.www\nКОМУ: вам");
        add("computer.mail_body",   "\nYou won a prize! Plz,\nclick link below\nto get it!\n",
            "\nВы выиграли приз! \nПожалуйста,\nперейдите по ссылке\nниже,\nчтобы получить его!\n");
        add("save.cancel",    "Cancel",     "Отмена");
        add("save.save",      "Save",       "Сохранить");
        add("save.question",  "Do you want to save?", "Хотите сохранить?");
    }

    private static void add(String key, String en, String ru) {
        Map<Language, String> map = new HashMap<>();
        map.put(Language.EN, en);
        map.put(Language.RU, ru);
        strings.put(key, map);
    }

    public static String get(String key) {
        Map<Language, String> map = strings.get(key);
        if (map == null) return key; // fallback — возвращает ключ
        return map.getOrDefault(currentLanguage, key);
    }

    public static Language getLanguage() {
        return currentLanguage;
    }

    public static void setLanguage(Language lang) {
        currentLanguage = lang;
    }

    public static void toggleLanguage() {
        currentLanguage = (currentLanguage == Language.EN) ? Language.RU : Language.EN;
    }
}
