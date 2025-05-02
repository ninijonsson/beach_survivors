package com.beachsurvivors.model.groundItems;

import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;

public class HealthHeart extends PowerUp implements PickUpAble {

    private final int healthAmount = 10;

    public HealthHeart(float x, float y, ParticleEffectPoolManager ppm) {
        super(AssetLoader.get().getTexture("assets/entities/power_ups/health_heart.png"), 0, x, y, ppm);

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
