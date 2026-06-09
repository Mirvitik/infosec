package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
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
import kgu.game.project.managers.LocalizationManager;
import kgu.game.project.objects.AnimatedHeroObject;
import kgu.game.project.objects.BatteryObject;
import kgu.game.project.objects.ComputerObject;
import kgu.game.project.objects.GameObject;
import kgu.game.project.objects.HeroObject;
import kgu.game.project.objects.TrashObject;
import kgu.game.project.objects.AntivirusObject;
import kgu.game.project.objects.DoorObject;
import kgu.game.project.managers.MemoryManager;
import kgu.game.project.managers.TiledMapManager;
import kgu.game.project.objects.BulletObject;

public class LevelFourScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;
    GameSession gameSession;
    HeroObject heroObject;

    ArrayList<TrashObject> trashArray;
    ArrayList<BulletObject> bulletArray;

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

    private String[] challenges = {
        "WKLV", "LV", "D", "YLJHQHUH", "FLSKHU"
    };

    private String[] challengeDecrypted = {
        "THIS", "IS", "A", "VIGENERE", "CIPHER"
    };

    ImageView topBlackoutView;
    LiveView liveView;
    ButtonView pauseButton;
    TouchpadView touchpadView;

    TextView pauseTextView;
    ButtonView homeButton;
    ButtonView continueButton;

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
    ContactManager contactManager;
    TextView text;
    Array<String> talks;
    Array<String> talks2;
    Boolean isNearAntivirus = false;
    boolean toDraw = false;
    boolean toDrawSave = false;
    boolean isNearBattery;
    float heroX = -1f;
    float heroY = -1f;
    ImageView image;
    BatteryObject batteryObject;
    DoorObject doorDown;
    SaveView saveView = new SaveView(350, 50, 500, 600);
    boolean isNearDoor;
    Boolean toDrawPassword = false;
    PasswordInputView passwordInput;
    ImageView cipherGamePanel;
    TextView challengeTitle;
    TextView ct;
    Stage stage;
    Table table;
    ImageView fon_image;
    Slider slider;
    boolean isDesktop;
    private boolean wasKKeyPressed = false;

    public LevelFourScreen(MyGdxGame myGdxGame) {
        Array<Body> bodies = new Array<>();
        myGdxGame.world.getBodies(bodies);
        asciiTable = new ComputerObject(12, 5, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE, GameResources.XOR_IMG, myGdxGame.world);
        passwordInput = new PasswordInputView(myGdxGame, () -> {
            gameSession.resumeGame();
            myGdxGame.setScreen(new LevelFiveScreen(myGdxGame));
        }, "123");

        for (Body body : bodies) {
            myGdxGame.world.destroyBody(body);
        }

        String[] initialValues = {
            LocalizationManager.get("xor.talk.0"),
            LocalizationManager.get("xor.talk.1"),
            LocalizationManager.get("xor.talk.2"),
            LocalizationManager.get("xor.talk.3"),
            LocalizationManager.get("xor.talk.4"),
            LocalizationManager.get("xor.talk.5"),
            LocalizationManager.get("xor.talk.6"),
            LocalizationManager.get("xor.talk.7")
        };
        talks = new Array<>(initialValues);
        bodies.clear();
        this.myGdxGame = myGdxGame;
        gameSession = new GameSession();
        heroSpriteSheet = new Texture(GameResources.SPRITE_SHEET);
        int frameWidth = 32;
        int frameHeight = 32;
        heroFrames = TextureRegion.split(heroSpriteSheet, frameWidth, frameHeight);

        trashArray = new ArrayList<>();
        bulletArray = new ArrayList<>();

        tiledMapManager = new TiledMapManager(GameResources.TMX_MAP_LEVEL_FOUR_PATH, myGdxGame.camera, myGdxGame.batch, 3.5f);
        topBlackoutView = new ImageView(0, 0, 1280, 64, GameResources.BLACKOUT_TOP_IMG_PATH);
        liveView = new LiveView(305, 1215);

        pauseButton = new ButtonView(1200, 742, 46, 54, GameResources.PAUSE_IMG_PATH);

        touchpadView = new TouchpadView(140, 140);

        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 525, 400, LocalizationManager.get("pause"));
        homeButton = new ButtonView(350, 300, 200, 35, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        continueButton = new ButtonView(GameSettings.SCREEN_WIDTH - 550, 300, 200, 35, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Continue");
        actionButton = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_IMG_PATH);
        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        recordsTextView = new TextView(myGdxGame.largeWhiteFont, 206, 842, "Last records");
        homeButton2 = new ButtonView(280, 365, 160, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        antiVirus = new AntivirusObject(GameResources.ANTIVIRUS_TEACHER_IMG_PATH, 200, 200, 128, 128, GameSettings.ANTIVIRUS_BIT, myGdxGame.world);

        cipherGamePanel = new ImageView(200, 300, 880, 400, GameResources.DIALOG_FON_IMG_PATH);
        encryptedCodeView = new TextView(myGdxGame.consolasFont, 350, 550,
            LocalizationManager.get("xor.text.example"));
        currentKeyView = new TextView(myGdxGame.consolasFont, 350, 520,
            LocalizationManager.get("xor.key.example"));
        ct = new TextView(myGdxGame.consolasFont, 350, 480,
            LocalizationManager.get("xor.cipher.example"));
        hintView = new TextView(myGdxGame.commonPixelFontText, 350, 420, "Use +/- to adjust key (0-25)");
        challengeTitle = new TextView(myGdxGame.commonWhiteFont, 350, 600, "Decrypt the code!");

        keyIncreaseButton = new ButtonView(600, 460, 100, 50, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "+");
        keyDecreaseButton = new ButtonView(350, 460, 100, 50, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "-");
        submitCodeButton = new ButtonView(500, 350, 200, 50, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Submit");
        nextChallengeButton = new ButtonView(500, 350, 250, 50, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Next Challenge");
        nextChallengeButton.hide();

        contactManager = new ContactManager(myGdxGame.world, (GameObject object) -> {
            if (object instanceof AntivirusObject) {
                isNearAntivirus = true;
            } else if (object instanceof ComputerObject) {
                isNearComputer = true;
            } else if (object instanceof DoorObject) {
                isNearDoor = true;
            } else if (object instanceof BatteryObject) {
                isNearBattery = true;
            }
        },
            (GameObject object) -> {
                if (object instanceof AntivirusObject ||
                    object instanceof ComputerObject || object instanceof BatteryObject) {
                    isNearComputer = false;
                    isNearAntivirus = false;
                    isNearBattery = false;
                    isNearDoor = false;
                }
            });

        message = new ImageView(210, 210, GameResources.HI_MESSAGE_IMG_PATH);
        message.setSize(message.getTextureWidth() + 30, message.getTextureHeight() + 30);
        image = new ImageView(180, 0, 1028, 720, GameResources.ASCII_PATH);
        batteryObject = new BatteryObject(9, 5, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE, GameResources.BATTERY_BUTTON_IMG_PATH, myGdxGame.world);
        doorDown = new DoorObject(1032, 400, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE * 2, GameResources.FRENCH_DOOR_IMG_PATH, myGdxGame.world, GameSettings.DOOR_BIT);
        fon_image = new ImageView(300, 200, 600, 400, GameResources.DIALOG_FON_IMG_PATH);

        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);
        skin.add("white", Color.WHITE);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        skin.add("default", labelStyle);

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

        skin.add("default-horizontal", sliderStyle);

        slider = new Slider(33f, 126f, 1f, false, skin);

        Table sliderContainer = new Table();
        sliderContainer.add(slider).width(300);
        sliderContainer.pack();
        sliderContainer.setPosition(400, 300);

        sliderContainer.addListener(new DragListener() {
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
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
            myGdxGame.uiCamera
        ));
        stage.addActor(sliderContainer);

        Gdx.input.setInputProcessor(stage);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = slider.getValue();
                if (myGdxGame.audioManager.isSoundOn) {
                    myGdxGame.audioManager.shootSound.play();
                }
                String binary = Integer.toBinaryString((int) value);
                if (binary.length() < 7) {
                    binary = "0" + binary;
                }
                currentKeyView.setText("Key:    " + binary + " (" + ((int) value) + ") " + (char) value);
            }
        });

        isDesktop = Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.Desktop;

        if (isDesktop) {
            text = new TextView(myGdxGame.commonPixelFontText, 250, 150, LocalizationManager.get("pressK"));
        } else {
            text = new TextView(myGdxGame.commonPixelFontText, 250, 150,  LocalizationManager.get("pressGreen"));
        }
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





    private void updateDecryptedPreview() {
        String decrypted = decryptVigenere(targetEncryptedCode, currentKey);
        hintView.setText("Preview: " + decrypted + " (Key: " + currentKey + ")");
    }

    private void submitAnswer() {
        String userDecrypted = decryptVigenere(targetEncryptedCode, currentKey);
        if (userDecrypted.equals(expectedDecryptedCode)) {
            scoreForLevel++;
            if (currentChallengeIndex + 1 < totalChallenges) {
                currentChallengeIndex++;
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

    private void handleKeyboardInput() {
        Vector2 direction = new Vector2(0, 0);
        float strength = 0;

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W) ||
            Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) {
            direction.y = 1;
            strength = 2;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S) ||
            Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            direction.y = -1;
            strength = 2;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A) ||
            Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            direction.x = -1;
            strength = 2;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D) ||
            Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            direction.x = 1;
            strength = 2;
        }

        if (direction.x != 0 && direction.y != 0) {
            direction.nor();
        }

        if (strength > 0) {
            heroObject.moveWithTouchpad(direction, strength);
        } else {
            heroObject.stop();
        }
    }

    private void handleDesktopAction() {
        boolean isKKeyPressed = Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.K);

        if (isKKeyPressed && !wasKKeyPressed && isNearAntivirus && dialog == null && dialogNo == null) {
            dialog = new DialogView(myGdxGame, (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                GameSettings.SCREEN_HEIGHT / 4f, talks);
        }
        else if (isKKeyPressed && !wasKKeyPressed && isNearComputer && !toDraw && !isCipherGameActive && dialog == null && dialogNo == null) {
            toDraw = true;
        } else if (isKKeyPressed && !wasKKeyPressed && isNearComputer && toDraw && dialog == null && dialogNo == null) {
            toDraw = false;
        } else if (isKKeyPressed && !wasKKeyPressed && isNearDoor && !toDrawPassword && dialog == null && dialogNo == null) {
            toDrawPassword = true;
            passwordInput.show();
            gameSession.pauseGame();
        } else if (isKKeyPressed && !wasKKeyPressed && isNearBattery && !toDrawSave && dialog == null && dialogNo == null) {
            toDrawSave = true;
            if (myGdxGame.audioManager.isSoundOn && myGdxGame.audioManager.saveSound != null) {
                myGdxGame.audioManager.saveSound.play();
            }
        } else if (isKKeyPressed && !wasKKeyPressed && isNearBattery && toDrawSave && dialog == null && dialogNo == null) {
            toDrawSave = false;
        }

        wasKKeyPressed = isKKeyPressed;
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
                if (isDesktop) {
                    handleKeyboardInput();


                    if (dialogNo != null) {
                        if (isTouched && dialogNo.nextButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            dialog = null;
                            dialogNo = null;
                        }
                    }

                    if (dialog != null && dialog.getCnt() == 6) {
                        dialog.nextButton.hide();
                        dialog.addCnt();
                    }

                    if (isNearAntivirus && isNearComputer && !toDraw && !isCipherGameActive) {
                        actionButton = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_ACTIVE_IMG_PATH);
                    } else if (!toDraw) {
                        actionButton = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_IMG_PATH);
                    }

                    handleDesktopAction();



                    if (isCipherGameActive) {
                        if (isTouched && submitCodeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            submitAnswer();
                        }
                        if (isTouched && keyIncreaseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            currentKey++;
                            if (currentKey > 25) currentKey = 0;
                            updateDecryptedPreview();
                        }
                        if (isTouched && keyDecreaseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            currentKey--;
                            if (currentKey < 0) currentKey = 25;
                            updateDecryptedPreview();
                        }
                    }

                    if (toDrawSave && isTouched && saveView.saveButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                        MemoryManager.saveGameState(4, heroObject.getX(), heroObject.getY());
                        toDrawSave = false;
                    }
                } else {
                    if (isTouched) {
                        isTouchingUI = false;


                        if (dialogNo != null) {
                            if (dialogNo.nextButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                                dialog = null;
                                dialogNo = null;
                            }
                        }

                        if (pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            isTouchingUI = true;
                            gameSession.pauseGame();
                        }

                        if ((isNearAntivirus || isNearComputer || isNearBattery || isNearDoor) && !toDraw) {
                            actionButton = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_ACTIVE_IMG_PATH);
                        } else if (!toDraw) {
                            actionButton = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_IMG_PATH);
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
                            actionButton = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_ACTIVE_IMG_PATH);
                        } else if (isNearComputer && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            toDraw = true;
                            actionButton = new ButtonView(1100, 70, 140, 140, GameResources.RED_ACTION_BUTTON_IMG_PATH);
                        }


                        if (isCipherGameActive) {
                            if (submitCodeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                                submitAnswer();
                            }
                            if (keyIncreaseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                                currentKey++;
                                if (currentKey > 25) currentKey = 0;
                                updateDecryptedPreview();
                            }
                            if (keyDecreaseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                                currentKey--;
                                if (currentKey < 0) currentKey = 25;
                                updateDecryptedPreview();
                            }
                        }

                        if (isNearBattery && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            toDrawSave = true;
                            if (myGdxGame.audioManager.isSoundOn && myGdxGame.audioManager.saveSound != null) {
                                myGdxGame.audioManager.saveSound.play();
                            }
                        } else if (!isNearBattery || saveView.cancelButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            toDrawSave = false;
                        }

                        if (isNearBattery && saveView.saveButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            MemoryManager.saveGameState(2, heroObject.getX(), heroObject.getY());
                            toDrawSave = false;
                        }


                        if (!isNearAntivirus) {
                            dialog = null;
                        }

                        if (!isTouchingUI) {
                            touchpadView.update(myGdxGame.touch.x, myGdxGame.touch.y, true);
                            if (touchpadView.isActive() && !toDraw && !toDrawSave && dialog == null && dialogNo == null) {
                                heroObject.moveWithTouchpad(touchpadView.getDirection(), touchpadView.getStrength());
                            }
                        } else {
                            touchpadView.reset();
                        }
                    } else {
                        touchpadView.reset();
                        heroObject.stop();
                    }
                }
                break;

            case PAUSED:
                // без изменений
                if (passwordInput != null && !passwordInput.isVisible() && toDrawPassword) {
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
                if (isDesktop && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
                    if (!toDrawPassword) {
                        gameSession.resumeGame();
                    }
                }
                break;

            case ENDED:
                if (isTouched) {
                    if (homeButton2.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        myGdxGame.setScreen(myGdxGame.menuScreen);
                    }
                }
                if (isDesktop && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
                    myGdxGame.setScreen(myGdxGame.menuScreen);
                }
                break;
        }
    }

    private void draw() {
        if (myGdxGame.touch == null) {
            myGdxGame.touch = new Vector3();
        }
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);
        tiledMapManager.render();

        myGdxGame.batch.begin();
        asciiTable.draw(myGdxGame.batch);
        batteryObject.draw(myGdxGame.batch);
        antiVirus.draw(myGdxGame.batch);
        doorDown.draw(myGdxGame.batch);
        heroObject.draw(myGdxGame.batch);
        if (isNearAntivirus) {
            message.draw(myGdxGame.batch);
        }
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
            if (!isDesktop) {
                touchpadView.draw(myGdxGame.batch);
            }
            if (isNearAntivirus && dialog == null && MemoryManager.loadAreSubtitlesOn()) {
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
        float uiHeight = myGdxGame.uiCamera.viewportHeight;
        float blackoutHeight = 64f;
        topBlackoutView.setPosition(0, uiHeight - blackoutHeight);
        topBlackoutView.draw(myGdxGame.batch);

        if (!isDesktop) {
            actionButton.draw(myGdxGame.batch);
        }
        if (!isDesktop) {
            pauseButton.draw(myGdxGame.batch);
        }

        if (dialogNo != null) {
            dialogNo.draw(myGdxGame.batch);
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
        heroObject = new AnimatedHeroObject((int) heroX, (int) heroY, 128, 128, heroFrames, myGdxGame.world);
        bulletArray.clear();
        createMapBorders();
        gameSession.startGame();
        wasKKeyPressed = false;
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
        createWall(mapWidth / 2, -wallThickness / 2 + 3.5f, mapWidth, wallThickness);
        createWall(mapWidth / 2, -wallThickness / 2 + 23.4f, mapWidth, wallThickness);
        createWall(-wallThickness / 2 + 2f, mapHeight / 2, wallThickness, mapHeight);
        createWall(-wallThickness / 2 + 55f, mapHeight / 2, wallThickness, mapHeight);
    }

    @Override
    public void hide() {
        myGdxGame.uiCamera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        myGdxGame.uiCamera.update();
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
