package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.utilities.ParticleEffectPoolManager;

public class Projectile {

    private Texture texture;
    private Sprite sprite;
    private Rectangle hitBox;
    private int width;
    private int height;
    private double damage;

    private Vector2 position;
    private Vector2 direction;
    private float projectileSpeed;
    private ParticleEffectPool.PooledEffect trailEffect;
    private float lifetime = 0f;

    public Projectile(String texturePath, double damage, float projectileSpeed,
                      int width, int height) {
        this.damage = damage;
        this.width = width;
        this.height = height;
        this.texture = AssetLoader.get().getTexture(texturePath);
        this.sprite = new Sprite(texture);
        sprite.setSize(width, height);
        this.hitBox = new Rectangle();
        hitBox.setSize(width, height);

        this.position = new Vector2();
        this.direction = new Vector2();
        this.projectileSpeed = projectileSpeed;
        trailEffect = ParticleEffectPoolManager.obtain("entities/particles/water_trail.p");
        trailEffect.setPosition(position.x, position.y);

    }

    public boolean hasExpired() {
        return lifetime >= 10f;
    }

    public void updatePosition(float delta) {
        lifetime += delta;

        Vector2 movement = direction.cpy().scl(projectileSpeed * delta);
        this.position.add(movement);

        getSprite().setPosition(position.x, position.y);
        getHitBox().setPosition(position.x, position.y);

        getSprite().setOriginCenter();
        getSprite().setRotation(direction.angleDeg() + 90);
        if (trailEffect != null) {
            trailEffect.setPosition(position.x + width / 2f, position.y + height / 2f);
            trailEffect.update(delta);
        }

    }

    public void draw(SpriteBatch batch) {
        if (trailEffect != null) {
            trailEffect.draw(batch);
        }
        sprite.draw(batch);
    }

    public void setPosition(Vector2 startPosition) {
        this.position = startPosition;
        getSprite().setPosition(position.x, position.y);
        getHitBox().setPosition(position.x, position.y);
    }

    public Vector2 getDirection() {
        return direction;
    }

    public void setDirection(Vector2 direction) {
        this.direction.set(direction);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public double getDamage() {
        return damage;
    }

    public void dispose() {
        if (trailEffect != null) {
            trailEffect.free();
            trailEffect = null;
        }
        sprite = null;
        hitBox = null;
    }
}
