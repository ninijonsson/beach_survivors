package model.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.math.Rectangle;



public abstract class Enemy implements Disposable {

    private double healthPoints;
    private int movementSpeed;
    private int damage;
    private int expOnDeath;
    private boolean isAlive;

    private Texture texture;
    private Sprite sprite;
    private Rectangle hitbox;

    public Enemy(String texturePath, int width, int height) {
        this.texture = new Texture(texturePath);
        this.sprite = new Sprite(texture);
        this.sprite.setSize(width, height);

        this.hitbox = new Rectangle(0, 0, width, height);

        healthPoints = 10;
        isAlive = true;
    }

    public abstract void move();
    public abstract void attack();
    public abstract void spawnEnemy();
    public abstract void onDeath();
    public abstract void dropItems();

    public int getDamage() {
        return damage;
    }

    public double getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(double healthPoints) {
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

    public Sprite getSprite() {
        return sprite;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
