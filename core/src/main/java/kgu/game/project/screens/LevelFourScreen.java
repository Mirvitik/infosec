package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;

import kgu.game.project.GameResources;
import kgu.game.project.GameSession;
import kgu.game.project.GameSettings;
import kgu.game.project.GameState;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ButtonView;
import kgu.game.project.components.DialogOkNoView;
import kgu.game.project.components.DialogView;
import kgu.game.project.components.ImageView;
import kgu.game.project.components.LiveView;
import kgu.game.project.components.PasswordInputView;
import kgu.game.project.components.RecordsListView;
import kgu.game.project.components.SaveView;
import kgu.game.project.components.TextView;
import kgu.game.project.components.TouchpadView;
import kgu.game.project.managers.ContactManager;
import kgu.game.project.objects.AnimatedHeroObject;
import kgu.game.project.objects.BatteryObject;
import kgu.game.project.objects.ComputerObject;
import kgu.game.project.objects.GameObject;
import kgu.game.project.objects.HeroObject;
import kgu.game.project.objects.TrashObject;
import kgu.game.project.objects.AntivirusObject;
import kgu.game.project.managers.MemoryManager;
import kgu.game.project.managers.TiledMapManager;
import kgu.game.project.objects.BulletObject;

public class LevelFourScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;
    GameSession gameSession;
    HeroObject heroObject;

    ArrayList<TrashObject> trashArray;
    ArrayList<BulletObject> bulletArray;

    // Vigenere cipher game elements
    private ArrayList<Object> cipherObjects;
    private TextView encryptedCodeView;
    private TextView currentKeyView;
    private TextView hintView;
    private ButtonView keyIncreaseButton;
    private ButtonView keyDecreaseButton;
    private ButtonView submitCodeButton;
    private ButtonView nextChallengeButton;
    private int currentChallengeIndex = 0;
    private int currentKey = 0;
    private boolean isCipherGameActive = false;
    private String targetEncryptedCode = "";
    private String expectedDecryptedCode = "";
    private int scoreForLevel = 0;
    private int totalChallenges = 5;

    // Vigenere challenges
    private String[] challenges = {
        "WKLV",      // THIS
        "LV",        // IS
        "D",         // A
        "YLJHQHUH",  // VIGENERE
        "FLSKHU"     // CIPHER
    };

    private String[] challengeDecrypted = {
        "THIS",
        "IS",
        "A",
        "VIGENERE",
        "CIPHER"
    };

    // PLAY state UI
    ImageView topBlackoutView;
    LiveView liveView;
    ButtonView pauseButton;
    TouchpadView touchpadView;

    // PAUSED state UI
    TextView pauseTextView;
    ButtonView homeButton;
    ButtonView continueButton;

    // ENDED state UI
    TextView recordsTextView;
    RecordsListView recordsListView;
    ButtonView homeButton2;
    ButtonView actionButton;
    Texture heroSpriteSheet;
    TextureRegion[][] heroFrames;
    AntivirusObject antiVirus;
    ImageView message;
    ComputerObject asciiTable;
    private TiledMapManager tiledMapManager;
    DialogView dialog;
    DialogView dialogNo;
    private Vector3 touch2;
    public boolean isNearComputer = false;

    private boolean isTouchingUI = false;
    DialogOkNoView dialogOkNoView;
    ContactManager contactManager;
    TextView text;
    Array<String> talks;
    Array<String> talks2;
    int ok_times = 1;
    Boolean isNearAntivirus = false;
    boolean toDraw = false;
    boolean toDrawSave = false;
    boolean isNearBattery;
    float heroX = -1f;
    float heroY = -1f;
    ImageView image;
    BatteryObject batteryObject;
    BatteryObject doorDown;
    SaveView saveView = new SaveView(350, 50, 500, 600);
    boolean isNearDoor;
    Boolean toDrawPassword = false;
    PasswordInputView passwordInput;
    ImageView cipherGamePanel;
    TextView challengeTitle;
    TextView ct;
    ButtonView startCipherGameButton;
    Stage stage;
    Table table;
    ImageView fon_image;
    Slider slider;

    public LevelFourScreen(MyGdxGame myGdxGame) {
        Array<Body> bodies = new Array<>();
        myGdxGame.world.getBodies(bodies);
        asciiTable = new ComputerObject(14, 5, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE, GameResources.ASCII_SPRITE_PATH, myGdxGame.world);
        passwordInput = new PasswordInputView(myGdxGame, () -> {
            gameSession.resumeGame();
            myGdxGame.setScreen(new LevelFiveScreen(myGdxGame));
        }, "123");

        for (Body body : bodies) {
            myGdxGame.world.destroyBody(body);
        }

        String[] initialValues = {"Ну что же...",
            "Теперь я хочу тебя научить ещё одному виду шфирования",
            "Как ты знаешь, есть несколько типов логических\nоператоров в информатике",
            "Самые известные: AND и OR\nНо самый интересный, по-моему, XOR",
            "Допустим, у нас есть символ, который с помощью ASCII\nбыл преобразован в биты,\nи мы хотим эти биты зашифровать",
            "Как можно заметить на таблице, при XOR бита\n",
            " ",
            " ",
            "Тебе надо узнать о шифре Виженера",
            "Шифр Виженера — это метод шифрования,\nиспользующий ключевое слово",
            "Для расшифровки нужно знать ключ и\nвычитать его значение из зашифрованного текста",
            "Попробуй расшифровать несколько слов,\nчтобы доказать, что ты готов!",
            " ",
            " "};
        talks = new Array<>(initialValues);

        String[] initialValues2 = {"Тогда удачи задыхаться в пыли",
            "Я антивирус, который Вы установили очень давно",
            "Видимо, Ваш компьютер взломали,\nпоэтому ты здесь",
            "Тебе надо было обновить меня,\nзагрузка обновлений длилась бы всего лишь 5 дней!",
            "Теперь ты застрял в своём компьютере надолго,\nпоздравляю тебя",
            "Я могу тебе помочь, но для возвращения своего устройства\nсебе тебе надо обучиться основам\nинформационной безопасности",
            " Ты хочешь начать обучение?;NO;OK",
            "?"};
        talks2 = new Array<>(initialValues2);

        bodies.clear();
        this.myGdxGame = myGdxGame;
        gameSession = new GameSession();
        heroSpriteSheet = new Texture(GameResources.SPRITE_SHEET);
        int frameWidth = 32;
        int frameHeight = 32;
        heroFrames = TextureRegion.split(heroSpriteSheet, frameWidth, frameHeight);

        trashArray = new ArrayList<>();
        bulletArray = new ArrayList<>();
        cipherObjects = new ArrayList<>();

        tiledMapManager = new TiledMapManager(GameResources.TMX_MAP_LEVEL_FOUR_PATH, myGdxGame.camera, myGdxGame.batch, 2);
        topBlackoutView = new ImageView(0, 656, 1280, 64, GameResources.BLACKOUT_TOP_IMG_PATH);
        liveView = new LiveView(305, 1215);

        pauseButton = new ButtonView(1200, 658, 46, 54, GameResources.PAUSE_IMG_PATH);
        touchpadView = new TouchpadView(100, 100);

        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 525, 400, "Pause");
        homeButton = new ButtonView(350, 300, 200, 35, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        text = new TextView(myGdxGame.commonPixelFontText, 250, 150, "Нажми зелёную кнопку, чтобы поговорить");
        continueButton = new ButtonView(GameSettings.SCREEN_WIDTH - 550, 300, 200, 35, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Continue");
        actionButton = new ButtonView(1100, 70, 70, 70, GameResources.ACTION_BUTTON_IMG_PATH);
        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        recordsTextView = new TextView(myGdxGame.largeWhiteFont, 206, 842, "Last records");
        homeButton2 = new ButtonView(280, 365, 160, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        antiVirus = new AntivirusObject(GameResources.ANTIVIRUS_TEACHER_IMG_PATH, 200, 200, 64, 64, GameSettings.ANTIVIRUS_BIT, myGdxGame.world);

        // Initialize Vigenere game UI
        cipherGamePanel = new ImageView(200, 300, 880, 400, GameResources.DIALOG_FON_IMG_PATH);
        encryptedCodeView = new TextView(myGdxGame.consolasFont, 350, 550, "Text:   1001101 (77) M");
        currentKeyView = new TextView(myGdxGame.consolasFont, 350, 520, "Key:    0");
        ct = new TextView(myGdxGame.consolasFont, 350, 480, "Cipher: 0110110 (54) 6");
        hintView = new TextView(myGdxGame.commonPixelFontText, 350, 420, "Use +/- to adjust key (0-25)");
        challengeTitle = new TextView(myGdxGame.commonWhiteFont, 350, 600, "Decrypt the code!");

        keyIncreaseButton = new ButtonView(600, 460, 100, 50, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "+");
        keyDecreaseButton = new ButtonView(350, 460, 100, 50, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "-");
        submitCodeButton = new ButtonView(500, 350, 200, 50, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Submit");
        nextChallengeButton = new ButtonView(500, 350, 250, 50, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Next Challenge");
        nextChallengeButton.hide();
        startCipherGameButton = new ButtonView(500, 400, 280, 60, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Start Cipher Game");

        contactManager = new ContactManager(myGdxGame.world, (GameObject object) -> {
            if (object.getClass().getSimpleName().equals("AntivirusObject")) {
                isNearAntivirus = true;
            } else if (object.getClass().getSimpleName().equals("ComputerObject")) {
                isNearComputer = true;
            } else if (object.getBit() == GameSettings.BATTERY_BIT) {
                isNearBattery = true;
            } else if (object.getBit() == GameSettings.DOOR_BIT) {
                isNearDoor = true;
            }
        },
            (GameObject object) -> {
                if (object.getClass().getSimpleName().equals("AntivirusObject") ||
                    object.getClass().getSimpleName().equals("ComputerObject") || object.getClass().getSimpleName().equals("BatteryObject")) {
                    isNearComputer = false;
                    isNearAntivirus = false;
                    isNearBattery = false;
                    isNearDoor = false;
                }
            });

        message = new ImageView(210, 210, GameResources.HI_MESSAGE_IMG_PATH);
        image = new ImageView(180, 0, 1028, 720, GameResources.ASCII_PATH);
        batteryObject = new BatteryObject(10, 7, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE, GameResources.BATTERY_BUTTON_IMG_PATH, myGdxGame.world);
        doorDown = new BatteryObject(18, 7, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE * 2, GameResources.FRENCH_DOOR_IMG_PATH, myGdxGame.world, GameSettings.DOOR_BIT);
        fon_image = new ImageView(300, 200, 600, 400, GameResources.DIALOG_FON_IMG_PATH);

        // Надо — добавить SliderStyle:
        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);
        skin.add("white", Color.WHITE);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        skin.add("default", labelStyle);

// Создаём текстуры для слайдера программно
        Pixmap bgPixmap = new Pixmap(200, 10, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(Color.GRAY);
        bgPixmap.fill();
        skin.add("slider-bg", new Texture(bgPixmap));
        bgPixmap.dispose();

        Pixmap knobPixmap = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        knobPixmap.setColor(Color.WHITE);
        knobPixmap.fillCircle(10, 10, 10);
        skin.add("slider-knob", new Texture(knobPixmap));
        knobPixmap.dispose();

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = skin.newDrawable("slider-bg");
        sliderStyle.knob = skin.newDrawable("slider-knob");

// Имя ОБЯЗАТЕЛЬНО "default-horizontal" для горизонтального слайдера
        skin.add("default-horizontal", sliderStyle);

        slider = new Slider(33f, 126f, 1f, false, skin);

// Контейнер для слайдера
        Table sliderContainer = new Table();
        sliderContainer.add(slider).width(300);
        sliderContainer.pack();
        sliderContainer.setPosition(400, 300); // начальная позиция


// DragListener на контейнер, не на сам слайдер
        sliderContainer.addListener(new DragListener() {
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                // Проверяем что тащим именно за контейнер, а не за ползунок
                if (event.getTarget() == slider) cancel();
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                sliderContainer.moveBy(
                    x - sliderContainer.getWidth() / 2,
                    y - sliderContainer.getHeight() / 2
                );
            }
        });

        stage = new Stage(new ExtendViewport(
            GameSettings.SCREEN_WIDTH,
            GameSettings.SCREEN_HEIGHT,
            myGdxGame.uiCamera        // ← UI-камера
        ));
        stage.addActor(sliderContainer);

// Чтобы stage получал события касания
        Gdx.input.setInputProcessor(stage);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = slider.getValue();
                // обновить текст, логику и т.д.
                String binary = Integer.toBinaryString((int) value);
                if (binary.length() < 7) {
                    binary = "0" + binary;
                }
                currentKeyView.setText("Key:    " + binary + " (" + ((int) value) + ") " + (char) value);
            }
        });
    }

    public LevelFourScreen(MyGdxGame myGdxGame, float x, float y) {
        this(myGdxGame);
        heroX = x;
        heroY = y;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    // Vigenere cipher decryption method
    private String decryptVigenere(String encrypted, int key) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < encrypted.length(); i++) {
            char c = encrypted.charAt(i);
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int shifted = (c - base - key + 26) % 26;
                result.append((char) (base + shifted));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private void startCipherGame() {
        isCipherGameActive = true;
        currentChallengeIndex = 0;
        scoreForLevel = 0;
        loadNextChallenge();
    }

    private void loadNextChallenge() {
        if (currentChallengeIndex < totalChallenges) {
            targetEncryptedCode = challenges[currentChallengeIndex];
            expectedDecryptedCode = challengeDecrypted[currentChallengeIndex];
            currentKey = 0;
            encryptedCodeView.setText("Encrypted: " + targetEncryptedCode);
            currentKeyView.setText("Key: 0100001 (33) !" + currentKey);
            updateDecryptedPreview();
            submitCodeButton.show();
            nextChallengeButton.hide();
            challengeTitle.setText("Challenge " + (currentChallengeIndex + 1) + "/" + totalChallenges);
        } else {
            completeCipherGame();
        }
    }

    private void updateDecryptedPreview() {
        String decrypted = decryptVigenere(targetEncryptedCode, currentKey);
        // Show preview but don't submit
        hintView.setText("Preview: " + decrypted + " (Key: " + currentKey + ")");
    }

    private void submitAnswer() {
        String userDecrypted = decryptVigenere(targetEncryptedCode, currentKey);
        if (userDecrypted.equals(expectedDecryptedCode)) {
            scoreForLevel++;
            if (currentChallengeIndex + 1 < totalChallenges) {
                currentChallengeIndex++;
                loadNextChallenge();
            } else {
                completeCipherGame();
            }
        } else {
            hintView.setText("Wrong! Try again. Expected: " + expectedDecryptedCode);
        }
    }

    private void completeCipherGame() {
        isCipherGameActive = false;
        if (scoreForLevel == totalChallenges) {
            hintView.setText("Congratulations! You've mastered the Vigenere cipher! +10 points!");
        } else {
            hintView.setText("Game completed! Score: " + scoreForLevel + "/" + totalChallenges);
        }
        submitCodeButton.hide();
        nextChallengeButton.hide();
        encryptedCodeView.setText("Cipher game completed!");
    }

    @Override
    public void show() {
        restartGame();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        if (gameSession.state == GameState.PLAYING) {
            if (!heroObject.isAlive()) {
                gameSession.endGame();
                recordsListView.setRecords(MemoryManager.loadRecordsTable());
            }
            updateTrash();
            updateBullets();
            myGdxGame.camera.position.set(heroObject.getX(), heroObject.getY(), 0);
            myGdxGame.camera.update();
            gameSession.updateScore();
            liveView.setLeftLives(heroObject.getLiveLeft());
            myGdxGame.stepWorld();

            if (dialog != null && dialog.isToDispose()) {
                dialog.dispose();
                dialog = null;
            }
        }
        handleInput(delta);
        draw();
    }

    private void handleInput(float delta) {
        boolean isTouched = Gdx.input.isTouched();
        if (isTouched) {
            myGdxGame.touch = myGdxGame.uiCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            touch2 = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        }

        switch (gameSession.state) {
            case PLAYING:
                if (isTouched) {
                    isTouchingUI = false;

                    if (dialogOkNoView != null) {
                        if (dialogOkNoView.okButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            dialogOkNoView.dispose();
                            dialogOkNoView = null;
                            dialog.nextButton.show();
                        } else if (dialogOkNoView.noButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            dialogNo = new DialogView(myGdxGame, (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                                GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                                GameSettings.SCREEN_HEIGHT / 4f, talks2);
                            dialogOkNoView = null;
                        }
                    }

                    if (dialogNo != null) {
                        if (dialogNo.nextButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            dialog = null;
                            dialogOkNoView = null;
                            dialogNo = null;
                        }
                    }

                    if (pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        isTouchingUI = true;
                        gameSession.pauseGame();
                    }

                    if ((isNearAntivirus || isNearComputer || isNearBattery || isNearDoor) && !toDraw) {
                        actionButton = new ButtonView(1100, 70, 70, 70, GameResources.ACTION_BUTTON_ACTIVE_IMG_PATH);
                    } else if (!toDraw) {
                        actionButton = new ButtonView(1100, 70, 70, 70, GameResources.ACTION_BUTTON_IMG_PATH);
                    }

                    if (isNearAntivirus && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        dialog = new DialogView(myGdxGame, (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                            GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                            GameSettings.SCREEN_HEIGHT / 4f, talks);
                    }

                    if (isNearDoor && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        toDrawPassword = true;
                        passwordInput.show();
                        gameSession.pauseGame();
                    }

                    if (isNearComputer && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched() && toDraw) {
                        toDraw = false;
                        actionButton = new ButtonView(1100, 70, 70, 70, GameResources.ACTION_BUTTON_ACTIVE_IMG_PATH);
                    } else if (isNearComputer && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                        toDraw = true;
                        actionButton = new ButtonView(1100, 70, 70, 70, GameResources.RED_ACTION_BUTTON_IMG_PATH);
                    }

                    // Handle Vigenere game UI
                    if (toDraw && isNearComputer && !isCipherGameActive) {
                        if (startCipherGameButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            startCipherGame();
                        }
                    }

                    if (isCipherGameActive) {
                        if (submitCodeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            submitAnswer();
                        }
                        if (nextChallengeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            loadNextChallenge();
                        }
                    }

                    if (isNearBattery && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                        toDrawSave = true;
                    } else if (!isNearBattery || saveView.cancelButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        toDrawSave = false;
                    }

                    if (isNearBattery && saveView.saveButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                        MemoryManager.saveGameState(2, heroObject.getX(), heroObject.getY());
                        toDrawSave = false;
                    }

                    if (dialog != null && dialog.getCnt() == 6) {
                        dialogOkNoView = new DialogOkNoView(myGdxGame,
                            (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                            GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                            GameSettings.SCREEN_HEIGHT / 4f,
                            "Ты хочешь начать обучение шифру Виженера,\nчтобы вернуться в свой мир?");
                        dialog.nextButton.hide();
                        dialog.addCnt();
                    }

                    if (!isNearAntivirus) {
                        dialog = null;
                    }

                    if (!isTouchingUI) {
                        touchpadView.update(myGdxGame.touch.x, myGdxGame.touch.y, true);
                        if (touchpadView.isActive()) {
                            heroObject.moveWithTouchpad(touchpadView.getDirection(), touchpadView.getStrength());
                        }
                    } else {
                        touchpadView.reset();
                    }
                } else {
                    touchpadView.reset();
                    heroObject.stop();
                }
                break;

            case PAUSED:
                if (passwordInput != null && !passwordInput.isVisible()) {
                    gameSession.resumeGame();
                    toDrawPassword = false;
                }
                if (toDrawPassword) {
                    passwordInput.update(delta);
                    passwordInput.handleTouch();
                }
                if (isTouched) {
                    if (!toDrawPassword && continueButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        gameSession.resumeGame();
                    }
                    if (!toDrawPassword && homeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        myGdxGame.setScreen(myGdxGame.menuScreen);
                    }
                }
                break;

            case ENDED:
                if (isTouched) {
                    if (homeButton2.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        myGdxGame.setScreen(myGdxGame.menuScreen);
                    }
                }
                break;
        }
    }

    private void draw() {
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);
        tiledMapManager.render();

        myGdxGame.batch.begin();
        antiVirus.draw(myGdxGame.batch);
        heroObject.draw(myGdxGame.batch);
        if (isNearAntivirus) {
            message.draw(myGdxGame.batch);
        }
        asciiTable.draw(myGdxGame.batch);
        batteryObject.draw(myGdxGame.batch);
        doorDown.draw(myGdxGame.batch);
        myGdxGame.batch.end();

        myGdxGame.uiCamera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.uiCamera.combined);

        myGdxGame.batch.begin();
        if (dialog != null) {
            dialog.draw(myGdxGame.batch);
        }

        if (isCipherGameActive) {
            cipherGamePanel.draw(myGdxGame.batch);
            challengeTitle.draw(myGdxGame.batch);
            encryptedCodeView.draw(myGdxGame.batch);
            keyDecreaseButton.draw(myGdxGame.batch);
            keyIncreaseButton.draw(myGdxGame.batch);
            submitCodeButton.draw(myGdxGame.batch);
            if (nextChallengeButton != null) {
                nextChallengeButton.draw(myGdxGame.batch);
            }
            hintView.draw(myGdxGame.batch);
        }

        if (gameSession.state == GameState.PAUSED) {
            if (!toDrawPassword) {
                pauseTextView.draw(myGdxGame.batch);
                homeButton.draw(myGdxGame.batch);
                continueButton.draw(myGdxGame.batch);
            } else {
                passwordInput.draw(myGdxGame.batch);
            }
        } else if (gameSession.state == GameState.ENDED) {
            recordsTextView.draw(myGdxGame.batch);
            recordsListView.draw(myGdxGame.batch);
            homeButton2.draw(myGdxGame.batch);
        } else if (gameSession.state == GameState.PLAYING) {
            touchpadView.draw(myGdxGame.batch);
            if (isNearAntivirus && dialog == null && dialogOkNoView == null && MemoryManager.loadAreSubtitlesOn()) {
                text.draw(myGdxGame.batch);
            }
        }
        if (toDraw && isNearComputer) {
            fon_image.draw(myGdxGame.batch);
            currentKeyView.draw(myGdxGame.batch);
            encryptedCodeView.draw(myGdxGame.batch);
            ct.draw(myGdxGame.batch);
            myGdxGame.batch.end();
            stage.draw();
            myGdxGame.batch.begin();
        }

        topBlackoutView.draw(myGdxGame.batch);   // всегда поверх
        actionButton.draw(myGdxGame.batch);
        pauseButton.draw(myGdxGame.batch);

        if (dialogNo != null) {
            dialogNo.draw(myGdxGame.batch);
        }
        if (dialogOkNoView != null) {
            dialogOkNoView.draw(myGdxGame.batch);
        }
        if (toDrawSave) {
            saveView.draw(myGdxGame.batch);
        }
        myGdxGame.batch.end();

        if (myGdxGame.debugMode) {
            myGdxGame.debugRenderer.render(myGdxGame.world, myGdxGame.camera.combined);
        }
    }

    private void updateTrash() {
        for (int i = 0; i < trashArray.size(); i++) {
            boolean hasToBeDestroyed = !trashArray.get(i).isAlive() || !trashArray.get(i).isInFrame();
            if (!trashArray.get(i).isAlive()) {
                gameSession.destructionRegistration();
                if (myGdxGame.audioManager.isSoundOn)
                    myGdxGame.audioManager.explosionSound.play(0.2f);
            }
            if (hasToBeDestroyed) {
                myGdxGame.world.destroyBody(trashArray.get(i).body);
                trashArray.remove(i--);
            }
        }
    }

    private void updateBullets() {
        for (int i = 0; i < bulletArray.size(); i++) {
            if (bulletArray.get(i).hasToBeDestroyed()) {
                myGdxGame.world.destroyBody(bulletArray.get(i).body);
                bulletArray.remove(i--);
            }
        }
    }

    private void restartGame() {
        for (int i = 0; i < trashArray.size(); i++) {
            myGdxGame.world.destroyBody(trashArray.get(i).body);
            trashArray.remove(i--);
        }

        if (heroObject != null) {
            myGdxGame.world.destroyBody(heroObject.body);
        }
        heroX = (heroX != -1f) ? heroX : GameSettings.SCREEN_WIDTH / 2 - 200;
        heroY = (heroY != -1f) ? heroY : 150;
        heroObject = new AnimatedHeroObject((int) heroX, (int) heroY, 64, 64, heroFrames, myGdxGame.world);
        bulletArray.clear();
        createMapBorders();
        gameSession.startGame();
    }

    @Override
    public void dispose() {
        heroSpriteSheet.dispose();
        touchpadView.dispose();
        tiledMapManager.dispose();
        stage.dispose();
    }

    private void createMapBorders() {
        float mapWidth = tiledMapManager.getMapWidthPixels() * tiledMapManager.getUnitScale();
        float mapHeight = tiledMapManager.getMapHeightPixels() * tiledMapManager.getUnitScale();
        float wallThickness = 1f;
        createWall(mapWidth / 2, -wallThickness / 2 + 1.5f, mapWidth, wallThickness);
        createWall(mapWidth / 2, -wallThickness / 2 + 12, mapWidth, wallThickness);
        createWall(-wallThickness / 2 + 0.8f, mapHeight / 2, wallThickness, mapHeight);
        createWall(-wallThickness / 2 + 32.2f, mapHeight / 2, wallThickness, mapHeight);
    }

    private void createWall(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        Body body = myGdxGame.world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f, height / 2f);
        body.createFixture(shape, 0);
        shape.dispose();
    }
}
