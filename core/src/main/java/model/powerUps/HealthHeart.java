package model.powerUps;

import model.PickUpAble;
import model.Player;

public class HealthHeart extends PowerUp implements PickUpAble {

    private int healthAmount;

    public HealthHeart(int duration) {
        super("" ,duration);
    }

    @Override
    public void onPickup(Player player) {

    }

    @Override
    public void applyAffect(Player player) {

    }
}
