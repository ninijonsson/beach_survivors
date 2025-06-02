package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.utilities.ParticleEffectPoolManager;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.utilities.CombatHelper;
import com.beachsurvivors.view.DamageText;

import java.util.HashSet;
import java.util.Set;

public class ChainLightning extends Ability {

    private final ParticleEffectPoolManager poolManager;
    private Array<Enemy> enemies;
    private int maxJumps;
    private float jumpRadius;
    private Array<Vector2> hitPositions = new Array<>();
    private final Array<ParticleEffectPool.PooledEffect> glowEffects = new Array<>();

    private boolean showLightning;
    private float lightningVisibleTime; //Hur länge lightning texturen visas efter den gjort damage

    public ChainLightning(Array<Enemy> enemies, ParticleEffectPoolManager poolManager) {
        super("ChainLightning", "entities/abilities/lightning.png", AbilityType.ATTACK,
            2, 8, 32, 32);
        maxJumps = 10;
        jumpRadius = 800;
        this.enemies = enemies;
        lightningVisibleTime = 0f;
        setIcon("entities/abilities/lightning.png");
        this.poolManager = poolManager;

    }

    @Override
    public void use(float delta, Player player, Array<Enemy> enemies, Array<Ability> abilities, Array<DamageText> damageTexts, Array<Projectile> playerProjectiles) {

        showLightning = true;
        lightningVisibleTime = 0.5f;

        for (ParticleEffectPool.PooledEffect effect : glowEffects) {
            effect.free();
        }
        glowEffects.clear();

        hitPositions.clear();

        Enemy current = CombatHelper.getNearestEnemy(player, enemies);
        if (current == null) return;

        Set<Enemy> alreadyHitEnemies = new HashSet<>();
        playSoundEffect();

        for (int i = 0; i < maxJumps && current != null; i++) { //om current == null så avbryter den (finns ingen nearby enemy)
            double damage = getDamageMultiplier() * player.getDamage();
            current.hit(damage);
            damageTexts.add(new DamageText((damage+""), current.getPosition().x+current.getWidth()/2f,
                current.getPosition().y+current.getHeight()+25, 1, player.isCriticalHit()));

            alreadyHitEnemies.add(current);
            Vector2 targetCenter = current.getCenter();
            hitPositions.add(targetCenter);
            current = getNextTarget(current, alreadyHitEnemies);
        }

        Vector2 start = player.getPosition();
        for (int i = 0; i < hitPositions.size; i++) {
            Vector2 end = hitPositions.get(i);
            int steps = (int)(start.dst(end) / 60f); // Lägg t.ex. var 60 pixlar
            for (int j = 1; j <= steps; j++) {
                Vector2 pos = new Vector2(start).lerp(end, j / (float)steps);
                ParticleEffectPool.PooledEffect effect = poolManager.obtain("entities/particles/electric_trail.p");
                effect.setPosition(pos.x, pos.y);
                glowEffects.add(effect);
            }
            start = end;
        }

    }


    //Uppdateras varje render ifall lightning effekten ska synas eller inte
    @Override
    public void update(float delta, Player player, Array<Enemy> enemies, Array<Ability> abilities) {
        super.update(delta, player, enemies, abilities);

        if (showLightning) {
            lightningVisibleTime -= delta;
            if (lightningVisibleTime <= 0) {
                showLightning = false;
                hitPositions.clear();

            }
        }

        for (int i = glowEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = glowEffects.get(i);
            effect.update(delta);
            if (effect.isComplete()) {
                effect.free();
                glowEffects.removeIndex(i);
            }
        }
    }

    private Enemy getNextTarget(Enemy previous, Set<Enemy> hitEnemies) {
        Enemy closest = null;
        float minDistance = Float.MAX_VALUE;

        for (int i = 0; i < enemies.size; i++) {
            if (hitEnemies.contains(enemies.get(i))) {
                continue;
            }

            float distance = enemies.get(i).getPosition().dst(previous.getPosition());
            if (distance <= jumpRadius && distance < minDistance) { //om distance är innanför jump radius
                minDistance = distance;  //och om distance < minDistance så blir den nya enemy närmre "closest"
                closest = enemies.get(i);
            }
        }

        return closest;
    }

    public void drawLightning(SpriteBatch spriteBatch, Vector2 playerPos) {

        if (!showLightning || hitPositions.size == 0) return;

        float deltaX = hitPositions.get(0).x - playerPos.x;
        float deltaY = hitPositions.get(0).y - playerPos.y;
        float length = playerPos.dst(hitPositions.get(0));
        float angle = MathUtils.atan2(deltaY, deltaX) * MathUtils.radiansToDegrees;

        spriteBatch.draw(getTexture(), playerPos.x, playerPos.y,
            0,0,
            length, getTexture().getHeight(),
            1f,1f,
            angle,
            0,0,
            getTexture().getWidth(), getTexture().getHeight(),
            false, false);

        for (int i = 0; i < hitPositions.size-1; i++) {
            deltaX = hitPositions.get(i+1).x - hitPositions.get(i).x;
            deltaY = hitPositions.get(i+1).y - hitPositions.get(i).y;
            length = hitPositions.get(i).dst(hitPositions.get(i+1));
            angle = MathUtils.atan2(deltaY, deltaX) * MathUtils.radiansToDegrees;

            spriteBatch.draw(getTexture(), hitPositions.get(i).x, hitPositions.get(i).y,
                0,0,
                length, getTexture().getHeight(),
                1f,1f,
                angle,
                0,0,
                getTexture().getWidth(), getTexture().getHeight(),
                false, false);
        }
        for (ParticleEffectPool.PooledEffect effect : glowEffects) {
            effect.draw(spriteBatch);
        }
    }

    private void playSoundEffect() {
        Sound sound = AssetLoader.get().getSound("sounds/chain_lightning.wav");
        long soundId = sound.play();
        sound.setVolume(soundId, 0.1f);
    }

    public void increaseMaxJumps(int jumpIncrease) {
        maxJumps += jumpIncrease;
    }

}
