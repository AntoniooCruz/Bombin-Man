package com.xlpoolsion.server.model.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.xlpoolsion.server.controller.GameController;
import com.xlpoolsion.server.model.entities.*;
import com.xlpoolsion.server.view.entities.ViewFactory;

import java.util.ArrayList;
import java.util.List;

import static com.xlpoolsion.server.controller.levels.BaseLevelController.*;

public abstract class BaseLevelModel {
    private PlayerModel[] players = new PlayerModel[GameController.MAX_PLAYERS];

    /**
     * A pool of bombs
     */
    private Pool<BombModel> bombPool = new Pool<BombModel>() {
        @Override
        protected BombModel newObject() {
            return new BombModel(0, 0, 0);
        }
    };

    /**
     * A pool of explosions
     */
    private Pool<ExplosionModel> explosionPool = new Pool<ExplosionModel>() {
        @Override
        protected ExplosionModel newObject() {
            return new ExplosionModel(0, 0, 0);
        }
    };

    private ArrayList<BombModel> bombs = new ArrayList<BombModel>();
    private ArrayList<ExplosionModel> explosions = new ArrayList<ExplosionModel>();
    private ArrayList<BrickModel> bricks = new ArrayList<BrickModel>();
    private ArrayList<BreakableBrickModel> breakableBricks = new ArrayList<BreakableBrickModel>();
    private ArrayList<PowerUpModel> powerUps = new ArrayList<PowerUpModel>();
    private EntityModel[][] brickMatrix = new EntityModel[GRID_END_X_BRICKS - GRID_START_X_BRICKS][GRID_END_Y_BRICKS - GRID_START_Y_BRICKS];

    /**
     * Constructs a Level Model with the indicated players that will be created in the indicated spawns. createBricks and createBreakableBricks are template methods
     * @param connectedPlayers Boolean array to indicate which players are connected
     * @param playerSpawns Array of player spawns. Coordinates are relative to the grid
     */
    public BaseLevelModel(boolean[] connectedPlayers, Vector2[] playerSpawns) {
        createPlayers(connectedPlayers, playerSpawns);
        createBricks();
        createBreakableBricks();
    }

    protected abstract void createBreakableBricks();

    protected abstract void createBricks();

    private void createPlayers(boolean[] connectedPlayers, Vector2[] playerSpawns) {
        if(playerSpawns.length < GameController.MAX_PLAYERS) {
            throw new IllegalArgumentException("Not enough player spawns");
        }

        for(int i = 0; i < GameController.MAX_PLAYERS; ++i) {
            if(connectedPlayers[i]) {
                players[i] = new PlayerModel(playerSpawns[i].x * GRID_PADDING_X, playerSpawns[i].y * GRID_PADDING_Y, 0, i);
            }
        }
    }

    /**
     * Creates a bomb at the player coordinates
     *
     * @param playerId The id of the player creating the bomb
     * @return The created BombModel
     */
    public BombModel createBomb(int playerId) {
        BombModel bomb = bombPool.obtain();

        bomb.setWalkable(true);
        bomb.setFlaggedForRemoval(false);
        //So that the bomb is created at the feet of the player instead at his head
        Vector2 bombPos = snapBombToGrid(players[playerId].getX() + PlayerModel.WIDTH/2, players[playerId].getY() - (PlayerModel.HEIGHT / 2) * 0.4f + PlayerModel.HEIGHT/4);
        bomb.setPosition(bombPos.x, bombPos.y);
        bomb.setRotation(players[playerId].getRotation());
        bomb.setTimeToExplosion(BombModel.EXPLOSION_DELAY);
        bomb.setOwner(players[playerId]);

        bombs.add(bomb);
        return bomb;
    }

    public void update(float delta) {
        for (BombModel bomb : bombs) {
            if (bomb.decreaseTimeToExplosion(delta)) {
                bomb.setFlaggedForRemoval(true);
                GameController.getInstance().createExplosions(bomb);
            }
        }

        for (ExplosionModel explosion : explosions) {
            if (explosion.decreaseTimeToDecay(delta)) {
                explosion.setFlaggedForRemoval(true);
            }
        }
    }

