package com.beachsurvivors.model.groundItems;

import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.utilities.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;

public class SpeedBoost extends PowerUp implements PickUpAble {

    private int speedIncrease = 300;

    /*public SpeedBoost() {
        super("entities/power_ups/speed_boost.png" , 10);
    }*/

    public SpeedBoost(float x, float y, ParticleEffectPoolManager ppm) {
        super(AssetLoader.get().getTexture("entities/power_ups/speed_boost.png"), 10, x, y, ppm);
        createIcon("entities/power_ups/speed_boost.png");

    }

    @Override
    public void onPickup(Player player) {
        System.out.println("You picked up SpeedBoost");
        applyEffect(player);
    }

    @Override
    protected void applyEffect(Player player) {
        player.increaseSpeedModifier(speedIncrease);
    }

    @Override
    public synchronized void removeEffect(Player player) {
        player.increaseSpeedModifier(-speedIncrease);
    }


}
