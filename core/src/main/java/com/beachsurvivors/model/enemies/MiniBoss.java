package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.groundItems.Chest;
import com.beachsurvivors.model.groundItems.GroundItem;

public class MiniBoss extends Enemy {

    public MiniBoss() {
        super("entities/enemies/crocodile2.png", 128*3, 128*3, 1000);
        createAnimation("entities/enemies/crocodile2.png", 1, 1);
        setX(1000);
        setY(1000);
        setHealthPoints(1000);
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

    public void dropChest(Array<GroundItem> groundItems) {

        Chest chest = new Chest(getX(), getY());
    }
}
