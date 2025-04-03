package com.beachsurvivors.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

    private Map map;

    public Player(Map map) {
        this.map = map;
        beachguyImage = new Texture("game_screen/Cool_beach_guy.png");
        beachGuySprite = new Sprite(beachguyImage);
        beachGuySprite.setSize(100, 100);
        beachGuyHitBox = new Rectangle(map.getStartingX(), map.getStartingY(), 80, 80);
        playerX = map.getStartingX();
        playerY = map.getStartingY();
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


    private void keyBinds() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            System.out.println("big");
            beachGuySprite.setSize(120,120);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            System.out.println("small");
            beachGuySprite.setSize(40,40);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            System.out.println("normal");
            beachGuySprite.setSize(80,80);
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
