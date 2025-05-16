package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.abilities.Ability;
import com.beachsurvivors.model.abilities.BaseAttack;

public class NavySeal extends Enemy {
    private float attackTimer = 0f;
    private float attackCooldown = 5f;

    public NavySeal() {
        super( 100, 100, 20, 25);
        createAnimation(AssetLoader.get().getTexture("entities/enemies/navy_seal_sheet.png"), 4, 1);
        setHealthPoints(30);
        setHitSound(AssetLoader.get().getSound("sounds/shark_damage_2.wav"));
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

        attackTimer += Gdx.graphics.getDeltaTime();

        if (attackTimer >= attackCooldown) {
            attackTimer = 0f;

            Vector2 direction = new Vector2(
                player.getPosition().x - this.getPosition().x,
                player.getPosition().y - this.getPosition().y)
                .nor();

            BaseAttack bullet = new BaseAttack("entities/abilities/fireball.png", getDamage(), 400f);
            bullet.setDirection(direction);
            //bullet.updatePosition(Gdx.graphics.getDeltaTime(), this.getPosition());
            bullet.setPosition(getPosition().cpy());

            enemyAbilities.add(bullet);
            System.out.println("bullet added");

        }
    }




}
