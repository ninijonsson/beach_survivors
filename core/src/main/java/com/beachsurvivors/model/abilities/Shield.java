package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.view.DamageText;

public class Shield extends Ability {


    private float initialShieldStrength = 30;
    private float currentShieldStrength;  //Hur mycket HP skölden skyddar (ifall vi ska ha en vanlig HP-sköld)
    private boolean isRegening;
    private double totalDamagePrevented;
    private Sound shieldBreak;
    private Sound shieldRegen;
    private Sound shieldHit;
    private Boolean shieldBroken;

    public Shield() {
        super("Shield", "entities/abilities/shield_bubble.png", AbilityType.SHIELD,
            0, 20, 150, 150);
        this.currentShieldStrength = initialShieldStrength;
        isRegening = false;
        getSprite().setColor(1f, 1f, 1f, 0.4f);
        setPersistent(true);
        shieldBreak = AssetLoader.get().getSound("entities/abilities/shield_break.wav");
        shieldRegen = AssetLoader.get().getSound("entities/abilities/shield_regen.wav");
        shieldHit = AssetLoader.get().getSound("entities/abilities/shield_hit.wav");
        shieldBroken = false;
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
            shieldBreak.play(0.05f);
            shieldBroken=true;
        } else {
            shieldHit.play(0.2f);
            currentShieldStrength -= (float) damage;
            totalDamagePrevented += damage;
        }

    }

    public void resetShield() {
        if (shieldBroken) {
            currentShieldStrength = initialShieldStrength;
            shieldBroken = false;
            shieldRegen.play(0.5f);
            if (getSprite() == null) {
                setSprite(new Sprite(AssetLoader.get().getTexture("entities/abilities/bubble2.png")));
            }
        }
    }

    public float getCurrentShieldStrength() {
        return currentShieldStrength;
    }

    public float getInitialShieldStrength() {
        return initialShieldStrength;
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

    public void increaseHealth(int i) {
        initialShieldStrength = initialShieldStrength * 1.1f;
        currentShieldStrength = initialShieldStrength;
    }
}
