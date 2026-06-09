package kgu.game.project.objects;

import static kgu.game.project.GameSettings.TILE_SIZE;


import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Vector;

import kgu.game.project.GameSettings;

public class ComputerObject extends GameObject {

    public ComputerObject(
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
            GameSettings.COMPUTER_BIT,
            world
        );

        body.setType(BodyDef.BodyType.StaticBody);
        body.setUserData(this);
    }
    public ComputerObject(
        float x,
        float y,
        int width,
        int height,
        String texturePath,
        World world
    ) {
        super(
            texturePath,
            (int) (TILE_SIZE * x),
            (int) (TILE_SIZE * y),
            width,
            height,
            GameSettings.COMPUTER_BIT,
            world
        );
        body.setType(BodyDef.BodyType.StaticBody);
        body.setUserData(this);
    }

    public boolean isClicked(Vector3 worldTouch) {
        return worldTouch.x >= getX()
            && worldTouch.x <= getX() + width
            && worldTouch.y >= getY()
            && worldTouch.y <= getY() + height;
    }

    public void onClick() {
        System.out.println("Computer clicked");
    }
}
