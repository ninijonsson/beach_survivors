package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.view.DamageText;

public class WaterWave extends Ability {

    private ParticleEffect effect;
    private Vector2 position;
    private Vector2 direction;
    private Vector2 startPosition;
    private float maxDistance = 3500f;
    private float speed = 750f;
    private float scaleFactor = 1f;
    private float waveAmplitude = 10f;
    private float waveFrequency = 10f;
    private float totalDistanceTraveled = 0f;
    private Texture crosshairTexture;
    private Vector2 crosshairPosition;
    private float crosshairDistance = 180f;
    private final float damageInterval = 0.7f; // sekunder mellan träffar per fiende
    private final ObjectMap<Enemy, Float> damageCooldowns = new ObjectMap<>();
    private double damage = 15;


    public WaterWave(String name, double damage, float cooldown, int width, int height,
                     Vector2 startPosition, ParticleEffectPoolManager poolManager) {
        super(name, "entities/particles/bullet.png", AbilityType.ATTACK, damage, cooldown, width, height);
        this.position = new Vector2(startPosition);
        this.startPosition = new Vector2(startPosition);
        this.effect = poolManager.obtain("entities/particles/blueFlame.p", position.x, position.y);
        effect.scaleEffect(1.0f);

        crosshairTexture = AssetLoader.get().manager.get("entities/coconut.png", Texture.class);
        crosshairPosition = new Vector2(startPosition); // placeholder tills update körs


    }

    public void setDirection(Vector2 dir) {
        this.direction = new Vector2(dir).nor();
        if (crosshairPosition == null) {
            crosshairPosition = new Vector2();
        }
        crosshairPosition.set(position).add(direction.cpy().scl(crosshairDistance));
    }


    public void update(float delta) {
        for (Enemy e : damageCooldowns.keys()) {
            float time = damageCooldowns.get(e) - delta;
            damageCooldowns.put(e, time);
        }

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
        if (crosshairTexture != null && crosshairPosition != null) {
            batch.draw(crosshairTexture, crosshairPosition.x - 16, crosshairPosition.y - 16, 32, 32);
        }
    }

    public boolean isComplete() {
        return effect == null || effect.isComplete();
    }

    @Override
    public void update(float delta, Player player, Array<Enemy> enemies, Array<Ability> abilities) {
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

        // ✅ Uppdatera crosshairPosition baserat på spelarens senaste direction
        Vector2 dir = player.getLastDirection();
        if (!dir.isZero()) {
            crosshairPosition.set(player.getPosition()).add(dir.cpy().scl(crosshairDistance));
        } else {
            crosshairPosition.set(player.getPosition()); // fallback mitt på spelaren
        }
    }

    @Override
    public void use(float delta, Player player, Array<Enemy> enemies, Array<Ability> abilities, Array<DamageText> damageTexts) {
        update(delta); // uppdatera position och animation

        for (Enemy e : damageCooldowns.keys()) {
            float newTime = damageCooldowns.get(e) - delta;
            damageCooldowns.put(e, newTime);
        }

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;

            float distance = position.dst(enemy.getCenter());

            if (distance < 300f) { // RADIUS PÅ SKADAN
                float cooldownLeft = damageCooldowns.get(enemy, 0f);
                if (cooldownLeft <= 0f) {
                    double totalDamage = damage;
                    boolean isCrit = player.isCriticalHit();
                    if (isCrit) totalDamage *= player.getCriticalHitDamage();

                    boolean damaged = enemy.hit(totalDamage);
                    if (damaged) {
                        damageTexts.add(new DamageText((int) totalDamage, enemy.getCenter(), isCrit));
                        player.onDamageDealt(totalDamage);
                        damageCooldowns.put(enemy, damageInterval);
                    }
                }
            }
        }
    }



    @Override
    public void dispose() {
        if (effect != null) {
            effect.dispose();
        }
    }
}
