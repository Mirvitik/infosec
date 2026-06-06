package kgu.game.project;

public class GameSettings {

    // Device settings

    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;

    // Physics settings

    public static final float STEP_TIME = 1f / 60f;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 6;
    public static final float SCALE = 0.05f;

    public static float SHIP_FORCE_RATIO = 10;
    public static float TRASH_VELOCITY = 20;
    public static float MONSTER_VELOCITY = 20;
    public static long STARTING_TRASH_APPEARANCE_COOL_DOWN = 2000; // in [ms] - milliseconds
    public static long STARTING_MONSTER_APPEARANCE_COOL_DOWN = 2000;
    public static int BULLET_VELOCITY = 10; // in [m/s] - meter per second
    public static int HERO_VELOCITY = 10;
    public static int SHOOTING_COOL_DOWN = 1000; // in [ms] - milliseconds

    public static final short TRASH_BIT = 2;
    public static final short SHIP_BIT = 4;
    public static final short BULLET_BIT = 8;
    public static final short COMPUTER_BIT = 16;
    public static final short ANTIVIRUS_BIT = 32;
    public static final short WALL_BIT = 0x0006;
    public static final short BATTERY_BIT = 0x0007;
    public static final short DOOR_BIT = 0x0008;
    public static final short SENSOR_MINUS_BIT = 0x0009;
    public static final short SENSOR_PLUS_BIT = 0x0010;
    public static final short MAIL_BIT = 0x0011;


    // Object sizes

    public static final int SHIP_WIDTH = 150;
    public static final int SHIP_HEIGHT = 150;
    public static final int TRASH_WIDTH = 140;
    public static final int TRASH_HEIGHT = 100;

    public static final int MONSTER_WIDTH = 140;
    public static final int MONSTER_HEIGHT = 100;
    public static final int BULLET_WIDTH = 15;
    public static final int BULLET_HEIGHT = 45;


    // camera
    public static final float CAMERA_FOLLOW_THRESHOLD_X = SCREEN_WIDTH * 0.3f;  // 30% от ширины экрана
    public static final float CAMERA_FOLLOW_THRESHOLD_Y = SCREEN_HEIGHT * 0.3f; // 30% от высоты экрана
    public static final float CAMERA_SMOOTH_FACTOR = 0.1f;  // Плавность движения камеры

    // World bounds (размер игрового мира)
    public static final int WORLD_WIDTH = 2000;   // Ширина мира в пикселях
    public static final int WORLD_HEIGHT = 2000;
    public static final int TILE_SIZE = 32;


}
