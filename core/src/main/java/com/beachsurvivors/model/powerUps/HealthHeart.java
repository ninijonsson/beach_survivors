package com.beachsurvivors.model.powerUps;

import com.beachsurvivors.model.PickUpAble;
import com.beachsurvivors.model.Player;

public class HealthHeart extends PowerUp implements PickUpAble {

    private int healthAmount;

    public HealthHeart(int duration) {
        super("" ,duration);
    }

    @Override
    public void onPickup(Player player) {

    }

    @Override
    public void applyAffect(Player player) {

    }
}
