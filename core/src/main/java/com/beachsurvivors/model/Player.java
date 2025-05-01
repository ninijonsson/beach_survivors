package com.beachsurvivors.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.Map.Map;

import java.util.Random;
import java.util.TimerTask;

public class Player extends Actor {

    private int healthPoints;
    private int experiencePoints;
    private float speed = 500f;
    private float critChance = 0.15f;
    private int level = 1;
    private boolean isImmune;
    private boolean isAlive;


    private Rectangle beachGuyHitBox;
    private float playerX;
    private float playerY;

    private float playerHeight;
    private float playerWidth;

    private static final int FRAME_COLS = 2, FRAME_ROWS = 1;
    private Animation<TextureRegion> walkAnimation;
    private Texture walkSheet;
    private boolean isMoving;
    private Color tint = Color.WHITE;

    private boolean movingRight = true;
    private boolean movingLeft = false;

    private float stateTime;

    private SpriteBatch spriteBatch;

    private Map map;

    public Player(Map map, SpriteBatch spriteBatch) {
        this.map = map;
        this.spriteBatch = spriteBatch;
        playerHeight = 128;
        playerWidth = 128;

        isMoving = false;
        isAlive = true;
        isImmune = false;

        playerX = map.getStartingX();
        playerY = map.getStartingY();
        beachGuyHitBox = new Rectangle(playerX - playerWidth / 2, playerY - playerHeight / 2, playerWidth, playerHeight);

        healthPoints = 100;

        createAnimation();
    }

    private void createAnimation() {
        Random random = new Random();
        int choice = random.nextInt(1,3);
        switch (choice) {
            case 1:
                ''
                walkSheet = AssetLoader.get().manager.get("assets/entities/beach_girl_sheet.png");
                break;
            case 2:
                walkSheet = AssetLoader.get().manager.get("assets/entities/beach_guy_sheet.png");
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

    public void runAnimation() {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;
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
        spriteBatch.begin();
        if (isMovingLeft() && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (!isMovingLeft() && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }
        // Rita animationen centrerad kring playerX och playerY
        spriteBatch.setColor(tint);
        spriteBatch.draw(currentFrame, playerX - playerWidth / 2, playerY - playerHeight / 2, playerWidth, playerHeight);


        spriteBatch.end();
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
        } else {
            isMoving = false;
        }

        Vector2 newPlayerPosition = new Vector2(playerX, playerY).add(direction.scl(speed * delta));

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
            !map.collidesWithObject(tempHitBox.getBoundingRectangle())) {
            playerX = newPlayerPosition.x;
            playerY = newPlayerPosition.y;
            beachGuyHitBox.setPosition(playerX - playerWidth / 2, playerY - playerHeight / 2);
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

    public void takeDamage(double damage){
        if (!isImmune) {
            healthPoints -= damage;
            if (healthPoints <= 0) {
                setAlive(false);
                return;
            }

            isImmune = true; //Immunitet när man tar damage
            tint = Color.RED; //Ändrar färgen när man tar damage

            Timer.schedule(new Task() {  //resetar immunitet efter 0,5sekunder
                @Override
                public void run() {
                    isImmune = false;
                }
            }, 0.5f);

            //Jag gjorde två olika tasks för att 0.1sekunder immunitet kändes lite om där är mkt mobs
            //men det kanske är fine med 0.1? i så fall kan vi slå ihop båda tasks

            Timer.schedule(new Task() {  //resetar färgen efter 0.1sekunder
                @Override
                public void run() {
                    tint = Color.WHITE;
                }
            }, 0.1f);
        }
    }

    public void onDeath() {
        dispose();
    }

    public void increaseSpeed(int speedIncrease) {
        if (speed + speedIncrease > 1200f) {
            speed = 1200f;
        } else {
            speed += speedIncrease;
        }
    }

    public void increaseCritChance(float critChanceIncrease) {
        critChance = critChanceIncrease;
    }

    public Rectangle getHitBox() {
        return beachGuyHitBox;
    }

    public float getPlayerX() {
        return playerX;
    }

    public void setPlayerX(float playerX) {
        this.playerX = playerX;
        beachGuyHitBox.setX(playerX - playerWidth / 2);
    }

    public float getPlayerY() {
        return playerY;
    }

    public void setPlayerY(float playerY) {
        this.playerY = playerY;
        beachGuyHitBox.setY(playerY - playerHeight / 2);
    }

    public float getCritChance() {
        return critChance;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = false;
    }

    public void increaseHealthPoints(int increasedHealthPoints) {
        healthPoints += increasedHealthPoints;

        if (healthPoints > 100) {
            healthPoints = 100;
        }
    }

    public void increaseDamage(double increasedDamage) {
    }

    public float getSpeed() {
        return speed;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

