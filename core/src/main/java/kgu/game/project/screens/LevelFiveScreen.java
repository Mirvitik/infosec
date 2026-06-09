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
import kgu.game.project.components.DialogView;
import kgu.game.project.components.ImageView;
import kgu.game.project.components.LiveView;
import kgu.game.project.components.NetworkLogView;
import kgu.game.project.components.IpInputView;
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

public class LevelFiveScreen extends ScreenAdapter {

    private static final String ATTACKER_IP = "192.168.1.47";

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
    ButtonView actionButtonActive;
    ButtonView actionButtonRed;

    Texture heroSpriteSheet;
    TextureRegion[][] heroFrames;

    AntivirusObject routerObject;
    ImageView routerMessage;
    ComputerObject networkComputer;
    BatteryObject batteryObject;
    BatteryObject doorDown;

    private TiledMapManager tiledMapManager;

    DialogView dialog;

    NetworkLogView networkLogView;
    IpInputView ipInputView;
    boolean toDrawNetworkLogs = false;
    boolean ipInputActive = false;

    SaveView saveView = new SaveView(350, 50, 500, 600);
    boolean toDrawSave = false;

    Array<String> talks;

    boolean isNearComputer = false;
    Boolean isNearRouter = false;
    boolean isNearBattery = false;
    boolean isNearDoor = false;

    float heroX = -1f;
    float heroY = -1f;

    ContactManager contactManager;
    TextView hintText;
    TextView networkHintText;
    private Vector3 touch2;
    private boolean isTouchingUI = false;
    private boolean isDesktop;
    private boolean wasKKeyPressed = false;

    public LevelFiveScreen(MyGdxGame myGdxGame) {
        this(myGdxGame, -1f, -1f);
    }

