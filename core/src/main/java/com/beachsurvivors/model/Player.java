package com.beachsurvivors.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.beachsurvivors.model.Map.Map;

import java.util.Random;

public class Player extends Actor {

    private int healthPoints;
    private int experiencePoints;
    private float speed = 500f;
    private float critChance = 0.15f;

    private Rectangle beachGuyHitBox;
    private float playerX;
    private float playerY;

    private float playerHeight;
    private float playerWidth;

    private static final int FRAME_COLS = 2, FRAME_ROWS = 1;
    private Animation<TextureRegion> walkAnimation;
    private Texture walkSheet;
    private boolean isMoving;
    private boolean isFacingLeft;

    private float stateTime;

    private SpriteBatch spriteBatch;

    private Map map;

    public Player(Map map, SpriteBatch spriteBatch) {
        this.map = map;
        this.spriteBatch = spriteBatch;
        playerHeight = 128;
        playerWidth = 128;

        isMoving = false;
        isFacingLeft = false;
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
                walkSheet = new Texture(Gdx.files.internal("entities/beach_girl_sheet.png"));
                break;
            case 2:
                walkSheet = new Texture(Gdx.files.internal("entities/beach_girl_sheet.png"));
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
        } else {
            currentFrame = walkAnimation.getKeyFrame(0);  //Om man står still visas bara första framen i spritesheet
        }
        spriteBatch.begin();
        if (isFacingLeft && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (!isFacingLeft && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }
        // Rita animationen centrerad kring playerX och playerY
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
            isFacingLeft = true;
        }
        if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT)) || (Gdx.input.isKeyPressed(Input.Keys.D))) {
            direction.x += 1;
            isFacingLeft = false;
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

    public void setPlayerSize(float size) {
        playerWidth = size;
        playerHeight = size;
        beachGuyHitBox.setSize(size, size);
    }


    public void dispose() {
        walkSheet.dispose();
    }

    public void damagePlayer(float damage){

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

    public void increaseHealthPoints(int increasedHealthPoints) {
        healthPoints += increasedHealthPoints;

        if (healthPoints > 100) {
            healthPoints = 100;
        }
    }

    public void increaseDamage(double increasedDamage) {
    }
}

