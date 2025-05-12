package com.beachsurvivors.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.Timer;
import com.beachsurvivors.model.Map.Map;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.groundItems.*;

public class GameManager extends Game implements Screen {
    private Array<GroundItem> groundItems;
    private Array<PowerUp> powerUps;
    private int totalEnemiesKilled;
    private float growthRateOfSpawningEnemies;
    private int secondsBetweenGrowthRate;
    private Controller controller;
    private ParticleEffectPoolManager poolManager;
    private boolean isPaused;

    // Map
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Map map;

    public GameManager(Controller controller) {
        totalEnemiesKilled = 0;
        growthRateOfSpawningEnemies = 1.5f;
        secondsBetweenGrowthRate = 10;
        isPaused = false;

        this.controller = controller;
        this.poolManager = controller.getGameScreen().getPoolManager();

        create();
    }

    @Override
    public void create() {
        createMap();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            Timer.instance().stop();
        }
    }

    public Map createMap() {
        System.out.println("Skapar en map...");
        tiledMap = new TmxMapLoader().load("map2/map2.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2f);
        assert tiledMap != null;
        this.map = new Map(tiledMap);
        System.out.println(map);

        return map;
    }

    // Getters & setters
    public int getTotalEnemiesKilled() {
        return totalEnemiesKilled;
    }

    public void setTotalEnemiesKilled(int totalEnemiesKilled) {
        this.totalEnemiesKilled = totalEnemiesKilled;
    }

    public int getSecondsBetweenGrowthRate() {
        return secondsBetweenGrowthRate;
    }

    public void setSecondsBetweenGrowthRate(int secondsBetweenGrowthRate) {
        this.secondsBetweenGrowthRate = secondsBetweenGrowthRate;
    }

    public float getGrowthRateOfSpawningEnemies() {
        return growthRateOfSpawningEnemies;
    }

    public void setGrowthRateOfSpawningEnemies(float growthRateOfSpawningEnemies) {
        this.growthRateOfSpawningEnemies = growthRateOfSpawningEnemies;
    }
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

    public Map getMap() {
        return map;
    }

    public OrthogonalTiledMapRenderer getTiledMapRenderer() {
        return tiledMapRenderer;
    }
}
