package com.beachsurvivors.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.beachsurvivors.AssetLoader;

public class Bullet {
    private Vector2 position;
    private Vector2 velocity;
    private Circle hitbox = new Circle();

    private float width = 64;
    private float height = 64;
    private Sprite sprite = new Sprite(AssetLoader.get().getTexture("entities/abilities/fireball.png"));

    public Bullet(Vector2 position, Vector2 velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    public void update(float delta) {
        position.mulAdd(velocity, delta);
        hitbox.set(position.x, position.y,width/2);
        //TODO: dispose if off screen
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(sprite, position.x - width/2, position.y - height/2, width, height);
    }

    // Reused method, with small changes, from ExperienceOrb.
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
}

