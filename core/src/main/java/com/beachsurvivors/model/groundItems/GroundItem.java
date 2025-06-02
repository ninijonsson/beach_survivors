package com.beachsurvivors.model.groundItems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.view.GameScreen;

public abstract class GroundItem implements PickUpAble {

    private Texture texture;
    private Sprite sprite;
    private ParticleEffectPool.PooledEffect particleEffect;
    private float time;

    private float width = 64;
    private float height = 64;
    private Rectangle hitbox;

    private float x;
    private float y;

    private Vector2 position;

    public GroundItem(Texture texturePath, float x, float y, GameScreen gameScreen) {

        this.texture=texturePath;

        sprite = new Sprite(texture);
        sprite.setSize(width, height);

        if(this instanceof ExperienceOrb){
            sprite.setSize(32,32);
        }
        sprite.setPosition(x, y);
        hitbox = new Rectangle();
        hitbox.setSize(64, 64);
        hitbox.setPosition(x, y);

        this.x = x;
        this.y = y;

    }

    @Override
    public void onPickup(Player player){
        particleEffect.free();
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
        particleEffect.free();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setParticleEffect(ParticleEffectPool.PooledEffect effect){
        this.particleEffect=effect;
        if(particleEffect!=null){
            particleEffect.setPosition(x, y);
            particleEffect.start();
        }
    }

    public void drawParticles(SpriteBatch batch) {
        if (particleEffect != null) {
            particleEffect.draw(batch);
        }
    }

    public void update(float deltaTime) {
        time += deltaTime;


        if (particleEffect != null) {
            particleEffect.setPosition(sprite.getX() + sprite.getWidth()*0.5f, sprite.getY() + sprite.getHeight()*0.5f);
            particleEffect.update(deltaTime);
        }
    }

    public void setScale(float scale){
        sprite.setScale(scale);
    }

    public Vector2 getPosition() {
        position = new Vector2(x,y);
        return position;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}


