package com.beachsurvivors.model.powerUps;

import com.beachsurvivors.model.PickUpAble;
import com.beachsurvivors.model.Player;

public class Berserk extends PowerUp implements PickUpAble {

    private double damageIncrease;
    private double movementSpeedIncrease;
    private double attackSpeedIncrease;

    public Berserk() {
        super("" , 10);
    }

    @Override
    public void onPickup(Player player) {

    }

    @Override
    public void applyAffect(Player player) {

    }
}
