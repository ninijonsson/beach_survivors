package com.beachsurvivors.model.groundItems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class GroundItem implements PickUpAble {

    private Texture texture;
    private Sprite sprite;
    private Rectangle hitbox;

    private float x;
    private float y;

    private Vector2 position;

    public GroundItem(String texturePath, float x, float y) {

        createTexture(texturePath);

        sprite = new Sprite(texture);
        sprite.setSize(64, 64);
        sprite.setPosition(x, y);

        hitbox = new Rectangle();
        hitbox.setSize(64, 64);
        hitbox.setPosition(x, y);

        this.x = x;
        this.y = y;

    }

    public void createTexture(String texturePath) {
        if (texturePath.isEmpty()) {
            texture = new Texture("placeholder.png");
        } else {
            texture = new Texture(texturePath);
        }
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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}


