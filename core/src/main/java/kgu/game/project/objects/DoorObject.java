package kgu.game.project.objects;

import static kgu.game.project.GameSettings.TILE_SIZE;


import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Vector;

import kgu.game.project.GameSettings;

public class DoorObject extends BatteryObject {


    public DoorObject(int x, int y, int width, int height, String texturePath, World world) {
        super(x, y, width, height, texturePath, world);
    }

    public DoorObject(
        int x,
        int y,
        int width,
        int height,
        String texturePath,
        World world, short Bit
    ) {
        super(x, y, width, height, texturePath, world, Bit);
    }
}
