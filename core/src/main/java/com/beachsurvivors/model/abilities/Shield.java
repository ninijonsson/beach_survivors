package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.utils.Timer;
import com.beachsurvivors.model.Player;

public class Shield extends Ability {

    private final int INITIAL_SHIELD_STRENGTH = 20;
    private int currentShieldStrength;  //Hur mycket HP skölden skyddar (ifall vi ska ha en vanlig HP-sköld)
    private boolean isRegening;

    public Shield() {
        super("Shield" , "assets/entities/abilities/bubble2.png", AbilityType.SHIELD, 0, 20, 130, 130);
        this.currentShieldStrength = INITIAL_SHIELD_STRENGTH;
        isRegening = false;
        getSprite().setColor(1f,1f,1f, 0.5f);
    }


    @Override
    public void use() {

    }

    public void damageShield(double damage) {
        if (currentShieldStrength - damage <= 0) {
            currentShieldStrength = 0;
            regenShield();
        } else {
            currentShieldStrength -= damage;
        }
        System.out.println("Shield was damaged, current shield is: " + currentShieldStrength);
    }

    public void regenShield() {
        if (!isRegening) {
            System.out.println("Shield is regening");
            isRegening = true;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    System.out.println("Timer task started");
                    resetShield();
                    isRegening = false;
                }
            }, (float) getCooldown()*10);
        }
        System.out.println("already regening");
    }

    @Override
    public void use(Player player) {

    }

    public void resetShield() {
        currentShieldStrength = INITIAL_SHIELD_STRENGTH;
        System.out.println("Shield regened, current shield is: " + getCurrentShieldStrength());

    }

    public int getCurrentShieldStrength() {
        return currentShieldStrength;
    }

    public int getINITIAL_SHIELD_STRENGTH() {
        return INITIAL_SHIELD_STRENGTH;
    }
    public boolean getIsDepleted() {
        return currentShieldStrength <= 0;
    }
}
