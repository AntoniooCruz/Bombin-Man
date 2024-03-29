package com.xlpoolsion.server.view.entities;

import com.xlpoolsion.server.XLPOOLsionServer;
import com.xlpoolsion.server.model.entities.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory used to create all views
 */
public class ViewFactory {
    private static Map<EntityModel, EntityView> viewMap = new HashMap<EntityModel, EntityView>();

    /**
     * Returns the View corresponding to the give model
     * @param xlpooLsionServer The game the views belong to
     * @param model The Model for which the view will be returned
     * @return The view correspondent to the model
     */
    public static EntityView getView(XLPOOLsionServer xlpooLsionServer, EntityModel model) {
        if(!viewMap.containsKey(model)) {
            createView(xlpooLsionServer, model);
        }
        return viewMap.get(model);
    }

    /**
     * Creates a view for the correct subclass based on the model
     * @param xlpooLsionServer The game the views will belong to
     * @param model The model based on which the view will be chosen
     */
    private static void createView(XLPOOLsionServer xlpooLsionServer, EntityModel model) {
        if (model instanceof BombModel) {
            viewMap.put(model, new BombView(xlpooLsionServer));
        } else if (model instanceof BreakableBrickModel) {
            viewMap.put(model, new BreakableBrickView(xlpooLsionServer));
        } else if (model instanceof BrickModel) {
            viewMap.put(model, new BrickView(xlpooLsionServer));
        } else if (model instanceof ExplosionModel) {
            viewMap.put(model, new ExplosionView(xlpooLsionServer));
        } else if (model instanceof PlayerModel) {
            createPlayerView(xlpooLsionServer, model);
        } else if (model instanceof PowerUpModel) {
            createPowerupView(xlpooLsionServer, model);
        } else if (model instanceof PowerDownModel) {
            createPowerDownView(xlpooLsionServer, model);
        } else if (model instanceof StunPowerModel) {
            viewMap.put(model, new StunPowerView(xlpooLsionServer));
        }
    }

    private static void createPlayerView(XLPOOLsionServer xlpooLsionServer, EntityModel model) {
        switch(((PlayerModel) model).getId()) {
            case 1:
                viewMap.put(model, new PlayerBlueView(xlpooLsionServer));
                break;
            case 2:
                viewMap.put(model, new PlayerBlackView(xlpooLsionServer));
                break;
            case 3:
                viewMap.put(model, new PlayerRedView(xlpooLsionServer));
                break;
            default:
                //Player 0 is the default sprite, and so this is future proofed for the case that more players are added
                viewMap.put(model, new PlayerWhiteView(xlpooLsionServer));
                break;
        }
    }

    private static void createPowerupView(XLPOOLsionServer xlpooLsionServer, EntityModel model) {
        switch(((PowerUpModel)model).getType()) {
            case SpeedUp:
                viewMap.put(model, new SpeedUpView(xlpooLsionServer));
                break;
            case BombRadUp:
                viewMap.put(model, new RadiusUpView(xlpooLsionServer));
                break;
            case BombsUp:
                viewMap.put(model, new BombUpView(xlpooLsionServer));
                break;
            case RandomUp:
                viewMap.put(model, new RandomPowerUpView(xlpooLsionServer));
        }
    }

    private static void createPowerDownView(XLPOOLsionServer xlpooLsionServer, EntityModel model) {
        switch(((PowerDownModel)model).getType()) {
            case SpeedDown:
                viewMap.put(model, new SpeedDownView(xlpooLsionServer));
                break;
            case BombRadDown:
                viewMap.put(model, new RadiusDownView(xlpooLsionServer));
                break;
            case BombsDown:
                viewMap.put(model, new BombDownView(xlpooLsionServer));
                break;
            case RandomDown:
                viewMap.put(model, new RandomPowerDownView(xlpooLsionServer));
                break;
        }
    }

    /**
     * Destroys the view correspondent to the model
     * @param model The model based on which the view will be destroyed
     */
    public static void destroyView(EntityModel model) {
        viewMap.remove(model);
    }
}
