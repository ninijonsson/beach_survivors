package com.beachsurvivors.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.view.GameScreen;
import com.beachsurvivors.view.GameUI;
import com.beachsurvivors.view.Main;

public class Controller extends Game implements Screen {
    private AbilityController ability;
    private EnemyController enemy;
    private LevelController level;
    private PlayerController player;
    private GameManager gameManager;
    private Main main;
    private ParticleEffectPoolManager poolManager;
    private GameScreen gameScreen;
    private GameUI gameUI;
    private FitViewport gameViewport;
    private Array<Enemy> enemies;

    public Controller(GameScreen gameScreen, GameUI gameUI) {
        this.gameScreen = gameScreen;
        this.gameViewport = gameScreen.getGameViewport();
        this.gameUI = gameUI;

        this.gameManager = new GameManager(this);
        this.level = new LevelController(this);
        this.player = new PlayerController(this);
        this.ability = new AbilityController(this);
        this.enemy = new EnemyController(this);

        this.enemies = enemy.getEnemies();
        this.main = gameScreen.getMain();
    }

    @Override
    public void create() {
        System.out.println("Kommer till controller.create();");

        gameManager.create();
        player.create();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // logic();
    }

    @Override
    public void resize(int x, int y) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public void logic() {
        player.updateCameraPosition();

        player.shoot();
        ability.updatePosition();

        enemy.spawn();
        enemy.updatePosition();
        enemy.attack();

        // Kontrollerar om spelaren plockar upp ett objekt/powerup
        player.pickUpPowerUp();
        player.pickUpGroundItem();

        // Abilities
        ability.updateShieldPosition();
    }

    public void input() {
        player.input();
    }

    // Getters & setters
    public AbilityController getAbilityController() {
        return ability;
    }

    public EnemyController getEnemyController() {
        return enemy;
    }

    public LevelController getLevelController() {
        return level;
    }

    public PlayerController getPlayerController() {
        return player;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public Main getMain() {
        return main;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public FitViewport getGameViewport() { return gameViewport; }

    public OrthogonalTiledMapRenderer getTiledMapRenderer() {
        System.out.println("Kommer till controller.getTiledMapRenderer();");
        System.out.println(gameManager.getTiledMapRenderer());
        return gameManager.getTiledMapRenderer();
    }

    public GameUI getGameUI() { return gameUI; }
}
