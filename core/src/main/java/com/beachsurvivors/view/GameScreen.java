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
import model.Player;
import model.Shark;

public class GameScreen extends Game implements Screen {

    private Main main;

    private SpriteBatch spriteBatch;
    private Texture beachImage;
    private FitViewport gameviewport;
    private Stage stage;

    private TextureRegion gameRegion;
    private final int worldWidth = 1920;
    private final int worldHeight = 1080;
    private ShapeRenderer shapeRenderer;

    Shark shark;
    private Player player;

    public GameScreen(Main main) {
        this.main = main;
        gameviewport = new FitViewport(worldWidth,worldHeight);

        create();

    }

    @Override
    public void create() {

        spriteBatch = new SpriteBatch();

        shark = new Shark();
        player = new Player();

        shapeRenderer = new ShapeRenderer();
        beachImage = new Texture("game_screen/epicbeach.png");
        gameRegion = new TextureRegion(beachImage);

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
        gameviewport.getCamera().position.set(player.getPlayerX(), player.getPlayerY(), 0);

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        gameviewport.apply();

        spriteBatch.setProjectionMatrix(gameviewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(gameviewport.getCamera().combined);

        spriteBatch.begin();
        spriteBatch.draw(gameRegion, 0,0);

        player.getSprite().draw(spriteBatch);
        player.getHitBox().setPosition(player.getPlayerX(), player.getPlayerY());

        shark.getSprite().draw(spriteBatch);

        spriteBatch.end();

        drawPlayer();

        stage.act();
        stage.draw();

    }

    private void drawPlayer() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(player.getSprite().getX(), player.getSprite().getY(), player.getSprite().getWidth(), player.getSprite().getHeight());
        shapeRenderer.rect(shark.getSprite().getX(), shark.getSprite().getY(), shark.getSprite().getWidth(), shark.getSprite().getHeight());
        shapeRenderer.end();
    }
    private void input() {
        player.playerInput();

    }

    private void logic() {

        player.getSprite().setX(MathUtils.clamp(player.getSprite().getX(), 0, worldWidth - player.getSprite().getWidth()));
        player.getSprite().setY(MathUtils.clamp(player.getSprite().getY(), 0, worldHeight - player.getSprite().getHeight()));

        if (player.getHitBox().overlaps(shark.getHitBox())) {
            shark = null;
            shark = new Shark();
            player.increaseSpeed();
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
        player.dispose();
    }


}
