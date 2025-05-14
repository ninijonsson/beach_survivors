package com.beachsurvivors.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.controller.Controller;
import com.beachsurvivors.model.abilities.Ability;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.model.groundItems.GroundItem;
import com.beachsurvivors.model.groundItems.PowerUp;

public class GameScreen extends Game implements Screen {
    private final int SCREEN_WIDTH = 1920;
    private final int SCREEN_HEIGHT = 1080;

    private Main main;
    private SpriteBatch spriteBatch;
    private final FitViewport gameViewport;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Controller controller;
    private GameUI gameUI;
    private ChestOverlay chestOverlay;
    private boolean isChestOverlayActive = false;
    private boolean isPaused = false;

    public GameScreen(Main main) {
        this.main = main;
        this.gameViewport = new FitViewport(SCREEN_WIDTH * 1.5f, SCREEN_HEIGHT * 1.5f);
        this.spriteBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.camera = (OrthographicCamera) gameViewport.getCamera();
        this.controller = new Controller(this);
        this.gameUI = new GameUI(new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT), this, controller);

        controller.setGameUI(gameUI);
        controller.create();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(gameUI.getStage());
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();

        controller.input();
        if (!isPaused) controller.logic();

        renderGameObjects();

        gameUI.getStage().act(delta);
        gameUI.update(delta);
        gameUI.draw();

        if (chestOverlay != null) {
            chestOverlay.update(delta);
            chestOverlay.draw();
        }
    }

    private void renderGameObjects() {
        // Rendera mappen
        controller.getTiledMapRenderer().setView(camera);
        controller.getTiledMapRenderer().render();

        // Setta sprite batch och kameran
        spriteBatch.setProjectionMatrix(gameViewport.getCamera().combined);
        spriteBatch.begin();

        // Ritar spelaren
        controller.getPlayer().drawAnimation();

        // Draw player abilities
        for (Ability a : controller.getAbilities()) {
            a.getSprite().draw(spriteBatch);
        }

        // Draw enemy abilities
        for (Ability a : controller.getEnemyAbilities()) {
            a.getSprite().draw(spriteBatch);
        }

        // Draw enemies
        for (Enemy enemy : controller.getEnemies()) {
            enemy.drawAnimation(spriteBatch);
        }

        // Draw power-ups
        for (PowerUp powerUp : controller.getPowerUps()) {
            powerUp.drawParticles(spriteBatch);
            powerUp.getSprite().draw(spriteBatch);
        }

        // Draw ground items
        for (GroundItem item : controller.getGroundItems()) {
            item.drawParticles(spriteBatch);
            item.getSprite().draw(spriteBatch);
        }

        // Draw damage text
        for (DamageText dt : gameUI.getDamageTexts()) {
            dt.draw(spriteBatch);
        }

        spriteBatch.end();
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        gameUI.getStage().getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        isPaused = true;
        Timer.instance().stop();
    }

    @Override
    public void resume() {
        isPaused = false;
        Timer.instance().start();
    }

    @Override public void hide() {}

    @Override public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        controller.dispose();
    }

    public void showChestOverlay() {
        if (chestOverlay == null) {
            chestOverlay = new ChestOverlay(this, controller);
            isChestOverlayActive = true;
        }
    }

    public SpriteBatch getSpriteBatch() { return spriteBatch; }
    public FitViewport getGameViewport() { return gameViewport; }
    public GameUI getGameUI() { return gameUI; }
    public Main getMain() { return main; }
    public boolean isPaused() { return isPaused; }
    public void setPaused(boolean paused) { isPaused = paused; }
    public int getScreenWidth() { return SCREEN_WIDTH; }
    public int getScreenHeight() { return SCREEN_HEIGHT; }
    public Controller getController() { return controller; }
}
