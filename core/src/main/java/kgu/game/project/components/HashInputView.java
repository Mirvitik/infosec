package kgu.game.project.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kgu.game.project.GameResources;
import kgu.game.project.MyGdxGame;


public class HashInputView {

    public static final int TASK_COUNT = 10;

    private static final float PANEL_X = 140f;
    private static final float PANEL_Y = 60f;
    private static final float PANEL_W = 1000f;
    private static final float PANEL_H = 600f;

    private static final float BTN_W = 430f;
    private static final float BTN_H = 62f;
    private static final float BTN_GAP_X = 40f;
    private static final float BTN_GAP_Y = 18f;

    private static final int WRAP = 64;
    private static final float LINE_STEP = 26f;

    private static final float FEEDBACK_TIME = 1.4f;


    private static final class Task {
        final String hash;
        final String[] options;
        final int correct;

        Task(String hash, int correct, String... options) {
            this.hash = hash;
            this.correct = correct;
            this.options = options;
        }
    }

    // основные
    private static List<Task> buildTasks() {
        List<Task> all = new ArrayList<>();

        all.add(new Task("482c811da5d5b4bc6d497ffa98491e38", 0,
            "MD5", "SHA-1", "SHA-256", "CRC32"));

        all.add(new Task("cbfdac6008f9cab4083784cbd1874f76618d2a97", 2,
            "MD5", "SHA-256", "SHA-1", "bcrypt"));

        all.add(new Task("ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f", 1,
            "SHA-1", "SHA-256", "SHA-512", "MD5"));

        all.add(new Task("bed4efa1d4fdbd954bd3705d6a2a78270ec9a52ecfbfb010c61862af5c76af17"
            + "61ffeb1aef6aca1bf5d02b3781aa854fabd2b69c790de74e17ecfec3cb6ac4bf", 3,
            "SHA-256", "SHA-384", "MD5", "SHA-512"));

        all.add(new Task("3d45597256050bb1e93bd9c10aee4c8716f8774f5a48c995bf0cf860", 2,
            "SHA-1", "SHA-256", "SHA-224", "CRC32"));

        all.add(new Task("648357a04407e0a73fe201d9aad9bec165cbf63b6db4311b28f7e256"
            + "b214a0725e45069c0162232d31412580255c461e", 1,
            "SHA-512", "SHA-384", "SHA-256", "MD5"));

        all.add(new Task("d043df23", 0,
            "CRC32", "MD5", "SHA-1", "Adler-32"));

        all.add(new Task("$2b$12$K8sPq1xLqFq0wXvHnX2bEOe3Q9c0oZ7ZC1uA5nF2s9mYy3bR4tW1K", 1,
            "SHA-512", "bcrypt", "MD5", "Argon2"));

        all.add(new Task("$1$Xy7Rm2Qz$8hL0pV3nC1dK9sT4uW6bE.", 3,
            "bcrypt", "SHA-256", "NTLM", "MD5-crypt ($1$)"));

        all.add(new Task("$argon2id$v=19$m=65536,t=3,p=4$c29tZXNhbHQ$RdescudvJCsgt3ub+b+dWRWJTmaaJObG", 0,
            "Argon2id", "bcrypt", "PBKDF2", "SHA-3"));

        all.add(new Task("ab3fe4003f14e3ef573417f95e47d4985c482eadd139c08b3758eeae7cc60b9d", 2,
            "SHA-256", "SHA-512", "SHA3-256", "RIPEMD-160"));

        all.add(new Task("$6$rounds=5000$saltsalt$3xUu7sA1Kp9Lm2Nq0Rv4Tw6Yz8Bc1De3Fg5Hj7Kl9"
            + "Mn1Pq3Rs5Tu7Vw9Xy1Za3Bc5De7Fg9Hj1Kl3Mn5Pq7R", 1,
            "MD5-crypt", "SHA-512-crypt ($6$)", "bcrypt", "Argon2"));

        Collections.shuffle(all);
        return new ArrayList<>(all.subList(0, TASK_COUNT));
    }


    private final MyGdxGame myGdxGame;
    private final Runnable onSuccess;
    private final Runnable onFailure;

    private Texture dimTexture;
    private Texture panelTexture;

    private final TextView titleText;
    private final TextView progressText;
    private final TextView questionText;
    private final TextView[] hashLines;
    private final TextView feedbackText;
    private final ButtonView[] answerButtons;
    private final ButtonView closeButton;

    private List<Task> tasks;
    private int currentIndex;
    private int correctCount;

    private boolean visible = false;
    private boolean answered = false;
    private boolean lastAnswerCorrect = false;
    private float feedbackTimer = 0f;

