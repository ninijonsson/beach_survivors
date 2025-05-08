package com.beachsurvivors.controller;

import com.badlogic.gdx.Game;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.view.*;


public class ScreenChanger {
    private GameUI ui;
    private Main main;
    private Game game;
    private LevelSystem levelSystem;
    private ChestOverlay chestScreen;
    private LevelUpScreen levelUpScreen;

    //TODO tror att en sådan här hade löst många problem.
    public ScreenChanger(GameScreen gameScreen, GameUI ui, Main main, Game game, Player player){
        levelSystem = new LevelSystem(ui, main);
        //chestScreen = new ChestScreen(gameScreen);
    }

    public void changeScreen(String screentype){
        switch (screentype){
            case "LevelUpScreen":
                main.setScreen(levelUpScreen);
        }
    }
}
