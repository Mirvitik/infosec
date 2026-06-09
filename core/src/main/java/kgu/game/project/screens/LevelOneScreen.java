package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

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
import kgu.game.project.objects.DoorObject;
import kgu.game.project.objects.GameObject;
import kgu.game.project.objects.HeroObject;
import kgu.game.project.objects.TrashObject;
import kgu.game.project.objects.AntivirusObject;
import kgu.game.project.managers.MemoryManager;
import kgu.game.project.managers.TiledMapManager;
import kgu.game.project.objects.BulletObject;

public class LevelOneScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;
    GameSession gameSession;
    HeroObject heroObject;

    ArrayList<TrashObject> trashArray;
    ArrayList<BulletObject> bulletArray;

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
    DialogOkNoView dialogOkNoView;
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
    boolean isDesktop;
    Array talksplayer = new Array<>();
    private boolean wasKKeyPressed = false;

    public LevelOneScreen(MyGdxGame myGdxGame) {
        Array<Body> bodies = new Array<>();
        myGdxGame.world.getBodies(bodies);
        asciiTable = new ComputerObject(14, 9, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE, GameResources.ASCII_SPRITE_PATH, myGdxGame.world);
        passwordInput = new PasswordInputView(myGdxGame, () -> {
            gameSession.resumeGame();
            String[] story = {
                GameResources.STORY_TELLING_ANCIENT,
                GameResources.STORY_TELLING_SPQR
            };
            String[] texts = {
                LocalizationManager.get("caesar.history.0"),
                LocalizationManager.get("caesar.history.1")
            };
            myGdxGame.audioManager.backgroundMusic.stop();
            myGdxGame.audioManager.storyMusic.play();
            myGdxGame.setScreen(new CutsceneScreen(myGdxGame, story, texts, () -> {
                myGdxGame.audioManager.storyMusic.stop();
                myGdxGame.audioManager.backgroundMusic.play();
                myGdxGame.setScreen(new LevelTwoScreen(myGdxGame));
            }));
        }, "FROSYA");
        for (Body body : bodies) {
            myGdxGame.world.destroyBody(body);
        }

        isDesktop = Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.Desktop;

        talks = new Array<>();
        for (int i = 0; i <= 17; i++) {
            talks.add(LocalizationManager.get("talk." + i));
        }

        talks2 = new Array<>();
        for (int i = 0; i <= 1; i++) {
            talks2.add(LocalizationManager.get("talk2." + i));
        }

        bodies.clear();
        this.myGdxGame = myGdxGame;
        gameSession = new GameSession();
        heroSpriteSheet = new Texture(GameResources.SPRITE_SHEET);
        int frameWidth = 32;
        int frameHeight = 32;
        heroFrames = TextureRegion.split(heroSpriteSheet, frameWidth, frameHeight);

        trashArray = new ArrayList<>();
        bulletArray = new ArrayList<>();

        tiledMapManager = new TiledMapManager(GameResources.TMX_MAP_LEVEL_ONE_PATH, myGdxGame.camera, myGdxGame.batch, 4f);
        topBlackoutView = new ImageView(0, 656, 1280, 64, GameResources.BLACKOUT_TOP_IMG_PATH);
        liveView = new LiveView(305, 1215);
        pauseButton = new ButtonView(1200, 658, 46, 54, GameResources.PAUSE_IMG_PATH);
        touchpadView = new TouchpadView(120, 120);

        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 525, 400, "Pause");
        homeButton = new ButtonView(350, 300, 200, 35, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");

        if (isDesktop) {
            text = new TextView(myGdxGame.commonPixelFontText, 250, 150,  LocalizationManager.get("pressK"));
        } else {
            text = new TextView(myGdxGame.commonPixelFontText, 250, 150, LocalizationManager.get("pressGreen"));
        }

        continueButton = new ButtonView(GameSettings.SCREEN_WIDTH - 550, 300, 200, 35, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Continue");
        actionButton = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_IMG_PATH);
        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        recordsTextView = new TextView(myGdxGame.largeWhiteFont, 206, 842, "Last records");
        homeButton2 = new ButtonView(280, 365, 160, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        antiVirus = new AntivirusObject(GameResources.ANTIVIRUS_TEXTURE_PATH, 200, 200, 128, 128, GameSettings.ANTIVIRUS_BIT, myGdxGame.world);

        contactManager = new ContactManager(myGdxGame.world, (GameObject object) -> {
            if (object instanceof AntivirusObject) {
                isNearAntivirus = true;
            } else if (object instanceof DoorObject) {
                isNearDoor = true;
            } else if (object instanceof BatteryObject) {
                isNearBattery = true;
            } else if (object instanceof ComputerObject) {
                isNearComputer = true;
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
        batteryObject = new BatteryObject(10, 9, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE, GameResources.BATTERY_BUTTON_IMG_PATH, myGdxGame.world);
        doorDown = new DoorObject(1186, 576, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE * 2, GameResources.DOOR_IMG_PATH, myGdxGame.world, GameSettings.DOOR_BIT);
        talksplayer.add(LocalizationManager.get("player.talk.0"));
        talksplayer.add(LocalizationManager.get("player.talk.1"));
        talksplayer.add(LocalizationManager.get("player.talk.2"));
        talksplayer.add(LocalizationManager.get("player.talk.3"));
        talksplayer.add(LocalizationManager.get("player.talk.4"));
        dialog = new DialogView(myGdxGame, (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
            GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
            GameSettings.SCREEN_HEIGHT / 4f, talksplayer, GameResources.PLAYER_AVATAR_IMG_PATH, LocalizationManager.get("player.name"));
    }

    public LevelOneScreen(MyGdxGame myGdxGame, float x, float y) {
        this(myGdxGame);
        heroX = x;
        heroY = y;
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
        if (isKKeyPressed && !wasKKeyPressed) {
            if (isNearAntivirus && dialog == null && dialogOkNoView == null && dialogNo == null) {
                dialog = new DialogView(myGdxGame, (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                    GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                    GameSettings.SCREEN_HEIGHT / 4f, talks);
            } else if (isNearDoor && !toDrawPassword && dialog == null && dialogOkNoView == null) {
                toDrawPassword = true;
                passwordInput.show();
                gameSession.pauseGame();
            } else if (isNearBattery && !toDrawSave && dialog == null && dialogOkNoView == null) {
                toDrawSave = true;
                if (myGdxGame.audioManager.isSoundOn && myGdxGame.audioManager.saveSound != null) {
                    myGdxGame.audioManager.saveSound.play();
                }
            } else if (isNearComputer && dialog == null && dialogOkNoView == null) {
                if (toDraw) {
                    toDraw = false;
                } else {
                    toDraw = true;
                }
            }
        }
        wasKKeyPressed = isKKeyPressed;
    }

    @Override
    public void show() {
        restartGame();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
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
                    handleDesktopAction();

                    if (dialogOkNoView != null) {
                        if (isTouched && dialogOkNoView.okButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            dialogOkNoView.dispose();
                            dialogOkNoView = null;
                            if (dialog != null) {
                                dialog.nextButton.show();
                            }
                        } else if (isTouched && dialogOkNoView.noButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            dialogNo = new DialogView(myGdxGame, (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                                GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                                GameSettings.SCREEN_HEIGHT / 4f, talks2);
                            dialogOkNoView = null;
                        }
                    }

                    if (dialogNo != null) {
                        if (isTouched && dialogNo.nextButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            dialog = null;
                            dialogOkNoView = null;
                            dialogNo.addCnt();
                            if (dialogNo.getCnt() >= talks2.size) {
                                dialogNo = null;
                            }
                        }
                    }

                    if (dialog != null && dialog.getCnt() == 6) {
                        dialogOkNoView = new DialogOkNoView(myGdxGame,
                            (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                            GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                            GameSettings.SCREEN_HEIGHT / 4f,
                            LocalizationManager.get("talk2.6"));
                        dialog.nextButton.hide();
                        dialog.addCnt();
                    }

                    if (toDrawSave && isTouched && Gdx.input.justTouched()) {
                        if (saveView.saveButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            MemoryManager.saveGameState(1, heroObject.getX(), heroObject.getY());
                            toDrawSave = false;
                        } else if (saveView.cancelButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            toDrawSave = false;
                        }
                    }
                } else {
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
                                if (dialogOkNoView != null) dialogOkNoView.dispose();
                                if (dialog != null) dialog.dispose();
                                dialogOkNoView = null;
                                dialog = null;
                                return;
                            }
                        }
                        if (dialogNo != null) {
                            if (dialogNo.nextButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                                dialogNo.addCnt();
                                if (dialogNo.getCnt() >= talks2.size) {
                                    dialogNo = null;
                                    dialog = null;
                                    dialogOkNoView = null;
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
                        if (dialog == null
                            && dialogNo == null
                            && dialogOkNoView == null
                            && isNearAntivirus
                            && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                            && Gdx.input.justTouched()) {
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
                            MemoryManager.saveGameState(1, heroObject.getX(), heroObject.getY());
                            toDrawSave = false;
                        }
                        if (dialog != null && dialog.getCnt() == 6) {
                            dialogOkNoView = new DialogOkNoView(myGdxGame,
                                (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                                GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f,
                                GameSettings.SCREEN_HEIGHT / 4f,
                                LocalizationManager.get("talk2.6"));
                            dialog.nextButton.hide();
                            dialog.addCnt();
                        }

                        if (!isTouchingUI) {
                            touchpadView.update(myGdxGame.touch.x, myGdxGame.touch.y, true);
                            if (touchpadView.isActive() && !toDraw && !toDrawSave && dialog == null && dialogOkNoView == null && dialogNo == null) {
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
        antiVirus.draw(myGdxGame.batch);
        if (doorDown != null) {
            doorDown.draw(myGdxGame.batch);
        }
        heroObject.draw(myGdxGame.batch);
        if (isNearAntivirus) {
            message.draw(myGdxGame.batch);
        }
        asciiTable.draw(myGdxGame.batch);
        batteryObject.draw(myGdxGame.batch);
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
            if (isNearAntivirus && dialog == null && dialogOkNoView == null && MemoryManager.loadAreSubtitlesOn()) {
                text.draw(myGdxGame.batch);
            }
        }

        float uiHeight = myGdxGame.uiCamera.viewportHeight;
        float blackoutHeight = 64f;
        topBlackoutView.setPosition(0, uiHeight - blackoutHeight);
        topBlackoutView.draw(myGdxGame.batch);

        if (!isDesktop) {
            actionButton.draw(myGdxGame.batch);
            pauseButton.draw(myGdxGame.batch);
        }

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
        heroObject = new AnimatedHeroObject((int) heroX + 200, (int) heroY + 200, 128, 128, heroFrames, myGdxGame.world);
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
    }

    private void createMapBorders() {
        float mapWidth = tiledMapManager.getMapWidthPixels() * tiledMapManager.getUnitScale();
        float mapHeight = tiledMapManager.getMapHeightPixels() * tiledMapManager.getUnitScale();
        float wallThickness = 1f;
        createWall(mapWidth / 2, -wallThickness / 2 + 4f, mapWidth, wallThickness);
        createWall(mapWidth / 2, -wallThickness / 2 + 31, mapWidth, wallThickness);
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
