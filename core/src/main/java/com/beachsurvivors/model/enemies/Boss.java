package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.Bullet;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.abilities.BombAttack;

public class Boss {
    private float phaseTimer;
    private int phase = 0;
    private boolean isAlive = true;

    private Vector2 position;
    private float width;
    private float height;
    private float healthPoints;
    private Sprite sprite;
    private Rectangle hitbox;

    private Array<Bullet> bullets = new Array<>();
    private float bulletCooldown;
    private float bulletSpeed = 500f;
    private float spiralAngle = 0;

    private Array<BombAttack> bombs = new Array<>();
    private float bombCooldown;
    private ParticleEffectPoolManager poolManager;


    public Boss(Vector2 position, ParticleEffectPoolManager poolManager) {
        this.position = position;
        width = 1500;
        height = 1500;
        healthPoints = 1000;
        sprite = new Sprite(new Texture("entities/enemies/ragnaros.png"));
        hitbox = new Rectangle(position.x, position.y, width, height);
        this.poolManager = poolManager;
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(sprite, position.x - width/2, position.y - height/2, width, height);

        for (Bullet bullet : bullets) {
            bullet.draw(spriteBatch);
        }

        for (BombAttack bomb : bombs) {
            bomb.draw(spriteBatch);
        }
    }

    public void update(float delta, Player player) {
        phaseTimer += delta;
        bulletCooldown -= delta;
        bombCooldown -= delta;

        switch (phase) {
            case 0:
                if (bulletCooldown <= 0) {
                    fireExpandingSpiral();
                    bulletCooldown = 0.05f;
                }
                if (bombCooldown <= 0) {
                    dropBomb(new Vector2(player.getPlayerX(), player.getPlayerY()));
                    bombCooldown = 2f;
                }
                if (phaseTimer > 5) {
                    nextPhase();
                }
                break;
            case 1:
                if (bulletCooldown <= 0) {
                    fireRadial();
                    bulletCooldown = 1f;
                }
                if (phaseTimer > 5) {
                    nextPhase();
                }
                break;
            case 2:
                if (bulletCooldown <= 0) {
                    fireSpiral();
                    bulletCooldown = 0.1f;
                }
                if (phaseTimer > 5) {
                    nextPhase();
                }
                break;


            //TODO: Add more phases
        }

        for (Bullet bullet : bullets) {
            bullet.update(delta);

            if (bullet.overlaps(player)) {
                player.takeDamage(100);

            }
        }

        for (BombAttack bomb : bombs) {
            bomb.update(delta);
        }
    }

    private void fireExpandingSpiral() {
        spiralAngle += 15f;
        if (spiralAngle >= 360f) spiralAngle -= 360f;

        Vector2 velocity = angleToVector(spiralAngle);
        spawnBullet(velocity);
    }

    private void fireRadial() {
        for (int i = 0; i < 360; i += 10) {
            Vector2 velocity = angleToVector(i);
            spawnBullet(velocity);
        }
    }

    private void fireSpiral() {
        // float value between 0.0 and 359.9. Changes over time.a
        float angle = (System.currentTimeMillis() % 3600) / 10f;
        float angle2 = 90+angle;
        float angle3 = 180+angle;
        float angle4 = 270+angle;

        Vector2 velocity = angleToVector(angle);
        Vector2 velocity2 = angleToVector(angle2);
        Vector2 velocity3 = angleToVector(angle3);
        Vector2 velocity4 = angleToVector(angle4);

        spawnBullet(velocity);
        spawnBullet(velocity2);
        spawnBullet(velocity3);
        spawnBullet(velocity4);
    }


    private Vector2 angleToVector(float angle) {
        float radians = angle * MathUtils.degreesToRadians;

        float vx = MathUtils.cos(radians) * bulletSpeed;
        float vy = MathUtils.sin(radians) * bulletSpeed;

        return new Vector2(vx, vy);
    }


    private void nextPhase() {
        phase++;
        phaseTimer = 0;
    }

    private void spawnBullet(Vector2 velocity) {
        bullets.add(new Bullet(new Vector2(position), velocity, poolManager));
    }

    private void dropBomb(Vector2 position) {
        bombs.add(new BombAttack(position));
    }

    public void drawBulletHitboxes(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        for (Bullet bullet : bullets) {
            Circle hitbox = bullet.getHitbox();
            shapeRenderer.circle(hitbox.x, hitbox.y, hitbox.radius);
        }

        shapeRenderer.end();
    }

    private void onDeath() {
        isAlive = false;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }
}

