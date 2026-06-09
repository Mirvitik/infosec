package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import kgu.game.project.GameResources;
import kgu.game.project.GameSession;
import kgu.game.project.GameSettings;
import kgu.game.project.GameState;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ButtonView;
import kgu.game.project.components.DialogView;
import kgu.game.project.components.HelloTrigger;
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
import kgu.game.project.objects.DoorObject;
import kgu.game.project.objects.GameObject;
import kgu.game.project.objects.HeroObject;
import kgu.game.project.objects.AntivirusObject;
import kgu.game.project.managers.MemoryManager;
import kgu.game.project.managers.TiledMapManager;

public class LevelThreeScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;
    GameSession gameSession;
    HeroObject heroObject;

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
    short num = 0;
    ButtonView image;
    BatteryObject batteryObject;
    BatteryObject doorDown;
    SaveView saveView = new SaveView(350, 50, 500, 600);
    boolean isNearDoor;
    Boolean toDrawPassword = false;
    short cnt;
    PasswordInputView passwordInput;
    HelloTrigger helloTrigger;
    boolean isNearHello;
    HelloTrigger plusTrigger;
    ButtonView numberView;
    String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String alpha2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    TextView alphabet;
    TextView alphabet_ratated;
    ImageView arrow;
    ComputerObject file;
    BitmapFont font;
    boolean isDesktop;
    private boolean wasKKeyPressed = false;
    Array talksplayer = new Array<>();

    public LevelThreeScreen(MyGdxGame myGdxGame) {
        Array<Body> bodies = new Array<>();
        myGdxGame.world.getBodies(bodies);
        passwordInput = new PasswordInputView(myGdxGame, () -> {
            gameSession.resumeGame();
            if (myGdxGame.levelFourScreen != null) {
                myGdxGame.levelFourScreen.dispose();
            }
            myGdxGame.levelFourScreen = new LevelFourScreen(myGdxGame);
            myGdxGame.setScreen(myGdxGame.levelFourScreen);
        }, "EXTMRJ");

        for (Body body : bodies) {
            myGdxGame.world.destroyBody(body);
        }

        isDesktop = Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.Desktop;

        String[] initialValues = {
            LocalizationManager.get("vigenere.talk.0"),
            LocalizationManager.get("vigenere.talk.1"),
            LocalizationManager.get("vigenere.talk.2"),
            LocalizationManager.get("vigenere.talk.3"),
            LocalizationManager.get("vigenere.talk.4"),
            LocalizationManager.get("vigenere.talk.5"),
            LocalizationManager.get("vigenere.talk.6"),
            LocalizationManager.get("vigenere.talk.7")
        };
        talks = new Array<>(initialValues);
        bodies.clear();
        this.myGdxGame = myGdxGame;
        gameSession = new GameSession();
        heroSpriteSheet = new Texture(GameResources.SPRITE_SHEET);
        int frameWidth = 32;
        int frameHeight = 32;
        heroFrames = TextureRegion.split(heroSpriteSheet, frameWidth, frameHeight);
        file = new ComputerObject(14.5f, 5.5f, 64, 64, GameResources.FILE_IMG_PATH, myGdxGame.world);

        tiledMapManager = new TiledMapManager(GameResources.TMX_MAP_LEVEL_THREE_PATH, myGdxGame.camera, myGdxGame.batch, 4);
        topBlackoutView = new ImageView(0, 656, 1280, 64, GameResources.BLACKOUT_TOP_IMG_PATH);
        helloTrigger = new HelloTrigger(192, 260, 64, 64, GameResources.MINUS_BUTTON, myGdxGame.world, GameSettings.SENSOR_MINUS_BIT);
        plusTrigger = new HelloTrigger(320, 260, 64, 64, GameResources.PLUS_BUTTON, myGdxGame.world, GameSettings.SENSOR_PLUS_BIT);
        liveView = new LiveView(305, 1215);
        pauseButton = new ButtonView(1200, 658, 46, 54, GameResources.PAUSE_IMG_PATH);

        touchpadView = new TouchpadView(140, 140);

        pauseTextView = new TextView(myGdxGame.xanmonoFont, 525, 400, LocalizationManager.get("game.pause"));
        homeButton = new ButtonView(
            GameSettings.SCREEN_WIDTH - 750, 300,
            200, 35,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            LocalizationManager.get("game.home")
        );
        if (isDesktop) {
            text = new TextView(myGdxGame.commonPixelFontText, 250, 150, LocalizationManager.get("pressK"));
        } else {
            text = new TextView(myGdxGame.commonPixelFontText, 250, 150, LocalizationManager.get("pressGreen"));
        }

        continueButton = new ButtonView(GameSettings.SCREEN_WIDTH - 550, 300, 200, 35, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Continue");
        actionButton = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_IMG_PATH);
        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        recordsTextView = new TextView(myGdxGame.largeWhiteFont, 206, 842, "Last records");
        homeButton2 = new ButtonView(280, 365, 160, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        antiVirus = new AntivirusObject(GameResources.ANTIVIRUS_NAPOLEON_TEXTURE_PATH, 200, 200, 128, 128, GameSettings.ANTIVIRUS_BIT, myGdxGame.world);

        contactManager = new ContactManager(myGdxGame.world, (GameObject object) -> {
            if (object instanceof AntivirusObject) {
                isNearAntivirus = true;
            } else if (object instanceof ComputerObject) {
                isNearComputer = true;
            } else if (object instanceof DoorObject) {
                isNearDoor = true;
            } else if (object instanceof BatteryObject) {
                isNearBattery = true;
            } else if (object instanceof HelloTrigger) {
                isNearHello = true;
                if (object.getBit() == GameSettings.SENSOR_PLUS_BIT) {
                    if (cnt == 0 || cnt == -2 || cnt == -1) {
                        cnt = 1;
                    } else if (cnt == 1) {
                        cnt = 2;
                    }
                } else if (object.getBit() == GameSettings.SENSOR_MINUS_BIT) {
                    if (cnt == 0 || cnt == 2 || cnt == 1) {
                        cnt = -1;
                    } else if (cnt == -1) {
                        cnt = -2;
                    }
                }
            } else if (object.getBit() == GameSettings.DOOR_BIT) {
                isNearDoor = true;
            }
        },
            (GameObject object) -> {
                if (object instanceof AntivirusObject ||
                    object instanceof ComputerObject || object instanceof BatteryObject || object instanceof HelloTrigger) {
                    isNearComputer = false;
                    isNearAntivirus = false;
                    isNearBattery = false;
                    isNearDoor = false;
                    isNearHello = false;
                    cnt = 0;
                }
            });

        numberView = new ButtonView(256f, 260, 64, 64, new BitmapFont(), "textures/output.png", "0", 1);
        alphabet = new TextView(myGdxGame.consolasFont, 280, 550, alpha);
        alphabet_ratated = new TextView(myGdxGame.consolasFont, 280, 530, alpha2);
        alphabet.setText(alpha);
        message = new ImageView(210, 210, GameResources.HI_MESSAGE_IMG_PATH);
        message.setSize(message.getTextureWidth() + 30, message.getTextureHeight() + 30);
        image = new ButtonView(180, 80, 1028, 360, MyGdxGame.arialFont, GameResources.DIALOG_FON_IMG_PATH, "КЛЮЧ:    295\nТЕКСТ: COOKIE");
        arrow = new ImageView(230, 530, 32, 32, "textures/arrow.png");
        batteryObject = new BatteryObject(11.5f, 6.5f, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE, GameResources.BATTERY_BUTTON_IMG_PATH, myGdxGame.world);
        doorDown = new DoorObject(1186, 448, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE * 2, GameResources.ROME_DOOR_IMG_PATH, myGdxGame.world, GameSettings.DOOR_BIT);
        font = new BitmapFont();
        talksplayer.add(LocalizationManager.get("player.talk.level3.0"));
        talksplayer.add(LocalizationManager.get("player.talk.level3.1"));
        talksplayer.add(LocalizationManager.get("player.talk.level3.2"));
        talksplayer.add(LocalizationManager.get("player.talk.level3.3"));
        talksplayer.add(LocalizationManager.get("player.talk.level3.4"));
        dialog = new DialogView(myGdxGame, (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
            GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
            GameSettings.SCREEN_HEIGHT / 4f, talksplayer, GameResources.PLAYER_AVATAR_IMG_PATH, LocalizationManager.get("player.name"));

    }



    @Override
    public void show() {
        restartGame();
        Gdx.input.setInputProcessor(null);
    }

    private String shiftString(String str, int shift) {
        if (str == null || str.isEmpty()) return str;
        shift = shift % str.length();
        if (shift == 0) return str;
        if (shift > 0) {
            return str.substring(str.length() - shift) + str.substring(0, str.length() - shift);
        } else {
            shift = -shift;
            return str.substring(shift) + str.substring(0, shift);
        }
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
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            gameSession.pauseGame();
        }
        if (isKKeyPressed && !wasKKeyPressed && isNearAntivirus && dialog == null && dialogNo == null) {
            dialog = new DialogView(myGdxGame, (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                GameSettings.SCREEN_HEIGHT / 4f, talks);
        } else if (isKKeyPressed && !wasKKeyPressed && isNearDoor && !toDrawPassword && dialog == null && dialogNo == null) {
            toDrawPassword = true;
            passwordInput.show();
            gameSession.pauseGame();
        } else if (isKKeyPressed && !wasKKeyPressed && isNearBattery && !toDrawSave && dialog == null && dialogNo == null) {
            toDrawSave = true;
            if (myGdxGame.audioManager.isSoundOn && myGdxGame.audioManager.saveSound != null) {
                myGdxGame.audioManager.saveSound.play();
            }
        } else if (isKKeyPressed && !wasKKeyPressed && isNearComputer && toDraw && dialog == null && dialogNo == null) {
            toDraw = false;
        } else if (isKKeyPressed && !wasKKeyPressed && isNearComputer && !toDraw && dialog == null && dialogNo == null) {
            toDraw = true;
        }

        wasKKeyPressed = isKKeyPressed;
    }

    @Override
    public void render(float delta) {
        if (gameSession.state == GameState.PLAYING) {
            if (!heroObject.isAlive()) {
                gameSession.endGame();
                recordsListView.setRecords(MemoryManager.loadRecordsTable());
            }
            if (cnt != 2 && cnt != -2) {
                num -= cnt;
                if (cnt == 1) {
                    cnt = 2;
                } else if (cnt == -1) {
                    cnt = -2;
                }
            }
            alphabet_ratated.setText(shiftString(alpha, num));
            numberView.setText(String.valueOf(-num));

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
            myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        }

        switch (gameSession.state) {
            case PLAYING:
                if (isDesktop) {
                    handleKeyboardInput();


                    if (dialogNo != null) {
                        if (isTouched && dialogNo.nextButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            dialog = null;
                            dialogNo.addCnt();
                            if (dialogNo.getCnt() >= talks2.size) {
                                dialogNo = null;
                            }
                        }
                    }

                    if ((isNearAntivirus || isNearComputer || isNearBattery || isNearDoor) && !toDraw) {
                        actionButton = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_ACTIVE_IMG_PATH);
                    } else if (!toDraw) {
                        actionButton = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_IMG_PATH);
                    }

                    handleDesktopAction();


                    if (toDrawSave && isTouched && Gdx.input.justTouched()) {
                        if (saveView.saveButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            MemoryManager.saveGameState(3, heroObject.getX(), heroObject.getY());
                            toDrawSave = false;
                        } else if (saveView.cancelButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            toDrawSave = false;
                        }
                    }
                } else {
                    if (isTouched) {
                        boolean isTouchingUI = false;

                        if (dialogNo != null) {
                            if (dialogNo.nextButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                                dialog = null;
                                dialogNo.addCnt();
                                if (dialogNo.getCnt() >= talks2.size) {
                                    dialogNo = null;
                                }
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
                        if (isNearAntivirus && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
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
                        if (isNearBattery && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            toDrawSave = true;
                            if (myGdxGame.audioManager.isSoundOn && myGdxGame.audioManager.saveSound != null) {
                                myGdxGame.audioManager.saveSound.play();
                            }
                        } else if (!isNearBattery || saveView.cancelButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            toDrawSave = false;
                        }
                        if (isNearBattery && saveView.saveButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            MemoryManager.saveGameState(3, heroObject.getX(), heroObject.getY());
                            toDrawSave = false;
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
        helloTrigger.draw(myGdxGame.batch);
        helloTrigger.drawOverlay(myGdxGame.batch);
        plusTrigger.draw(myGdxGame.batch);
        plusTrigger.drawOverlay(myGdxGame.batch);
        numberView.draw(myGdxGame.batch);
        antiVirus.draw(myGdxGame.batch);
        alphabet.draw(myGdxGame.batch);
        alphabet_ratated.draw(myGdxGame.batch);
        doorDown.draw(myGdxGame.batch);
        heroObject.draw(myGdxGame.batch);
        if (isNearAntivirus) {
            message.draw(myGdxGame.batch);
        }
        batteryObject.draw(myGdxGame.batch);
        arrow.draw(myGdxGame.batch);
        file.draw(myGdxGame.batch);
        myGdxGame.batch.end();

        myGdxGame.uiCamera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.uiCamera.combined);

        myGdxGame.batch.begin();
        if (dialog != null) {
            dialog.draw(myGdxGame.batch);
        }
        if (toDraw) {
            image.draw(myGdxGame.batch);
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
        topBlackoutView.draw(myGdxGame.batch);
        if (!isDesktop) {
            actionButton.draw(myGdxGame.batch);
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

    private void restartGame() {
        if (heroObject != null) {
            myGdxGame.world.destroyBody(heroObject.body);
        }
        heroX = (heroX != -1f) ? heroX : (float) GameSettings.SCREEN_WIDTH / 2 - 100;
        heroY = (heroY != -1f) ? heroY : 250;
        heroObject = new AnimatedHeroObject((int) heroX, (int) heroY, 128, 128, heroFrames, myGdxGame.world);
        createMapBorders();
        gameSession.startGame();
        wasKKeyPressed = false;
    }

    @Override
    public void dispose() {
        heroSpriteSheet.dispose();
        touchpadView.dispose();
        tiledMapManager.dispose();
    }

    private void createMapBorders() {
        float mapWidth = tiledMapManager.getMapWidthPixels() * tiledMapManager.getUnitScale();
        float mapHeight = tiledMapManager.getMapHeightPixels() * tiledMapManager.getUnitScale();
        float wallThickness = 1f;
        createWall(mapWidth / 2, -wallThickness / 2 + 4f, mapWidth, wallThickness);
        createWall(mapWidth / 2, -wallThickness / 2 + 25, mapWidth, wallThickness);
        createWall(-wallThickness / 2 + 2f, mapHeight / 2, wallThickness, mapHeight);
        createWall(-wallThickness / 2 + 63f, mapHeight / 2, wallThickness, mapHeight);
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
