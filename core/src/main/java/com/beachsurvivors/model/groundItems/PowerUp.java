package com.beachsurvivors.model.groundItems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;

public abstract class PowerUp implements PickUpAble {

    private Texture texture;
    private Sprite sprite;
    private Rectangle hitbox;
    private Image icon;

    private int duration;
    private float remainingDuration;
    private float x;
    private float y;
    private ParticleEffectPool.PooledEffect lootBeamEffect;
    private boolean active;

    // VARIABLER FÖR ATT KONTROLLERA "FLOATING POWER UP"
    private float floatAmplitude = 10.0f;
    private float floatFrequency = 2.0f;
    private float time = 0.0f;

    public PowerUp(Texture texture, int duration, float x, float y ,ParticleEffectPoolManager ppm) {
        this.duration = duration;
        remainingDuration = duration;
        this.texture=texture;
        sprite = new Sprite(texture);
        sprite.setSize(64, 64);
        sprite.setPosition(x, y);

        hitbox = new Rectangle();
        hitbox.setSize(64, 64);
        hitbox.setPosition(x, y);

        this.x = x;
        this.y = y;
        active = true;


        this.lootBeamEffect = ppm.obtain("entities/particles/lootPile.p");

        if(lootBeamEffect!=null){
            lootBeamEffect.setPosition(x + 32, y + 32);
        }

    }

    protected abstract void applyAffect(Player player);
    public abstract void removeEffect(Player player);

    public void dispose() {
        sprite = null;
        if (lootBeamEffect != null) {
            lootBeamEffect.free();
            lootBeamEffect = null;
        }

    }

    public void updateDuration(float deltaTime, Player player) {
        //if (!active) return;

        remainingDuration -= deltaTime;
        if (remainingDuration <= 0) {
            active = false;
            removeEffect(player);
        }
    }


    public void update(float deltaTime) {
        time += deltaTime;
        float newY = y + floatAmplitude * (float) Math.sin(time * floatFrequency);
        sprite.setPosition(x, newY);
        hitbox.setPosition(x, newY);

        if (lootBeamEffect != null) {
            lootBeamEffect.setPosition(sprite.getX() + 32, sprite.getY() + 32);
            lootBeamEffect.update(deltaTime);
        }
    }

    public void drawParticles(SpriteBatch batch) {
        if (lootBeamEffect != null) {
            lootBeamEffect.draw(batch);
        }
    }

    public void createIcon(String imagePath) {
        Texture texture = AssetLoader.get().getTexture(imagePath);
        this.icon = new Image(texture);
        icon.setSize(64,64);
    }

    public Image getIcon() {
        return icon;
    }

    public int getDuration() {
        return duration;
    }

    public float getRemainingDuration() {
        return remainingDuration;
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
}