    static final int GRID_START_X_BRICKS = 0;
    static final int GRID_START_Y_BRICKS = 0;
    static final float GRID_START_X = GRID_START_X_BRICKS * BrickModel.WIDTH;
    static final float GRID_START_Y = GRID_START_Y_BRICKS * BrickModel.HEIGHT;
    static final int GRID_END_X_BRICKS = LEVEL_WIDTH_BRICKS;
    static final int GRID_END_Y_BRICKS = LEVEL_HEIGHT_BRICKS;
    static final float GRID_END_X = LEVEL_WIDTH;
    static final float GRID_END_Y = LEVEL_HEIGHT;
    static final float GRID_PADDING_X = BrickModel.WIDTH;
    static final float GRID_PADDING_Y = BrickModel.HEIGHT;

    private Vector2 snapBombToGrid(float x, float y) {
        int x_k = (int) ((x - GRID_START_X)/GRID_PADDING_X);
        int y_k = (int) ((y - GRID_START_Y)/GRID_PADDING_Y);

        return new Vector2(GRID_START_X + x_k * GRID_PADDING_X,GRID_START_Y+ y_k * GRID_PADDING_Y);
    }

    /**
     * Creates an explosion at the bomb coordinates, with the associated player radius
     *
     * @return The created ExplosionModels
     */
    public List<ExplosionModel> createExplosions(BombModel bomb) {
        ArrayList<ExplosionModel> temp_explosions = new ArrayList<ExplosionModel>();

        final int explosion_radius = bomb.getOwner().getExplosionRadius();

        //Creating Center
        Vector2 origin = new Vector2(bomb.getX(), bomb.getY());
        temp_explosions.add(createSingleExplosion(origin));

        //Creating Up
        List<ExplosionModel> upExplosions = createExplosionHelper(origin, new Vector2(0, GRID_PADDING_Y), explosion_radius);
        temp_explosions.addAll(upExplosions);

        //Creating Down
        List<ExplosionModel> downExplosions = createExplosionHelper(origin, new Vector2(0, -GRID_PADDING_Y), explosion_radius);
        temp_explosions.addAll(downExplosions);

        //Creating Left
        List<ExplosionModel> leftExplosions = createExplosionHelper(origin, new Vector2(-GRID_PADDING_X, 0), explosion_radius);
        temp_explosions.addAll(leftExplosions);

        //Creating Right
        List<ExplosionModel> rightExplosions = createExplosionHelper(origin, new Vector2(GRID_PADDING_X, 0), explosion_radius);
        temp_explosions.addAll(rightExplosions);

        explosions.addAll(temp_explosions);
        return temp_explosions;
    }

    private List<ExplosionModel> createExplosionHelper(Vector2 origin, Vector2 shift, int explosionRadius) {
        ArrayList<ExplosionModel> explosions = new ArrayList<ExplosionModel>();
        EntityModel fetchedBrick;

        for (int i = 1; i <= explosionRadius; ++i) {
            Vector2 tempvec = new Vector2(origin);
            tempvec.mulAdd(shift, i);
            if((fetchedBrick = getBrickAt(tempvec.x, tempvec.y)) == null) {
                //No brick, continue creating explosions
                explosions.add(createSingleExplosion(tempvec));
            } else {
                //Found brick, stop creating explosions, and if brick is destroyable mark it for destruction
                //(explosions destroy adjacent bricks but do not propagate there)
                if(fetchedBrick instanceof  BreakableBrickModel) {
                    fetchedBrick.setFlaggedForRemoval(true);
                }
                break;
            }
        }

        return explosions;
    }

    private EntityModel getBrickAt(float x, float y) {
        return brickMatrix[(int) ((x - GRID_START_X)/GRID_PADDING_X)][(int) ((y - GRID_START_Y)/GRID_PADDING_Y)];
    }

