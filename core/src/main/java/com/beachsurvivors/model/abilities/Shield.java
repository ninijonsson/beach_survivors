package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Timer;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.Player;

public class Shield extends Ability {

    private final int INITIAL_SHIELD_STRENGTH = 20;
    private int currentShieldStrength;  //Hur mycket HP skölden skyddar (ifall vi ska ha en vanlig HP-sköld)
    private boolean isRegening;
    private double totalDamagePrevented;

    public Shield() {
        super("Shield" , "entities/abilities/shield_bubble.png", AbilityType.SHIELD, 0, 20, 150, 150);
        this.currentShieldStrength = INITIAL_SHIELD_STRENGTH;
        isRegening = false;
        getSprite().setColor(1f,1f,1f, 0.4f);
        setPersistent(true);
    }


    @Override
    public void use() {

    }

    @Override
    public void use(Player player) {

    }

    @Override
    public void dispose() {
        setSprite(null);
        setHitBox(null);
    }

    public void damageShield(double damage) {
        if (currentShieldStrength - damage <= 0) {
            totalDamagePrevented += currentShieldStrength;
            currentShieldStrength = 0;
            regenShield();
        } else {
            currentShieldStrength -= damage;
            totalDamagePrevented += damage;
        }
    }

    public void regenShield() {
        if (!isRegening) {
            isRegening = true;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    resetShield();
                }
            }, (float) getCooldown());
        }
    }

    public void resetShield() {
        currentShieldStrength = INITIAL_SHIELD_STRENGTH;
        isRegening = false;

        if (getSprite() == null) {
            setSprite(new Sprite(AssetLoader.get().getTexture("entities/abilities/bubble2.png")));
        }

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

    public double getTotalDamagePrevented() {
        return totalDamagePrevented;
    }
}
