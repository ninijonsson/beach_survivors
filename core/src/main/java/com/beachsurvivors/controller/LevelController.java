package com.beachsurvivors.controller;

import com.beachsurvivors.view.GameUI;
import com.beachsurvivors.view.Main;

public class LevelController {
    /* int currentLevel;
    private int currentExp;
    private int expToNextLevel;
    private Main main;
    private Controller controller;

    public LevelController(Controller controller) {
        this.currentLevel = 1;
        this.currentExp = 0;
        this.expToNextLevel = calculateExpForLevelUp(currentLevel);

        this.controller = controller;
    }

    public void gainExp(int exp) {
        currentExp += exp;

        // Kontrollera ifall vi ska levela
        while (currentExp >= expToNextLevel) {
            currentExp -= expToNextLevel;
            currentLevel++;
            expToNextLevel = calculateExpForLevelUp(currentLevel);

            onLevelUp();
        }
    }

    private int calculateExpForLevelUp(int level) {
        // TODO: Ändra formulan
        return 100 * level;
    }

    private void onLevelUp() {
        System.out.println("Level up!");
        //ui.updateInfoTable("Congratulations, you are now level " + currentLevel);
        // För att levela, gå via Controller istället
        //main.levelUp(); // Välja abilities/stats
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentExp() {
        return currentExp;
    }

    public int getExpToNextLevel() {
        return expToNextLevel;
    }*/
}
