package com.xlpoolsion.server.model.entities;

import com.xlpoolsion.server.view.entities.ExplosionView;

import static com.xlpoolsion.server.view.GameView.PIXEL_TO_METER;

public class ExplosionModel extends EntityModel {
    public static final float WIDTH = ExplosionView.WIDTH * PIXEL_TO_METER;
    public static final float HEIGHT = ExplosionView.HEIGHT * PIXEL_TO_METER;
    public static final float EXPLOSION_DECAY_TIME = 3.0f;

    private float time_to_decay;

    public ExplosionModel(float x, float y, float rotation) {
        super(x, y, rotation);
    }

    public float getTimeToDecay() {
        return time_to_decay;
    }

    public void setTimeToDecay(float time_to_decay) {
        this.time_to_decay = time_to_decay;
    }

    public boolean decreaseTimeToDecay(float delta) {
        time_to_decay -= delta;
        return time_to_decay < 0;
    }
}
