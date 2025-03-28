package model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;

import java.awt.*;


public abstract class Enemy implements Disposable {

    private int healthPoints;
    private int movementSpeed;
    private int damage;

    private Texture texture;
    private Sprite sprite;
    private Rectangle hitbox;


    public abstract void move();

    public abstract void attack();

    public abstract void spawnEnemy();

    public int getDamage() {
        return damage;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    public int getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(int movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public Texture getTexture() {
        return texture;
    }

    public abstract Sprite getSprite();


    public Rectangle getHitbox() {
        return hitbox;
    }
}
