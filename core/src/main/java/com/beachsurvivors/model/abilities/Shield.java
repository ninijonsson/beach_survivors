package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.view.DamageText;

public class Shield extends Ability {

    private final int INITIAL_SHIELD_STRENGTH = 20;
    private int currentShieldStrength;  //Hur mycket HP skölden skyddar (ifall vi ska ha en vanlig HP-sköld)
    private boolean isRegening;
    private double totalDamagePrevented;

    public Shield() {
        super("Shield" , "entities/abilities/shield_bubble.png", AbilityType.SHIELD,
            0, 20, 150, 150);
        this.currentShieldStrength = INITIAL_SHIELD_STRENGTH;
        isRegening = false;
        getSprite().setColor(1f,1f,1f, 0.4f);
        setPersistent(true);
    }

    @Override
    public void use(float delta, Player player, Array<Enemy> enemies, Array<Ability> abilities, Array<DamageText> damageTexts, Array<Projectile> playerProjectiles) {

        resetShield();
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

        } else {
            currentShieldStrength -= damage;
            totalDamagePrevented += damage;
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

    public void rotate(int i) {
        getSprite().setOriginCenter();
        getSprite().rotate(2);
    }
}
