package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.Player;

public class Shark extends Enemy {



    public Shark () {
        super( 100, 100, 20);
        createAnimation((AssetLoader.get().getTexture("assets/entities/enemies/shark_sheet.png")) , 4, 1);
        setHealthPoints(20);
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
