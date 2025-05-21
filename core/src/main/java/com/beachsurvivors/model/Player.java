package com.beachsurvivors.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.controller.LevelSystem;
import com.beachsurvivors.model.Map.Map;
import com.beachsurvivors.view.GameScreen;

import java.util.Random;

public class Player extends Actor {

    //Player stats
    private final int STARTING_HEALTH_POINTS = 100;
    private float maxHealthPoints;
    private float currentHealthPoints;
    private int experiencePoints;
    private float speed = 500f;
    private double damage = 10;
    private float cooldownReduction = 10f;  //CDr i procent, börjar med 10%, högre cdr = snabbare cast time
    private float criticalHitChance = 0.10f;
    private double criticalHitDamage = 2;
    private float hpRegenPerSecond = 0.1f;
    private float regenTimer = 1f;
    private float areaIncrease = 0f;  //Hur stor AoE spelaren har, för Boomerangen, Magnet/vacuum osv
    private float lifesteal = 10f;

    private boolean isImmune;
    private boolean isAlive;
    private LevelSystem levelSystem;
    private GameScreen gameScreen;

    private double damageTaken = 0;
    private double healingReceived = 0;

    private float vaccumRadius = 400;
    private Circle vaccumHitbox;
    // Movementspeed of the item while being sucked
    private float vaccumStrength = 100;

    private Rectangle beachGuyHitBox;
    private Vector2 position;

    private float playerHeight;
    private float playerWidth;

    private static final int FRAME_COLS = 2;
    private static final int FRAME_ROWS = 1;
    private Animation<TextureRegion> walkAnimation;
    private Texture walkSheet;
    private Texture shadowTexture;
    protected float shadowOffsetX = 0f;
    protected float shadowOffsetY = -10f;
    private boolean isMoving;
    private Color tint = Color.WHITE;

    private boolean movingRight = true;
    private boolean movingLeft = false;

    private float stateTime;

    private Random random = new Random();
    private SpriteBatch spriteBatch;
    private Map map;
    private Sound footstepSound;
    private float footstepTimer = 0;
    private float footstepInterval = 0.4f;



    public Player(Map map, SpriteBatch spriteBatch, GameScreen gameScreen) {
        this.map = map;
        this.spriteBatch = spriteBatch;
        this.gameScreen = gameScreen;

        playerHeight = 128;
        playerWidth = 128;

        levelSystem = new LevelSystem(this.gameScreen.getGameUI(), this.gameScreen.getMain());

        isMoving = false;
        isAlive = true;
        isImmune = false;

//        playerX = map.getStartingX();
//        playerY = map.getStartingY();

        position = new Vector2(map.getStartingX(), map.getStartingY());

        beachGuyHitBox = new Rectangle(position.x - playerWidth / 2, position.y - playerHeight / 2, playerWidth, playerHeight);
        vaccumHitbox = new Circle(position.x, position.y, vaccumRadius);

        currentHealthPoints = STARTING_HEALTH_POINTS;
        maxHealthPoints = STARTING_HEALTH_POINTS;

        createAnimation();
        footstepSound = AssetLoader.get().manager.get("sounds/footstep.mp3", Sound.class);

    }

    public void drawShadow(SpriteBatch spriteBatch) {
        float shadowX = position.x - playerWidth / 2f + shadowOffsetX;
        float shadowY = position.y - playerHeight / 2f + shadowOffsetY;
        spriteBatch.draw(shadowTexture, shadowX, shadowY, playerWidth, playerHeight / 4f);
    }

