package com.beachsurvivors.model.groundItems;

import com.badlogic.gdx.utils.Timer;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;

public class LuckyClover extends PowerUp implements PickUpAble {

    private final float critChanceIncrease = 1f;

    public LuckyClover(float x, float y, ParticleEffectPoolManager ppm) {
        super(AssetLoader.get().getTexture("assets/entities/power_ups/lucky_clover.png"),  20, x, y, ppm);
    }

    @Override
    public void onPickup(Player player) {
        applyAffect(player);
    }

    @Override
    public void applyAffect(Player player) {
        player.increaseCritChance(critChanceIncrease);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                player.increaseCritChance(-critChanceIncrease); // Återgår till 0.5f (standard)
            }
        }, getDuration());
    }
}
