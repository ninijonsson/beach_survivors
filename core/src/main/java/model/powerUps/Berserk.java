package model.powerUps;

import model.PickUpAble;
import model.Player;

public class Berserk extends PowerUp implements PickUpAble {

    private double damageIncrease;
    private double movementSpeedIncrease;
    private double attackSpeedIncrease;

    public Berserk() {
        super("" , 10);
    }

    @Override
    public void onPickup(Player player) {

    }

    @Override
    public void applyAffect(Player player) {

    }
}
