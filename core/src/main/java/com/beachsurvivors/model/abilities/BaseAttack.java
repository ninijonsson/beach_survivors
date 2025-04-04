package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.beachsurvivors.model.Player;

public class BaseAttack extends Ability{
    private Vector2 direction;
    private Rectangle hitBox;

    public BaseAttack() {
        super("bullet", "entities/bullet.png", AbilityType.ATTACK, 5.0, 1, 16, 16);
        this.direction = new Vector2(0, 0);
        //this.hitBox = new Rectangle(getSprite().getX(), getSprite().getY(), 16, 16);
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
