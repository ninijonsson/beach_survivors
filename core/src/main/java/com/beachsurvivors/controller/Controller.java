package com.beachsurvivors.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.Map.Map;
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
    private GameUI gameUI;
    private Main main;
    private ParticleEffectPoolManager poolManager;
    private GameScreen gameScreen;
    private Array<Enemy> enemies;

    public Controller(GameUI gameUI, Main main, Map map) {
        this.gameUI = gameUI;
        this.main = main;

        enemy = new EnemyController(this);
        level = new LevelController(gameUI, main, this);
        player = new PlayerController(map, this);
        ability = new AbilityController(this);
        gameManager = new GameManager(this);
        gameScreen = main.getGameScreen();

        this.enemies = enemy.getEnemies();
    }

    @Override
    public void create() {

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        logic();
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

    public GameManager getGameManagerController() {
        return gameManager;
    }

    public Main getMain() {
        return main;
    }

    public GameUI getGameUI() {
        return gameUI;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }
}