    private void createAnimation() {
        int choice = random.nextInt(1,3);
        this.shadowTexture = AssetLoader.get().manager.get("entities/abilities/bomb_shadow.png", Texture.class);

        switch (choice) {
            case 1:
                walkSheet = AssetLoader.get().manager.get("entities/beach_girl_sheet.png");
                break;
            case 2:
                walkSheet = AssetLoader.get().manager.get("entities/beach_guy_sheet.png");
                break;
        }

        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth() / FRAME_COLS, walkSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;

        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }
        walkAnimation = new Animation<TextureRegion>(0.25f, walkFrames);
        stateTime = 0f;
    }

    public void drawAnimation() {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;
        drawShadow(spriteBatch);
        if (isMoving) {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);

            if (isMovingLeft() && !currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            } else if (isMovingRight() && currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            }
        } else {
            currentFrame = walkAnimation.getKeyFrame(0);  //Om man står still visas bara första framen i spritesheet
        }
        // Rita animationen centrerad kring playerX och playerY
        spriteBatch.setColor(tint);
        spriteBatch.draw(currentFrame, position.x - playerWidth / 2, position.y - playerHeight / 2, playerWidth, playerHeight);

    }

    public void gainExp(int exp) {
        levelSystem.gainExp(exp);
    }

    public void playerInput() {
        movementKeys();
        //FLYTTADE KEYBINDS TILL GAMESCREEN
    }

    private void movementKeys() {
        float delta = Math.min(Gdx.graphics.getDeltaTime(), 1/30f);
        Vector2 direction = new Vector2(0, 0);

        if ((Gdx.input.isKeyPressed(Input.Keys.LEFT)) || (Gdx.input.isKeyPressed(Input.Keys.A))) {
            direction.x -= 1;
            setMovingLeftRight(true, false);
        }

        if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT)) || (Gdx.input.isKeyPressed(Input.Keys.D))) {
            direction.x += 1;
            setMovingLeftRight(false, true);
        }

        if ((Gdx.input.isKeyPressed(Input.Keys.DOWN)) || (Gdx.input.isKeyPressed(Input.Keys.S))) {
            direction.y -= 1;
        }
        if ((Gdx.input.isKeyPressed(Input.Keys.UP)) || (Gdx.input.isKeyPressed(Input.Keys.W))) {
            direction.y += 1;
        }

        if (direction.len() > 0) {
            direction.nor();
        }
        if (!direction.isZero()) {
            isMoving = true;
            if (isMoving) {
                footstepTimer += delta;
                if (footstepTimer >= footstepInterval) {
                    int randomPitch= random.nextInt(5);
                    footstepSound.setPitch(footstepSound.play(0.01f), randomPitch);

                    footstepTimer = 0;
                }
            } else {
                footstepTimer = footstepInterval; // så att det spelas direkt när man börjar gå igen
            }

        } else {
            isMoving = false;
        }

        Vector2 newPlayerPosition = new Vector2(position.x, position.y).add(direction.scl(speed * delta));

        // LOGIK FÖR ATT KONTROLLERA SPELARENS NYA POSITION. OM DEN ÄR GILTIG ELLER EJ
        Polygon tempHitBox = new Polygon(new float[]{
            0, 0,
            beachGuyHitBox.width, 0,
            beachGuyHitBox.width, beachGuyHitBox.height,
            0, beachGuyHitBox.height
        });
        tempHitBox.setPosition(newPlayerPosition.x - playerWidth / 2, newPlayerPosition.y - playerHeight / 2);

        // CHECKA OM DET GÅR ATT GÖRA MOVET
        if (map.isInsidePolygon(newPlayerPosition.x, newPlayerPosition.y) &&
            map.isValidMove(tempHitBox) &&
            !map.collidesWithObject(tempHitBox)) {
            position.x = newPlayerPosition.x;
            position.y = newPlayerPosition.y;
            beachGuyHitBox.setPosition(position.x - playerWidth / 2, position.y - playerHeight / 2);
        }
    }

    public void setMovingLeftRight(boolean movingLeft, boolean movingRight) {
        this.movingLeft = movingLeft;
        this.movingRight = movingRight;
    }

    public boolean isMovingLeft() {
        return movingLeft;
    }
    public boolean isMovingRight() {
        return movingRight;
    }

    public void setPlayerSize(float size) {
        playerWidth = size;
        playerHeight = size;
        beachGuyHitBox.setSize(size, size);
    }

    public void dispose() {
        walkSheet.dispose();
    }

    public void takeDamage(double damage) {
        if (damage <= 0) return;
        if (!isImmune) {
            currentHealthPoints -= damage;
            damageTaken += damage;

            gameScreen.showPlayerDamageText("-" + (int) damage, true);  // Visa endast om skadan faktiskt tillämpas

            if (currentHealthPoints <= 0) {
                setAlive(false);
                return;
            }

            isImmune = true;
            tint = Color.RED;

            Timer.schedule(new Task() {
                @Override
                public void run() {
                    isImmune = false;
                }
            }, 0.5f);

            Timer.schedule(new Task() {
                @Override
                public void run() {
                    tint = Color.WHITE;
                }
            }, 0.1f);
        }
    }

    public void onDamageDealt(double damageDealt) {
        int healedAmount = Math.round((float) (damageDealt * lifesteal));
        restoreHealthPoints(healedAmount);
    }

    public void increaseLifesteal(float amount) {
        lifesteal += amount;
    }


    public void onDeath() {
        dispose();
    }

    public void increaseSpeed(int speedIncrease) {
        float newSpeed = speed + speedIncrease;
        if (newSpeed > 1200) {
            speed = 1200f;
        } else if (newSpeed < 300) {
            speed = 300f;
        } else {
            speed = newSpeed;
        }
    }

    public void update(float deltaTime) {
        regenerateHp(deltaTime);
    }

    private void regenerateHp(float delta) {

        regenTimer -= delta;
        if (regenTimer <= 0) {
            regenTimer = 1;
            restoreHealthPoints(hpRegenPerSecond);
            return;
        }
    }

    public void restoreHealthPoints(float increasedHealthPoints) {
        currentHealthPoints += increasedHealthPoints;
        healingReceived += increasedHealthPoints;

        if (currentHealthPoints > maxHealthPoints) {
            currentHealthPoints = maxHealthPoints;
        }
    }

    public void increaseMaximumHealthPoints(int maxHPincrease) {
        this.maxHealthPoints += maxHPincrease;
        restoreHealthPoints(maxHPincrease);
    }

    public void increaseDamage(double increasedDamage) {
        damage += increasedDamage;
    }


    public void increaseCritChance(float critChanceIncrease) {
        criticalHitChance += critChanceIncrease;
    }

    public void increaseCritDamage(float critDamageIncrease) {
        criticalHitDamage += critDamageIncrease;
    }

    public void increaseCooldownReduction(double cooldownReduction) {
        this.cooldownReduction += cooldownReduction;
    }

    public void increaseAreaRange(float areaIncrease) {
        this.areaIncrease += areaIncrease;
    }


    public boolean isCriticalHit() {
        return random.nextFloat() < criticalHitChance;
    }

    public Rectangle getHitBox() {
        return beachGuyHitBox;
    }


    public Vector2 getPosition() {
        return position;
    }

    public double getDamage() {
        return damage;
    }

    public float getCriticalHitChance() {
        return criticalHitChance;
    }

    public double getCriticalHitDamage() {
        return criticalHitDamage;
    }

    public float getCurrentHealthPoints() {
        return currentHealthPoints;
    }

    public float getMaxHealthPoints() {
        return maxHealthPoints;
    }

    public float getCooldownReduction() {
        return cooldownReduction;
    }

    public float getHpRegenPerSecond() {
        return hpRegenPerSecond;
    }

    public float getLifesteal() {
        return lifesteal;
    }

    public float getAreaIncrease() {
        return areaIncrease;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public float getSpeed() {
        return speed;
    }

    public int getLevel() {
        return levelSystem.getCurrentLevel();
    }

    public double getDamageTaken() {
        return damageTaken;
    }

    public double getHealingReceived() {
        return healingReceived;
    }

    public LevelSystem getLevelSystem() {
        return levelSystem;
    }

    public Circle getVaccumHitbox() {
        vaccumHitbox.setPosition(position.x, position.y);
        return vaccumHitbox;
    }

    public float getVaccumStrength() {
        return vaccumStrength;
    }

}

