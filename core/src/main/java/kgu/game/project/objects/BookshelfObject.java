package kgu.game.project.objects;

import static kgu.game.project.GameSettings.TILE_SIZE;


import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;


import kgu.game.project.GameSettings;

public class BookshelfObject extends GameObject {

    public BookshelfObject(
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
            GameSettings.BOOK_SHELF_BIT,
            world
        );

        body.setType(BodyDef.BodyType.StaticBody);
        body.setUserData(this);
    }
}
