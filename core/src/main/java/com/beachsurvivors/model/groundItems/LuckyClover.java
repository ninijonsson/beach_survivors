package com.beachsurvivors.model.groundItems;

import com.badlogic.gdx.utils.Timer;
import com.beachsurvivors.model.Player;

public class LuckyClover extends PowerUp implements PickUpAble {

    private final float critChanceIncrease = 1f;

    public LuckyClover(float x, float y) {
        super("entities/power_ups/lucky_clover.png",  20, x, y);
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
                player.increaseCritChance(0.5f); // Återgår till 0.5f (standard)
            }
        }, getDuration());
    }
}
