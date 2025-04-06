package com.beachsurvivors.model.powerUps;

import com.badlogic.gdx.utils.Timer;
import com.beachsurvivors.model.PickUpAble;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.abilities.BaseAttack;

public class Berserk extends PowerUp implements PickUpAble {

    private final double damageIncrease = 10;
    private final int movementSpeedIncrease = 200;
    private double attackSpeedIncrease = 2.5;

    public Berserk(float x, float y) {
        super("entities/power_ups/beer.png" , 20, x, y);
    }

    @Override
    public void onPickup(Player player) {
        applyAffect(player);
    }

    @Override
    public void applyAffect(Player player) {
        player.increaseSpeed(movementSpeedIncrease);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                player.increaseSpeed(-movementSpeedIncrease);
            }
        }, getDuration());
    }

    public void onPickupBullet(BaseAttack bullet) {
        applyAffectBullet(bullet);
    }

    private void applyAffectBullet(BaseAttack bullet) {
        bullet.increaseDamage(damageIncrease);
        bullet.decreaseCooldown(attackSpeedIncrease);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                bullet.increaseDamage(-damageIncrease);
                bullet.decreaseCooldown(+attackSpeedIncrease);
            }
        }, getDuration());
    }

}
