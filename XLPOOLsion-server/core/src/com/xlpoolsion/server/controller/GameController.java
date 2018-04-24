package com.xlpoolsion.server.controller;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.xlpoolsion.server.controller.entities.BombBody;
import com.xlpoolsion.server.controller.entities.PlayerBody;
import com.xlpoolsion.server.model.GameModel;
import com.xlpoolsion.server.model.entities.BombModel;
import com.xlpoolsion.server.model.entities.EntityModel;
import com.xlpoolsion.server.model.entities.PlayerModel;

public class GameController implements ContactListener {
    private static GameController instance = null;

    private final World world;
    private final PlayerBody player;

    /**
     * The map width in meters.
     */
    public static final float GAME_WIDTH = 50;

    /**
     * The map height in meters.
     */
    public static final float GAME_HEIGHT = 50;

    private GameController() {
        world = new World(new Vector2(0, 0), true);

        //Creating bodies
        player = new PlayerBody(world, GameModel.getInstance().getPlayer());

        world.setContactListener(this);
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    private float accumulator = 0;
    private static final float TIME_STEP = 1 / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    public void update(float delta) {
        GameModel.getInstance().update(delta);

        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(delta, 0.25f);

        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }

        //Updating bodies
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);

        for (Body body : bodies) {
            //verifyBounds(body);
            ((EntityModel) body.getUserData()).setPosition(body.getPosition().x, body.getPosition().y);
            ((EntityModel) body.getUserData()).setRotation(body.getAngle());
        }
    }

    public void movePlayerUp(float delta) {
        player.moveUp();
        ((PlayerModel) player.getUserData()).setMoving(true);
        ((PlayerModel) player.getUserData()).setOrientation(PlayerModel.Orientation.UP);
    }

    public void movePlayerDown(float delta) {
        player.moveDown();
        ((PlayerModel) player.getUserData()).setMoving(true);
        ((PlayerModel) player.getUserData()).setOrientation(PlayerModel.Orientation.DOWN);
    }

    public void movePlayerLeft(float delta) {
        player.moveLeft();
        ((PlayerModel) player.getUserData()).setMoving(true);
        ((PlayerModel) player.getUserData()).setOrientation(PlayerModel.Orientation.LEFT);
    }

    public void movePlayerRight(float delta) {
        player.moveRight();
        ((PlayerModel) player.getUserData()).setMoving(true);
        ((PlayerModel) player.getUserData()).setOrientation(PlayerModel.Orientation.RIGHT);
    }

    public void stopPlayer(float delta) {
        player.stop();
        ((PlayerModel) player.getUserData()).setMoving(false);
    }

    public void addBomb() {
        //TODO: Time and bomb limit verifications
        BombModel bomb = GameModel.getInstance().createBomb();
        //No need to do anything with the declared body, as it is stored in the world
        BombBody body = new BombBody(world, bomb);
    }

    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();
    }

    @Override
    public void endContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        if (bodyA.getUserData() instanceof PlayerModel && bodyB.getUserData() instanceof  BombModel && ((BombModel)bodyB.getUserData()).isWalkable()) {
            enableBombCollisions(bodyB);
        }

        if (bodyB.getUserData() instanceof PlayerModel && bodyA.getUserData() instanceof  BombModel && ((BombModel)bodyA.getUserData()).isWalkable()) {
            enableBombCollisions(bodyA);
        }
    }

    /**
     * Enables collisions of a bomb body
     * @param bodyB Body of bomb to enable collisions for
     */
    private void enableBombCollisions(Body bodyB) {
        ((BombModel)bodyB.getUserData()).setWalkable(false);
        Array<Fixture> fixtures = bodyB.getFixtureList();
        for(Fixture fixture : fixtures) {
            fixture.setSensor(false);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public void removeFlagged() {
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for (Body body : bodies) {
            if (((EntityModel)body.getUserData()).isFlaggedForRemoval()) {
                GameModel.getInstance().remove((EntityModel) body.getUserData());
                world.destroyBody(body);
            }
        }
    }
}
