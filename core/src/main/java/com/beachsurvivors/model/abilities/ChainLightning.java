package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.Enemy;

import java.util.HashSet;
import java.util.Set;

public class ChainLightning extends Ability {

    private Array<Enemy> enemies;
    private int maxJumps;
    private float jumpRadius;
    private Array<Vector2> hitPositions = new Array<>();

    private boolean showLightning;
    private float lightningVisibleTime;
    private double chainLightningTimer;


    public ChainLightning(Array<Enemy> enemies) {
        super("ChainLightning", "entities/abilities/lightning.png", AbilityType.ATTACK, 10, 7, 32, 32);
        maxJumps = 5;
        jumpRadius = 500;
        this.enemies = enemies;
        chainLightningTimer = getCooldown();
        lightningVisibleTime = 0.5f;

    }

    public void cast(Enemy enemy, float delta) {
        chainLightningTimer += delta;  //Timer går upp med tiden

        if (chainLightningTimer >= getCooldown()) { //När timer är högre än cooldown, casta chain lightning
            chainLightningTimer = 0;
            showLightning = true;
            lightningVisibleTime = 0.5f;

            hitPositions.clear();

            Enemy current = enemy;
            if (current != null) {
                playSoundEffect();

            }
            Set<Enemy> alreadyHitEnemies = new HashSet<>();

            for (int i = 0; i < maxJumps && current != null; i++) { //om current == null så avbryter den (finns ingen nearby enemy)
                current.hit(getDamage());

                alreadyHitEnemies.add(current);
                hitPositions.add(current.getEnemyPos());
                current = getNextTarget(current, alreadyHitEnemies);
            }
        }
    }

    //Uppdateras varje render ifall lightning effekten ska synas eller inte
    public void update(float delta) {
        if (showLightning) {
            lightningVisibleTime -= delta;
            if (lightningVisibleTime <= 0) {
                showLightning = false;
                hitPositions.clear();
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

            float distance = enemies.get(i).getEnemyPos().dst(previous.getEnemyPos());
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
    }

    private void playSoundEffect() {
        Sound sound = AssetLoader.get().getSound("assets/sounds/chain_lightning.wav");
        long soundId = sound.play();
        sound.setVolume(soundId, 0.3f);
    }

    @Override
    public void use() {

    }

    @Override
    public void use(Player player) {

    }

}
