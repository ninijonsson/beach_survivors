package com.beachsurvivors.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.beachsurvivors.model.Map.Map;

public class Player implements Disposable {

    private int healthPoints;
    private int experiencePoints;
    private float speed = 400f;
    private float critChance=0.5f;

    private Texture beachguyImage;
    private Sprite beachGuySprite;
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
        beachguyImage = new Texture("game_screen/Cool_beach_guy.png");
        beachGuySprite = new Sprite(beachguyImage);
        beachGuySprite.setSize(100, 100);
        beachGuyHitBox = new Rectangle(map.getStartingX(), map.getStartingY(), playerWidth, playerHeight);
        playerX = map.getStartingX();
        playerY = map.getStartingY();

        playerHeight = 128;
        playerWidth = 128;

        createAnimation();

    }

    private void createAnimation() {
        walkSheet = new Texture(Gdx.files.internal("assets/entities/beach_guy_sheet.png"));

        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth()/FRAME_COLS, walkSheet.getHeight()/FRAME_ROWS);
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
        spriteBatch.draw(currentFrame, playerX, playerY, playerWidth, playerHeight);
        spriteBatch.end();
    }



    public void playerInput() {
        movementKeys();
        keyBinds();
        flipPlayer();
    }

    private void movementKeys() {
        float delta = Gdx.graphics.getDeltaTime();
        float newPlayerX = playerX;
        float newPlayerY = playerY;

        if ((Gdx.input.isKeyPressed(Input.Keys.LEFT)) || (Gdx.input.isKeyPressed(Input.Keys.A))) {
            newPlayerX -= speed * delta;
        }
        if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT)) || (Gdx.input.isKeyPressed(Input.Keys.D))) {
            newPlayerX += speed * delta;
        }
        if ((Gdx.input.isKeyPressed(Input.Keys.DOWN)) || (Gdx.input.isKeyPressed(Input.Keys.S))) {
            newPlayerY -= speed * delta;
        }
        if ((Gdx.input.isKeyPressed(Input.Keys.UP)) || (Gdx.input.isKeyPressed(Input.Keys.W))) {
            newPlayerY += speed * delta;
        }

        //LOGIK FÖR ATT KONTROLLERA SPELARENS NYA POSITION. OM DEN ÄR GILTIG ELLER EJ
        Polygon tempHitBox = new Polygon(new float[]{
            0, 0,
            beachGuyHitBox.width, 0,
            beachGuyHitBox.width, beachGuyHitBox.height,
            0, beachGuyHitBox.height
        });
        tempHitBox.setPosition(newPlayerX, newPlayerY);

        // Check if the new position is inside the polygon and the move is valid
        if (map.isInsidePolygon(newPlayerX, newPlayerY) &&
            map.isValidMove(tempHitBox) &&
            !map.collidesWithObject(tempHitBox.getBoundingRectangle())) {
            playerX = newPlayerX;
            playerY = newPlayerY;
            beachGuySprite.setPosition(playerX, playerY);
            beachGuyHitBox.setPosition(playerX, playerY);
        }

    }

    public void setPlayerSize(float size) {
        playerWidth = size;
        playerHeight = size;
    }


    private void keyBinds() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            System.out.println("big");
            beachGuySprite.setSize(120,120);
            setPlayerSize(120);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            System.out.println("small");
            beachGuySprite.setSize(40,40);
            setPlayerSize(40);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            System.out.println("normal");
            beachGuySprite.setSize(80,80);
            setPlayerSize(80);
        }
    }

    private void flipPlayer() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            beachGuySprite.flip(true, false);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            beachGuySprite.flip(true, false);
        }
    }

    @Override
    public void dispose() {
        beachguyImage.dispose();
        walkSheet.dispose();
    }

    public Texture getTexture() {
        return beachguyImage;
    }

    public void increaseSpeed(int speedIncrease) {
        speed += speedIncrease;
    }

    public void setBeachguyImage(Texture beachguyImage) {
        this.beachguyImage = beachguyImage;
    }

    public Sprite getSprite() {
        return beachGuySprite;
    }

    public Texture getWalkSheet() {
        return walkSheet;
    }

    public void setBeachGuySprite(Sprite beachGuySprite) {
        this.beachGuySprite = beachGuySprite;
    }

    public Rectangle getHitBox() {
        return beachGuyHitBox;
    }

    public void setBeachGuyHitBox(Rectangle beachGuyHitBox) {
        this.beachGuyHitBox = beachGuyHitBox;
    }

    public float getPlayerX() {
        return playerX;
    }

    public void setPlayerX(float playerX) {
        this.playerX = playerX;
    }

    public float getPlayerY() {
        return playerY;
    }

    public void setPlayerY(float playerY) {
        this.playerY = playerY;
    }

    public float getCritChance() {
        return critChance;
    }
}
