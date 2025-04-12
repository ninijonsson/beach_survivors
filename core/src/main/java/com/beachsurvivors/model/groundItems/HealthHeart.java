package com.beachsurvivors.model.groundItems;

import com.beachsurvivors.model.Player;

public class HealthHeart extends PowerUp implements PickUpAble {

    private final int healthAmount = 10;

    public HealthHeart(float x, float y) {
        super("entities/power_ups/health_heart.png", 0, x, y);

    }

    @Override
    public void onPickup(Player player) {
        applyAffect(player);
    }

    @Override
    public void applyAffect(Player player) {
        player.increaseHealthPoints(healthAmount);
    }
}
