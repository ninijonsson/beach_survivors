package com.beachsurvivors.model.powerUps;

import com.beachsurvivors.model.PickUpAble;
import com.beachsurvivors.model.Player;

public class Berserk extends PowerUp implements PickUpAble {

    private double damageIncrease;
    private double movementSpeedIncrease;
    private double attackSpeedIncrease;

    public Berserk(float x, float y) {
        super("" , 10, x, y);
    }

    @Override
    public void onPickup(Player player) {

    }

    @Override
    public void applyAffect(Player player) {

    }
}
