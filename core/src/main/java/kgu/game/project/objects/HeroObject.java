package kgu.game.project.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

import kgu.game.project.GameSettings;

public class HeroObject extends GameObject {

    long lastShotTime;
    int livesLeft;
    private final Vector2 movementDirection;
    private float movementStrength;
    private float minX;
    private float maxX;
    private float minY;
    private float maxY;

    public HeroObject(int x, int y, int width, int height, String texturePath, World world) {
        super(texturePath, x, y, width, height, GameSettings.SHIP_BIT, world);
        body.setLinearDamping(5f);
        livesLeft = 3;
        movementDirection = new Vector2(0, 0);
        movementStrength = 0f;
        setMovementBounds(
            width / 2f,
            GameSettings.SCREEN_WIDTH - width / 2f,
            height / 2f,
            GameSettings.SCREEN_HEIGHT - height / 2f
        );
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
        movementStrength = strength;

        float targetVelocityX = direction.x * strength * GameSettings.BULLET_VELOCITY;
        float targetVelocityY = direction.y * strength * GameSettings.BULLET_VELOCITY;

        Vector2 currentVelocity = body.getLinearVelocity();
        float newVelocityX = currentVelocity.x + (targetVelocityX - currentVelocity.x) * 0.1f;
        float newVelocityY = currentVelocity.y + (targetVelocityY - currentVelocity.y) * 0.1f;

        body.setLinearVelocity(newVelocityX, newVelocityY);
    }

    public void stop() {
        movementDirection.set(0, 0);
        movementStrength = 0;
        body.setLinearVelocity(0, 0);
    }

    public boolean isMoving() {
        return movementStrength > 0.1f && (movementDirection.x != 0 || movementDirection.y != 0);
    }

    public Vector2 getMovementDirection() {
        return movementDirection;
    }

    public float getMovementStrength() {
        return movementStrength;
    }

    public void setMovementBounds(float minX, float maxX, float minY, float maxY) {
        this.minX = Math.min(minX, maxX);
        this.maxX = Math.max(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.maxY = Math.max(minY, maxY);
    }

    private void clampToBounds() {

        Vector2 pos = body.getPosition();

        float clampedX = Math.max(minX, Math.min(pos.x, maxX));
        float clampedY = Math.max(minY, Math.min(pos.y, maxY));

        if (clampedX != pos.x || clampedY != pos.y) {

            body.setTransform(
                clampedX,
                clampedY,
                body.getAngle()
            );

            body.setLinearVelocity(0, 0);
        }
    }

    public boolean needToShoot() {
        if (TimeUtils.millis() - lastShotTime >= GameSettings.SHOOTING_COOL_DOWN) {
            lastShotTime = TimeUtils.millis();
            return true;
        }
        return false;
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

    public float getCurrentSpeed() {
        return body.getLinearVelocity().len();
    }

    public void updateBounds() {
        clampToBounds();
    }

    public void clampToMap(float mapWidthPixels, float mapHeightPixels) {
    }
}
