package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.Player;

public class Crab extends Enemy {
    public Crab(String texturePath, int width, int height) {

        super( width, height, 20, 20);
        //TODO crab_sheet finns inte. när den skapad kan linjen under användas.
        //createAnimation(AssetLoader.get().getTexture("assets/entities/enemies/crab_sheet.png"), 4, 1);
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
