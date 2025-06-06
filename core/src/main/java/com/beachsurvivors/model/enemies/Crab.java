package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.abilities.Ability;
import com.beachsurvivors.model.abilities.Projectile;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.Player;

public class Crab extends Enemy {
    public Crab() {
        super(128, 128, 50, 15);
        createAnimation(AssetLoader.get().getTexture("entities/enemies/crab_sheet.png"), 3, 1);
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
}
