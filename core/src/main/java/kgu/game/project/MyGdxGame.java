package kgu.game.project;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import kgu.game.project.managers.AudioManager;
import kgu.game.project.screens.*;

import static kgu.game.project.GameSettings.*;

public class MyGdxGame extends Game {

    public World world;
    public BitmapFont largeWhiteFont;
    public BitmapFont commonWhiteFont;
    public BitmapFont commonBlackFont;
    public BitmapFont commonBlackFontText;
    public BitmapFont commonHelpFontText;
    public BitmapFont commonPixelFontText;
    public BitmapFont commonPixelLogFontText;
    public BitmapFont commonPixelFontGreyText;
    public BitmapFont xanmonoFont;
    public BitmapFont xanmonoFontBig;
    public static BitmapFont arialFont;
    public BitmapFont consolasFont;
    public BitmapFont arialFontRed;

    public Vector3 touch;
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public OrthographicCamera uiCamera;
    public AudioManager audioManager;

    public GameScreen gameScreen;
    public LoadGameScreen loadScreen;
    public MenuScreen menuScreen;
    public SettingsScreen settingsScreen;
    public ComputerScreen computerScreen;
    public LoginScreen loginScreen;
    public LevelOneScreen levelOneScreen;
    public LevelThreeScreen levelThreeScreen;
    public LevelFourScreen levelFourScreen;
    public Box2DDebugRenderer debugRenderer;
    public static BitmapFont arialWhiteFont;
    public boolean debugMode = false;
    float accumulator = 0;

    @Override
    public void create() {
        // Инициализируем шрифты ПОСЛЕ того как LibGDX готов
        initFonts();

        Box2D.init();
        world = new World(new Vector2(0, 0), true);

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        uiCamera = new OrthographicCamera();

        uiCamera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        audioManager = new AudioManager();

        loadScreen = new LoadGameScreen(this);
        menuScreen = new MenuScreen(this);
        settingsScreen = new SettingsScreen(this);
        debugRenderer = new Box2DDebugRenderer();

        setScreen(menuScreen);
    }

    private void initFonts() {
        largeWhiteFont = FontBuilder.generate(60, Color.WHITE, GameResources.FONT_PATH);
        commonWhiteFont = FontBuilder.generate(36, Color.WHITE, GameResources.FONT_PATH);
        commonBlackFont = FontBuilder.generate(36, Color.BLACK, GameResources.FONT_PATH_HACKED_CYR);
        commonBlackFontText = FontBuilder.generate(36, Color.BLACK, GameResources.FONT_PATH_HACK_TEXT);
        commonHelpFontText = FontBuilder.generate(36, Color.GREEN, GameResources.FONT_PATH_HACK_TEXT);
        commonPixelFontText = FontBuilder.generate(20, Color.GREEN, GameResources.FONT_PATH_PIXEL);
        commonPixelLogFontText = FontBuilder.generate(8, Color.GREEN, GameResources.FONT_PATH_PIXEL);
        commonPixelFontGreyText = FontBuilder.generate(20, Color.GRAY, GameResources.FONT_PATH_PIXEL);
        xanmonoFont = FontBuilder.generate(20, Color.GRAY, GameResources.XANMONO_FONT_PATH);
        xanmonoFontBig = FontBuilder.generate(60, Color.WHITE, GameResources.XANMONO_FONT_PATH);
        arialFont = FontBuilder.generate(20, Color.GRAY, GameResources.ARIAL_FONT_PATH);
        arialWhiteFont = FontBuilder.generate(20, Color.WHITE, GameResources.ARIAL_FONT_PATH);
        consolasFont = FontBuilder.generate(20, Color.GRAY, GameResources.CONSOLAS_FONT_PATH);
        arialFontRed = FontBuilder.generate(20, Color.RED, GameResources.ARIAL_FONT_PATH);
    }

    @Override
    public void dispose() {
        batch.dispose();

        // Не забывай dispose для шрифтов
        if (largeWhiteFont != null) largeWhiteFont.dispose();
        if (commonWhiteFont != null) commonWhiteFont.dispose();
        if (commonBlackFont != null) commonBlackFont.dispose();
        if (commonBlackFontText != null) commonBlackFontText.dispose();
        if (commonHelpFontText != null) commonHelpFontText.dispose();
        if (commonPixelFontText != null) commonPixelFontText.dispose();
        if (commonPixelLogFontText != null) commonPixelLogFontText.dispose();
        if (commonPixelFontGreyText != null) commonPixelFontGreyText.dispose();
        if (xanmonoFont != null) xanmonoFont.dispose();
        if (xanmonoFontBig != null) xanmonoFontBig.dispose();
        if (arialFont != null) arialFont.dispose();
        if (consolasFont != null) consolasFont.dispose();
        if (arialFontRed != null) arialFontRed.dispose();
    }

    public void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();
        accumulator += Math.min(delta, 0.25f);

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }
}
