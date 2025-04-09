package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SmokeParticle {

    private Sprite sprite;
    private float lifeTime;

    public SmokeParticle(float x, float y) {
        sprite = new Sprite(new Texture("entities/Smoke.png")); // fixa en liten rök-bild
        sprite.setSize(16, 16);
        sprite.setPosition(x, y);
        sprite.setColor(Color.RED);
        lifeTime = 0.15f; // lever i 1 sekund
    }

    public void update(float delta) {
        lifeTime -= delta;
        sprite.setAlpha(lifeTime); // fade out
    }

    public boolean isDead() {
        return lifeTime <= 0;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void dispose() {
        sprite.getTexture().dispose();
    }
}
