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
import java.util.Objects;

import kgu.game.project.GameResources;
import kgu.game.project.GameSession;
import kgu.game.project.GameSettings;
import kgu.game.project.GameState;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ButtonView;
import kgu.game.project.components.DialogView;
import kgu.game.project.components.ImageView;
import kgu.game.project.components.LiveView;
import kgu.game.project.components.PasswordInputView;
import kgu.game.project.components.QuestionView;
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

public class LevelSixScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;
    GameSession gameSession;
    HeroObject heroObject;
    QuestionView questionDialog;
    boolean toDrawQuestion = false;
    private int savedDialogCnt = 0;
    private int antivirusApproachCount = 0;
    Array<String> hint;

    ArrayList<TrashObject> trashArray;
    ArrayList<BulletObject> bulletArray;

    // Assembly learning components
    private final TextView assemblyCodeView;
    private final TextView registerView;
    private final TextView hintView;
    private final ButtonView nextInstructionButton;
    private final ButtonView prevInstructionButton;
    private final ButtonView executeButton;
    private final ButtonView resetButton;
    private final boolean isAssemblyGameActive = false;
    public Integer exitCnt;

    // Assembly simulation variables
    private String[] assemblyProgram = {
        "MOV AX, 5",
        "MOV BX, 3",
        "ADD AX, BX",
        "MOV CX, AX",
        "SUB CX, 2",
        "MOV DX, CX",
        "MUL DX, 2",
        "MOV RESULT, DX"
    };
    private int currentInstructionIndex = 0;
    private int registerAX = 0;
    private int registerBX = 0;
    private int registerCX = 0;
    private int registerDX = 0;
    private int resultValue = 0;
    private boolean isProgramComplete = false;

    ImageView topBlackoutView;
    LiveView liveView;
    ButtonView pauseButton;
    TouchpadView touchpadView;
    ImageView pauseBackground;
    TextView pauseTextView;
    ButtonView homeButton;
    ButtonView continueButton;

    TextView recordsTextView;
    RecordsListView recordsListView;
    ButtonView homeButton2;
    ButtonView actionButton;
    ButtonView actionButtonActive;
    ButtonView actionButtonRed;
    Texture heroSpriteSheet;
    TextureRegion[][] heroFrames;
    AntivirusObject antiVirus;
    ImageView message;
    ComputerObject asciiTable;
    private final TiledMapManager tiledMapManager;
    DialogView dialog;
    DialogView dialogNo;
    public boolean isNearComputer = false;

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
    ImageView assemblyPanel;
    TextView challengeTitle;
    TextView registerDisplay;
    TextView resultDisplay;
    TextView instructionDisplay;
    Stage stage;
    Table table;
    ImageView fon_image;
    Slider slider;
    boolean isDesktop;
    private boolean wasKKeyPressed = false;

    public LevelSixScreen(MyGdxGame myGdxGame) {
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

        // Updated dialogue for assembly theme
        String[] initialValues = {
            LocalizationManager.get("assembly.talk.0"),
            LocalizationManager.get("assembly.talk.1"),
            LocalizationManager.get("assembly.talk.2"),
            LocalizationManager.get("assembly.talk.3"),
            LocalizationManager.get("assembly.talk.4"),
            LocalizationManager.get("assembly.talk.5"),
            LocalizationManager.get("assembly.talk.6"),
            LocalizationManager.get("assembly.talk.7")
        };
        talks = new Array<>(initialValues);

        talks2 = new Array<>();
        talks2.add(LocalizationManager.get("xor.talk2.0"));
        talks2.add(LocalizationManager.get("xor.talk2.1"));

        hint = new Array<>();
        hint.add(LocalizationManager.get("hint.6.assembly"));

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

        pauseButton = new ButtonView(1200, 742, 64, 64, GameResources.PAUSE_IMG_PATH);

        touchpadView = new TouchpadView(140, 140);
        pauseBackground = new ImageView(480, 180, 300, 300, GameResources.PAUSE_BACKGROUND);

        pauseTextView = new TextView(myGdxGame.xanmonoFont, 580, 400, LocalizationManager.get("game.pause"));
        homeButton = new ButtonView(
            GameSettings.SCREEN_WIDTH - 750, 300,
            200, 35,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            LocalizationManager.get("game.home")
        );
        continueButton = new ButtonView(
            GameSettings.SCREEN_WIDTH - 750, 250,
            200, 35,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            LocalizationManager.get("game.continue")
        );
        actionButton = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_IMG_PATH);
        actionButtonActive = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_ACTIVE_IMG_PATH);
        actionButtonRed = new ButtonView(1100, 70, 140, 140, GameResources.RED_ACTION_BUTTON_IMG_PATH);
        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        recordsTextView = new TextView(myGdxGame.largeWhiteFont, 206, 842, "Last records");
        homeButton2 = new ButtonView(280, 365, 160, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        antiVirus = new AntivirusObject(GameResources.ANTIVIRUS_TEACHER_IMG_PATH, 200, 200, 128, 128, GameSettings.ANTIVIRUS_BIT, myGdxGame.world);

        // Assembly panel UI
        assemblyPanel = new ImageView(150, 200, 980, 500, GameResources.DIALOG_FON_IMG_PATH);

        challengeTitle = new TextView(myGdxGame.commonWhiteFont, 350, 600, "Assembly Language Learning");

        // Register display
        registerDisplay = new TextView(myGdxGame.consolasFont, 250, 530,
            "Registers: AX=0 BX=0 CX=0 DX=0");

        // Instruction display
        instructionDisplay = new TextView(myGdxGame.consolasFont, 250, 490,
            "Current: " + assemblyProgram[0]);

        // Result display
        resultDisplay = new TextView(myGdxGame.consolasFont, 250, 450,
            "Result: 0");

        // Assembly code viewer
        assemblyCodeView = new TextView(myGdxGame.consolasFont, 250, 380,
            "Program:\n" + String.join("\n", assemblyProgram));

        registerView = new TextView(myGdxGame.consolasFont, 250, 320,
            "Step through the program and watch registers change!");

        hintView = new TextView(myGdxGame.commonPixelFontText, 250, 280,
            "Use Prev/Next buttons to step through the program");

        // Navigation buttons
        prevInstructionButton = new ButtonView(350, 230, 120, 40,
            myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Prev");
        nextInstructionButton = new ButtonView(550, 230, 120, 40,
            myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Next");
        executeButton = new ButtonView(750, 230, 120, 40,
            myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Run All");
        resetButton = new ButtonView(450, 180, 120, 40,
            myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Reset");

        // Initially show only navigation buttons, hide execute and reset
        executeButton.hide();
        resetButton.hide();

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
                // Use slider value for something interesting in assembly context
                String binary = Integer.toBinaryString((int) value);
                if (binary.length() < 7) {
                    binary = "0" + binary;
                }
                registerDisplay.setText("Registers: AX=0 BX=0 CX=0 DX=" + ((int) value));
            }
        });

        isDesktop = Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.Desktop;

        if (isDesktop) {
            text = new TextView(myGdxGame.commonPixelFontText, 250, 150, LocalizationManager.get("pressK"));
        } else {
            text = new TextView(myGdxGame.commonPixelFontText, 250, 150, LocalizationManager.get("pressGreen"));
        }

        questionDialog = new QuestionView(myGdxGame,
            (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
            GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
            GameSettings.SCREEN_HEIGHT / 4f);
    }

    public LevelSixScreen(MyGdxGame myGdxGame, float x, float y) {
        this(myGdxGame);
        heroX = x;
        heroY = y;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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

    // Assembly execution logic
    private void executeInstruction(int index) {
        if (index >= assemblyProgram.length) {
            isProgramComplete = true;
            return;
        }

        String instruction = assemblyProgram[index];
        String[] parts = instruction.split(" ");
        String op = parts[0];
        String dest = parts[1];

        switch(op) {
            case "ADD":
                if (dest.equals("AX")) registerAX += registerBX;
                else if (dest.equals("BX")) registerBX += registerAX;
                else if (dest.equals("CX")) registerCX += registerAX;
                else if (dest.equals("DX")) registerDX += registerAX;
                break;
            case "SUB":
                if (dest.equals("AX")) registerAX -= registerBX;
                else if (dest.equals("BX")) registerBX -= registerAX;
                else if (dest.equals("CX")) registerCX -= 2;
                else if (dest.equals("DX")) registerDX -= 2;
                break;
            case "MUL":
                if (dest.equals("DX")) registerDX *= 2;
                else if (dest.equals("AX")) registerAX *= 2;
                else if (dest.equals("BX")) registerBX *= 2;
                else if (dest.equals("CX")) registerCX *= 2;
                break;
            case "MOV":
                if (dest.equals("RESULT")) resultValue = registerDX;
                break;
            default:
                break;
        }

        updateDisplay();
    }

    private void updateDisplay() {
        registerDisplay.setText(String.format("Registers: AX=%d BX=%d CX=%d DX=%d",
            registerAX, registerBX, registerCX, registerDX));
        instructionDisplay.setText("Current: " + (currentInstructionIndex < assemblyProgram.length ?
            assemblyProgram[currentInstructionIndex] : "Program Complete"));
        resultDisplay.setText("Result: " + resultValue);
    }

    private void resetAssembly() {
        registerAX = 0;
        registerBX = 0;
        registerCX = 0;
        registerDX = 0;
        resultValue = 0;
        currentInstructionIndex = 0;
        isProgramComplete = false;
        updateDisplay();
        nextInstructionButton.show();
        prevInstructionButton.show();
        executeButton.show();
        resetButton.show();
    }

    private void runAllInstructions() {
        for (int i = currentInstructionIndex; i < assemblyProgram.length; i++) {
            executeInstruction(i);
        }
        currentInstructionIndex = assemblyProgram.length;
        isProgramComplete = true;
        updateDisplay();
        // Show completion message
        challengeTitle.setText("Assembly Program Complete!");
        nextInstructionButton.hide();
        prevInstructionButton.hide();
    }

    private void stepNextInstruction() {
        if (currentInstructionIndex < assemblyProgram.length) {
            executeInstruction(currentInstructionIndex);
            currentInstructionIndex++;
            if (currentInstructionIndex >= assemblyProgram.length) {
                isProgramComplete = true;
                challengeTitle.setText("Program Complete! Check the result!");
                nextInstructionButton.hide();
                prevInstructionButton.hide();
                executeButton.hide();
                resetButton.hide();
            }
            updateDisplay();
        }
    }

    private void stepPrevInstruction() {
        if (currentInstructionIndex > 0) {
            // Reset to previous state - we'll reset and re-execute up to previous instruction
            resetAssembly();
            for (int i = 0; i < currentInstructionIndex - 1; i++) {
                executeInstruction(i);
            }
            currentInstructionIndex--;
            updateDisplay();
            challengeTitle.setText("Assembly Language Learning");
            nextInstructionButton.show();
            prevInstructionButton.show();
            executeButton.show();
            resetButton.show();
        }
    }

    private void handleDesktopAction() {
        boolean isKKeyPressed = Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.K);
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            gameSession.pauseGame();
        }

        if (isKKeyPressed && !wasKKeyPressed) {
            if (isNearAntivirus && dialog == null && dialogNo == null && !toDrawQuestion) {
                dialog = new DialogView(myGdxGame, (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                    GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                    GameSettings.SCREEN_HEIGHT / 4f, talks);
            } else if (isNearComputer && dialog == null && dialogNo == null) {
                toDraw = !toDraw;
                if (toDraw) {
                    resetAssembly();
                }
            } else if (isNearDoor && !toDrawPassword && dialog == null && dialogNo == null) {
                toDrawPassword = true;
                passwordInput.show();
                gameSession.pauseGame();
            } else if (isNearBattery && !toDrawSave && dialog == null && dialogNo == null) {
                toDrawSave = true;
                if (myGdxGame.audioManager.isSoundOn && myGdxGame.audioManager.saveSound != null) {
                    myGdxGame.audioManager.saveSound.play();
                }
            } else if (isNearBattery && toDrawSave && dialog == null && dialogNo == null) {
                toDrawSave = false;
            }
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
                recordsListView.setRecords(Objects.requireNonNull(MemoryManager.loadRecordsTable()));
            }
            updateTrash();
            updateBullets();
            int x, y;
            x = heroObject.getX();
            y = heroObject.getY();
            if (heroObject.getX() > 600) {
                x = 600;
            }
            if (heroObject.getX() < 565) {
                x = 565;
            }
            if (heroObject.getY() < 380) {
                y = 380;
            }
            System.out.println(x);
            myGdxGame.camera.position.set(
                x,
                y,
                0
            );
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
        }

        switch (gameSession.state) {
            case PLAYING:
                if (isDesktop) {
                    handleKeyboardInput();
                    if (isTouched) {
                        myGdxGame.touch = myGdxGame.uiCamera.unproject(
                            new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)
                        );
                        if (pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            gameSession.pauseGame();
                        }
                    }

                    if (dialogNo != null) {
                        if (isTouched && dialogNo.nextButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            dialogNo.addCnt();
                            if (dialogNo.getCnt() >= talks2.size) {
                                dialogNo = null;
                                dialog = null;
                            }
                        }
                    }

                    if (questionDialog != null) {
                        if (questionDialog.restartButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                            && Gdx.input.justTouched()) {
                            savedDialogCnt = 0;
                            dialog = new DialogView(myGdxGame,
                                (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                                GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                                GameSettings.SCREEN_HEIGHT / 4f, talks);
                            questionDialog = null;
                            toDrawQuestion = false;

                        } else if (questionDialog.continueButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                            && Gdx.input.justTouched()) {
                            dialog = new DialogView(myGdxGame,
                                (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                                GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                                GameSettings.SCREEN_HEIGHT / 4f, talks);
                            for (int i = 0; i < savedDialogCnt; i++) {
                                dialog.addCntAndUpdate();
                            }
                            questionDialog = null;
                            toDrawQuestion = false;

                        } else if (questionDialog.hintButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                            && Gdx.input.justTouched()) {
                            if (dialog != null) {
                                savedDialogCnt = 0;
                                dialog.dispose();
                                dialog = null;
                            }
                            dialog = new DialogView(myGdxGame, (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                                GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                                GameSettings.SCREEN_HEIGHT / 4f, hint);
                            questionDialog = null;
                            toDrawQuestion = false;

                        } else if (questionDialog.exitButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                            && Gdx.input.justTouched()) {
                            if (dialog != null) {
                                savedDialogCnt = dialog.getCnt();
                                dialog.dispose();
                                dialog = null;
                            }
                            questionDialog = null;
                            toDrawQuestion = false;
                        }
                    }

                    // Handle assembly buttons on desktop
                    if (toDraw) {
                        if (isTouched) {
                            if (nextInstructionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                                stepNextInstruction();
                            }
                            if (prevInstructionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                                stepPrevInstruction();
                            }
                            if (executeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                                runAllInstructions();
                            }
                            if (resetButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                                resetAssembly();
                            }
                        }
                    }

                    handleDesktopAction();

                    if (toDrawSave && isTouched && saveView.saveButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                        MemoryManager.saveGameState(6, heroObject.getX(), heroObject.getY());
                        toDrawSave = false;
                    }

                    if (toDrawSave && isTouched && saveView.cancelButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                        toDrawSave = false;
                    }

                    if (!isNearAntivirus) {
                        dialog = null;
                    }
                } else {
                    if (isTouched) {
                        boolean isTouchingUI = false;

                        if (dialogNo != null) {
                            if (isTouched && dialogNo.nextButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                                dialogNo.addCnt();
                                if (dialogNo.getCnt() >= talks2.size) {
                                    dialogNo = null;
                                    dialog = null;
                                }
                            }
                        }

                        if (questionDialog != null) {
                            if (questionDialog.restartButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                                && Gdx.input.justTouched()) {
                                savedDialogCnt = 0;
                                dialog = new DialogView(myGdxGame,
                                    (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                                    GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                                    GameSettings.SCREEN_HEIGHT / 4f, talks);
                                questionDialog = null;
                                toDrawQuestion = false;

                            } else if (questionDialog.continueButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                                && Gdx.input.justTouched()) {
                                dialog = new DialogView(myGdxGame,
                                    (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                                    GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                                    GameSettings.SCREEN_HEIGHT / 4f, talks);
                                for (int i = 0; i < savedDialogCnt; i++) {
                                    dialog.addCntAndUpdate();
                                }
                                questionDialog = null;
                                toDrawQuestion = false;

                            } else if (questionDialog.hintButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                                && Gdx.input.justTouched()) {
                                if (dialog != null) {
                                    savedDialogCnt = 0;
                                    dialog.dispose();
                                    dialog = null;
                                }
                                dialog = new DialogView(myGdxGame, (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                                    GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                                    GameSettings.SCREEN_HEIGHT / 4f, hint);
                                questionDialog = null;
                                toDrawQuestion = false;

                            } else if (questionDialog.exitButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                                && Gdx.input.justTouched()) {
                                if (dialog != null) {
                                    savedDialogCnt = dialog.getCnt();
                                    dialog.dispose();
                                    dialog = null;
                                }
                                questionDialog = null;
                                toDrawQuestion = false;
                            }
                        }

                        if (pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            isTouchingUI = true;
                            gameSession.pauseGame();
                        }

                        if ((isNearAntivirus || isNearComputer || isNearBattery || isNearDoor) && !toDraw) {
                            actionButtonActive = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_ACTIVE_IMG_PATH);
                        } else if (!toDraw) {
                            actionButtonActive = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_IMG_PATH);
                        }

                        if (dialog == null
                            && dialogNo == null
                            && isNearAntivirus
                            && actionButtonActive.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                            && Gdx.input.justTouched()) {

                            antivirusApproachCount++;

                            if (antivirusApproachCount >= 2) {
                                if (dialog != null) {
                                    savedDialogCnt = dialog.getCnt();
                                }
                                questionDialog = new QuestionView(myGdxGame,
                                    (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                                    GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                                    GameSettings.SCREEN_HEIGHT / 4f);
                                toDrawQuestion = true;
                            } else {
                                dialog = new DialogView(myGdxGame, (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                                    GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                                    GameSettings.SCREEN_HEIGHT / 4f, talks);
                            }
                        }

                        if (isNearDoor && actionButtonActive.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            toDrawPassword = true;
                            passwordInput.show();
                            gameSession.pauseGame();
                        }

                        if (isNearComputer && actionButtonActive.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched() && toDraw) {
                            toDraw = false;
                        } else if (isNearComputer && actionButtonActive.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            toDraw = true;
                            resetAssembly();
                        }

                        if (isNearBattery && actionButtonActive.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            toDrawSave = true;
                            if (myGdxGame.audioManager.isSoundOn && myGdxGame.audioManager.saveSound != null) {
                                myGdxGame.audioManager.saveSound.play();
                            }
                        } else if (!isNearBattery || saveView.cancelButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            toDrawSave = false;
                        }

                        if (isNearBattery && saveView.saveButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            if (MemoryManager.loadIsSoundOn()) {
                                myGdxGame.audioManager.saveSound.play();
                            }
                            MemoryManager.saveGameState(6, heroObject.getX(), heroObject.getY());
                            toDrawSave = false;
                        }

                        // Handle assembly buttons on mobile
                        if (toDraw && isTouched) {
                            if (nextInstructionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                                stepNextInstruction();
                            }
                            if (prevInstructionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                                stepPrevInstruction();
                            }
                            if (executeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                                runAllInstructions();
                            }
                            if (resetButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                                resetAssembly();
                            }
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
                if (passwordInput != null && !passwordInput.isVisible() && toDrawPassword) {
                    gameSession.resumeGame();
                    toDrawPassword = false;
                }
                if (toDrawPassword) {
                    assert passwordInput != null;
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

        if (toDrawQuestion && isNearAntivirus && questionDialog != null) {
            questionDialog.draw(myGdxGame.batch);
        }

        if (toDraw) {
            assemblyPanel.draw(myGdxGame.batch);
            challengeTitle.draw(myGdxGame.batch);
            registerDisplay.draw(myGdxGame.batch);
            instructionDisplay.draw(myGdxGame.batch);
            resultDisplay.draw(myGdxGame.batch);
            assemblyCodeView.draw(myGdxGame.batch);
            registerView.draw(myGdxGame.batch);
            hintView.draw(myGdxGame.batch);
            nextInstructionButton.draw(myGdxGame.batch);
            prevInstructionButton.draw(myGdxGame.batch);
            executeButton.draw(myGdxGame.batch);
            resetButton.draw(myGdxGame.batch);
        }

        if (gameSession.state == GameState.PAUSED) {
            if (!toDrawPassword) {
                pauseBackground.draw(myGdxGame.batch);
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

            if (!isDesktop) {
                ButtonView currentButton = actionButton;
                boolean nearSomething = isNearAntivirus || isNearComputer || isNearBattery || isNearDoor;
                if (nearSomething && !toDraw) {
                    currentButton = actionButtonActive;
                } else if (toDraw) {
                    currentButton = actionButtonRed;
                }
                currentButton.draw(myGdxGame.batch);
            }

            if (isNearAntivirus && dialog == null && questionDialog == null && MemoryManager.loadAreSubtitlesOn()) {
                text.draw(myGdxGame.batch);
            }
        }

        float uiHeight = myGdxGame.uiCamera.viewportHeight;
        float blackoutHeight = 64f;
        topBlackoutView.setPosition(0, uiHeight - blackoutHeight);
        topBlackoutView.draw(myGdxGame.batch);

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

        if (dialog != null && dialog.exitButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
            savedDialogCnt = dialog.getCnt();
            dialog.exitCnt();
            exitCnt = dialog.getCnt();
            System.out.println(exitCnt);
        }

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
        heroX = (heroX != -1f) ? heroX : (float) GameSettings.SCREEN_WIDTH / 2 - 200;
        heroY = (heroY != -1f) ? heroY : 150;
        heroObject = new AnimatedHeroObject((int) heroX, (int) heroY, 128, 128, heroFrames, myGdxGame.world);
        bulletArray.clear();
        createMapBorders();
        gameSession.startGame();
        wasKKeyPressed = false;
        antivirusApproachCount = 0;
        savedDialogCnt = 0;
        toDrawQuestion = false;
        questionDialog = null;
        dialog = null;
        dialogNo = null;

        // Reset assembly state
        resetAssembly();
    }

    @Override
    public void dispose() {
        heroSpriteSheet.dispose();
        touchpadView.dispose();
        tiledMapManager.dispose();
        stage.dispose();
        if (actionButton != null) actionButton.dispose();
        if (actionButtonActive != null) actionButtonActive.dispose();
        if (actionButtonRed != null) actionButtonRed.dispose();
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
