package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.Player;

public class Boomerang extends Ability {

    private Sprite sprite;
    private Rectangle hitBox;
    private double damage;
    private float spinSpeed = 360;
    private float angle;
    private float orbitRadius = 300;

    public Boomerang() {
        super("Boomerang", "entities/abilities/boomerangmc.png", AbilityType.AoE, 10, 0, 32,32);
        sprite = new Sprite(AssetLoader.get().getTexture("entities/abilities/boomerangmc.png"));
        sprite.setSize(32, 32);
        hitBox = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        this.damage = 10;
        setPersistent(true);
    }

    @Override
    public void updatePosition(float playerX, float playerY) {
        float delta = Gdx.graphics.getDeltaTime();

        // Uppdatera vinkeln
        angle += spinSpeed * delta;
        angle %= 360;

        // Beräkna boomerangens position baserat på vinkeln
        float radian = (float) Math.toRadians(angle);

        float coconutX = playerX + MathUtils.cos(radian) * orbitRadius - sprite.getWidth() / 2;  //Jag la till +64 temporärt för att boomerangerna snurrade off-center.
        float coconutY = playerY + MathUtils.sin(radian) * orbitRadius - sprite.getHeight() / 2; //Tror att vi kan fixa det på något annat sätt men ska sova nu godnatt

        // Uppdatera boomerangens position
        sprite.setPosition(coconutX, coconutY);
        hitBox.setPosition(coconutX, coconutY);
    }

    @Override
    public void use() {

    }

    @Override
    public void use(Player player) {

    }



    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public Rectangle getHitBox() {
        return hitBox;
    }

    @Override
    public void dispose() {
        sprite.getTexture().dispose();
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
