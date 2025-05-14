package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.controller.Controller;
import com.beachsurvivors.model.Player;

public class Crab extends Enemy {
    public Crab(Controller controller) {
        super(128, 128, 20, 15, controller);
        createAnimation(AssetLoader.get().getTexture("entities/enemies/crab_sheet.png"), 3, 1);
        setHealthPoints(15);
        setMovementSpeed(150f);
        setDamage(5);
    }

    @Override
    public void move() {

    }

    @Override
    public void attack(Player player, Array enemyAbilities) {

    }

    @Override
    public void dropItems() {

    }
}
