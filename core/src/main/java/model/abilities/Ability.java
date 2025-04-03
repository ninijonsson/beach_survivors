package model.abilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import model.DamageText;
import model.Player;

import java.util.Random;

public abstract class Ability implements Disposable {

    private String name;
    private AbilityType type;
    private double damage;
    private double cooldown;  //Vissa abilities kanske behöver ha en cooldown?
                                // kanske flytta till subclass också
    private Texture texture;
    private Sprite sprite;
    private Rectangle hitbox;  //Kanske flytta till subclasser eftersom alla abilities kanske inte har hitbox, t.ex shield eller healing


    public Ability (String name, String texturePath, AbilityType type, double damage, double cooldown, int width, int height) {
        this.texture = new Texture(texturePath);
        this.sprite = new Sprite(texture);
        sprite.setSize(width, height);
        this.hitbox = new Rectangle();
        hitbox.setSize(width, height);

        this.name = name;
        this.type = type;
        this.damage = damage;
        this.cooldown = cooldown;
    }

    public void updatePosition(float x, float y) {
        getSprite().setPosition(x, y);
        getHitbox().setPosition(x, y);
    }


    public abstract void use();
    public abstract void use(Player player);

    public AbilityType getType() {
        return type;
    }

    public Double getDamage() {
        return damage;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void setType(AbilityType type) {
        this.type = type;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public void setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
    }

    @Override
    public String toString() {
        return name + " | Type: " +type +
                    " | Base damage: " + damage +
                    " | Cooldown " + cooldown + "s" ;
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    public void updatePosition(float delta, float playerX, float playerY) {
    }

    public Rectangle getHitBox() {
        return null;
    }
}
