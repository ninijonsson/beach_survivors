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
import com.badlogic.gdx.utils.Disposable;
import com.beachsurvivors.model.Map.Map;

public class Player implements Disposable {

    private int healthPoints;
    private int experiencePoints;
    private float speed = 400f;
    private float critChance = 0.5f;

    private Rectangle beachGuyHitBox;
    private float playerX;
    private float playerY;

    private float playerHeight;
    private float playerWidth;

    private static final int FRAME_COLS = 2, FRAME_ROWS = 1;
    private Animation<TextureRegion> walkAnimation;
    private Texture walkSheet;

    private float stateTime;

    private SpriteBatch spriteBatch;

    private Map map;

    public Player(Map map, SpriteBatch spriteBatch) {
        this.map = map;
        this.spriteBatch = spriteBatch;
        playerHeight = 128;
        playerWidth = 128;


        playerX = map.getStartingX();
        playerY = map.getStartingY();
        beachGuyHitBox = new Rectangle(playerX - playerWidth / 2, playerY - playerHeight / 2, playerWidth, playerHeight);

        healthPoints = 100;

        createAnimation();
    }

    private void createAnimation() {
        walkSheet = new Texture(Gdx.files.internal("assets/entities/beach_guy_sheet.png"));

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
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        spriteBatch.begin();

        // Rita animationen centrerad kring playerX och playerY
        spriteBatch.draw(currentFrame, playerX - playerWidth / 2, playerY - playerHeight / 2, playerWidth, playerHeight);

        spriteBatch.end();
    }

    public void playerInput() {
        movementKeys();
        keyBinds();
        flipPlayer();
    }

    private void movementKeys() {
        float delta = Gdx.graphics.getDeltaTime();
        Vector2 direction = new Vector2(0, 0);

        if ((Gdx.input.isKeyPressed(Input.Keys.LEFT)) || (Gdx.input.isKeyPressed(Input.Keys.A))) {
            direction.x -= 1;
        }
        if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT)) || (Gdx.input.isKeyPressed(Input.Keys.D))) {
            direction.x += 1;
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

    private void keyBinds() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            System.out.println("big");
            setPlayerSize(120);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            System.out.println("small");
            setPlayerSize(40);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            System.out.println("normal");
            setPlayerSize(80);
        }
    }

    private void flipPlayer() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            //beachGuySprite.flip(true, false);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            //beachGuySprite.flip(true, false);
        }
    }

    @Override
    public void dispose() {
        walkSheet.dispose();
    }

    public void damagePlayer(float damage){

    }

    public void increaseSpeed(int speedIncrease) {
        speed += speedIncrease;
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

