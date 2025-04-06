package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.Player;

public class Crocodile extends Enemy {


    public Crocodile() {
        super("", 150,150);
        createAnimation("assets/entities/Crocodile1.png", 1, 1);
    }


    @Override
    public void move() {

    }

    @Override
    public void attack(Player player, Array enemyAbilities) {

    }

    @Override
    public void onDeath() {

    }

    @Override
    public void dropItems() {

    }
}