    public LevelFiveScreen(MyGdxGame myGdxGame, float x, float y) {
        this.myGdxGame = myGdxGame;
        this.heroX = x;
        this.heroY = y;

        isDesktop = Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.Desktop;

        Array<Body> bodies = new Array<>();
        myGdxGame.world.getBodies(bodies);
        for (Body body : bodies) {
            myGdxGame.world.destroyBody(body);
        }
        bodies.clear();

        talks = new Array<>();
        for (int i = 0; i <= 15; i++) {
            talks.add(LocalizationManager.get("network.talk." + i));
        }

        gameSession = new GameSession();
        heroSpriteSheet = new Texture(GameResources.SPRITE_SHEET);
        heroFrames = TextureRegion.split(heroSpriteSheet, 32, 32);

        trashArray = new ArrayList<>();
        bulletArray = new ArrayList<>();

        tiledMapManager = new TiledMapManager(
            GameResources.TMX_MAP_LEVEL_FIVE_PATH,
            myGdxGame.camera, myGdxGame.batch, 4
        );

        topBlackoutView = new ImageView(0, 0, 1280, 64, GameResources.BLACKOUT_TOP_IMG_PATH);

        liveView = new LiveView(305, 1215);
        pauseButton = new ButtonView(
            GameSettings.SCREEN_WIDTH - 80,
            GameSettings.SCREEN_HEIGHT - 60,
            46, 54,
            GameResources.PAUSE_IMG_PATH
        );

        touchpadView = new TouchpadView(140, 140);

        actionButton = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_IMG_PATH);
        actionButtonActive = new ButtonView(1100, 70, 140, 140, GameResources.ACTION_BUTTON_ACTIVE_IMG_PATH);
        actionButtonRed = new ButtonView(1100, 70, 140, 140, GameResources.RED_ACTION_BUTTON_IMG_PATH);

        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 525, 400, LocalizationManager.get("pause"));
        homeButton = new ButtonView(350, 300, 200, 35, myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        continueButton = new ButtonView(GameSettings.SCREEN_WIDTH - 550, 300, 200, 35,
            myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Continue");

        recordsTextView = new TextView(myGdxGame.largeWhiteFont, 206, 842, "Last records");
        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        homeButton2 = new ButtonView(280, 365, 160, 70, myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");

        if (isDesktop) {
            hintText = new TextView(myGdxGame.commonPixelFontText, 250, 150, "Press K to interact");
            networkHintText = new TextView(myGdxGame.commonPixelFontText, 250, 100, "Find attacker IP in logs and press K at door!");
        } else {
            hintText = new TextView(myGdxGame.commonPixelFontText, 250, 150, "Нажми зелёную кнопку, чтобы взаимодействовать");
            networkHintText = new TextView(myGdxGame.commonPixelFontText, 250, 100, "Найди IP атакующего в логах и введи его у двери!");
        }

        routerObject = new AntivirusObject(
            GameResources.ANTIVIRUS_FIVE_TEXTURE_PATH,
            200, 200, 128, 128,
            GameSettings.ANTIVIRUS_BIT, myGdxGame.world
        );
        networkComputer = new ComputerObject(
            14, 6,
            GameSettings.TILE_SIZE, GameSettings.TILE_SIZE,
            GameResources.LOGS_IMG,
            myGdxGame.world
        );

        routerMessage = new ImageView(210, 210, GameResources.HI_MESSAGE_IMG_PATH);
        routerMessage.setSize(routerMessage.getTextureWidth() + 30, routerMessage.getTextureHeight() + 30);
        batteryObject = new BatteryObject(
            10, 6,
            GameSettings.TILE_SIZE, GameSettings.TILE_SIZE,
            GameResources.BATTERY_BUTTON_IMG_PATH, myGdxGame.world
        );

        doorDown = new DoorObject(
            1121, 448,
            GameSettings.TILE_SIZE, GameSettings.TILE_SIZE * 2,
            GameResources.SERVER_DOOR_IMG_PATH, myGdxGame.world, GameSettings.DOOR_BIT
        );

        networkLogView = new NetworkLogView(
            myGdxGame,
            28, 0, 1028, 720,
            ATTACKER_IP,
            () -> {
                ipInputActive = true;
                ipInputView.show();
            }
        );

        ipInputView = new IpInputView(
            myGdxGame,
            ATTACKER_IP,
            () -> {
                gameSession.resumeGame();
                myGdxGame.setScreen(new EndScreen(myGdxGame));
            },
            () -> {
            }
        );

        contactManager = new ContactManager(myGdxGame.world,
            (GameObject object) -> {
                if (object instanceof AntivirusObject) isNearRouter = true;
                else if (object instanceof ComputerObject) isNearComputer = true;
                else if (object instanceof DoorObject) isNearDoor = true;
                else if (object instanceof BatteryObject) isNearBattery = true;
            },
            (GameObject object) -> {
                if (object instanceof AntivirusObject || object instanceof ComputerObject
                    || object instanceof BatteryObject) {
                    isNearComputer = false;
                    isNearRouter = false;
                    isNearBattery = false;
                    isNearDoor = false;
                }
            }
        );
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
        isDesktop = Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.Desktop;
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            gameSession.pauseGame();
        }

        if (isKKeyPressed && !wasKKeyPressed) {
            if (isNearRouter && dialog == null) {
                dialog = new DialogView(myGdxGame,
                    (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                    GameSettings.SCREEN_WIDTH - (GameSettings.SCREEN_WIDTH / 4f) - 200f,
                    GameSettings.SCREEN_HEIGHT / 4f, talks);
            } else if (isNearComputer) {
                toDrawNetworkLogs = !toDrawNetworkLogs;
            } else if (isNearDoor && !ipInputActive && dialog == null) {
                ipInputActive = true;
                ipInputView.show();
                gameSession.pauseGame();
            } else if (isNearBattery && !toDrawSave && dialog == null) {
                toDrawSave = true;
                if (myGdxGame.audioManager.isSoundOn && myGdxGame.audioManager.saveSound != null) {
                    myGdxGame.audioManager.saveSound.play();
                }
            } else if (isNearBattery && toDrawSave && dialog == null) {
                toDrawSave = false;
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
            myGdxGame.touch = myGdxGame.uiCamera.unproject(
                new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            touch2 = myGdxGame.camera.unproject(
                new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        }

        switch (gameSession.state) {
            case PLAYING:
                if (isDesktop) {
                    handleKeyboardInput();
                    if (isTouched && pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                        gameSession.pauseGame();
                    }
                    handleDesktopAction();

                    if (toDrawSave && isTouched && saveView.saveButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                        MemoryManager.saveGameState(5, heroObject.getX(), heroObject.getY());
                        toDrawSave = false;
                    }

                    if (!isNearRouter) {
                        dialog = null;
                    }
                } else {
                    if (isTouched) {
                        isTouchingUI = false;

                        if (pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            isTouchingUI = true;
                            gameSession.pauseGame();
                        }

                        if (isNearRouter && actionButtonActive.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            dialog = new DialogView(myGdxGame,
                                (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                                GameSettings.SCREEN_WIDTH - (GameSettings.SCREEN_WIDTH / 4f) - 200f,
                                GameSettings.SCREEN_HEIGHT / 4f, talks);
                        }

                        if (isNearComputer && actionButtonActive.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            toDrawNetworkLogs = !toDrawNetworkLogs;
                        }

                        if (isNearDoor && actionButtonActive.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            ipInputActive = true;
                            ipInputView.show();
                            gameSession.pauseGame();
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
                            MemoryManager.saveGameState(5, heroObject.getX(), heroObject.getY());
                            toDrawSave = false;
                        }

                        if (!isNearRouter) {
                            dialog = null;
                        }

                        if (!isTouchingUI) {
                            touchpadView.update(myGdxGame.touch.x, myGdxGame.touch.y, true);
                            if (touchpadView.isActive() && !toDrawSave && dialog == null && !toDrawNetworkLogs) {
                                heroObject.moveWithTouchpad(touchpadView.getDirection(),
                                    touchpadView.getStrength());
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
                if (ipInputActive) {
                    ipInputView.update(delta);
                    ipInputView.handleTouch();
                }
                if (ipInputView != null && !ipInputView.isVisible() && ipInputActive) {
                    ipInputActive = false;
                    gameSession.resumeGame();
                }
                if (isTouched) {
                    if (!ipInputActive && continueButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        gameSession.resumeGame();
                    }
                    if (!ipInputActive && homeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        myGdxGame.setScreen(myGdxGame.menuScreen);
                    }
                }
                if (isDesktop && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
                    if (!ipInputActive) {
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
        routerObject.draw(myGdxGame.batch);
        if (doorDown != null) {
            doorDown.draw(myGdxGame.batch);
        }
        heroObject.draw(myGdxGame.batch);
        if (isNearRouter) {
            routerMessage.draw(myGdxGame.batch);
        }
        networkComputer.draw(myGdxGame.batch);
        batteryObject.draw(myGdxGame.batch);
        myGdxGame.batch.end();

        myGdxGame.uiCamera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.uiCamera.combined);
        myGdxGame.batch.begin();

        if (dialog != null) {
            dialog.draw(myGdxGame.batch);
        }

        if (toDrawNetworkLogs) {
            networkLogView.draw(myGdxGame.batch);
        }

        if (gameSession.state == GameState.PAUSED) {
            if (!ipInputActive) {
                pauseTextView.draw(myGdxGame.batch);
                homeButton.draw(myGdxGame.batch);
                continueButton.draw(myGdxGame.batch);
            } else {
                ipInputView.draw(myGdxGame.batch);
            }
        } else if (gameSession.state == GameState.ENDED) {
            recordsTextView.draw(myGdxGame.batch);
            recordsListView.draw(myGdxGame.batch);
            homeButton2.draw(myGdxGame.batch);
        } else if (gameSession.state == GameState.PLAYING) {
            if (!isDesktop) {
                touchpadView.draw(myGdxGame.batch);
            }

            ButtonView currentButton = actionButton;
            if (!isDesktop) {
                boolean nearSomething = isNearRouter || isNearComputer || isNearBattery || isNearDoor;
                if (nearSomething && !toDrawNetworkLogs) {
                    currentButton = actionButtonActive;
                } else if (toDrawNetworkLogs) {
                    currentButton = actionButtonRed;
                }
                currentButton.draw(myGdxGame.batch);
            }

            if (toDrawNetworkLogs) {
                networkHintText.draw(myGdxGame.batch);
            }
        }

        float uiHeight = myGdxGame.uiCamera.viewportHeight;
        float blackoutHeight = 64f;
        topBlackoutView.setPosition(0, uiHeight - blackoutHeight);
        topBlackoutView.draw(myGdxGame.batch);
        if (!isDesktop) {
            pauseButton.draw(myGdxGame.batch);
        }
        liveView.draw(myGdxGame.batch);

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
            boolean destroy = !trashArray.get(i).isAlive() || !trashArray.get(i).isInFrame();
            if (!trashArray.get(i).isAlive()) {
                gameSession.destructionRegistration();
                if (myGdxGame.audioManager.isSoundOn)
                    myGdxGame.audioManager.explosionSound.play(0.2f);
            }
            if (destroy) {
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
        heroX = (heroX != -1f) ? heroX : GameSettings.SCREEN_WIDTH / 2f - 200;
        heroY = (heroY != -1f) ? heroY : 150;
        heroObject = new AnimatedHeroObject((int) heroX, (int) heroY, 128, 128,
            heroFrames, myGdxGame.world);
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
        if (actionButton != null) actionButton.dispose();
        if (actionButtonActive != null) actionButtonActive.dispose();
        if (actionButtonRed != null) actionButtonRed.dispose();
    }

    private void createMapBorders() {
        float mapWidth = tiledMapManager.getMapWidthPixels() * tiledMapManager.getUnitScale();
        float mapHeight = tiledMapManager.getMapHeightPixels() * tiledMapManager.getUnitScale();
        float wallThickness = 1f;
        createWall(mapWidth / 2, -wallThickness / 2 + 1f, mapWidth, wallThickness);
        createWall(mapWidth / 2, -wallThickness / 2 + 23.4f, mapWidth, wallThickness);
        createWall(-wallThickness / 2, mapHeight / 2, wallThickness, mapHeight);
        createWall(-wallThickness / 2 + 66f, mapHeight / 2, wallThickness, mapHeight);
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

    @Override
    public void hide() {
        myGdxGame.uiCamera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        myGdxGame.uiCamera.update();
    }
}
