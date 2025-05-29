package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.Player;

public class Crocodile extends Enemy {
    public Crocodile() {
        super( 200,100, 120, 30);
        createAnimation(AssetLoader.get().getTexture("entities/enemies/crocodile1_sheet.png"), 2, 1);
        setMovementSpeed(100f);
        setDamage(20);
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
