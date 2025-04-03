package com.beachsurvivors.model.powerUps;

import com.beachsurvivors.model.PickUpAble;
import com.beachsurvivors.model.Player;

public class LuckyClover extends PowerUp implements PickUpAble {

    private float critChanceIncrease;

    public LuckyClover() {
        super("",  10);
    }

    @Override
    public void onPickup(Player player) {

    }

    @Override
    public void applyAffect(Player player) {

    }
}
