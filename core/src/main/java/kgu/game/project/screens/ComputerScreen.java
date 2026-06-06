package kgu.game.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Iterator;

import kgu.game.project.GameResources;
import kgu.game.project.GameSession;
import kgu.game.project.MyGdxGame;
import kgu.game.project.components.ButtonView;
import kgu.game.project.components.DraggableWindow;
import kgu.game.project.components.IconView;
import kgu.game.project.components.ImageView;
import kgu.game.project.components.TextView;
import kgu.game.project.managers.LocalizationManager;

public class ComputerScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    GameSession gameSession;

    // PAUSED state UI
    ImageView fullBlackoutView;

    private boolean isNearComputer = false;

    // Track if touch is on UI elements
    ArrayList<IconView> iconsArray;
    private ImageView image;
    private DraggableWindow mailWindow;
    private ImageView os_icon;
    private TextView menu_text;
    ButtonView mail;

    public ComputerScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        gameSession = new GameSession();
        iconsArray = new ArrayList<>();

        System.out.println("Screen height: " + Gdx.graphics.getHeight());
        System.out.println("Screen width: " + Gdx.graphics.getWidth());

        image = new ImageView(
            0, 0,
            myGdxGame.camera.viewportWidth,
            myGdxGame.camera.viewportHeight,
            GameResources.IMAGE_FON_PATH
        );
        fullBlackoutView = new ImageView(0, 0, 1280, 100, GameResources.BLACKOUT_TOP_IMG_PATH);
        os_icon = new ImageView(18, 18, 32, 32, GameResources.SHINDOWS_LOGOTYPE_PATH);
        menu_text = new TextView(myGdxGame.commonPixelFontGreyText, 60, 30, LocalizationManager.get("computer.menu"));
        mail = new ButtonView(
            200,
            200,
            64, 64,
            GameResources.MAIL_ICON_PATH
        );

        // Создаем окно (изначально скрыто)
        mailWindow = new DraggableWindow(
            myGdxGame, 400, 100,
            LocalizationManager.get("computer.mail_from"),
            LocalizationManager.get("computer.mail_body"),
            GameResources.WINDOW_PATH,
            GameResources.CLOSE_BUTTON_PATH,
            myGdxGame.commonPixelFontText
        );

        // CloseListener - удаляем иконку при закрытии окна
        mailWindow.setOnCloseListener(() -> {
            removeMailIcon();
            System.out.println("Window closed, mail icon removed");
        });

        // OpenListener - добавляем иконку при открытии окна (только если её нет)
        mailWindow.setOnOpenListener(() -> {
            System.out.println("Window opened");
            addMailIconIfNotExists();
        });
    }

    @Override
    public void show() {
        Gdx.input.setOnscreenKeyboardVisible(false);
    }

    // Вспомогательный метод для проверки наличия иконки mail
    private boolean hasMailIcon() {
        for (IconView icon : iconsArray) {
            if (icon.name != null && icon.name.equals("mail")) {
                return true;
            }
        }
        return false;
    }

    // Вспомогательный метод для удаления иконки mail
    private void removeMailIcon() {
        Iterator<IconView> iterator = iconsArray.iterator();
        while (iterator.hasNext()) {
            IconView icon = iterator.next();
            if (icon.name != null && icon.name.equals("mail")) {
                icon.dispose(); // Освобождаем ресурсы
                iterator.remove();
                break; // Удаляем только первую найденную
            }
        }
    }

    // Вспомогательный метод для добавления иконки mail (только если её нет)
    private void addMailIconIfNotExists() {
        if (!hasMailIcon()) {
            int newX = 150 * (iconsArray.size() + 1);
            iconsArray.add(new IconView(newX, 5, 64, 64, GameResources.MAIL_ICON_PATH, "mail"));
            System.out.println("Mail icon added at position: " + newX);
        } else {
            System.out.println("Mail icon already exists, not adding duplicate");
        }
    }

    @Override
    public void render(float delta) {
        handleInput();
        draw();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touch = myGdxGame.uiCamera.unproject(
                new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)
            );
            boolean handledByWindow = false;

            // Проверяем клик по окну, если оно видимо
            if (mailWindow.isVisible()) {
                handledByWindow = mailWindow.handleTouch(touch, true);
            }

            // Проверяем клик по кнопке mail
            if (!handledByWindow && mail.isHit(touch.x, touch.y)) {
                System.out.println("Mail button touched!");
                removeMailIcon();

                // Переключаем видимость окна
                if (mailWindow.isVisible()) {
                    mailWindow.hide();  // CloseListener сработает здесь
                } else {
                    mailWindow.show();  // OpenListener сработает здесь
                }
            }
        } else {
            if (mailWindow.isVisible()) {
                mailWindow.handleTouch(null, false);
            }
        }
    }

    private void draw() {
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();
        image.draw(myGdxGame.batch);
        mail.draw(myGdxGame.batch);
        fullBlackoutView.draw(myGdxGame.batch);
        menu_text.draw(myGdxGame.batch);
        os_icon.draw(myGdxGame.batch);

        // Рисуем иконки из массива
        for (IconView icon : iconsArray) {
            icon.draw(myGdxGame.batch);
        }

        // Окно рисуется ТОЛЬКО если оно видимо
        if (mailWindow.isVisible()) {
            mailWindow.draw(myGdxGame.batch);
        }

        myGdxGame.batch.end();

        myGdxGame.batch.setProjectionMatrix(myGdxGame.uiCamera.combined);
        myGdxGame.batch.begin();
        myGdxGame.batch.end();
    }

    @Override
    public void dispose() {
        if (image != null) {
            image.dispose();
            image = null;
        }
        if (fullBlackoutView != null) {
            fullBlackoutView.dispose();
            fullBlackoutView = null;
        }
        if (os_icon != null) {
            os_icon.dispose();
            os_icon = null;
        }

        // Удаляем ButtonView
        if (mail != null) {
            mail.dispose();
            mail = null;
        }

        // Удаляем DraggableWindow
        if (mailWindow != null) {
            mailWindow.dispose();
            mailWindow = null;
        }

        // Удаляем все иконки
        if (iconsArray != null) {
            for (IconView icon : iconsArray) {
                if (icon != null) {
                    icon.dispose();
                }
            }
            iconsArray.clear();
        }
    }
}
