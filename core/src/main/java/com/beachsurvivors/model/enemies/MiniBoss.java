package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.groundItems.GroundItem;
import com.beachsurvivors.model.groundItems.LootChest;
import com.beachsurvivors.model.groundItems.PowerUp;

public class MiniBoss extends Enemy {

    public MiniBoss() {
        super("entities/enemies/crocodile_2.png", 250,250);

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
        LootChest chest = new LootChest(getX(), getY());
        groundItems.add(chest);
    }
}
