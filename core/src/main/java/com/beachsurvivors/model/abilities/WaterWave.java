package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.Enemy;

public class WaterWave extends Ability {

    private ParticleEffect effect;
    private Vector2 position;
    private Vector2 direction;
    private Vector2 startPosition;
    private float maxDistance = 1500f;
    private float speed = 500f;
    private float scaleFactor = 1f;
    private float waveAmplitude = 10f;
    private float waveFrequency = 10f;
    private float totalDistanceTraveled = 0f;

    public WaterWave(String name, double damage, double cooldown, int width, int height,
                     Vector2 startPosition, ParticleEffectPoolManager poolManager) {
        super(name, "entities/particles/bullet.png", AbilityType.ATTACK, damage, cooldown, width, height);
        this.position = new Vector2(startPosition);
        this.startPosition = new Vector2(startPosition);
        this.effect = poolManager.obtain("entities/particles/blueFlame.p", position.x, position.y);
        effect.scaleEffect(1.0f);
    }

    public void setDirection(Vector2 dir) {
        this.direction = new Vector2(dir).nor();
    }

    public void update(float delta) {
        if (effect == null || direction == null) return;

        float dx = direction.x * speed * delta;
        float dy = direction.y * speed * delta;

        totalDistanceTraveled += Math.sqrt(dx * dx + dy * dy);

        float offset = (float)Math.sin(totalDistanceTraveled / waveFrequency) * waveAmplitude;
        Vector2 normal = new Vector2(-direction.y, direction.x);

        position.add(dx, dy);
        position.add(normal.scl(offset * delta));

        effect.setPosition(position.x, position.y);

        effect.update(delta);

        float distance = position.dst(startPosition);
        scaleFactor = scaleFactor + (distance / maxDistance) * 0.5f;

        if (distance >= maxDistance) {
            effect.allowCompletion();
        }
    }

    public void draw(SpriteBatch batch) {
        if (effect != null) {
            effect.draw(batch);
        }
    }

    public boolean isComplete() {
        return effect == null || effect.isComplete();
    }

    @Override
    public void use(float delta, Player player, Array<Enemy> enemies, Array<Ability> abilities) {

    }

    @Override
    public void dispose() {
        if (effect != null) {
            effect.dispose();
        }
    }
}
