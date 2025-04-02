package model.abilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public abstract class Ability implements Disposable {

    private String name;
    private AbilityType type;
    private double baseDamage;
    private double cooldown;  //sekunder

    private Texture texture;
    private Sprite sprite;
    private Rectangle hitbox;  //Kanske flytta till subclasser eftersom alla abilities kanske inte har hitbox, t.ex shield eller healing

    public Ability (String name, String texturePath, AbilityType type, double baseDamage, double cooldown, int width, int height) {
        this.texture = new Texture(texturePath);
        this.sprite = new Sprite(texture);
        this.hitbox = new Rectangle();
        hitbox.setSize(width, height);

        this.name = name;
        this.type = type;
        this.baseDamage = baseDamage;
        this.cooldown = cooldown;
    }

    public abstract void use();

    public AbilityType getType() {
        return type;
    }

    public Double getBaseDamage() {
        return baseDamage;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setType(AbilityType type) {
        this.type = type;
    }

    public void setBaseDamage(double baseDamage) {
        this.baseDamage = baseDamage;
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
                    " | Base damage: " + baseDamage +
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
