package model.powerUps;

import model.PickUpAble;
import model.Player;

public class LuckyClover extends PowerUp implements PickUpAble {

    private float critChanceIncrease;

    public LuckyClover() {
        super("",  10);
    }

    @Override
    public void onPickup(Player player) {

    }

    @Override
    public void applyAffect(Player player) {

    }
}
