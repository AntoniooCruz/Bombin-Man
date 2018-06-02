package com.xlpoolsion.server.view.entities;

import com.xlpoolsion.server.XLPOOLsionServer;
import com.xlpoolsion.server.view.TextureManager;

public class PlayerRedView extends PlayerView {
    public PlayerRedView(XLPOOLsionServer xlpooLsionServer) {
        super(xlpooLsionServer);
    }

     void createAnimations(XLPOOLsionServer xlpooLsionServer) {
         super.upAnim = xlpooLsionServer.getTextureManager().getPlayerAnimation(TextureManager.PlayerColor.RED, TextureManager.PlayerAnimType.UP);
         super.rightAnim = xlpooLsionServer.getTextureManager().getPlayerAnimation(TextureManager.PlayerColor.RED, TextureManager.PlayerAnimType.RIGHT);
         super.downAnim = xlpooLsionServer.getTextureManager().getPlayerAnimation(TextureManager.PlayerColor.RED, TextureManager.PlayerAnimType.DOWN);
         super.leftAnim = xlpooLsionServer.getTextureManager().getPlayerAnimation(TextureManager.PlayerColor.RED, TextureManager.PlayerAnimType.LEFT);
         super.deathAnim = xlpooLsionServer.getTextureManager().getPlayerAnimation(TextureManager.PlayerColor.RED, TextureManager.PlayerAnimType.DEATH);
         super.stunAnim = xlpooLsionServer.getTextureManager().getPlayerAnimation(TextureManager.PlayerColor.RED, TextureManager.PlayerAnimType.STUN);
    }
}
