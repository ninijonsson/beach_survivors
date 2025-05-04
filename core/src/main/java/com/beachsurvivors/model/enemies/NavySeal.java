package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.abilities.Ability;
import com.beachsurvivors.model.abilities.BaseAttack;

public class NavySeal extends Enemy {
    float bulletTimer = 0f;

    public NavySeal() {
        super( 100, 100, 20, 25);
        createAnimation(AssetLoader.get().getTexture("assets/entities/enemies/navy_seal_sheet.png"), 4, 1);
        setHealthPoints(30);
        setHitSound(AssetLoader.get().getSound("assets/sounds/Seal_Damage.wav"));
        setMovementSpeed(40f);
        setDamage(10);
    }

    @Override
    public void move() {
    }

    @Override
    public void dropItems() {
    }

    @Override
    public void dispose() {

    }

    public void attack(Player player, Array<Ability> enemyAbilities) {
        float bulletCooldown = 3f; // Gör om cooldown till float

        bulletTimer += Gdx.graphics.getDeltaTime();

        if (bulletTimer >= bulletCooldown) {
            bulletTimer = 0f;

            Vector2 direction = new Vector2(
                player.getPlayerX() - this.getSprite().getX(),
                player.getPlayerY() - this.getSprite().getY())
                .nor();

            BaseAttack bullet = new BaseAttack("assets/entities/abilities/fireball.png", getDamage());
            bullet.updatePosition(this.getSprite().getX(), this.getSprite().getY());
            bullet.setDirection(direction);

            enemyAbilities.add(bullet);

        }
    }


}
