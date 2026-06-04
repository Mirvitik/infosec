package kgu.game.project.objects;

import static kgu.game.project.GameSettings.TILE_SIZE;


import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Vector;

import kgu.game.project.GameSettings;

public class BatteryObject extends GameObject {

    public BatteryObject(
        int x,
        int y,
        int width,
        int height,
        String texturePath,
        World world
    ) {
        super(
            texturePath,
            TILE_SIZE * x,
            TILE_SIZE * y,
            width,
            height,
            GameSettings.BATTERY_BIT,
            world
        );

        body.setType(BodyDef.BodyType.StaticBody);
        body.setUserData(this);
    }

    public BatteryObject(
        int x,
        int y,
        int width,
        int height,
        String texturePath,
        World world, short Bit
    ) {
        super(
            texturePath,
            TILE_SIZE * x,
            TILE_SIZE * y,
            width,
            height,
            Bit,
            world
        );

        body.setType(BodyDef.BodyType.StaticBody);
        body.setUserData(this);
    }

    public BatteryObject(
        float x,
        float y,
        int width,
        int height,
        String texturePath,
        World world, short Bit
    ) {
        super(
            texturePath,
            (int) x,
            (int) y,
            width,
            height,
            Bit,
            world
        );

        body.setType(BodyDef.BodyType.StaticBody);
        body.setUserData(this);
    }

    @Override
    public short getBit() {
        return cBits;
    }
}
