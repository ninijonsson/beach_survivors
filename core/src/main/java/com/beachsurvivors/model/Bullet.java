package com.beachsurvivors.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.utilities.ParticleEffectPoolManager;

public class Bullet {
    private Vector2 position;
    private Vector2 velocity;
    private Circle hitbox = new Circle();
    private float lifeTimer;

    private float width = 64;
    private float height = 64;
    private Sprite sprite = new Sprite(AssetLoader.get().getTexture("entities/abilities/fire_ball.png"));

    private ParticleEffectPool.PooledEffect trailEffect;

    public Bullet(Vector2 position, Vector2 velocity, ParticleEffectPoolManager poolManager) {
        this.position = new Vector2(position);
        this.velocity = new Vector2(velocity);
        this.sprite.setSize(width, height);
        this.sprite.setOriginCenter();
        this.trailEffect = poolManager.obtain("entities/particles/fire_trail.p");
        trailEffect.setPosition(position.x, position.y);
        trailEffect.start();
        this.lifeTimer = 10f;
    }

    public void update(float delta) {
        position.mulAdd(velocity, delta);
        hitbox.set(position.x, position.y, width / 2f);

        if(trailEffect!=null) {
            trailEffect.setPosition(position.x, position.y);
            trailEffect.update(delta);
        }

        lifeTimer -= delta;
        if (lifeTimer <= 0 && trailEffect != null) {
            dispose();
        }

    }

    public boolean isExpired() {
        return lifeTimer <= 0;
    }


    public void draw(SpriteBatch spriteBatch) {
        sprite.rotate(10);
        sprite.setPosition(position.x - width / 2f, position.y - height / 2f);

        sprite.draw(spriteBatch);
        if(trailEffect!=null){
            trailEffect.draw(spriteBatch);
        }

    }


    public boolean overlaps(Player player) {
        float closestX = Math.max(player.getHitBox().getX(), Math.min(getHitbox().x, player.getHitBox().getX() + player.getHitBox().getWidth()));
        float closestY = Math.max(player.getHitBox().getY(), Math.min(getHitbox().y, player.getHitBox().getY() + player.getHitBox().getHeight()));

        float dx = hitbox.x - closestX;
        float dy = hitbox.y - closestY;

        return (dx * dx + dy * dy) < (hitbox.radius * hitbox.radius);
    }

    public Circle getHitbox() {
        return hitbox;
    }

    public void dispose() {
        trailEffect.free();
        trailEffect = null;
    }

    public boolean isEffectComplete() {
        return trailEffect.isComplete();
    }
}
