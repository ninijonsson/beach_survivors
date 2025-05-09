package com.beachsurvivors.controller;

import com.beachsurvivors.view.GameUI;
import com.beachsurvivors.view.Main;

public class Controller {
    private AbilityController ability;
    private EnemyController enemy;
    private LevelController level;
    private PlayerController player;
    private GameManager gameManager;

    public Controller(GameUI ui, Main main) {
        enemy = new EnemyController();
        level = new LevelController(ui, main);
        player = new PlayerController();
        ability = new AbilityController(player);
        gameManager = new GameManager();
    }
}
