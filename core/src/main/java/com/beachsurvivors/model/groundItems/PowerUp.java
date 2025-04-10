package com.beachsurvivors.model.groundItems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.beachsurvivors.model.Player;

public abstract class PowerUp extends GroundItem {

    private int duration;


    // VARIABLER FÖR ATT KONTROLLERA "FLOATING POWER UP"
    private float floatAmplitude = 10.0f;
    private float floatFrequency = 2.0f;
    private float time = 0.0f;

    public PowerUp(String texturePath, int duration, float x, float y) {
        super(texturePath, x, y);
        this.duration = duration;

        createTexture(texturePath);
    }

    protected abstract void applyAffect(Player player);


    public void update(float deltaTime) {
        time += deltaTime;
        float newY = getY() + floatAmplitude * (float) Math.sin(time * floatFrequency);
        getSprite().setPosition(getX(), newY);
        getHitbox().setPosition(getX(), newY);

    }

}
