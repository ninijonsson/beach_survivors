package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.abilities.BaseAttack;

public class NavySeal extends Enemy {

    float bulletTimer = 0f;


    public NavySeal() {
        super("", 100, 100);
        createAnimation("entities/enemies/Seal rocket launcher-Sheet.png", 4, 1);
        setHealthPoints(30);
        setHitSound(Gdx.audio.newSound(Gdx.files.internal("sounds/Seal_Damage.wav")));
        setMovementSpeed(40f);
    }

    @Override
    public void move() {
    }

    public void attack(Player player, Array enemyAbilities) {
        float bulletCooldown = 3f; // Gör om cooldown till float

        bulletTimer += Gdx.graphics.getDeltaTime();

        if (bulletTimer >= bulletCooldown) {
            bulletTimer = 0f;

            Vector2 direction = new Vector2(
                player.getPlayerX() - this.getSprite().getX(),
                player.getPlayerY() - this.getSprite().getY())
                .nor();

            BaseAttack bullet = new BaseAttack("entities/abilities/fireball.png");
            bullet.updatePosition(this.getSprite().getX(), this.getSprite().getY());
            bullet.setDirection(direction);

            enemyAbilities.add(bullet);

        }
    }

    @Override
    public void dropItems() {
    }
}
