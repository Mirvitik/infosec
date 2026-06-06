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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.ScreenUtils;

import kgu.game.project.GameResources;
import kgu.game.project.GameSession;
import kgu.game.project.GameSettings;
import kgu.game.project.GameState;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ButtonView;
import kgu.game.project.components.ImageView;
import kgu.game.project.components.LiveView;
import kgu.game.project.components.RecordsListView;
import kgu.game.project.components.TextView;
import kgu.game.project.components.TouchpadView;
import kgu.game.project.managers.LocalizationManager;
import kgu.game.project.objects.AnimatedHeroObject;
import kgu.game.project.objects.AntivirusObject;
import kgu.game.project.objects.ComputerObject;
import kgu.game.project.objects.GameObject;
import kgu.game.project.objects.HeroObject;
import kgu.game.project.objects.TrashObject;
import kgu.game.project.managers.ContactManager;
import kgu.game.project.managers.MemoryManager;
import kgu.game.project.managers.TiledMapManager;

public class GameScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;
    GameSession gameSession;
    HeroObject heroObject;

    ContactManager contactManager;
    ComputerObject computer;

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
    Body low_wall;
    Body up_wall;
    Body left_wall;
    Body right_wall;
    ImageView message;
    private TiledMapManager tiledMapManager;
    private Vector3 touch2;
    public boolean isNearComputer = false;

    private boolean isTouchingUI = false;
    boolean isDesktop;
    TextView text;
    private boolean wasKKeyPressed = false;

    public GameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        gameSession = new GameSession();
        heroSpriteSheet = new Texture(GameResources.SPRITE_SHEET);
        int frameWidth = 32;
        int frameHeight = 32;
        heroFrames = TextureRegion.split(heroSpriteSheet, frameWidth, frameHeight);

        contactManager = new ContactManager(myGdxGame.world, (GameObject object) -> {
            // Определяем тип объекта
            if (object instanceof AntivirusObject) {
                isNearComputer = true;
                System.out.println("Touched ANTIVIRUS (computer)");
            } else if (object instanceof  ComputerObject) {
                isNearComputer = true;
                System.out.println("Touched COMPUTER");
            } else if (object instanceof TrashObject) {
                System.out.println("Touched TRASH - take damage!");
                heroObject.hit(); // Например, наносим урон
            }
        },
            (GameObject object) -> {
                if (object instanceof AntivirusObject ||
                    object.getClass().getSimpleName().equals("ComputerObject")) {
                    isNearComputer = false;
                    System.out.println("Left computer area");
                }
            });
        isDesktop = Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.Desktop;

        heroObject = new AnimatedHeroObject(
            GameSettings.SCREEN_WIDTH / 2 - 600, 150,
            64, 64,
            heroFrames,
            myGdxGame.world
        );
        tiledMapManager = new TiledMapManager(GameResources.TMX_MAP_PATH, myGdxGame.camera, myGdxGame.batch, 2);
        createMapBorders();
        topBlackoutView = new ImageView(0, 656, 1280, 64, GameResources.BLACKOUT_TOP_IMG_PATH);
        liveView = new LiveView(305, 1215);
        pauseButton = new ButtonView(
            1200, 658,
            46, 54,
            GameResources.PAUSE_IMG_PATH
        );

        // Initialize touchpad in bottom left corner
        touchpadView = new TouchpadView(100, 100);

        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 525, 400, LocalizationManager.get("game.pause"));
        homeButton = new ButtonView(
            350, 300,
            200, 35,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            LocalizationManager.get("game.home")
        );
        if (isDesktop){
            text = new TextView(myGdxGame.commonPixelFontText, 325, 110, LocalizationManager.get("game.login2_hint"));

        } else{
            text = new TextView(myGdxGame.commonPixelFontText, 325, 110, LocalizationManager.get("game.login_hint"));
        }
        continueButton = new ButtonView(
            GameSettings.SCREEN_WIDTH - 550, 300,
            200, 35,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            LocalizationManager.get("game.continue")
        );
        computer = new ComputerObject(5, 5, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE, GameResources.COMPUTER_SPRITE_PATH, myGdxGame.world);
        actionButton = new ButtonView(1100, 70, 70, 70, GameResources.ACTION_BUTTON_IMG_PATH);
        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        recordsTextView = new TextView(myGdxGame.largeWhiteFont, 206, 842, LocalizationManager.get("game.last_records"));
        homeButton2 = new ButtonView(
            280, 365,
            160, 70,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            LocalizationManager.get("game.home")
        );
    }

    @Override
    public void show() {
        restartGame();
    }

    @Override
    public void render(float delta) {
        if (gameSession.state == GameState.PLAYING) {
            if (!heroObject.isAlive()) {
                gameSession.endGame();
                recordsListView.setRecords(MemoryManager.loadRecordsTable());
            }

            myGdxGame.camera.position.set(
                heroObject.getX(),
                heroObject.getY(),
                0
            );

            myGdxGame.camera.update();
            myGdxGame.camera.update();
            gameSession.updateScore();
            liveView.setLeftLives(heroObject.getLiveLeft());

            myGdxGame.stepWorld();
        }
        if (myGdxGame.debugMode) {
            myGdxGame.debugRenderer.render(myGdxGame.world, myGdxGame.camera.combined);
        }
        handleInput();
        draw();
    }

    private void handleKeyboardInput() {
        Vector2 direction = new Vector2(0, 0);
        float strength = 0;

        // WASD или стрелки для управления
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W) ||
            Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) {
            direction.y = 1;
            strength = 1;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S) ||
            Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            direction.y = -1;
            strength = 1;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A) ||
            Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            direction.x = -1;
            strength = 1;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D) ||
            Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            direction.x = 1;
            strength = 1;
        }

        // Нормализуем диагональное движение
        if (direction.x != 0 && direction.y != 0) {
            direction.nor();
        }

        // Применяем движение если есть ввод
        if (strength > 0) {
            heroObject.moveWithTouchpad(direction, strength);
        } else {
            heroObject.stop();
        }
    }

    private void handleDesktopAction() {
        // Обработка клавиши K для взаимодействия с компьютером
        boolean isKKeyPressed = Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.K);

        // Используем флаг для однократного срабатывания при нажатии
        if (isKKeyPressed && !wasKKeyPressed && isNearComputer) {
            this.dispose();
            myGdxGame.loginScreen = new LoginScreen(myGdxGame);
            myGdxGame.camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
            myGdxGame.setScreen(myGdxGame.loginScreen);
        }
        wasKKeyPressed = isKKeyPressed;
    }

    private void handleInput() {
        boolean isTouched = Gdx.input.isTouched();
        if (isTouched) {
            myGdxGame.touch = myGdxGame.uiCamera.unproject(
                new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)
            );
            touch2 = myGdxGame.camera.unproject(
                new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)
            );
        }

        switch (gameSession.state) {
            case PLAYING:
                if (isDesktop) {
                    handleKeyboardInput();

                    if (isNearComputer) {
                        actionButton = new ButtonView(1100, 70, 70, 70, GameResources.ACTION_BUTTON_ACTIVE_IMG_PATH);
                        computer.onClick();
                    } else {
                        actionButton = new ButtonView(1100, 70, 70, 70, GameResources.ACTION_BUTTON_IMG_PATH);
                    }

                    handleDesktopAction();

                    if (isTouched && !isTouchingUI) {
                        touchpadView.update(myGdxGame.touch.x, myGdxGame.touch.y, true);
                        if (touchpadView.isActive()) {
                            heroObject.moveWithTouchpad(
                                touchpadView.getDirection(),
                                touchpadView.getStrength()
                            );
                        }
                    }
                } else {
                    if (isTouched) {
                        isTouchingUI = false;

                        if (pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            isTouchingUI = true;
                            gameSession.pauseGame();
                        }
                        if (actionButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y) && isNearComputer) {
                            this.dispose();
                            myGdxGame.loginScreen = new LoginScreen(myGdxGame);
                            myGdxGame.camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
                            myGdxGame.setScreen(myGdxGame.loginScreen);
                        }

                        if (isNearComputer) {
                            actionButton = new ButtonView(1100, 70, 70, 70, GameResources.ACTION_BUTTON_ACTIVE_IMG_PATH);
                            computer.onClick();
                        } else {
                            actionButton = new ButtonView(1100, 70, 70, 70, GameResources.ACTION_BUTTON_IMG_PATH);
                        }

                        if (!isTouchingUI) {
                            touchpadView.update(myGdxGame.touch.x, myGdxGame.touch.y, true);

                            if (touchpadView.isActive()) {
                                heroObject.moveWithTouchpad(
                                    touchpadView.getDirection(),
                                    touchpadView.getStrength()
                                );
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
                if (isTouched) {
                    if (continueButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        gameSession.resumeGame();
                    }
                    if (homeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        myGdxGame.setScreen(myGdxGame.menuScreen);
                    }
                }
                if (isDesktop && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
                    gameSession.resumeGame();
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
        int bodyCount = myGdxGame.world.getBodyCount();
        System.out.println(bodyCount);

        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);
        tiledMapManager.render();

        myGdxGame.batch.begin();
        heroObject.draw(myGdxGame.batch);
        computer.draw(myGdxGame.batch);
        myGdxGame.batch.end();
        myGdxGame.batch.setProjectionMatrix(
            myGdxGame.uiCamera.combined
        );

        myGdxGame.batch.begin();
        if (gameSession.state == GameState.PAUSED) {
            pauseTextView.draw(myGdxGame.batch);
            homeButton.draw(myGdxGame.batch);
            continueButton.draw(myGdxGame.batch);
        } else if (gameSession.state == GameState.ENDED) {
            recordsTextView.draw(myGdxGame.batch);
            recordsListView.draw(myGdxGame.batch);
            homeButton2.draw(myGdxGame.batch);
        } else if (gameSession.state == GameState.PLAYING) {
            if (!isDesktop) {
                touchpadView.draw(myGdxGame.batch);
            }
            if (isNearComputer && MemoryManager.loadAreSubtitlesOn()) {
                text.draw(myGdxGame.batch);
            }
        }
        topBlackoutView.draw(myGdxGame.batch);
        if (!isDesktop) {
            actionButton.draw(myGdxGame.batch);
            pauseButton.draw(myGdxGame.batch);
        }

        myGdxGame.batch.end();
    }

    private void restartGame() {
        if (heroObject != null) {
            myGdxGame.world.destroyBody(heroObject.body);
        }
        if (computer != null) {
            myGdxGame.world.destroyBody(computer.body);
        }
        if (low_wall != null) {
            myGdxGame.world.destroyBody(low_wall);
        }
        if (up_wall != null) {
            myGdxGame.world.destroyBody(up_wall);
        }
        if (right_wall != null) {
            myGdxGame.world.destroyBody(right_wall);
        }
        if (left_wall != null) {
            myGdxGame.world.destroyBody(left_wall);
        }

        heroObject = new AnimatedHeroObject(
            GameSettings.SCREEN_WIDTH / 2 - 600, 150,
            64, 64,
            heroFrames,
            myGdxGame.world
        );
        computer = new ComputerObject(5, 5, GameSettings.TILE_SIZE, GameSettings.TILE_SIZE, GameResources.COMPUTER_SPRITE_PATH, myGdxGame.world);
        createMapBorders();
        gameSession.startGame();

        wasKKeyPressed = false;
    }

    @Override
    public void dispose() {
        heroSpriteSheet.dispose();
        touchpadView.dispose();
        tiledMapManager.dispose();
        if (heroObject != null) {
            myGdxGame.world.destroyBody(heroObject.body);
        }
        if (computer != null) {
            myGdxGame.world.destroyBody(computer.body);
        }
        if (low_wall != null) {
            myGdxGame.world.destroyBody(low_wall);
        }
        if (up_wall != null) {
            myGdxGame.world.destroyBody(up_wall);
        }
        if (right_wall != null) {
            myGdxGame.world.destroyBody(right_wall);
        }
        if (left_wall != null) {
            myGdxGame.world.destroyBody(left_wall);
        }
    }

    private void createMapBorders() {
        float mapWidth = tiledMapManager.getMapWidthPixels() * tiledMapManager.getUnitScale();
        float mapHeight = tiledMapManager.getMapHeightPixels() * tiledMapManager.getUnitScale();

        float wallThickness = 1f;

        low_wall = createWall(
            mapWidth / 2,
            -wallThickness / 2 + 1.5f,
            mapWidth,
            wallThickness
        );
        up_wall = createWall(
            mapWidth / 2,
            -wallThickness / 2 + 18,
            mapWidth,
            wallThickness
        );

        left_wall = createWall(
            -wallThickness / 2 + 0.8f,
            mapHeight / 2,
            wallThickness,
            mapHeight
        );

        right_wall = createWall(
            -wallThickness / 2 + 32.2f,
            mapHeight / 2,
            wallThickness,
            mapHeight
        );
    }

    private Body createWall(
        float x,
        float y,
        float width,
        float height
    ) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = myGdxGame.world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f, height / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GameSettings.WALL_BIT;
        fixtureDef.filter.maskBits = GameSettings.SHIP_BIT;
        fixtureDef.isSensor = false;
        body.createFixture(fixtureDef);

        shape.dispose();
        return body;
    }
}
