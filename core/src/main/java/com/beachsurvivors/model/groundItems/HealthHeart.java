package com.beachsurvivors.model.groundItems;

import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;

public class HealthHeart extends PowerUp implements PickUpAble {

    private final int healthAmount = 10;

    public HealthHeart(float x, float y, ParticleEffectPoolManager ppm) {
        super(AssetLoader.get().getTexture("entities/power_ups/health_heart.png"), 0, x, y, ppm);
        createIcon("entities/power_ups/health_heart.png");

    }

    @Override
    public void onPickup(Player player) {
        applyEffect(player);
    }

    @Override
    public void applyEffect(Player player) {
        player.restoreHealthPoints(healthAmount);

    }

    @Override
    public void removeEffect(Player player) {

    }
}
