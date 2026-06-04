package kgu.game.project.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import kgu.game.project.GameResources;

public class TouchpadView {
    private ImageView background;
    private ImageView knob;
    private Vector2 touchpadPosition;
    private Vector2 knobPosition;
    private float knobRadius;
    private float baseRadius;
    private boolean active;
    private Vector2 direction;  // Направление (-1..1 по X и Y)
    private float strength;      // Сила нажатия (0..1)

    public TouchpadView(float x, float y) {
        this.touchpadPosition = new Vector2(x, y);
        this.knobPosition = new Vector2(x, y);
        this.baseRadius = 80;     // Увеличим радиус для удобства
        this.knobRadius = 35;
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

            // Calculate delta from touchpad center
            float deltaX = touchX - touchpadPosition.x;
            float deltaY = touchY - touchpadPosition.y;

            // Calculate distance from center
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            // Limit distance to base radius
            float maxDistance = baseRadius - knobRadius;
            if (distance > maxDistance) {
                deltaX = deltaX * maxDistance / distance;
                deltaY = deltaY * maxDistance / distance;
                distance = maxDistance;
                strength = 1.0f;  // Максимальная сила
            } else {
                // Сила пропорциональна расстоянию от центра
                strength = distance / maxDistance;
            }

            // Нормализуем направление (единичный вектор)
            if (distance > 0.01f) {
                direction.x = deltaX / maxDistance;
                direction.y = deltaY / maxDistance;
            } else {
                direction.x = 0;
                direction.y = 0;
                strength = 0;
            }

            // Обновляем позицию ручки
            knobPosition.x = touchpadPosition.x + deltaX;
            knobPosition.y = touchpadPosition.y + deltaY;
            knob.setPosition(knobPosition.x - knobRadius, knobPosition.y - knobRadius);
        } else if (active && !isTouching) {
            // Сброс при отпускании
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

    // Получить вектор движения (направление * сила)
    public Vector2 getMovementVector() {
        return new Vector2(direction.x * strength, direction.y * strength);
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


    public void setPosition(float x, float y) {
        this.touchpadPosition.set(x, y);
        this.knobPosition.set(x, y);
        background.setPosition(x - baseRadius, y - baseRadius);
        knob.setPosition(x - knobRadius, y - knobRadius);
    }
}
