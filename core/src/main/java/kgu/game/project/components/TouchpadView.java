package kgu.game.project.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import kgu.game.project.GameResources;

public class TouchpadView {
    private final ImageView background;
    private final ImageView knob;
    private final Vector2 touchpadPosition;
    private final Vector2 knobPosition;
    private final float knobRadius;
    private final float baseRadius;
    private boolean active;
    private final Vector2 direction;
    private float strength;

    public TouchpadView(float x, float y) {
        this.touchpadPosition = new Vector2(x, y);
        this.knobPosition = new Vector2(x, y);
        this.baseRadius = 120;
        this.knobRadius = 58;
        this.active = false;
        this.direction = new Vector2(0, 0);
        this.strength = 0;

        background = new ImageView(x - baseRadius, y - baseRadius,
            GameResources.TOUCHPAD_BG_IMG_PATH);
        background.setSize(baseRadius * 2, baseRadius * 2);

        knob = new ImageView(x - knobRadius, y - knobRadius,
            GameResources.TOUCHPAD_KNOB_IMG_PATH);
        knob.setSize(knobRadius * 2, knobRadius * 2);
    }

    public void update(float touchX, float touchY, boolean isTouching) {
        if (isTouching && isHit(touchX, touchY)) {
            active = true;

            float deltaX = touchX - touchpadPosition.x;
            float deltaY = touchY - touchpadPosition.y;

            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            float maxDistance = baseRadius - knobRadius;
            if (distance > maxDistance) {
                deltaX = deltaX * maxDistance / distance;
                deltaY = deltaY * maxDistance / distance;
                distance = maxDistance;
                strength = 2.0f;
            } else {
                strength = distance / maxDistance;
            }

            if (distance > 0.01f) {
                direction.x = deltaX / maxDistance;
                direction.y = deltaY / maxDistance;
            } else {
                direction.x = 0;
                direction.y = 0;
                strength = 0;
            }

            knobPosition.x = touchpadPosition.x + deltaX;
            knobPosition.y = touchpadPosition.y + deltaY;
            knob.setPosition(knobPosition.x - knobRadius, knobPosition.y - knobRadius);
        } else if (active && !isTouching) {

            reset();
        }
    }

    public void reset() {
        active = false;
        direction.x = 0;
        direction.y = 0;
        strength = 0;
        knobPosition.x = touchpadPosition.x;
        knobPosition.y = touchpadPosition.y;
        knob.setPosition(knobPosition.x - knobRadius, knobPosition.y - knobRadius);
    }

    public boolean isHit(float x, float y) {
        float distance = (float) Math.sqrt(
            Math.pow(x - touchpadPosition.x, 2) +
                Math.pow(y - touchpadPosition.y, 2)
        );
        return distance <= baseRadius;
    }

    public Vector2 getDirection() {
        return direction;
    }

    public float getStrength() {
        return strength;
    }


    public void draw(SpriteBatch batch) {
        background.draw(batch);
        knob.draw(batch);
    }

    public void dispose() {
        background.dispose();
        knob.dispose();
    }

    public boolean isActive() {
        return active;
    }


}
