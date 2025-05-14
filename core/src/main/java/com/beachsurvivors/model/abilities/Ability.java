package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Disposable;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.Player;

public abstract class Ability implements Disposable {

    private String name;
    private AbilityType type;
    private double damage;
    private double cooldown;
    private Texture texture;
    private Sprite sprite;
    private Rectangle hitBox;
    private boolean isPersistent = false; //Om abilityn ska vara "permanent" (sköld) eller försvinna, t.ex (BaseAttack)
    private Image icon;

    public Ability(String name, String texturePath, AbilityType type, double damage, double cooldown, int width, int height) {
        this.texture = AssetLoader.get().getTexture(texturePath);
        this.sprite = new Sprite(texture);
        sprite.setSize(width, height);
        this.hitBox = new Rectangle();
        hitBox.setSize(width, height);

        this.name = name;
        this.type = type;
        this.damage = damage;
        this.cooldown = cooldown;
    }

    public void updatePosition(float x, float y) {
        getSprite().setPosition(x, y);
        getHitBox().setPosition(x, y);
    }

    public abstract void use();
    public abstract void use(Player player);

    public AbilityType getType() {
        return type;
    }

    public Double getBaseDamage() {
        double min = damage;
        double max = damage * 1.5;
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    public double getDamage() {
        return damage;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public double getCooldown() {
        return cooldown;
    }

    public void decreaseCooldown(double attackSpeed) {
        cooldown -= attackSpeed;
    }

    public void setType(AbilityType type) {
        this.type = type;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void increaseDamage(double damage) {
        this.damage += damage;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(String iconImagePath) {
        Texture texture = AssetLoader.get().getTexture(iconImagePath);
        icon = new Image(texture);
        icon.setSize(64,64);
    }

    public Texture getTexture() {
        return texture;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public void setHitBox(Rectangle hitBox) {
        this.hitBox = hitBox;
    }

    public String getName() {
        return name;
    }

    public void setPersistent(boolean isPersistent) {
        this.isPersistent = isPersistent;
    }

    public boolean isPersistent() {
        return isPersistent;
    }

    public void updatePosition(float delta, float playerX, float playerY) {}

    @Override
    public String toString() {
        return name + " | Type: " + type +
            " | Base damage: " + damage +
            " | Cooldown " + cooldown + "s";
    }

    @Override
    public void dispose() {

    }
}
