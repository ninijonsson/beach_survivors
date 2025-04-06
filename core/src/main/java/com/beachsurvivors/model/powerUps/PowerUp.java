package com.beachsurvivors.model.powerUps;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.beachsurvivors.model.PickUpAble;
import com.beachsurvivors.model.Player;



public abstract class PowerUp implements PickUpAble {

    private Texture texture;
    private Sprite sprite;
    private Rectangle hitbox;

    private int duration;
    private float x;
    private float y;

    public PowerUp(String texturePath, int duration, float x, float y) {
        this.duration = duration;

        createTexture(texturePath);

        sprite = new Sprite(texture);
        sprite.setSize(64, 64);
        sprite.setPosition(x, y);

        hitbox = new Rectangle();
        hitbox.setSize(64,64);
        hitbox.setPosition(x, y);
    }

    protected abstract void applyAffect(Player player);

    public void createTexture(String texturePath) {
        if (texturePath.isEmpty()) {
            texture = new Texture("assets/placeholder.png");
        } else {
            texture = new Texture(texturePath);
        }

    }

    public int getDuration() {
        return duration;
    }

    public Texture getTexture() {
        return texture;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }
    public void dispose() {
        texture.dispose();
    }
}
