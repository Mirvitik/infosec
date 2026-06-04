package kgu.game.project.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import kgu.game.project.GameSettings;
import kgu.game.project.managers.MemoryManager;

public class MonsterObject extends TrashObject {
    public MonsterObject(int width, int height, String texturePath, World world) {
        super(width, height, texturePath, world);
        body.setLinearVelocity(new Vector2(0, -GameSettings.MONSTER_VELOCITY * MemoryManager.loadDifficultyNums()));
    }
}
