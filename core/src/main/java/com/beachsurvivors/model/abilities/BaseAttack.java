package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.beachsurvivors.model.Player;

public class BaseAttack extends Ability {
    private Vector2 direction;
    private Rectangle hitBox;

    public BaseAttack() {
        super("bullet", "assets/entities/abilities/bullet.png", AbilityType.ATTACK, 5.0, 1, 32, 32);
        this.direction = new Vector2(0, 0);
    }



    public BaseAttack(String texturePath, int damage) {
        super("bullet", texturePath, AbilityType.ATTACK, damage, 1, 32, 32);

    }

    public Vector2 getDirection() { return direction; }

    public void setDirection(Vector2 direction) { this.direction = direction; }

    @Override
    public void updatePosition(float delta, float playerX, float playerY) {
        float speed = 600f;
        float newX = getSprite().getX() + direction.x * speed * delta;
        float newY = getSprite().getY() + direction.y * speed * delta;
        getSprite().setPosition(newX, newY);
        getHitBox().setPosition(newX, newY);
    }

    @Override
    public void use() {

    }

    @Override
    public void use(Player player) {

    }
}
