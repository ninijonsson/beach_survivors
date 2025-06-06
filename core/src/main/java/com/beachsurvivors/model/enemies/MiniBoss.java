package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.abilities.Ability;
import com.beachsurvivors.model.abilities.Projectile;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.utilities.ParticleEffectPoolManager;
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
        createAnimation(AssetLoader.get().getTexture("entities/enemies/crocodile2_walk_sheet.png"), 2, 1);
        setMovementSpeed(250f);
        setDamage(20);

        setShadowOffsetY(-30);
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

    public void dropChest(Array<GroundItem> groundItems) {

        Chest chest = new Chest(getPosition().x + getWidth() / 2, getPosition().y + getHeight() / 2, poolManager, gameScreen);
        groundItems.add(chest);
    }
}
