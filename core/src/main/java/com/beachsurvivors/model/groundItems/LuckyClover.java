package com.beachsurvivors.model.groundItems;

import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;

public class LuckyClover extends PowerUp implements PickUpAble {

    private final float critChanceIncrease = 1f;

    public LuckyClover(float x, float y, ParticleEffectPoolManager ppm) {
        super(AssetLoader.get().getTexture("entities/power_ups/lucky_clover.png"),  20, x, y, ppm);
        createIcon("entities/power_ups/lucky_clover.png");
    }

    @Override
    public void onPickup(Player player) {
        applyEffect(player);
    }

    @Override
    public void applyEffect(Player player) {
        player.increaseCritChance(critChanceIncrease);

    }

    @Override
    public void removeEffect(Player player) {
        player.increaseCritChance(-critChanceIncrease); // Återgår till 0.5f (standard)
    }


}
