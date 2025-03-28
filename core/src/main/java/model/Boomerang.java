package model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Boomerang extends Ability {
    private Sprite sprite;
    private String type;
    private double damage;

    public Boomerang(){
        Texture fireballTexture = new Texture("entities/Coconut.png");
        sprite = new Sprite(fireballTexture);
        type = "AOE";
        damage = 10;
    }

    @Override
    public String getType() {
        return "";
    }

    @Override
    public Double getDamage() {
        return 0.0;
    }

    @Override
    public Sprite getSprite() {
        return null;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
}
