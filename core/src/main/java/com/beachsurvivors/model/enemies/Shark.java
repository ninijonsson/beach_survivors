package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.abilities.Ability;
import com.beachsurvivors.model.abilities.Projectile;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.Player;

public class Shark extends Enemy {
    public Shark () {
        super( 100, 100, 20, 20);
        createAnimation((AssetLoader.get().getTexture("entities/enemies/shark_sheet.png")) , 4, 1);
        setMovementSpeed(150f);
        setDamage(5);
    }


    @Override
    public void move() {

    }


    @Override
    public void attack(Player player, Array<Ability> enemyAbilities, Array<Projectile> enemyProjectiles) {

    }

    @Override
    public void dropItems() {

    }

    @Override
    public void dispose() {

    }



}
