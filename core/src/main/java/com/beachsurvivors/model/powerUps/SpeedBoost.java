package com.beachsurvivors.model.powerUps;

import com.badlogic.gdx.utils.Timer;
import com.beachsurvivors.model.PickUpAble;
import com.beachsurvivors.model.Player;

public class SpeedBoost extends PowerUp implements PickUpAble {

    private int speedIncrease = 300;

    public SpeedBoost() {
        super("entities/power_ups/speed_boost.png" , 10);
    }

    public SpeedBoost(float x, float y) {
        super("entities/power_ups/speed_boost.png" , 10);
        this.getSprite().setPosition(x, y);
        this.getHitbox().setPosition(x,y);
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
