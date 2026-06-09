package kgu.game.project.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class TiledMapManager {

    private final TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer renderer;
    private final OrthographicCamera camera;
    private final float mapWidthPixels;
    private final float mapHeightPixels;
    private float unitScale = 1f;


    public TiledMapManager(String mapPath, OrthographicCamera camera, Batch batch, float unitScale) {
        this.unitScale = unitScale;
        this.camera = camera;
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMagFilter = Texture.TextureFilter.Nearest;
        params.textureMinFilter = Texture.TextureFilter.Nearest;

        tiledMap = new TmxMapLoader().load(mapPath, params);


        for (TiledMapTileSet tileset : tiledMap.getTileSets()) {
            for (TiledMapTile tile : tileset) {
                tile.getTextureRegion().getTexture()
                    .setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            }
        }
        renderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale, batch);

        MapProperties properties = tiledMap.getProperties();
        int mapWidthTiles = properties.get("width", Integer.class);
        int mapHeightTiles = properties.get("height", Integer.class);
        int tileWidth = properties.get("tilewidth", Integer.class);
        int tileHeight = properties.get("tileheight", Integer.class);

        mapWidthPixels = mapWidthTiles * tileWidth * unitScale;
        mapHeightPixels = mapHeightTiles * tileHeight * unitScale;
    }

    public void render() {
        camera.position.x = Math.round(camera.position.x);
        camera.position.y = Math.round(camera.position.y);
        camera.update();

        renderer.setView(camera);
        renderer.render();
    }

    public float getMapWidthPixels() {
        return mapWidthPixels;
    }

    public float getMapHeightPixels() {
        return mapHeightPixels;
    }

    public void dispose() {
        renderer.dispose();
        tiledMap.dispose();
    }

    public float getUnitScale() {
        return unitScale;
    }
}
