package com.beachsurvivors.model.powerUps;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.beachsurvivors.model.Player;

public abstract class PowerUp implements PickUpAble {

    private Texture texture;
    private Sprite sprite;
    private Rectangle hitbox;

    private int duration;
    private float x;
    private float y;

    // VARIABLER FÖR ATT KONTROLLERA "FLOATING POWER UP"
    private float floatAmplitude = 10.0f;
    private float floatFrequency = 2.0f;
    private float time = 0.0f;

    public PowerUp(String texturePath, int duration, float x, float y) {
        this.duration = duration;

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

    protected abstract void applyAffect(Player player);

    public void createTexture(String texturePath) {
        if (texturePath.isEmpty()) {
            texture = new Texture("placeholder.png");
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

    public void update(float deltaTime) {
        time += deltaTime;
        float newY = y + floatAmplitude * (float) Math.sin(time * floatFrequency);
        sprite.setPosition(x, newY);
        hitbox.setPosition(x, newY);

    }

}