    private ExplosionModel createSingleExplosion(Vector2 coordinates) {
        ExplosionModel explosion = explosionPool.obtain();
        explosion.setFlaggedForRemoval(false);
        explosion.setPosition(coordinates.x, coordinates.y);
        //explosion.setDirection(something With The CoordinateVector and maybe something passed from createExplosionHelper)
        explosion.setRotation(0);
        explosion.setTimeToDecay(ExplosionModel.EXPLOSION_DECAY_TIME);
        return explosion;
    }

    private static final float POWERUP_CHANCE = 0.6f;

    public void remove(EntityModel model) {
        //Destroying the no longer necessary view for this model
        ViewFactory.destroyView(model);

        if (model instanceof BombModel) {
            bombs.remove(model);
            bombPool.free((BombModel) model);
        } else if (model instanceof ExplosionModel) {
            explosions.remove(model);
            explosionPool.free((ExplosionModel) model);
        } else if (model instanceof BreakableBrickModel) {
            if(Math.random() > POWERUP_CHANCE){
                GameController.getInstance().createPowerUp((BreakableBrickModel) model);
            }
            brickMatrix[(int) ((model.getX() - GRID_START_X)/GRID_PADDING_X)][(int) ((model.getY() - GRID_START_Y)/GRID_PADDING_Y)] = null;
            breakableBricks.remove(model);
        } else if (model instanceof PlayerModel) {
            players[((PlayerModel) model).getId()] = null;
        }
        else if (model instanceof PowerUpModel) {
            powerUps.remove(model);
        }

    }

    /**
     * Creates a brick at the given coordinates (in bricks) and adds it to internal storage
     *
     * @param x x Coordinate in bricks
     * @param y y Coordinate in bricks
     */
    public void createBrick(int x, int y) {
        BrickModel brick = new BrickModel(x * BrickModel.WIDTH, y * BrickModel.HEIGHT, 0);

        brick.setFlaggedForRemoval(false);
        brick.setPosition(x * BrickModel.WIDTH, y * BrickModel.HEIGHT);

        bricks.add(brick);
        brickMatrix[x][y] = brick;
    }

    /**
     * Creates a Breakable brick at the given coordinates (in bricks) and adds it to internal storage
     *
     * @param x x Coordinate in bricks
     * @param y y Coordinate in bricks
     */
    public void createBreakableBrick(int x, int y) {
        BreakableBrickModel breakablebrick = new BreakableBrickModel(x * BreakableBrickModel.WIDTH, y * BreakableBrickModel.HEIGHT, 0);

        breakablebrick.setFlaggedForRemoval(false);
        breakablebrick.setPosition(x * BreakableBrickModel.WIDTH, y * BreakableBrickModel.HEIGHT);

        breakableBricks.add(breakablebrick);
        brickMatrix[x][y] = breakablebrick;
    }

    /**
     * Creates a powerUp at the given coordinates and adds it to internal storage
     *
     * @param brick The Model that was destroid to give room for the powerUp
     */
    public PowerUpModel createPowerUp(BreakableBrickModel brick) {
        PowerUpModel powerUp= new PowerUpModel(brick.getX(), brick.getY(), 0);

        powerUp.setFlaggedForRemoval(false);
        powerUp.setPosition(brick.getX(),brick.getY());

        powerUps.add(powerUp);
        return powerUp;
    }

    public PlayerModel getPlayer(int playerId) {
        return players[playerId];
    }

    public List<BombModel> getBombs() {
        return bombs;
    }

    public List<ExplosionModel> getExplosions() {
        return explosions;
    }

    public List<BreakableBrickModel> getBreakableBricks() {
        return breakableBricks;
    }

    public List<BrickModel> getBricks() {
        return bricks;
    }

    public List<PowerUpModel> getPowerUps() {
        return powerUps;
    }
}
