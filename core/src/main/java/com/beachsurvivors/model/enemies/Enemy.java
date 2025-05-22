package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.math.Rectangle;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.abilities.Ability;
import com.beachsurvivors.model.groundItems.*;

import java.util.Random;


public abstract class Enemy implements Disposable {

    private float healthPoints;
    private float movementSpeed;
    private int damage;
    private int expOnDeath;
    private boolean isAlive;
    private int width;
    private int height;

    private boolean movingRight = true;
    private boolean movingLeft = false;

    private Vector2 position = new Vector2();
    private float radius;

    private Texture texture;
    private Sprite sprite;
    private Rectangle hitbox;
    private final Texture shadowTexture;
    protected float shadowOffsetX = 0f;
    protected float shadowOffsetY = -10f;

    private Sound hitSound;
    private boolean isImmune;

    private Animation<TextureRegion> walkAnimation;
    private Color tint = Color.WHITE;
    private Texture walkSheet;
    private float stateTime;
    private ProgressBar healthBar;
    private boolean healthBarAddedToStage = false;
    private Timer.Task hideHealthBarTask;
    private Stage stage;

    public Enemy(int width, int height, int healthPoints, int expOnDeath) {
        this.width = width;
        this.height = height;
        this.healthPoints = healthPoints;
        this.expOnDeath = expOnDeath;
        this.texture = AssetLoader.get().getTexture("placeholder.png");
        this.sprite = new Sprite(texture);
        this.sprite.setSize(width, height);
        this.hitSound = AssetLoader.get().getSound("sounds/shark_damage_2.wav");

        this.radius = width /4;

        this.hitbox = new Rectangle(32, 32, width, height);
        isImmune=false;
        isAlive = true;
        this.shadowTexture = AssetLoader.get().manager.get("entities/abilities/bomb_shadow.png", Texture.class);
        createHealthBar(healthPoints);

    }

    public abstract void move();
    public abstract void attack(Player player, Array<Ability> enemyAbilities);
    public abstract void dropItems();

    private void createHealthBar(int healthPoints) {
        Skin healthSkin = new Skin(Gdx.files.internal("skin_composer/healthbutton.json"));
        healthBar = new ProgressBar(0, healthPoints, 0.5f, false, healthSkin);
        healthBar.setValue(healthPoints);
        healthBar.setPosition(hitbox.x+hitbox.width/2, hitbox.y+height);
        healthBar.setSize(70, 50);
        healthBar.setScale(0.2f);

    }

    public void addHealthBar(Stage stage) {
        if (!healthBarAddedToStage) {
            this.stage = stage;
            stage.addActor(healthBar);
            healthBarAddedToStage = true;
        }
    }

    public void updateHealthBarPosition() {
        //healthBar.setPosition(getPosition().x, getPosition().y + sprite.getHeight() + 5);

        healthBar.setPosition(getPosition().x + getWidth() / 2f - healthBar.getWidth() / 2f,
            getPosition().y + getHeight() + 5);
        healthBar.setValue(healthPoints);
    }

    public void showHealthBarTemporarily(float duration) {
        healthBar.setVisible(true);

        // Avbryt tidigare timer om den fortfarande körs
        if (hideHealthBarTask != null) {
            hideHealthBarTask.cancel();
        }

        // Skapa en ny timer som gömmer healthbaren efter en viss tid
        hideHealthBarTask = new Timer.Task() {
            @Override
            public void run() {
                Gdx.app.postRunnable(() -> healthBar.setVisible(false));
            }
        };
        Timer.schedule(hideHealthBarTask, duration);
    }

    public void createAnimation(Texture texture, int sheetColumns, int sheetRows) {
        walkSheet=texture;
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth()/sheetColumns, walkSheet.getHeight()/sheetRows);
        TextureRegion[] walkFrames = new TextureRegion[sheetColumns * sheetRows];
        int index = 0;

        for (int i = 0; i < sheetRows; i++) {
            for (int j = 0; j < sheetColumns; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }

        walkAnimation = new Animation<>(0.25f, walkFrames);
        stateTime = 0f;
    }

