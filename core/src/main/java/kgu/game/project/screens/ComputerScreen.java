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

    ImageView fullBlackoutView;


    ArrayList<IconView> iconsArray;
    private ImageView image;
    private DraggableWindow mailWindow;
    private ImageView os_icon;
    private final TextView menu_text;
    TextView apps_text;
    boolean toDrawMenu = false;
    ButtonView mail;
    ImageView menu;
    ImageView fon;
    ButtonView logout_button;
    ImageView mail_app;

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
        fon = new ImageView(0, 0, 100, 100, GameResources.EMPTY_IMG_PATH);
        os_icon = new ImageView(18, 18, 32, 32, GameResources.SHINDOWS_LOGOTYPE_PATH);
        menu_text = new TextView(myGdxGame.commonPixelFontGreyText, 60, 30, LocalizationManager.get("computer.menu"));
        mail = new ButtonView(
            1200,
            200,
            64, 64,
            GameResources.MAIL_ICON_PATH
        );
        mail_app = new ImageView(20, 580, 64, 64, GameResources.MAIL_ICON_PATH);
        menu = new ImageView(0, 100, GameResources.MENU_IMG_PATH);
        apps_text = new TextView(MyGdxGame.arialWhiteFont, 20, 650, "Приложения:");
        logout_button = new ButtonView(270, 130, GameResources.LOGOUT_IMG_PATH);
        logout_button.hide();
        mailWindow = new DraggableWindow(
            myGdxGame, 400, 100,
            LocalizationManager.get("computer.mail_from"),
            LocalizationManager.get("computer.mail_body"),
            GameResources.WINDOW_PATH,
            GameResources.CLOSE_BUTTON_PATH,
            myGdxGame.commonPixelFontText
        );

        mailWindow.setOnCloseListener(() -> {
            removeMailIcon();
            System.out.println("Window closed, mail icon removed");
        });

        mailWindow.setOnOpenListener(() -> {
            System.out.println("Window opened");
            addMailIconIfNotExists();
        });
        myGdxGame.camera.position.set(myGdxGame.camera.viewportWidth / 2f, myGdxGame.camera.viewportHeight / 2f, 0);
    }

    @Override
    public void show() {
        Gdx.input.setOnscreenKeyboardVisible(false);
    }

    private boolean hasMailIcon() {
        for (IconView icon : iconsArray) {
            if (icon.name != null && icon.name.equals("mail")) {
                return true;
            }
        }
        return false;
    }

    private void removeMailIcon() {
        Iterator<IconView> iterator = iconsArray.iterator();
        while (iterator.hasNext()) {
            IconView icon = iterator.next();
            if (icon.name != null && icon.name.equals("mail")) {
                icon.dispose();
                iterator.remove();
                break;
            }
        }
    }

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
        if (os_icon == null) {
            myGdxGame.setScreen(new HackScreen(myGdxGame));
            return;
        }
        if (Gdx.input.justTouched()) {
            Vector3 touch = myGdxGame.uiCamera.unproject(
                new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)
            );
            boolean handledByWindow = false;
            if (os_icon.isHit(touch.x, touch.y) || menu_text.isHit(touch.x, touch.y) || fon.isHit(touch.x, touch.y)) {
                toDrawMenu = !toDrawMenu;
                if (toDrawMenu) {
                    logout_button.show();
                } else {
                    logout_button.hide();
                }
            }
            if (logout_button.isHit(touch.x, touch.y)) {
                if (myGdxGame.loginScreen == null) {
                    myGdxGame.loginScreen = new LoginScreen(myGdxGame);
                }
                myGdxGame.setScreen(myGdxGame.loginScreen);
                return;
            }

            if (mailWindow.isVisible()) {
                handledByWindow = mailWindow.handleTouch(touch, true);
            }
            if (toDrawMenu) {
                if (mail_app != null && mail_app.isHit(touch.x, touch.y)) {
                    if (mailWindow.isVisible()) {
                        mailWindow.hide();
                        removeMailIcon();
                    } else {
                        mailWindow.show();
                    }
                }
            }

            if (!handledByWindow && mail.isHit(touch.x, touch.y)) {
                System.out.println("Mail button touched!");
                removeMailIcon();

                if (mailWindow.isVisible()) {
                    mailWindow.hide();
                } else {
                    mailWindow.show();
                }
            }
        } else {
            if (mailWindow == null) {
                return;
            }
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
        if (image != null) {
            image.draw(myGdxGame.batch);
        }
        if (mail != null) {
            mail.draw(myGdxGame.batch);
        }
        if (fullBlackoutView != null) {
            fullBlackoutView.draw(myGdxGame.batch);
        }
        if (menu_text != null) {
            menu_text.draw(myGdxGame.batch);
        }
        if (os_icon != null) {
            os_icon.draw(myGdxGame.batch);
        }
        if (fon != null) {
            fon.draw(myGdxGame.batch);
        }

        for (IconView icon : iconsArray) {
            icon.draw(myGdxGame.batch);
        }

        if (mailWindow != null && mailWindow.isVisible()) {
            mailWindow.draw(myGdxGame.batch);
        }
        if (toDrawMenu) {
            menu.draw(myGdxGame.batch);
            apps_text.draw(myGdxGame.batch);
            logout_button.draw(myGdxGame.batch);
            mail_app.draw(myGdxGame.batch);
        }
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

        if (mail != null) {
            mail.dispose();
            mail = null;
        }

        if (mailWindow != null) {
            mailWindow.dispose();
            mailWindow = null;
        }
        if (mail_app != null) {
            mail_app.dispose();
        }
        menu.dispose();

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