    public HashInputView(MyGdxGame myGdxGame, Runnable onSuccess, Runnable onFailure) {
        this.myGdxGame = myGdxGame;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;

        dimTexture = new Texture(GameResources.FON_WITH_OPACITY_PATH);
        panelTexture = new Texture(GameResources.DIALOG_FON_IMG_PATH);

        titleText = new TextView(myGdxGame.commonBlackFont,
            PANEL_X + 40, PANEL_Y + PANEL_H - 70, "ОПРЕДЕЛИ АЛГОРИТМ ХЭШИРОВАНИЯ");

        progressText = new TextView(myGdxGame.xanmonoFont,
            PANEL_X + PANEL_W - 180, PANEL_Y + PANEL_H - 66, "0 / " + TASK_COUNT);

        questionText = new TextView(myGdxGame.xanmonoFont,
            PANEL_X + 40, PANEL_Y + PANEL_H - 130, "Каким алгоритмом получен этот хэш?");

        hashLines = new TextView[4];
        for (int i = 0; i < hashLines.length; i++) {
            hashLines[i] = new TextView(myGdxGame.xanmonoFont,
                PANEL_X + 40, PANEL_Y + PANEL_H - 190 - i * LINE_STEP, "");
        }

        float gridW = BTN_W * 2 + BTN_GAP_X;
        float startX = PANEL_X + (PANEL_W - gridW) / 2f;
        float bottomRowY = PANEL_Y + 150f;

        answerButtons = new ButtonView[4];
        for (int i = 0; i < 4; i++) {
            int col = i % 2;
            int row = i / 2; // 0 - верхний ряд, 1 - нижний
            float bx = startX + col * (BTN_W + BTN_GAP_X);
            float by = bottomRowY + (1 - row) * (BTN_H + BTN_GAP_Y);
            answerButtons[i] = new ButtonView(
                bx, by, BTN_W, BTN_H,
                myGdxGame.commonBlackFont,
                GameResources.BUTTON_LONG_BG_IMG_PATH,
                "-"
            );
        }

        feedbackText = new TextView(myGdxGame.xanmonoFont,
            PANEL_X + 40, PANEL_Y + 90, "");

        closeButton = new ButtonView(
            PANEL_X + PANEL_W - 180, PANEL_Y + 20,
            150, 52,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "Выйти"
        );
    }

    public void show() {
        visible = true;
        answered = false;
        feedbackTimer = 0f;
        correctCount = 0;
        currentIndex = 0;
        tasks = buildTasks();
        loadTask();
    }

    public void hide() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }


    public void update(float delta) {
        if (!visible || !answered) return;

        feedbackTimer -= delta;
        if (feedbackTimer <= 0f) {
            answered = false;
            currentIndex++;
            if (currentIndex >= tasks.size()) {
                finish();
            } else {
                loadTask();
            }
        }
    }

    public void handleTouch() {
        if (!visible) return;

        // клавиши 1-4 на десктопе
        if (!answered) {
            for (int i = 0; i < 4; i++) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                    answer(i);
                    return;
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            hide();
            return;
        }

        if (!Gdx.input.justTouched()) return;

        float tx = myGdxGame.touch.x;
        float ty = myGdxGame.touch.y;

        if (closeButton.isHit(tx, ty)) {
            hide();
            return;
        }

        if (answered) return;

        for (int i = 0; i < answerButtons.length; i++) {
            if (answerButtons[i].isHit(tx, ty)) {
                answer(i);
                return;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        if (!visible) return;

        batch.draw(dimTexture, 0, 0, 1280, 720);
        batch.draw(panelTexture, PANEL_X, PANEL_Y, PANEL_W, PANEL_H);

        titleText.draw(batch);
        progressText.draw(batch);
        questionText.draw(batch);
        for (TextView line : hashLines) {
            line.draw(batch);
        }
        for (ButtonView button : answerButtons) {
            button.draw(batch);
        }
        feedbackText.draw(batch);
        closeButton.draw(batch);
    }


    private void loadTask() {
        Task task = tasks.get(currentIndex);

        for (int i = 0; i < hashLines.length; i++) {
            int from = i * WRAP;
            if (from >= task.hash.length()) {
                hashLines[i].setText("");
            } else {
                int to = Math.min(from + WRAP, task.hash.length());
                hashLines[i].setText(task.hash.substring(from, to));
            }
        }

        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setText((i + 1) + ". " + task.options[i]);
        }

        progressText.setText((currentIndex + 1) + " / " + tasks.size());
        feedbackText.setText("Верных ответов: " + correctCount);
    }

    private void answer(int index) {
        Task task = tasks.get(currentIndex);
        lastAnswerCorrect = index == task.correct;
        if (lastAnswerCorrect) {
            correctCount++;
            feedbackText.setText("ВЕРНО: " + task.options[task.correct]);
        } else {
            feedbackText.setText("НЕВЕРНО. Правильный ответ: " + task.options[task.correct]);
        }
        answered = true;
        feedbackTimer = FEEDBACK_TIME;
    }

    private void finish() {
        visible = false;
        if (correctCount >= TASK_COUNT) {
            if (onSuccess != null) onSuccess.run();
        } else {
            if (onFailure != null) onFailure.run();
        }
    }

    public void dispose() {
        if (dimTexture != null) {
            dimTexture.dispose();
            dimTexture = null;
        }
        if (panelTexture != null) {
            panelTexture.dispose();
            panelTexture = null;
        }
        for (ButtonView button : answerButtons) {
            if (button != null) button.dispose();
        }
        closeButton.dispose();
        // TextView.dispose() уничтожает общий BitmapFont из MyGdxGame - здесь его звать нельзя
    }
}
