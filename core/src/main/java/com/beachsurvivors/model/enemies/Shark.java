package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.Player;

public class Shark extends Enemy {



    public Shark () {
        super("entities/enemies/shark.png", 100, 100);
        createAnimation("entities/enemies/shark_sheet.png" , 4, 1);
        setHealthPoints(20);
        setMovementSpeed(300f);
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
