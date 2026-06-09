package kgu.game.project.managers;

import java.util.HashMap;
import java.util.Map;

public class LocalizationManager {

    public enum Language {EN, RU}

    private static Language currentLanguage = Language.RU;

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
            "Теперь ты застрял в своём компьютере надолго,\nпоздравляю тебя.\nТы проведёшь здесь 5 дней, пока я обновляюсь.");
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
        add("talk.15", "You can try approaching the sign.\nPress the button when it turns\ngreen to interact.", "Ты можешь попробовать подойти к табличке.\nНажимай на кнопку, когда она станет\nзелёной для взаимодействия");

        add("talk.16", "Use the battery to save between levels", "Используй батарейку для сохранения мужду уровнями");
        add("talk.17", " ", " ");

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
        add("login.password_hint", "Enter password", "Введите пароль");
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
        add("talkVirus.6", "If you don't enter the phishing email number, you won't get out, hahahahahaha",
            "Если ты не введёшь номер фишингвого письма, ты не выберишься, хахахахаххаха");
        add("theend.title", "You completed the game!", "Вы прошли игру!");
        add("theend.subtitle", "Congratulations!", "Поздравляем!");
        add("level2.talk.0", "So, now I'm not inside the computer case", "Так, теперь я не в корпусе компьютера");
        add("level2.talk.1", "This place looks like some hall from the Roman Empire era", "Это место похоже на какой-то зал эпохи Римской Империи");
        add("level2.talk.2", "I see that guy again, he thinks he's an emperor", "Опять вижу этого чувака, думает, он император");
        add("level2.talk.3", "Such is fate, I need to approach him,\notherwise I won't return home", "Такая судьба, мне стоит к нему подойти,\nиначе я не вернусь домой");
        add("level2.talk.4", "", "");

        add("caesar.talk.vulnerability.0", "Caesar's cipher had one vulnerability.\nThe decryption key for the original text\ncould be found by simple brute force.", "Шифр Цезаря имел одну уязвимость.\nКлюч до расшифровки исходного текста\nможно было найти обычным подбором.");
        add("caesar.talk.vulnerability.1", "A French diplomat became interested in cryptography\nand created a cipher that is based on Caesar's cipher,\nbut more resistant to brute force attacks.", "Один французский дипломат увлёкся криптографией\nи создал шифр, который основан на шифре Цезаря,\nно более устойчив к переборам.");

        add("caesar.talk.0", "Greetings again!\nI see you've figured out how the computer sees characters", "Снова привет!\nВижу, ты разобрался, как компьютер видит символы");
        add("caesar.talk.1", "Now I'll teach you how to encrypt data", "Сейчас я научу тебя шифровать данные");
        add("caesar.talk.2", " ", " ");
        add("caesar.talk.3", "", "");
        add("caesar.talk.4", "He invented a cipher\nand named it after himself", "Он придумал шифр\nи назвал его в честь себя");
        add("caesar.talk.5", "Caesar cipher works like this:\nthe alphabet is taken and each character\nbecomes another character\naccording to a shift (an integer)", "Шифр Цезаря работает так:\nберётся алфавит и каждый символ\nстановится другим символом\nсогласно сдвигу (целому числу)");
        add("caesar.talk.6", "For example, the letter A from the alphabet,\nencrypted with a shift of 1,\nbecomes B", "Например, буква А из алфавита,\nзашифрованная шифром со сдвигом 1,\nстанет Б");
        add("caesar.talk.7", "The letter B with a shift of 2\nbecomes D", "Буква Б с шифром со сдвигом 2\nстанет Г");
        add("caesar.talk.8", "You can see how the cipher works on the screen.\nUse the floor plates to shift the alphabet", "Ты можешь посмотреть, как работает шифр на экране.\nИспользуй плиты на полу чтобы сдвигать алфавит");
        add("caesar.talk.9", "The plate with a minus sign decreases the shift by 1\nThe plate with a plus sign increases the shift by 1", "Плита со знаком - уменьшает сдвиг на 1\nПлита со знаком + увеличивает сдвиг на 1");
        add("caesar.talk.10", "The arrow at the top shows\nhow characters are encrypted with the given shift", "Стрелка вверху показывает,\nкак шифруются символы с данным сдвигом");
        add("caesar.talk.11", "To decrypt a 26-letter English alphabet code,\nuse the formula 26-n,\nwhere n is the shift", "Для расшифровки кода из 26 буквенного\nанглийского алфавита\nиспользуй формулу 26-n,\nгде n-это сдвиг");
        add("caesar.talk.12", "To decrypt a cipher with shift 5,\nyou need to encrypt the cipher again, but with shift 21", "Для расшифровки шифра со сдвигом 5,\nнадо зашифровать шифр снова, но со сдвигом 21");
        add("caesar.talk.13", "The virus has infected the computer, decrypt the important system file", "Вирус заразил компьютер, расшифруй важный системный файл");
        add("caesar.talk.14", " ", " ");
        add("caesar.talk.15", "You can try approaching the tablet.\nPress the button when it turns green\nto interact", "Ты можешь попробовать подойти к табличке.\nНажимай на кнопку, когда она станет\nзелёной для взаимодействия");

        add("caesar.talk2.0", "Correct, because he died before\nyou were born", "Правильно, ведь он умер ещё до\nтвоего рождения");
        add("caesar.talk2.1", "", "");
        add("caesar.talk2.2", "He invented a cipher\nand named it after himself", "Он придумал шифр,\nи назвал его в честь себя");
        add("caesar.talk2.3", "", "");
        add("caesar.talk2.4", "Caesar cipher works like this:\nthe alphabet is taken and each character\nbecomes another character\naccording to a shift (an integer)", "Шифр Цезаря работает так:\nберётся алфавит и каждый символ\nстановится другим символом\nсогласно сдвигу (целому числу)");
        add("caesar.talk2.5", "For example, the letter A from the alphabet,\nencrypted with a shift of 1,\nbecomes B", "Например, буква А из алфавита,\nзашифрованная шифром со сдвигом 1,\nстанет Б");
        add("caesar.talk2.6", "The letter B with a shift of 2\nbecomes D", "Буква Б с шифром со сдвигом 2\nстанет Г");
        add("caesar.talk2.7", "You can see how the cipher works on the screen.\nUse the floor plates to see\nhow the cipher works", "Ты можешь посмотреть, как работает шифр на экране.\nИспользуй плиты на полу чтобы посмотреть,\nкак работает шифр");
        add("caesar.talk2.8", "The virus has infected the computer, decrypt the important system file", "Вирус заразил компьютер, расшифруй важный системный файл");

        add("pressK", "Press K to talk", "Нажми K для разговора");
        add("pressGreen", "Press green button to talk", "Нажми зелёную кнопку, чтобы поговорить");
        add("player.name", "Player", "Игрок");
        add("caesar.question", "Do you know Gaius Julius Caesar?", "Ты знаешь Гая Юлия Цезаря?");
        add("caesar.history.0", "In ancient times, people needed a secure\nway to exchange information\nso that enemies wouldn't find out about it", "В древние времена люди нуждались в безопасном\nспособе обмена информацией,\nчтобы её не узнали враги");
        add("caesar.history.1", "Such a method was invented in the Roman Empire.\nAnd it is called the Caesar cipher.", "Такой способ был придуман в Римской Империи.\nИ именуется он шифром Цезаря.");
        add("player.talk.0", "AAAAH, what happened!?", "ААААА, что произошло!?");
        add("player.talk.1", "I got sucked into some strange place!\nIt's very dusty here :(", "Меня засосало в какое-то странное место!\nЗдесь очень пыльно :(");
        add("player.talk.2", "This looks like my computer case", "Это похоже на корпус моего компьютера");
        add("player.talk.3", "There's a person standing nearby, I should approach him", "Рядом стоит человек, мне стоит подойти к нему");
        add("player.talk.4", "", "");
        add("vigenere.talk.0", "Some people find the Caesar cipher complicated", "Некоторым шифр Цезаря кажется сложным");
        add("vigenere.talk.1", "Now I want to introduce you to the Vigenere cipher", "Теперь я хочу познакомить тебя с шифром Виженера");
        add("vigenere.talk.2", "The Vigenere cipher is a more complex version\nof the Caesar cipher", "Шифр Виженера - более сложная версия\nшифра Цезаря");
        add("vigenere.talk.3", "In the Caesar cipher, we use\none shift for the entire word,\nwhile in the Vigenere cipher, we use a different shift\nfor each letter", "Если в шифре Цезаря мы используем\nодин сдвиг для целого слова,\nто в шифре Виженера мы для каждой буквы\nиспользуем разный сдвиг");
        add("vigenere.talk.4", "The shifts for letters are set by a key like: 123.\nOriginally, the key could be a string,\nbut we have a simplified version of the cipher.", "Сдвиги для букв задаются ключом вида: 123.\nВ оригинале в виде ключа может быть строка,\nно у нас более упрощённая версия шифра.");
        add("vigenere.talk.5", "With the key 15234, we will encrypt\nthe 1st letter of the string with a shift of 1,\nthe 2nd letter with a shift of 5, etc.", "С ключом 15234 каждую 1 букву строки мы будем\nшифровать со сдвигом 1,\nкаждую вторую со сдвигом 5 и т.д.");
        add("vigenere.talk.6", "I need you to encrypt one file to protect it from the virus. Next to the battery, you’ll find a key and the text. Try to ENCRYPT it.", "Я хочу, чтобы ты зашифровал один файл от вируса.\nРядом с батарейкой есть ключ и текст, попробуй\nЗАШИФРОВАТЬ его.");
        add("vigenere.talk.7", "", "");
        add("player.talk.level3.0", "Now this guy has gone with a French theme", "Теперь этот чел сделал французские мотивы");
        add("player.talk.level3.1", "And he's dressed like Napoleon", "И оделся он, как Наполеон");
        add("player.talk.level3.2", "Looks like he's going to talk about some French scientist", "Видимо, будет говорить про какого-то французского учёного");
        add("player.talk.level3.3", "He's always standing in one place, I need to approach him again", "Он вечно стоит на месте, надо подойти опять");
        add("player.talk.level3.4", "", "");
        add("xor.talk.0", "Well then...", "Ну что же...");
        add("xor.talk.1", "Now I want to teach you another type of encryption", "Теперь я хочу тебя научить ещё одному виду шифрования");
        add("xor.talk.2", "As you know, there are several types of logical\noperators in computer science", "Как ты знаешь, есть несколько типов логических\nоператоров в информатике");
        add("xor.talk.3", "The most famous are AND and OR\nBut the most interesting, in my opinion, is XOR", "Самые известные: AND и OR\nНо самый интересный, по-моему, XOR");
        add("xor.talk.4", "Let's say we have a character that was converted to bits\nusing ASCII,\nand we want to encrypt these bits", "Допустим, у нас есть символ, который с помощью ASCII\nбыл преобразован в биты,\nи мы хотим эти биты зашифровать");
        add("xor.talk.5", "XOR has the property: A XOR B = C,\nC XOR B = A,\nwhich is used in encryption.", "XOR имеет совйство A XOR B = C\n" +
            "C XOR B = A,\nкоторое используют при шфировке");
        add("xor.talk.6", "Approach the computer labeled \"XOR\",\nfind the correct key so that when you perform XOR\non the text with this key,\nthe given ciphertext is created. XOR is performed bitwise.", "Подойди к компьютеру с надписью 'XOR',\nподбери ключ, чтобы при выполнении XOR\nптекста данным ключос создавался\nпданный шифр. XOR выполняется побитово.");
        add("xor.talk.7", " ", " ");
        add("xor.talk.8", "You need to learn about the Vigenere cipher", "Тебе надо узнать о шифре Виженера");
        add("xor.talk.9", "The Vigenere cipher is an encryption method\nthat uses a keyword", "Шифр Виженера — это метод шифрования,\nиспользующий ключевое слово");
        add("xor.talk.10", "To decrypt, you need to know the key and\nsubtract its value from the encrypted text", "Для расшифровки нужно знать ключ и\nвычитать его значение из зашифрованного текста");
        add("xor.talk.11", "Try to decrypt a few words\nto prove you're ready!", "Попробуй расшифровать несколько слов,\nчтобы доказать, что ты готов!");
        add("xor.talk.12", " ", " ");
        add("xor.talk.13", " ", " ");
        add("xor.text.example", "Text:   1001101 (77) M", "Текст:   1001101 (77) М");
        add("xor.key.example", "Key:    0", "Ключ:    0");
        add("xor.cipher.example", "Cipher: 0110110 (54) 6", "Шифр: 0110110 (54) 6");
        add("player.talk.start", "What a nice day to play on the computer!\nI need to go up to it and turn it on.", "Какой хороший день, чтобы поиграть в комп!\nМне надо подойти к нему и включить его");
        add("pause", "Pause", "Пауза");
        add("loadgame.title", "LOAD GAME", "ЗАГРУЗКА ИГРЫ");
        add("loadgame.subtitle", "SELECT SAVE SLOT", "ВЫБЕРИТЕ СЛОТ");
        add("story.intro.0",
            "Not so long ago, there was a boy\nwhose parents bought him a computer.\nHe loved his device very much, protecting it\nfrom his mom's friend's sons and distant cousins.",
            "Не так давно был один мальчик,\nкоторому родители купили компьютер.\nОн очень любил своё устройство, берёг его\nот сыновей маминой подруги и пятиюродных братьев.");

        add("story.intro.1",
            "At the same time, a virus was spreading across the Internet,\nsent through phishing emails.\nThe virus infected one device after another.\nIt encrypted data on the computers.",
            "В то же самое время по Интернету гулял вирус,\nрассылающийся с помощью фишинговых писем.\nВирус заражал устройство за устройством.\nШифровал данные на компьютерах.");

        add("story.intro.2",
            "But that didn't affect our good guy. Until one moment...",
            "Но это не касалось нашего добряка. До одного момента...");
    }

    private static void add(String key, String en, String ru) {
        Map<Language, String> map = new HashMap<>();
        map.put(Language.EN, en);
        map.put(Language.RU, ru);
        strings.put(key, map);
    }

    public static String get(String key) {
        Map<Language, String> map = strings.get(key);
        if (map == null) return key;
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
