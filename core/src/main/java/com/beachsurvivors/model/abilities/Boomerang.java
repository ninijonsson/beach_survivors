package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.view.DamageText;

public class Boomerang extends Ability {

    private float spinSpeed = 360;
    private float angle;
    private float orbitRadius = 300;

    public Boomerang() {
        super("Boomerang", "entities/abilities/boomerangmc.png", AbilityType.AoE, 1, 0, 32,32);
        setPersistent(true);
        setIcon("entities/abilities/boomerangmc.png");
    }

    @Override
    public void updatePosition(float delta, Vector2 position) {
        // Uppdatera vinkeln
        angle += spinSpeed * delta;
        angle %= 360;

        // Beräkna boomerangens position baserat på vinkeln
        float radian = (float) Math.toRadians(angle);

        float coconutX = position.x + MathUtils.cos(radian) * orbitRadius - getSprite().getWidth() / 2;  //Jag la till +64 temporärt för att boomerangerna snurrade off-center.
        float coconutY = position.y + MathUtils.sin(radian) * orbitRadius - getSprite().getHeight() / 2; //Tror att vi kan fixa det på något annat sätt men ska sova nu godnatt

        // Uppdatera boomerangens position
        getSprite().setPosition(coconutX, coconutY);
        getHitBox().setPosition(coconutX, coconutY);
    }


    @Override
    public void use(float delta, Player player, Array<Enemy> enemies, Array<Ability> abilities, Array<DamageText> damageTexts) {

    }


    @Override
    public void dispose() {
        getSprite().getTexture().dispose();
    }

    public void increaseOrbitRadius(float increase) {
        this.orbitRadius += increase;
    }


    public float getSpinSpeed() {
        return spinSpeed;
    }

    public void setSpinSpeed(float newSpeed) {
        this.spinSpeed = newSpeed;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getOrbitRadius() {
        return orbitRadius;
    }

    public void setOrbitRadius(float orbitRadius) {
        this.orbitRadius = orbitRadius;
    }

}
