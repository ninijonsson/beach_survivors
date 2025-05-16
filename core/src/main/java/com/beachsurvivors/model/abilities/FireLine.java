package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.Enemy;

public class FireLine extends Ability {
    private final Player player;
    private ParticleEffectPoolManager poolManager;
    private Vector2 direction;
    private Array<ParticleEffectPool.PooledEffect> fireEffects = new Array<>();
    private float segmentSpacing = 40f;
    private float lifetime = 1f;
    private float timeAlive = 0f;


    public FireLine(Player player, ParticleEffectPoolManager poolManager) {

        super("Fire Line", "texturePath", AbilityType.ATTACK, 1, 5, 50, 50);
        this.direction = new Vector2(0, 0);
        this.player = player;
        this.poolManager = poolManager;

        fire();
    }

    public void setDirection(Vector2 direction) { this.direction = direction; }

    public void updatePosition(float delta, float playerX, float playerY) {
        float speed = 600f;
        float newX = getSprite().getX() + direction.x * speed * delta;
        float newY = getSprite().getY() + direction.y * speed * delta;
        getSprite().setPosition(newX, newY);
        getHitBox().setPosition(newX, newY);

        getSprite().setOriginCenter();
        getSprite().setRotation(direction.angleDeg() + 90);
    }


    public void fire() {
        fireEffects.clear();
        timeAlive = 0f;

        Vector2 start = new Vector2(player.getX(), player.getY());
        Vector2 step = new Vector2(direction).nor().scl(segmentSpacing);

        for (int i = 0; i < 5; i++) {
            Vector2 pos = new Vector2(start).add(step.x * i, step.y * i);
            ParticleEffectPool.PooledEffect effect = poolManager.obtain("fire");
            effect.setPosition(pos.x, pos.y);
            fireEffects.add(effect);
        }
    }

    @Override
    public void use(float delta, Player player, Array<Enemy> enemies, Array<Ability> abilities) {

    }
}
