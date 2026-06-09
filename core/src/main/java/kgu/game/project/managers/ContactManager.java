package kgu.game.project.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

import kgu.game.project.GameSettings;
import kgu.game.project.objects.GameObject;

public class ContactManager {

    public interface OnBeginContact {
        void onPlayerHit(GameObject object);
    }

    public interface OnEndContact {
        void onPlayerLeave(GameObject object);
    }

    private final OnBeginContact onBeginContact;
    private final OnEndContact onEndContact;

    public ContactManager(World world, OnBeginContact onBegin, OnEndContact onEnd) {
        this.onBeginContact = onBegin;
        this.onEndContact = onEnd;

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixA = contact.getFixtureA();
                Fixture fixB = contact.getFixtureB();

                int cDef = fixA.getFilterData().categoryBits;
                int cDef2 = fixB.getFilterData().categoryBits;

                GameObject player = null;
                GameObject otherObject = null;

                if (cDef == GameSettings.SHIP_BIT) {
                    player = (GameObject) fixA.getUserData();
                    otherObject = (GameObject) fixB.getUserData();
                } else if (cDef2 == GameSettings.SHIP_BIT) {
                    player = (GameObject) fixB.getUserData();
                    otherObject = (GameObject) fixA.getUserData();
                }
                if (player != null && otherObject != null && onBeginContact != null) {
                    Gdx.app.log("ContactManager", "Player hit: " + otherObject.getClass().getSimpleName());
                    onBeginContact.onPlayerHit(otherObject);

                    otherObject.setJustTouched(true);
                }


            }

            @Override
            public void endContact(Contact contact) {
                Fixture fixA = contact.getFixtureA();
                Fixture fixB = contact.getFixtureB();

                int cDef = fixA.getFilterData().categoryBits;
                int cDef2 = fixB.getFilterData().categoryBits;

                GameObject player = null;
                GameObject otherObject = null;

                if (cDef == GameSettings.SHIP_BIT) {
                    player = (GameObject) fixA.getUserData();
                    otherObject = (GameObject) fixB.getUserData();
                } else if (cDef2 == GameSettings.SHIP_BIT) {
                    player = (GameObject) fixB.getUserData();
                    otherObject = (GameObject) fixA.getUserData();
                }
                if (player != null && otherObject != null && onEndContact != null) {
                    onEndContact.onPlayerLeave(otherObject);
                    otherObject.setJustTouched(false);
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
    }
}
