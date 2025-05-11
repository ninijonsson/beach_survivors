package com.beachsurvivors.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
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

        player.create();
        gameManager.create();
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
        System.out.println("controller.logic();");
        player.updateCameraPosition();

        player.shoot();
        ability.updatePosition();
        enemy.spawn();
        enemy.updatePosition();
        enemy.attack();

        // Kontrollerar om spelaren plockar upp ett objekt/powerup
        player.pickUpPowerUp();
        player.pickUpGroundItem();
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
}
