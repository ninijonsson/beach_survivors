package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.math.Rectangle;
import com.beachsurvivors.model.powerUps.*;

import java.util.Random;


public abstract class Enemy implements Disposable {

    private double healthPoints;
    private int movementSpeed;
    private int damage;
    private int expOnDeath;
    private boolean isAlive;
    private int width;
    private int height;
    private float x;
    private float y;

    private Texture texture;
    private Sprite sprite;
    private Rectangle hitbox;
    private Sound hitSound;
    private boolean isImmune;

    private Animation<TextureRegion> walkAnimation;
    private Texture walkSheet;
    private float stateTime;

    public Enemy(String texturePath, int width, int height) {
        this.width = width;
        this.height = height;
        this.texture = new Texture(texturePath);
        this.sprite = new Sprite(texture);
        this.sprite.setSize(width, height);
        this.hitSound = hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Shark_Damage.wav"));

        this.hitbox = new Rectangle(0, 0, width, height);
        healthPoints = 20;
        isImmune=false;
        isAlive = true;


    }

    public void createAnimation(String sheetPath, int sheetColumns, int sheetRows) {
        walkSheet = new Texture(Gdx.files.internal(sheetPath));

        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth()/sheetColumns, walkSheet.getHeight()/sheetRows);
        TextureRegion[] walkFrames = new TextureRegion[sheetColumns * sheetRows];
        int index = 0;
        for (int i = 0; i < sheetRows; i++) {
            for (int j = 0; j < sheetColumns; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }
        walkAnimation = new Animation<TextureRegion>(0.25f, walkFrames);
        stateTime = 0f;
    }

    public void drawAnimation(SpriteBatch spriteBatch) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
//        spriteBatch.begin();
        spriteBatch.draw(currentFrame, getSprite().getX(), getSprite().getY(), getWidth(), getHeight());
//        spriteBatch.end();
    }

    public abstract void move();
    public abstract void attack();
    public abstract void onDeath();
    public abstract void dropItems();

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

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
    public void playSound(){
        hitSound.setVolume(hitSound.play(), 0.05f);
    }

    public boolean hit(double damage) {
        if (!isImmune) {

            healthPoints -= damage;
            playSound();
            if (healthPoints <= 0) {
                isAlive = false;
                sprite.setColor(Color.BLACK);
                onDeath();
                return true;
            } else {
                sprite.setColor(Color.RED);
                isImmune = true;


                Timer.schedule(new Task() {
                    @Override
                    public void run() {
                        Gdx.app.postRunnable(() -> {
                            sprite.setColor(Color.CLEAR);
                            isImmune = false;


                        });
                    }
                }, 0.1f);
                return true;}
        }
        return false;
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

    public void dropItems(Array<PowerUp> droppedItems) {
        Random random = new Random();
        int chance = random.nextInt(0,100);

        // Drop items koordinater
        float x = getSprite().getWidth()/2 + getSprite().getX();
        float y = getSprite().getHeight()/2 + getSprite().getY();

        switch (chance) {
            case 1:
                SpeedBoost speedBoost = new SpeedBoost(x, y);
                droppedItems.add(speedBoost);
                System.out.println("Item dropped  X" + speedBoost.getHitbox().getX() + " Y " + speedBoost.getHitbox().getY());
                break;
            case 2:
                LuckyClover luckyClover = new LuckyClover(x, y);
                droppedItems.add(luckyClover);
                break;
            case 3:
                HealthHeart healthHeart = new HealthHeart(x, y);
                droppedItems.add(healthHeart);
                break;
            case 4:
                Berserk berserk = new Berserk(x, y);
                droppedItems.add(berserk);
        }

        /*if (chance == 1) {
            SpeedBoost speedBoost = new SpeedBoost((getSprite().getWidth()/2)+getSprite().getX(), (getSprite().getHeight()/2) + getSprite().getY());
            droppedItems.add(speedBoost);
            System.out.println("Item dropped  X" + speedBoost.getHitbox().getX() + " Y " + speedBoost.getHitbox().getY());
        }*/

    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
