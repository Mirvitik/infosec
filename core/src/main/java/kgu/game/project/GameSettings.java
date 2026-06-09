package kgu.game.project;

public class GameSettings {
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    public static final float STEP_TIME = 1f / 60f;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 6;
    public static final float SCALE = 0.05f;
    public static float TRASH_VELOCITY = 20;
    public static float MONSTER_VELOCITY = 20;

    public static int BULLET_VELOCITY = 10;
    public static int SHOOTING_COOL_DOWN = 1000;

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
    public static final int TILE_SIZE = 64;


}