    public void drawAnimation(SpriteBatch spriteBatch) {
        stateTime += Gdx.graphics.getDeltaTime();
        drawShadow(spriteBatch);
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        if (isMovingLeft() && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (isMovingRight() && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        spriteBatch.setColor(tint);
        spriteBatch.draw(currentFrame, getPosition().x, getPosition().y, getWidth(), getHeight());
        spriteBatch.setColor(Color.WHITE);

    }

    public void drawShadow(SpriteBatch spriteBatch) {
        float shadowX = position.x + shadowOffsetX;
        float shadowY = position.y + shadowOffsetY;
        spriteBatch.draw(shadowTexture, shadowX, shadowY, width, height / 4f);
    }

    public void setShadowOffsetY(int offset){
        this.shadowOffsetY=offset;
    }

    public void onDeath(){
        if (stage != null) {
            healthBar.remove();
        }
    }

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

    public void setHealthPoints(float healthPoints) {
        this.healthPoints = healthPoints;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public void setHitSound(Sound hitSound) {
        this.hitSound = hitSound;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
    public void playSound(){
        hitSound.play(0.7f);
    }
    public void setMovementSpeed() {}

    public boolean hit(double damage) {
        if (!isImmune) {
            healthPoints -= damage;
            playSound();
            showHealthBarTemporarily(2.0f);

            if (healthPoints <= 0) {
                isAlive = false;
                onDeath();
                return true;
            } else {
                tint = Color.RED;
                isImmune = true;

                Timer.schedule(new Task() {
                    @Override
                    public void run() {
                        Gdx.app.postRunnable(() -> {
                            tint = Color.WHITE;
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

    public Texture getWalkSheet() {
        return walkSheet;
    }

    public Animation<TextureRegion> getWalkAnimation() {
        return walkAnimation;
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

    public void dropItems(Array<PowerUp> droppedItems, ParticleEffectPoolManager poolManager) {
        Random random = new Random();
        int chance = random.nextInt(0,100);

        // Drop items koordinater
        float x = getPosition().x + getWidth() / 2f;
        float y = getPosition().y + getHeight() / 2f;

        switch (chance) {
            case 1:
                SpeedBoost speedBoost = new SpeedBoost(x, y, poolManager);
                droppedItems.add(speedBoost);
                break;
            case 2:
                LuckyClover luckyClover = new LuckyClover(x, y, poolManager);
                droppedItems.add(luckyClover);
                break;
            case 3:
                HealthHeart healthHeart = new HealthHeart(x, y, poolManager);
                droppedItems.add(healthHeart);
                break;
            case 4:
                Berserk berserk = new Berserk(x, y, poolManager);
                droppedItems.add(berserk);
                break;
            default:
        }
    }

    @Override
    public void dispose() {

    }

    public Vector2 moveTowardsPlayer(float delta, Vector2 playerPosition, Vector2 enemyPosition) {
        Vector2 direction = new Vector2(playerPosition.x - enemyPosition.x, playerPosition.y - enemyPosition.y);
        direction.nor();
        return direction;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getCenter() {
        Vector2 center = new Vector2(position.x+width/2f, position.y + height/2f);
        return center;
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
        sprite.setPosition(position.x, position.y);
        hitbox.setPosition(position.x, position.y);
    }

    public Circle getCircle() {
        return new Circle(position.x, position.y, radius);
    }

    public void setMovingLeftRight(boolean movingLeft, boolean movingRight) {
        this.movingLeft = movingLeft;
        this.movingRight = movingRight;
    }

    public void setMovingLeftRight(float directionX) {
        if (directionX < 0) setMovingLeftRight(true, false);
        else setMovingLeftRight(false, true);
    }

    public boolean isMovingLeft() {
        return movingLeft;
    }
    public boolean isMovingRight() {
        return movingRight;
    }


    public int getExp() {
        return expOnDeath;
    }
}
