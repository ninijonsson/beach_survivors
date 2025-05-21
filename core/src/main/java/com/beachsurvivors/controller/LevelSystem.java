package com.beachsurvivors.controller;

import com.beachsurvivors.view.GameUI;
import com.beachsurvivors.view.Main;

public class LevelSystem {
    private int currentLevel;
    private int currentExp;
    private int expToNextLevel;
    private GameUI ui;
    private Main main;

    public LevelSystem(GameUI ui, Main main) {
        this.currentLevel = 1;
        this.currentExp = 0;
        this.expToNextLevel = calculateExpForLevelUp(currentLevel);

        this.ui = ui;
        this.main = main;
    }

    public void gainExp(int exp) {
        currentExp += exp;

        // Kontrollera ifall vi ska levela
        while (currentExp >= expToNextLevel) {
            currentExp -= expToNextLevel;
            currentLevel++;
            expToNextLevel = calculateExpForLevelUp(currentLevel);

            onLevelUp();
            ui.updateXpBar();

        }
    }

    private int calculateExpForLevelUp(int level) {
        // TODO: Ändra formulan
        return 100 * level;
    }

    private void onLevelUp() {
        ui.updateInfoTable("Congratulations, you are now level " + currentLevel);
        main.levelUp(); // Välja abilities/stats
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentExp() {
        return currentExp;
    }

    public int getExpToNextLevel() {
        return expToNextLevel;
    }
}
