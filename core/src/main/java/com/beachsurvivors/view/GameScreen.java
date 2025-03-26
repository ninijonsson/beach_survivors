package com.beachsurvivors.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen extends Game implements Screen {

    private Main main;

    private SpriteBatch spriteBatch;
    private Texture beachImage;
    private FitViewport gameviewport;
    private Texture beachguyImage;
    private Sprite beachGuySprite;
    private Stage stage;

    private TextureRegion gameRegion;
    private float playerX;
    private float playerY;
    private final int worldWidth = 1920;
    private final int worldHeight = 1080;
    private Rectangle beachGuyHitBox;
    private ShapeRenderer shapeRenderer;
    private float speed = 400f;

    public GameScreen(Main main) {
        this.main = main;
        gameviewport = new FitViewport(worldWidth,worldHeight);

        create();

    }

    @Override
    public void create() {

        spriteBatch = new SpriteBatch();

        shapeRenderer = new ShapeRenderer();
        beachImage = new Texture("game_screen/epicbeach.png");
        gameRegion = new TextureRegion(beachImage);

        beachguyImage = new Texture("game_screen/Cool_beach_guy.png");

        beachGuySprite = new Sprite(beachguyImage);
        beachGuySprite.setSize(80,80);
        beachGuyHitBox = new Rectangle(playerX, playerY, 80,80);

        stage = new Stage(gameviewport);
        stage.clear();


    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float v) {

        input();
        logic();
        draw();
    }


    @Override
    public void resize(int width, int height) {
        gameviewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    private void draw() {
        gameviewport.getCamera().position.set(playerX, playerY, 0);

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        beachGuyHitBox.setPosition(playerX, playerY);

        gameviewport.apply();

        spriteBatch.setProjectionMatrix(gameviewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(gameviewport.getCamera().combined);

        spriteBatch.begin();
        spriteBatch.draw(gameRegion, 0,0);
        beachGuySprite.draw(spriteBatch);
        spriteBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(beachGuySprite.getX(), beachGuySprite.getY(), beachGuySprite.getWidth(), beachGuySprite.getHeight());
        shapeRenderer.end();

        stage.act();
        stage.draw();

    }
    private void input() {

        keyBinds();
        movementKeys();
        flipPlayer();

    }

    private void logic() {

        beachGuySprite.setX(MathUtils.clamp(beachGuySprite.getX(), 0, worldWidth - beachGuySprite.getWidth()));
        beachGuySprite.setY(MathUtils.clamp(beachGuySprite.getY(), 0, worldHeight - beachGuySprite.getHeight()));

    }

    private void movementKeys() {

        float delta = Gdx.graphics.getDeltaTime();

        if ((Gdx.input.isKeyPressed(Input.Keys.LEFT))  || (Gdx.input.isKeyPressed(Input.Keys.A))) {
            playerX -= speed * delta;
            if (playerX < 0) {
                playerX = 0;
            }
            beachGuySprite.setX(playerX);
        }

        if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT)) || (Gdx.input.isKeyPressed(Input.Keys.D))) {
            playerX += speed * delta;
            if (playerX > worldWidth-beachGuySprite.getWidth()) {
                playerX = worldWidth-beachGuySprite.getWidth();
            }
            beachGuySprite.setX(playerX);
        }

        if ((Gdx.input.isKeyPressed(Input.Keys.DOWN)) || (Gdx.input.isKeyPressed(Input.Keys.S))) {
            playerY -= speed * delta;
            if (playerY < 0) {
                playerY = 0;
            }
            beachGuySprite.setY(playerY);
        }

        if ((Gdx.input.isKeyPressed(Input.Keys.UP)) || (Gdx.input.isKeyPressed(Input.Keys.W))) {
            playerY += speed * delta;
            if (playerY > worldHeight-beachGuySprite.getHeight()) {
                playerY = worldHeight-beachGuySprite.getHeight();
            }
            beachGuySprite.setY(playerY);
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
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        gameRegion.getTexture().dispose();
        beachguyImage.dispose();
    }


}
