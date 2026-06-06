package kgu.game.project.managers;

import java.util.HashMap;
import java.util.Map;

public class LocalizationManager {

    public enum Language {EN, RU}

    private static Language currentLanguage = Language.RU;

    // Словарь строк
    private static final Map<String, Map<Language, String>> strings = new HashMap<>();

    static {
        add("settings.title", "Settings", "Настройки");
        add("settings.clear", "clear records", "очистить рекорды");
        add("settings.cleared", "clear records (cleared)", "очистить рекорды (очищено)");
        add("settings.difficulty", "difficulty: ", "сложность: ");
        add("settings.music", "music: ", "музыка: ");
        add("settings.sound", "sound: ", "звук: ");
        add("settings.subtitles", "Subtitles: ", "Субтитры: ");
        add("settings.language", "language: EN", "язык: RU");
        add("settings.return", "return", "назад");
        add("state.on", "ON", "ВКЛ");
        add("state.off", "OFF", "ВЫКЛ");
        add("menu.exit", "EXIT", "ВЫХОД");
        add("menu.start", "START", "НАЧАТЬ");
        add("menu.load", "LOAD GAME", "ЗАГРУЗИТЬ ИГРУ");
        add("menu.settings", "SETTINGS", "НАСТРОЙКИ");

        add("talk.0", "Greetings, most esteemed user!\nIt's quite dusty inside your computer case",
            "Здравствуйте, многоуважаемый пользователь!\nНу и пыльно в корпусе Вашего компьютера");
        add("talk.1", "I am the antivirus you installed a very long time ago",
            "Я антивирус, который Вы установили очень давно");
        add("talk.2", "It appears your computer has been hacked,\nthat's why you're here",
            "Видимо, Ваш компьютер взломали,\nпоэтому ты здесь");
        add("talk.3", "You should have updated me,\nthe update would have taken only 5 days to download!",
            "Тебе надо было обновить меня,\nзагрузка обновлений длилась бы всего лишь 5 дней!");
        add("talk.4", "Now you're stuck inside your own computer for a while,\ncongratulations",
            "Теперь ты застрял в своём компьютере надолго,\nпоздравляю тебя");
        add("talk.5", "I can help you, but to get your device back\nyou need to learn the basics\nof information security",
            "Я могу тебе помочь, но для возвращения своего устройства\nсебе тебе надо обучиться основам\nинформационной безопасности");
        add("talk.6", " ", " ");
        add("talk.7", " ", " ");
        add("talk.8", "You need to learn about the ASCII table",
            "Тебе надо узнать об ASCII таблице");
        add("talk.9", "ASCII is a standard\nfor encoding Latin alphabet letters,\ndigits, some special characters\nand control symbols",
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
        add("login.title", "Log in", "Вход");
        add("login.user", "User", "Пользователь");
        add("login.confirm", "OK", "ОК");
        add("login.forgot", "Forgot your password?", "Забыли пароль?");
        add("login.error", "Incorrect password", "Неверный пароль");
        add("login.incorrect", "Incorrect password", "Неверный пароль");
        add("login.hint", "Password is 'password'", "Пароль это 'password'");
        add("login.password_hint", "Enter your password", "Введите пароль");
        add("game.pause", "Pause", "Пауза");
        add("game.home", "Home", "Домой");
        add("game.continue", "Continue", "Продолжить");
        add("game.last_records", "Last records", "Последние рекорды");
        add("game.login_hint", "Log in to your computer", "Войдите в свой компьютер");
        add("game.login2_hint", "Press k to turn on computer", "Нажми k, чтобы просмотреть компьютер");
        add("computer.menu", "Menu", "Меню");
        add("computer.mail_from", "FROM: john@mail.www\nTO: you", "ОТ: john@mail.www\nКОМУ: вам");
        add("computer.mail_body", "\nYou won a prize! Plz,\nclick link below\nto get it!\n",
            "\nВы выиграли приз! \nПожалуйста,\nперейдите по ссылке\nниже,\nчтобы получить его!\n");
        add("save.cancel", "Cancel", "Отмена");
        add("save.save", "Save", "Сохраниться");
        add("save.question", "Do you want to save?", "Хотите сохраниться?");
        add("network.talk.0", "Well then", "Ну что ж");
        add("network.talk.1", "We're almost done", "Мы почти закончили");
        add("network.talk.2", "Now I'll tell you about network traffic dumps", "Теперь я расскажу тебе про дампы сетевого трафика");
        add("network.talk.3", "When you use a browser,\nyour computer sends a request from your IP address\nto the server", "Когда ты пользуешься браузером,\nтвой компьютер отправляет с твоего IP-адреса\nзапрос на сервер");
        add("network.talk.4", "The server processes this request and returns the page", "Сервер обрабатывает этот запрос и возвращает страницу");
        add("network.talk.5", "Hackers often send requests to the server\nthat are designed to\nharm the project", "Хакеры часто отправляют запросы на сервер,\nкоторые создаются с целью\nнавредить проекту");
        add("network.talk.6", "To prevent this,\nIT forensic experts record histories of\nnetwork traffic usage to understand\nwhat requests were sent to the server", "Чтобы это не произошло,\nIT-криминалисты записывают истории об использовании\nсетевого трафика для понимания,\nкакие запросы шли на сервер");
        add("network.talk.7", "Dumps usually store information\nabout the traffic sender and destination.", "В дампах обычно хранится информация\nоб отправителе трафика и о месте назначения.");
        add("network.talk.8", "Additional traffic information is also stored", "Также хранится дополнительная информация о трафике");
        add("network.talk.9", "For example, the path to a resource like /user/id1,\nthe type of attack", "Например, путь до ресурса вида /user/id1,\nтип атаки");
        add("network.talk.10", "The following headers are typical for attacks:", "Для атак характерны заголовки:");
        add("network.talk.11", "TLS_HANDSHAKE flood,\nPORT_SCAN (hackers look for open ports to attack),\nbrute-force", "TLS_HANDSHAKE flood,\nPORT_SCAN (хакеры ищут открытые порты для атак),\nbrute-force");
        add("network.talk.12", "There is a network traffic dump file here", "Здесь есть файл с дампом сетевого трафика");
        add("network.talk.13", "Try to find the IP address of the attacking machine", "Попробуй узнать IP атакующей машины");
        add("network.talk.14", "I've told you everything you need to know", "Всё, что нужно было я тебе рассказал");
        add("network.talk.15", "Good luck...", "Удачи...");
        add("dialog.next", "Next", "Далее");
        add("dialog.yes", "Yes", "Да");
        add("dialog.no", "No", "Нет");
        add("passwoidInput.door", "DOOR TO THE NEXT LEVEL", "ДВЕРЬ НА СЛЕДУЮЩИЙ УРОВЕНЬ");
        add("passwoidInput.answer", "Enter answer", "Введи ответ");
        add("Incorrect password", "Incorrect password", "Неправильный пароль");
        add("pressK", "Press K to talk", "Нажми K для разговора");
        add("pressGreen", "Press green button to talk", "Нажми зелёную кнопку, чтобы поговорить");
        add("talkVirus.0", "I'm so glad we saw each other, User",
            "Как же я рад, что мы увиделись, User");
        add("talkVirus.1", "As you have already understood, I am a virus",
            "Как ты уже понял, я вирус");
        add("talkVirus.2", "I infected your computer to send\n" +
                "phishing emails to your contacts!",
            "Я заразил твой компьютер, чтобы переслать\nфишинговые письма твоим контактам!");
        add("talkVirus.3", "But I see\n" +
                "that you have a decent understanding of cybersecurity.",
            "Но я вижу" +
                "что вы неплохо разбираетесь в кибербезопасности");
        add("talkVirus.4", "Your antivirus has been updated\n" +
                "and almost defeated me\n" +
                "while you were studying.\n" +
                "But I can send emails to your contacts.",
            "Твой антивирус обновился\nи почти одолел меня,\nпока ты обучался.\nНо я могу разослать письма твоим контактам");
        add("talkVirus.5", "You won't be able to tell,\n" +
                " which of the emails is not phishing",
            "Ты не сможешь определить,\n какое из писем не является фишинговым\n");
        add("talkVirus.6", "hahahahahaha",
            "хахахахаххаха");
        add("theend.title", "You completed the game!", "Вы прошли игру!");
        add("theend.subtitle", "Congratulations!", "Поздравляем!");
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
