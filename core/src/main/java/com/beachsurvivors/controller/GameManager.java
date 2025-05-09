package com.beachsurvivors.controller;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.groundItems.*;

public class GameManager {
    private Array<GroundItem> groundItems;
    private Array<PowerUp> powerUps;
    private int totalEnemiesKilled;
    private float growthRateOfSpawningEnemies;
    private int secondsBetweenGrowthRate;
    private Controller controller;
    private ParticleEffectPoolManager poolManager;

    public GameManager(Controller controller) {
        totalEnemiesKilled = 0;
        growthRateOfSpawningEnemies = 1.5f;
        secondsBetweenGrowthRate = 10;

        this.controller = controller;
        this.poolManager = controller.getGameScreen().getPoolManager();
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
}
