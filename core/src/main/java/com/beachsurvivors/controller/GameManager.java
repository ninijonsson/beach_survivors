package com.beachsurvivors.controller;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.groundItems.*;

public class GameManager {
    private Array<GroundItem> groundItems;
    private Array<PowerUp> powerUps;
    private int totalEnemiesKilled;
    private float growthRateOfSpawningEnemies;
    private int secondsBetweenGrowthRate;

    public GameManager() {}
}
