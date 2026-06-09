package kgu.game.project.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import kgu.game.project.GameResources;
import kgu.game.project.GameSettings;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ImageView;
import kgu.game.project.objects.WallObject;

public class MinigameScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    ImageView backGround;
    WallObject wall;

    public MinigameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        backGround = new ImageView(0, 0, GameResources.MINIGAME_BACKGROUND_PATH_PIXEL);
        wall = new WallObject(myGdxGame, 100, 50, 200, 20, GameResources.WALL_TEXTURE_PATH);

    }

    public void draw() {
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        myGdxGame.batch.begin();
        backGround.draw(myGdxGame.batch);
        wall.draw(myGdxGame.batch);
        myGdxGame.batch.end();
    }

    @Override

    public void render(float delta) {
        createMapBorders();
        draw();
    }

    private void createMapBorders() {
        float mapWidth = GameSettings.SCREEN_WIDTH;
        float mapHeight = GameSettings.SCREEN_HEIGHT;
        float wallThickness = 1f;
        createWall(mapWidth / 2, -wallThickness / 2 + 4f, mapWidth, wallThickness);
        createWall(mapWidth / 2, -wallThickness / 2 + 28, mapWidth, wallThickness);
        createWall(-wallThickness / 2 + 2f, mapHeight / 2, wallThickness, mapHeight);
        createWall(-wallThickness / 2 + 63f, mapHeight / 2, wallThickness, mapHeight);
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

