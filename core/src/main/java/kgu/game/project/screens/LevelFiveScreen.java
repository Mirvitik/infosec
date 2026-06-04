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
import kgu.game.project.components.NetworkLogView;   // <-- новый компонент с логами
import kgu.game.project.components.IpInputView;       // <-- новый компонент ввода IP
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

/**
 * LevelFiveScreen — уровень "Сети".
 * <p>
 * Сюжет: игрок оказывается внутри заражённой системы. Ему нужно подойти к
 * компьютеру, изучить сетевые логи и определить IP-адрес злоумышленника,
 * который проводит атаку (серия подозрительных запросов). Найдя нужный IP,
 * игрок вводит его в специальное поле — дверь открывается, уровень пройден.
 * <p>
 * Ключевые изменения по сравнению с оригиналом:
 * - toDraw теперь показывает NetworkLogView (список поддельных сетевых логов)
 * вместо ASCII-таблицы.
 * - Добавлен IpInputView — поле ввода IP-адреса. Открывается кнопкой "Ввести IP"
 * внутри NetworkLogView.
 * - Правильный ответ задаётся константой ATTACKER_IP. В логах он встречается
 * чаще всего с паттернами "SYN flood", "port scan", "REFUSED" и т.д.
 * - AntivirusObject заменён на RouterObject (визуально — роутер/маршрутизатор).
 * - При приближении к роутеру появляется подсказка о подозрительном трафике.
 * - PasswordInputView для двери заменён на IpInputView с той же логикой перехода.
 */
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

    Texture heroSpriteSheet;
    TextureRegion[][] heroFrames;


    AntivirusObject routerObject;
    ImageView routerMessage;
    ComputerObject networkComputer;
    BatteryObject batteryObject;
    BatteryObject doorDown;

    private TiledMapManager tiledMapManager;

    DialogView dialog;
    DialogView dialogNo;
    DialogOkNoView dialogOkNoView;

    NetworkLogView networkLogView;
    IpInputView ipInputView;
    boolean toDrawNetworkLogs = false;
    boolean ipInputActive = false;

    SaveView saveView = new SaveView(350, 50, 500, 600);
    boolean toDrawSave = false;

    Array<String> talks;
    Array<String> talks2;
    int ok_times = 1;

    boolean isNearComputer = false;
    Boolean isNearRouter = false;
    boolean isNearBattery = false;
    boolean isNearDoor = false;

    float heroX = -1f;
    float heroY = -1f;

    // Прочее
    ContactManager contactManager;
    TextView hintText;
    TextView networkHintText;
    private Vector3 touch2;
    private boolean isTouchingUI = false;

    public LevelFiveScreen(MyGdxGame myGdxGame) {
        this(myGdxGame, -1f, -1f);
    }

    public LevelFiveScreen(MyGdxGame myGdxGame, float x, float y) {
        this.myGdxGame = myGdxGame;
        this.heroX = x;
        this.heroY = y;

        // Очищаем Box2D мир от старых тел
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
        talks2 = new Array<>();
        for (int i = 0; i <= 7; i++) {
            talks2.add(LocalizationManager.get("network.talk2." + i));
        }

        gameSession = new GameSession();
        heroSpriteSheet = new Texture(GameResources.SPRITE_SHEET);
        heroFrames = TextureRegion.split(heroSpriteSheet, 32, 32);

        trashArray = new ArrayList<>();
        bulletArray = new ArrayList<>();

        tiledMapManager = new TiledMapManager(
            GameResources.TMX_MAP_LEVEL_ONE_PATH,
            myGdxGame.camera, myGdxGame.batch, 2
        );

        topBlackoutView = new ImageView(0, 656, 1280, 64, GameResources.BLACKOUT_TOP_IMG_PATH);
        liveView = new LiveView(305, 1215);
        pauseButton = new ButtonView(1200, 658, 46, 54, GameResources.PAUSE_IMG_PATH);
        touchpadView = new TouchpadView(100, 100);
        actionButton = new ButtonView(1100, 70, 70, 70, GameResources.ACTION_BUTTON_IMG_PATH);

        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 525, 400, "Pause");
        homeButton = new ButtonView(350, 300, 200, 35, myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        continueButton = new ButtonView(GameSettings.SCREEN_WIDTH - 550, 300, 200, 35,
            myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Continue");

        recordsTextView = new TextView(myGdxGame.largeWhiteFont, 206, 842, "Last records");
        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        homeButton2 = new ButtonView(280, 365, 160, 70, myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");

        hintText = new TextView(myGdxGame.commonPixelFontText, 250, 150,
            "Нажми зелёную кнопку, чтобы взаимодействовать");
        networkHintText = new TextView(myGdxGame.commonPixelFontText, 250, 100,
            "Найди IP атакующего в логах и введи его у двери!");

        routerObject = new AntivirusObject(
            GameResources.ANTIVIRUS_FIVE_TEXTURE_PATH,
            200, 200, 64, 64,
            GameSettings.ANTIVIRUS_BIT, myGdxGame.world
        );
        networkComputer = new ComputerObject(
            14, 9,
            GameSettings.TILE_SIZE, GameSettings.TILE_SIZE,
            GameResources.ASCII_SPRITE_PATH,
            myGdxGame.world
        );

        // Иконка сообщения над роутером
        routerMessage = new ImageView(210, 210, GameResources.HI_MESSAGE_IMG_PATH);

        // Батарейка (сохранение)
        batteryObject = new BatteryObject(
            10, 9,
            GameSettings.TILE_SIZE, GameSettings.TILE_SIZE,
            GameResources.BATTERY_BUTTON_IMG_PATH, myGdxGame.world
        );

        // Дверь в следующий уровень
        doorDown = new BatteryObject(
            18, 9,
            GameSettings.TILE_SIZE, GameSettings.TILE_SIZE * 2,
            GameResources.DOOR_IMG_PATH, myGdxGame.world, GameSettings.DOOR_BIT
        );

        // ── NetworkLogView — сердце уровня ───────────────────────────────────
        // Генерируем поддельные сетевые логи; ATTACKER_IP будет встречаться
        // чаще всего и с тревожными паттернами.
        networkLogView = new NetworkLogView(
            myGdxGame,
            180, 0, 1028, 720,
            ATTACKER_IP,
            () -> {
                // Колбэк: игрок нажал «Ввести IP» внутри NetworkLogView
                ipInputActive = true;
                ipInputView.show();
            }
        );

        // ── IpInputView — поле ввода IP злоумышленника ───────────────────────
        // Правильный ответ = ATTACKER_IP. При успехе — переход на следующий уровень.
        ipInputView = new IpInputView(
            myGdxGame,
            ATTACKER_IP,
            () -> {
                // Успех: правильный IP введён — открываем дверь / переходим
                gameSession.resumeGame();
                myGdxGame.setScreen(new LevelTwoScreen(myGdxGame));
            },
            () -> {
                // Неудача: неверный IP — показываем подсказку, не закрываем
                // (можно добавить штраф или встряхнуть экран)
            }
        );

        // ── ContactManager ───────────────────────────────────────────────────
        contactManager = new ContactManager(myGdxGame.world,
            (GameObject object) -> {
                String cls = object.getClass().getSimpleName();
                if (cls.equals("AntivirusObject")) isNearRouter = true;
                else if (cls.equals("ComputerObject")) isNearComputer = true;
                else if (object.getBit() == GameSettings.BATTERY_BIT) isNearBattery = true;
                else if (object.getBit() == GameSettings.DOOR_BIT) isNearDoor = true;
            },
            (GameObject object) -> {
                String cls = object.getClass().getSimpleName();
                if (cls.equals("AntivirusObject") || cls.equals("ComputerObject")
                    || cls.equals("BatteryObject")) {
                    isNearComputer = false;
                    isNearRouter = false;
                    isNearBattery = false;
                    isNearDoor = false;
                }
            }
        );
    }

    // ─────────────────────────────── show ────────────────────────────────────
    @Override
    public void show() {
        restartGame();
        Gdx.input.setInputProcessor(null);
    }

    // ─────────────────────────────── render ──────────────────────────────────
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
                if (isTouched) {
                    isTouchingUI = false;

                    // Диалог Да/Нет с роутером
                    if (dialogOkNoView != null) {
                        if (dialogOkNoView.okButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                            && Gdx.input.justTouched()) {
                            dialogOkNoView.dispose();
                            dialogOkNoView = null;
                            if (dialog != null) dialog.nextButton.show();
                        } else if (dialogOkNoView.noButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                            && Gdx.input.justTouched()) {
                            dialogNo = new DialogView(myGdxGame,
                                (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                                GameSettings.SCREEN_WIDTH - (GameSettings.SCREEN_WIDTH / 4f) - 200f,
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
                    boolean nearSomething = isNearRouter || isNearComputer || isNearBattery || isNearDoor;
                    if (nearSomething && !toDrawNetworkLogs) {
                        actionButton = new ButtonView(1100, 70, 70, 70,
                            GameResources.ACTION_BUTTON_ACTIVE_IMG_PATH);
                    } else if (!toDrawNetworkLogs) {
                        actionButton = new ButtonView(1100, 70, 70, 70,
                            GameResources.ACTION_BUTTON_IMG_PATH);
                    }
                    if (isNearRouter && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        dialog = new DialogView(myGdxGame,
                            (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                            GameSettings.SCREEN_WIDTH - (GameSettings.SCREEN_WIDTH / 4f) - 200f,
                            GameSettings.SCREEN_HEIGHT / 4f, talks);
                    }

                    if (isNearComputer && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                        && Gdx.input.justTouched()) {
                        if (toDrawNetworkLogs) {
                            toDrawNetworkLogs = false;
                            actionButton = new ButtonView(1100, 70, 70, 70,
                                GameResources.ACTION_BUTTON_ACTIVE_IMG_PATH);
                        } else {
                            toDrawNetworkLogs = true;
                            actionButton = new ButtonView(1100, 70, 70, 70,
                                GameResources.RED_ACTION_BUTTON_IMG_PATH);
                        }
                    }

                    if (isNearDoor && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                        && Gdx.input.justTouched()) {
                        ipInputActive = true;
                        ipInputView.show();
                        gameSession.pauseGame();
                    }

                    // Взаимодействие с БАТАРЕЙКОЙ → сохранение
                    if (isNearBattery && actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                        && Gdx.input.justTouched()) {
                        toDrawSave = true;
                    } else if (!isNearBattery
                        || saveView.cancelButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        toDrawSave = false;
                    }
                    if (isNearBattery && saveView.saveButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)
                        && Gdx.input.justTouched()) {
                        MemoryManager.saveGameState(1, heroObject.getX(), heroObject.getY());
                        toDrawSave = false;
                    }

                    // Диалог роутера: на 6-й реплике появляется выбор Да/Нет
                    if (dialog != null && dialog.getCnt() == 6) {
                        dialogOkNoView = new DialogOkNoView(myGdxGame,
                            (GameSettings.SCREEN_WIDTH - 180f) / 4f, 0,
                            GameSettings.SCREEN_WIDTH - (GameSettings.SCREEN_WIDTH / 4f) - 200f,
                            GameSettings.SCREEN_HEIGHT / 4f,
                            LocalizationManager.get("network.talk2.6"));
                        dialog.nextButton.hide();
                        dialog.addCnt();
                    }
                    if (!isNearRouter) {
                        dialog = null;
                    }

                    // Обновляем джойстик
                    if (!isTouchingUI) {
                        touchpadView.update(myGdxGame.touch.x, myGdxGame.touch.y, true);
                        if (touchpadView.isActive()) {
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
                break;

            // ── PAUSED ────────────────────────────────────────────────────────
            case PAUSED:
                if (ipInputActive) {
                    ipInputView.update(delta);
                    ipInputView.handleTouch();
                }
                if (!ipInputView.isVisible()) {
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
                break;

            // ── ENDED ─────────────────────────────────────────────────────────
            case ENDED:
                if (isTouched) {
                    if (homeButton2.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        myGdxGame.setScreen(myGdxGame.menuScreen);
                    }
                }
                break;
        }
    }

    // ─────────────────────────────── draw ────────────────────────────────────
    private void draw() {
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);
        tiledMapManager.render();

        // ── Мировые объекты (camera.combined) ────────────────────────────────
        myGdxGame.batch.begin();
        routerObject.draw(myGdxGame.batch);
        heroObject.draw(myGdxGame.batch);
        if (isNearRouter) {
            routerMessage.draw(myGdxGame.batch);
        }
        networkComputer.draw(myGdxGame.batch);
        batteryObject.draw(myGdxGame.batch);
        if (doorDown != null) {
            doorDown.draw(myGdxGame.batch);
        }
        myGdxGame.batch.end();

        // ── UI (uiCamera.combined) ────────────────────────────────────────────
        myGdxGame.uiCamera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.uiCamera.combined);
        myGdxGame.batch.begin();

        // Диалог роутера
        if (dialog != null) {
            dialog.draw(myGdxGame.batch);
        }

        // ─── Сетевые логи ────────────────────────────────────────────────────
        if (toDrawNetworkLogs) {
            networkLogView.draw(myGdxGame.batch);
        }

        // ─── Состояния игры ──────────────────────────────────────────────────
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
            touchpadView.draw(myGdxGame.batch);

            // Подсказка рядом с роутером
            if (isNearRouter && dialog == null && dialogOkNoView == null
                && MemoryManager.loadAreSubtitlesOn()) {
                hintText.draw(myGdxGame.batch);
            }
            // Глобальная подсказка об IP (показываем всегда или только после компьютера)
            if (toDrawNetworkLogs) {
                networkHintText.draw(myGdxGame.batch);
            }
        }

        topBlackoutView.draw(myGdxGame.batch);
        actionButton.draw(myGdxGame.batch);
        pauseButton.draw(myGdxGame.batch);
        liveView.draw(myGdxGame.batch);

        if (dialogNo != null) dialogNo.draw(myGdxGame.batch);
        if (dialogOkNoView != null) dialogOkNoView.draw(myGdxGame.batch);
        if (toDrawSave) saveView.draw(myGdxGame.batch);

        myGdxGame.batch.end();

        if (myGdxGame.debugMode) {
            myGdxGame.debugRenderer.render(myGdxGame.world, myGdxGame.camera.combined);
        }
    }

    // ─────────────────────────────── вспомогательные ─────────────────────────
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
        heroObject = new AnimatedHeroObject((int) heroX, (int) heroY, 64, 64,
            heroFrames, myGdxGame.world);
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
        float t = 1f;
        createWall(mapWidth / 2, -t / 2 + 1.5f, mapWidth, t);
        createWall(mapWidth / 2, -t / 2 + 16, mapWidth, t);
        createWall(-t / 2 + 0.8f, mapHeight / 2, t, mapHeight);
        createWall(-t / 2 + 32.2f, mapHeight / 2, t, mapHeight);
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
