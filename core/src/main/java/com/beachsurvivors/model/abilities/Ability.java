package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.utilities.CombatHelper;
import com.beachsurvivors.view.DamageText;

public abstract class Ability implements Disposable {

    private String name;
    private AbilityType type;
    private double damageMultiplier;
    private float cooldown;
    private float cooldownTimer;
    private Texture texture;
    private Sprite sprite;
    private Rectangle hitBox;
    private boolean isPersistent = false; //Om abilityn ska vara "permanent" (sköld) eller försvinna, t.ex (BaseAttack)
    private Image icon;
    private boolean isOffCooldown;
    private int height;
    private int width;

    public Ability(String name, String texturePath, AbilityType type, double damageMultiplier, float cooldown, int width, int height) {
        this.texture = AssetLoader.get().getTexture(texturePath);
        this.sprite = new Sprite(texture);
        sprite.setSize(width, height);
        this.hitBox = new Rectangle();
        hitBox.setSize(width, height);

        this.height = height;
        this.width = width;
        this.name = name;
        this.type = type;
        this.damageMultiplier = damageMultiplier;
        this.cooldown = cooldown;
    }

    public void updatePosition(float delta, Vector2 position) {
        getSprite().setPosition(position.x - getSprite().getWidth() / 2, position.y - getSprite().getHeight() / 2);
        getHitBox().setPosition(position.x, position.y);
    }

    public void update(float delta, Player player, Array<Enemy> enemies, Array<Ability> abilities) {
        cooldownTimer += delta;
        float actualCooldown = CombatHelper.getActualCooldown(getCooldown(), player.getCooldownTime());

        if (cooldownTimer >= actualCooldown) {
            cooldownTimer = 0f;
            //Do something
            isOffCooldown = true;
        }
    }

    public abstract void use(float delta, Player player, Array<Enemy> enemies, Array<Ability> abilities, Array<DamageText> damageTexts);

    public AbilityType getType() {
        return type;
    }

    public Double getBaseDamage() {
        double min = damageMultiplier;
        double max = damageMultiplier * 1.5;
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public float getCooldown() {
        return cooldown;
    }

    public boolean isOffCooldown() {
        return isOffCooldown;
    }

    public void setOffCooldown(boolean isOffCooldown) {
        this.isOffCooldown = isOffCooldown;
    }

    public float getCooldownTimer() {
        return cooldownTimer;
    }

    public void setCooldownTimer(float value) {
        this.cooldownTimer = value;
    }

    public void increaseCooldownTimer(float value) {
        this.cooldownTimer += value;
    }

    public void decreaseCooldown(double attackSpeed) {
        cooldown -= attackSpeed;
    }

    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
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
        icon.setSize(64, 64);
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

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setPersistent(boolean isPersistent) {
        this.isPersistent = isPersistent;
    }

    public boolean isPersistent() {
        return isPersistent;
    }

    @Override
    public String toString() {
        return name + " | Type: " + type +
            " | Base damage: " + damageMultiplier +
            " | Cooldown " + cooldown + "s";
    }

    @Override
    public void dispose() {
        sprite = null;
        hitBox = null;
    }
}
