package com.xlpoolsion.server.view.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.xlpoolsion.server.XLPOOLsionServer;

/**
 * A view representing a random power up
 */
public class RandomPowerUpView extends EntityView{
    public static final float WIDTH = 32;
    public static final float HEIGHT = 32;

    /**
     * Creates a random power up view
     * @param xlpooLsionServer The game the view belongs to
     */
    public RandomPowerUpView(XLPOOLsionServer xlpooLsionServer) {
        super(xlpooLsionServer);
    }

    @Override
    protected Sprite createSprite(XLPOOLsionServer xlpooLsionServer) {
        Texture img = xlpooLsionServer.getAssetManager().get("PwrRandomUp.png");
        Sprite temp_sprite = new Sprite(img);
        temp_sprite.setSize(WIDTH, HEIGHT);
        return temp_sprite;
    }
}
