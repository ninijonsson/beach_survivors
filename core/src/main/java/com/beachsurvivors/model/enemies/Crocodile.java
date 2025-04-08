package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.Player;

public class Crocodile extends Enemy {


    public Crocodile() {
        super("", 200,100);
        createAnimation("entities/enemies/Crocodile1-Sheet.png", 2, 1);
        setMovementSpeed(100f);
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
