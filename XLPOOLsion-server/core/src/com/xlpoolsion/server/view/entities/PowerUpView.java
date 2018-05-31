package com.xlpoolsion.server.view.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.xlpoolsion.server.XLPOOLsionServer;

public class PowerUpView extends EntityView{
    public static final float WIDTH = 32;
    public static final float HEIGHT = 32;

    public PowerUpView(XLPOOLsionServer xlpooLsionServer) {
        super(xlpooLsionServer);
    }

    @Override
    protected Sprite createSprite(XLPOOLsionServer xlpooLsionServer) {
        Texture img = xlpooLsionServer.getAssetManager().get("explosion.png");
        Sprite temp_sprite = new Sprite(img);
        temp_sprite.setSize(WIDTH, HEIGHT);
        return temp_sprite;
    }
}