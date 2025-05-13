package com.beachsurvivors.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.model.Map.Map;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.model.groundItems.Berserk;
import com.beachsurvivors.model.groundItems.GroundItem;
import com.beachsurvivors.model.groundItems.PowerUp;
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
    private Array<GroundItem> groundItems;
    private Array<GroundItem> groundItemsToRemove;
    private Array<PowerUp> powerUps;
    private Array<PowerUp> powerUpsToRemove;

    private int totalEnemiesKilled;
    private float growthRateOfSpawningEnemies;
    private int secondsBetweenGrowthRate;
    private boolean isPaused;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Map map;

    public Controller(GameScreen gameScreen, GameUI gameUI) {
        this.gameScreen = gameScreen;
        this.gameViewport = gameScreen.getGameViewport();
        this.gameUI = gameUI;

        this.map = createMap();
        this.gameManager = new GameManager(this);
        this.level = new LevelController(this);
        this.player = new PlayerController(this);
        this.ability = new AbilityController(this);
        this.enemy = new EnemyController(this);

        this.enemies = enemy.getEnemies();
        this.main = gameScreen.getMain();

        this.groundItems = new Array<>();
        this.groundItemsToRemove = new Array<>();
        this.powerUps = new Array<>();
        this.powerUpsToRemove = new Array<>();

        totalEnemiesKilled = 0;
        growthRateOfSpawningEnemies = 1.5f;
        secondsBetweenGrowthRate = 10;
        isPaused = false;

        create();
    }

    @Override
    public void create() {
        // createMap();
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
        pickUpPowerUp();
        pickUpGroundItem();

        // Abilities
        ability.updateShieldPosition();
    }

    public Map createMap() {
        tiledMap = new TmxMapLoader().load("map2/map2.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2f);
        assert tiledMap != null;
        this.map = new Map(tiledMap);
        System.out.println(map);

        return map;
    }

    public void input() {
        player.input();
    }

    public void pickUpPowerUp() {
        for (PowerUp powerUp : powerUps) {

            if (getPlayerController().getPlayer().getHitBox().overlaps(powerUp.getHitbox())) {

                if (powerUp instanceof Berserk) {
                    powerUp.onPickup(getPlayerController().getPlayer());
                    ((Berserk) powerUp).onPickupBullet(getAbilityController().getBullet());
                } else {
                    powerUp.onPickup(getPlayerController().getPlayer());
                }

                powerUpsToRemove.add(powerUp);
                powerUp.dispose();
            }
        }
        gameUI.setHealthBarValue(getPlayerController().getPlayer().getHealthPoints());
        powerUps.removeAll(powerUpsToRemove, true);
        powerUpsToRemove.clear();
    }

    public void pickUpGroundItem() {
        for (GroundItem groundItem : groundItems) {

            if (getPlayerController().getPlayer().getHitBox().overlaps(groundItem.getHitbox())) {
                groundItem.onPickup(getPlayerController().getPlayer());
                groundItemsToRemove.add(groundItem);
            }
        }
        groundItems.removeAll(groundItemsToRemove, true);
        groundItemsToRemove.clear();
    }

    public void increaseEnemiesKilledCounter() {
        totalEnemiesKilled++;
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
        return tiledMapRenderer;
    }

    public Map getMap() {
        return map;
    }

    public GameUI getGameUI() { return gameUI; }

    public Array<GroundItem> getGroundItems() {
        return groundItems;
    }

    public void setGroundItems(Array<GroundItem> groundItems) {
        this.groundItems = groundItems;
    }

    public Array<PowerUp> getPowerUps() {
        return powerUps;
    }

    public void setPowerUps(Array<PowerUp> powerUps) {
        this.powerUps = powerUps;
    }

    public ParticleEffectPoolManager getPoolManager() {
        return poolManager;
    }

    public int getTotalEnemiesKilled() {
        return totalEnemiesKilled;
    }

    public float getSecondsBetweenGrowthRate() {
        return secondsBetweenGrowthRate;
    }

    public double getGrowthRateOfSpawningEnemies() {
        return growthRateOfSpawningEnemies;
    }
}
