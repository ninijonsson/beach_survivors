package com.beachsurvivors.model.groundItems;

import com.badlogic.gdx.utils.Timer;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;

public class SpeedBoost extends PowerUp implements PickUpAble {

    private int speedIncrease = 300;

    /*public SpeedBoost() {
        super("entities/power_ups/speed_boost.png" , 10);
    }*/

    public SpeedBoost(float x, float y, ParticleEffectPoolManager ppm) {
        super(AssetLoader.get().getTexture("assets/entities/power_ups/speed_boost.png"), 10, x, y, ppm);
    }

    @Override
    public void onPickup(Player player) {
        System.out.println("You picked up SpeedBoost");
        applyAffect(player);
    }

    @Override
    protected void applyAffect(Player player) {
        player.increaseSpeed(speedIncrease);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                player.increaseSpeed(-speedIncrease);
            }
        }, getDuration());

    }
}
