package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.controller.Controller;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.groundItems.Chest;
import com.beachsurvivors.model.groundItems.GroundItem;
import com.beachsurvivors.view.GameScreen;

public class MiniBoss extends Enemy {
    private ParticleEffectPoolManager poolManager;
    private GameScreen gameScreen;

    public MiniBoss(ParticleEffectPoolManager poolManager, GameScreen gameScreen, Controller controller) {
        super(128 * 3, 128 * 3, 500, 100, controller);
        this.poolManager = poolManager;
        this.gameScreen = gameScreen;
        createAnimation(AssetLoader.get().getTexture("assets/entities/enemies/crocodile2.png"), 1, 1);
        setMovementSpeed(200f);
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

    public void dropChest(Array<GroundItem> groundItems) {

        Chest chest = new Chest(getX() + getWidth() / 2, getY() + getHeight() / 2, poolManager, gameScreen);
        groundItems.add(chest);
    }
}
