package kgu.game.project.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

import kgu.game.project.GameSettings;

public class HeroObject extends GameObject {


    int livesLeft;
    private final Vector2 movementDirection;

    public HeroObject(int x, int y, int width, int height, String texturePath, World world) {
        super(texturePath, x, y, width, height, GameSettings.SHIP_BIT, world);
        body.setLinearDamping(5f);
        livesLeft = 3;
        movementDirection = new Vector2(0, 0);

    }

    public int getLiveLeft() {
        return livesLeft;
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    public void moveWithTouchpad(Vector2 direction, float strength) {
        if (direction == null) return;

        movementDirection.set(direction);

        float targetVelocityX = direction.x * strength * GameSettings.BULLET_VELOCITY;
        float targetVelocityY = direction.y * strength * GameSettings.BULLET_VELOCITY;

        Vector2 currentVelocity = body.getLinearVelocity();
        float newVelocityX = currentVelocity.x + (targetVelocityX - currentVelocity.x) * 0.1f;
        float newVelocityY = currentVelocity.y + (targetVelocityY - currentVelocity.y) * 0.1f;

        body.setLinearVelocity(newVelocityX, newVelocityY);
    }

    public void stop() {
        movementDirection.set(0, 0);
        body.setLinearVelocity(0, 0);
    }


    @Override
    public void hit() {
        livesLeft -= 1;
        if (isAlive()) {
            Vector2 pushBack = new Vector2(0, -5);
            body.setLinearVelocity(pushBack);
        }
    }

    public boolean isAlive() {
        return livesLeft > 0;
    }


}
