package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.sql.Struct;
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
import kgu.game.project.managers.MemoryManager;
import kgu.game.project.managers.TiledMapManager;
import kgu.game.project.objects.BulletObject;

public class LevelOneScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;
    GameSession gameSession;
    HeroObject heroObject;

    ArrayList<TrashObject> trashArray;
    ArrayList<BulletObject> bulletArray;


    // PLAY state UI
    ImageView topBlackoutView;
    LiveView liveView;
    ButtonView pauseButton;
    TouchpadView touchpadView;  // Add touchpad

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

    // Track if touch is on UI elements
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

    public LevelOneScreen(MyGdxGame myGdxGame) {
        // удаляем все тела из игрового мира
        Array<Body> bodies = new Array<>();
        myGdxGame.world.getBodies(bodies);
        asciiTable = new ComputerObject(14, 9, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE, GameResources.ASCII_SPRITE_PATH, myGdxGame.world);
        passwordInput = new PasswordInputView(myGdxGame, () -> {
            gameSession.resumeGame();
            myGdxGame.setScreen(new LevelTwoScreen(myGdxGame));
        }, "FROSYA");
        // Удаляем все тела
        for (Body body : bodies) {
            myGdxGame.world.destroyBody(body);
        }
        talks = new Array<>();
        for (int i = 0; i <= 15; i++) {
            talks.add(LocalizationManager.get("talk." + i));
        }

        talks2 = new Array<>();
        for (int i = 0; i <= 7; i++) {
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


        tiledMapManager = new TiledMapManager(GameResources.TMX_MAP_LEVEL_ONE_PATH, myGdxGame.camera, myGdxGame.batch, 2);
        topBlackoutView = new ImageView(0, 656, 1280, 64, GameResources.BLACKOUT_TOP_IMG_PATH);
        liveView = new LiveView(305, 1215);
        pauseButton = new ButtonView(
            1200, 658,
            46, 54,
            GameResources.PAUSE_IMG_PATH
        );
        // Initialize touchpad in bottom left corner
        touchpadView = new TouchpadView(100, 100);  // Position at x=100, y=100

        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 525, 400, "Pause");
        homeButton = new ButtonView(
            350, 300,
            200, 35,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "Home"
        );
        text = new TextView(myGdxGame.commonPixelFontText, 250, 150, "Нажми зелёную кнопку, чтобы поговорить");
        continueButton = new ButtonView(
            GameSettings.SCREEN_WIDTH - 550, 300,
            200, 35,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "Continue"
        );
        actionButton = new ButtonView(1100, 70, 70, 70, GameResources.ACTION_BUTTON_IMG_PATH);
        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        recordsTextView = new TextView(myGdxGame.largeWhiteFont, 206, 842, "Last records");
        homeButton2 = new ButtonView(280, 365, 160, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        antiVirus = new AntivirusObject(GameResources.ANTIVIRUS_TEXTURE_PATH, 200, 200, 64, 64, GameSettings.ANTIVIRUS_BIT, myGdxGame.world);
        contactManager = new ContactManager(myGdxGame.world, (GameObject object) -> {
            // Определяем тип объекта
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
                    System.out.println("Left computer area");
                }
            });

        message = new ImageView(210, 210, GameResources.HI_MESSAGE_IMG_PATH);
        image = new ImageView(180, 0, 1028, 720, GameResources.ASCII_PATH);
        batteryObject = new BatteryObject(10, 9, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE, GameResources.BATTERY_BUTTON_IMG_PATH, myGdxGame.world);
        doorDown = new BatteryObject(18, 9, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE * 2, GameResources.DOOR_IMG_PATH, myGdxGame.world, GameSettings.DOOR_BIT);
    }

    public LevelOneScreen(MyGdxGame myGdxGame, float x, float y) {
        // удаляем все тела из игрового мира
        Array<Body> bodies = new Array<>();
        myGdxGame.world.getBodies(bodies);
        asciiTable = new ComputerObject(14, 9, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE, GameResources.ASCII_SPRITE_PATH, myGdxGame.world);

        // Удаляем все тела
        for (Body body : bodies) {
            myGdxGame.world.destroyBody(body);
        }
        talks = new Array<>();
        for (int i = 0; i <= 15; i++) {
            talks.add(LocalizationManager.get("talk." + i));
        }

        talks2 = new Array<>();
        for (int i = 0; i <= 7; i++) {
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


        tiledMapManager = new TiledMapManager(GameResources.TMX_MAP_LEVEL_ONE_PATH, myGdxGame.camera, myGdxGame.batch, 2);
        topBlackoutView = new ImageView(0, 1180, GameResources.BLACKOUT_TOP_IMG_PATH);
        liveView = new LiveView(305, 1215);
        pauseButton = new ButtonView(605, 1200, 46, 54, GameResources.PAUSE_IMG_PATH);

        // Initialize touchpad in bottom left corner
        touchpadView = new TouchpadView(100, 100);  // Position at x=100, y=100

        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 282, 842, "Pause");
        homeButton = new ButtonView(138, 695, 200, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        text = new TextView(myGdxGame.commonPixelFontText, 250, 150, "Нажми зелёную кнопку, чтобы поговорить");
        continueButton = new ButtonView(393, 695, 200, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Continue");
        actionButton = new ButtonView(1100, 70, 70, 70, GameResources.ACTION_BUTTON_IMG_PATH);
        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        recordsTextView = new TextView(myGdxGame.largeWhiteFont, 206, 842, "Last records");
        homeButton2 = new ButtonView(280, 365, 160, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        antiVirus = new AntivirusObject(GameResources.ANTIVIRUS_TEXTURE_PATH, 200, 200, 64, 64, GameSettings.ANTIVIRUS_BIT, myGdxGame.world);
        contactManager = new ContactManager(myGdxGame.world, (GameObject object) -> {
            // Определяем тип объекта
            if (object.getClass().getSimpleName().equals("AntivirusObject")) {
                isNearAntivirus = true;
            } else if (object.getClass().getSimpleName().equals("ComputerObject")) {
                isNearComputer = true;
            } else if (object.getBit() == GameSettings.DOOR_BIT) {
                isNearDoor = true;
                System.out.println("door");
            } else if (object.getClass().getSimpleName().equals("BatteryObject")) {
                isNearBattery = true;
            }
        },
            (GameObject object) -> {
                if (object.getClass().getSimpleName().equals("AntivirusObject") ||
                    object.getClass().getSimpleName().equals("ComputerObject")) {
                    isNearComputer = false;
                    isNearAntivirus = false;
                    isNearBattery = false;
                    isNearDoor = false;
                    System.out.println("Left computer area");
                }
            });

        message = new ImageView(210, 210, GameResources.HI_MESSAGE_IMG_PATH);
        image = new ImageView(180, 0, 1028, 720, GameResources.ASCII_PATH);
        batteryObject = new BatteryObject(10, 9, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE, GameResources.BATTERY_BUTTON_IMG_PATH, myGdxGame.world);
        doorDown = new BatteryObject(18, 9, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE * 2, GameResources.DOOR_IMG_PATH, myGdxGame.world, GameSettings.DOOR_BIT);
        heroX = x;
        heroY = y;
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
            myGdxGame.camera.update();
            gameSession.updateScore();
            liveView.setLeftLives(heroObject.getLiveLeft());

            myGdxGame.stepWorld();
            if (dialog != null && dialog.isToDispose()) {
                dialog.dispose();
                dialog = null;
            }
        } else if (gameSession.state == GameState.PLAYING) {

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
                            System.out.println(dialogOkNoView);
                            dialogOkNoView.dispose();
                            dialogOkNoView = null;
                            System.out.println(dialogOkNoView);
                            dialog.nextButton.show();
                        } else if (dialogOkNoView.noButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                            dialogNo = new DialogView(myGdxGame, (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0, GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f, GameSettings.SCREEN_HEIGHT / 4f, talks2);
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
                    // Check UI buttons first
                    if (pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        isTouchingUI = true;
                        gameSession.pauseGame();
                    }

                    // Для проверки компьютера используем только мировые координаты
                    if ((isNearAntivirus || isNearComputer || isNearBattery || isNearDoor) && !toDraw) {
                        actionButton = new ButtonView(1100, 70, 70, 70, GameResources.ACTION_BUTTON_ACTIVE_IMG_PATH);
                    } else if (!toDraw) {
                        actionButton = new ButtonView(1100, 70, 70, 70, GameResources.ACTION_BUTTON_IMG_PATH);
                    }
                    if (isNearAntivirus && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        dialog = new DialogView(myGdxGame, (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0, GameSettings.SCREEN_WIDTH - ((GameSettings.SCREEN_WIDTH) / 4f) - 200f, GameSettings.SCREEN_HEIGHT / 4f, talks);
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
                    if (isNearBattery && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && Gdx.input.justTouched()) {
                        toDrawSave = true;
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
                    if (!isNearAntivirus) {
                        dialog = null;
                    }

                    // Update touchpad and move ship if not touching UI
                    if (!isTouchingUI) {
                        touchpadView.update(myGdxGame.touch.x, myGdxGame.touch.y, true);

                        // Move ship using touchpad
                        if (touchpadView.isActive()) {
                            heroObject.moveWithTouchpad(touchpadView.getDirection(), touchpadView.getStrength());
                        }
                    } else {
                        // If touching UI, reset touchpad
                        touchpadView.reset();
                    }
                } else {
                    // No touch - reset touchpad and stop ship
                    touchpadView.reset();
                    heroObject.stop();
                }
                break;

            case PAUSED:
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
        if (doorDown != null){
            doorDown.draw(myGdxGame.batch);
        };
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
            touchpadView.draw(myGdxGame.batch);
            if (isNearAntivirus && dialog == null && dialogOkNoView == null && MemoryManager.loadAreSubtitlesOn()) {
                text.draw(myGdxGame.batch);
            }
        }
        topBlackoutView.draw(myGdxGame.batch);
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
    }

    private void createMapBorders() {
        float mapWidth = tiledMapManager.getMapWidthPixels() * tiledMapManager.getUnitScale();
        float mapHeight = tiledMapManager.getMapHeightPixels() * tiledMapManager.getUnitScale();

        // Увеличим толщину стен, чтобы игрок не мог проскочить
        float wallThickness = 1f;  // Увеличено с 1f до 10f

        // Нижняя стена
        createWall(mapWidth / 2, -wallThickness / 2 + 1.5f, mapWidth, wallThickness);
        createWall(mapWidth / 2, -wallThickness / 2 + 16, mapWidth, wallThickness);

        // Левая стена
        createWall(-wallThickness / 2 + 0.8f, mapHeight / 2, wallThickness, mapHeight);

        // Правая стена
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
