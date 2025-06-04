package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.utilities.CombatHelper;
import com.beachsurvivors.utilities.ParticleEffectPoolManager;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.view.DamageText;

public class BaseAttack extends Ability {

    private Sound fireSound;
    private float projectileSpeed = 600;

    public BaseAttack(ParticleEffectPoolManager poolManager) {
        super("bullet", "entities/abilities/bullet.png", AbilityType.ATTACK, 1.0, 1f, 64, 64);

        setPersistent(true);
        this.fireSound = AssetLoader.get().getSound("entities/abilities/water_gun_fire.wav");
    }

    @Override
    public void use(float delta, Player player, Array<Enemy> enemies, Array<Ability> abilities,
                    Array<DamageText> damageTexts, Array<Projectile> playerProjectiles) {

        Enemy target = CombatHelper.getNearestEnemy(player, enemies);

        if (target != null) {
            Vector2 targetCenter = target.getCenter();
            Vector2 direction = new Vector2(targetCenter.x - player.getPosition().x,
                targetCenter.y - player.getPosition().y).nor();

            Projectile projectile = new Projectile("entities/abilities/bullet.png",
                player.getDamage(), projectileSpeed, 64, 64);
            projectile.setDirection(direction);
            projectile.setPosition(player.getPosition().cpy());

            playerProjectiles.add(projectile);
            fireSound.setVolume(fireSound.play(), 0.5f);

        }
        setOffCooldown(false);
    }

    @Override
    public void updatePosition(float delta, Vector2 vector) {
    }

    public float getProjectileSpeed() {
        return projectileSpeed;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}
