package model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public abstract class Ability {
    private Sprite sprite;
    private String type;
    private double damage;
    public Ability (){
        Texture texture=new Texture("entities/Coconut.png");
        sprite = new Sprite(texture);
    }

    public String getType() {
        return type;
    }

    public Double getDamage() {
        return damage;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
