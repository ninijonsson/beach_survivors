package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.utilities.ParticleEffectPoolManager;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.view.DamageText;

public class BaseAttack extends Ability {
    private Vector2 position;
    private Vector2 direction;
    private Sound fireSound;
    private float projectileSpeed;
    private ParticleEffectPool.PooledEffect trailEffect;
    private float lifetime = 0f;



    //Constructor för default baseattack (spelarens)
    public BaseAttack(ParticleEffectPoolManager poolManager) {
        super("bullet", "entities/abilities/bullet.png", AbilityType.ATTACK, 1.0, 0.7f, 64, 64);
        this.position = new Vector2();
        this.direction = new Vector2();
        this.projectileSpeed = 600f;
        trailEffect = poolManager.obtain("entities/particles/water_trail.p");
        trailEffect.setPosition(position.x, position.y);
        this.fireSound = AssetLoader.get().getSound("entities/abilities/water_gun_fire.wav");
        fireSound.setVolume(fireSound.play(), 0.5f);
    }

    //Constructor för custom base attack (för Navy Seals t.ex.)
    public BaseAttack(String texturePath, int damage, float projectileSpeed) {
        super("bullet", texturePath, AbilityType.ATTACK, damage, 1, 32, 32);
        this.projectileSpeed = projectileSpeed;
        this.position = new Vector2();
        this.direction = new Vector2();

    }

    public Vector2 getDirection() {
        return direction;
    }

    public void setDirection(Vector2 direction) {
        this.direction.set(direction);
    }

    @Override
    public void use(float delta, Player player, Array<Enemy> enemies, Array<Ability> abilities, Array<DamageText> damageTexts) {

    }

    public boolean hasExpired() {
        return lifetime >= 5f;
    }

    @Override
    public void updatePosition(float delta, Vector2 vector) {
        lifetime += delta;

        Vector2 movement = direction.cpy().scl(projectileSpeed * delta);
        this.position.add(movement);

        getSprite().setPosition(position.x, position.y);
        getHitBox().setPosition(position.x, position.y);

        getSprite().setOriginCenter();
        getSprite().setRotation(direction.angleDeg() + 90);
        if (trailEffect != null) {
            trailEffect.setPosition(position.x + getWidth() / 2f, position.y + getHeight() / 2f);

            trailEffect.update(delta);
        }

    }

    public void setPosition(Vector2 startPosition) {
        this.position = startPosition;
        getSprite().setPosition(position.x, position.y);
        getHitBox().setPosition(position.x, position.y);
    }

    public void drawTrail(SpriteBatch batch) {
        if (trailEffect != null) trailEffect.draw(batch);
    }

    public Vector2 getPosition() {
        return position;
    }


    public float getProjectileSpeed() {
        return projectileSpeed;
    }

    @Override
    public void dispose() {
        if (trailEffect != null) {
            trailEffect.free();
            trailEffect = null;
        }
        super.dispose();

    }

}
