package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.groundItems.Chest;
import com.beachsurvivors.model.groundItems.GroundItem;
import com.beachsurvivors.view.GameScreen;

public class MiniBoss extends Enemy {
    private ParticleEffectPoolManager poolManager;
    private GameScreen gameScreen;

    public MiniBoss(ParticleEffectPoolManager poolManager, GameScreen gameScreen) {
        super(128 * 3, 128 * 3, 250, 100);
        this.poolManager = poolManager;
        this.gameScreen = gameScreen;
        createAnimation(AssetLoader.get().getTexture("entities/enemies/crocodile2.png"), 1, 1);
        setMovementSpeed(100f);
        setDamage(20);

        setShadowOffsetY(-30);
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

        Chest chest = new Chest(getPosition().x + getWidth() / 2, getPosition().y + getHeight() / 2, poolManager, gameScreen);
        //groundItems.add(chest);
    }
}
