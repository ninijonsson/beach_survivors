package com.beachsurvivors.model.powerUps;

import com.badlogic.gdx.utils.Timer;
import com.beachsurvivors.model.PickUpAble;
import com.beachsurvivors.model.Player;

import java.util.TimerTask;

public class LuckyClover extends PowerUp implements PickUpAble {

    private final int critChanceIncrease = 10;

    public LuckyClover(float x, float y) {
        super("entities/power_ups/lucky_clover.png",  20);
        this.getSprite().setPosition(x, y);
        this.getHitbox().setPosition(x, y);
    }

    @Override
    public void onPickup(Player player) {
        System.out.println("Du plockade upp lucky clover");
        applyAffect(player);
    }

    @Override
    public void applyAffect(Player player) {
        player.increaseCritChance(critChanceIncrease);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                player.increaseCritChance(critChanceIncrease);
            }
        }, getDuration());
    }
}
