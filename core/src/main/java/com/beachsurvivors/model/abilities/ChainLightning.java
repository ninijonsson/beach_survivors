package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
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
    private float chainLightningTimer;


    public ChainLightning(Array<Enemy> enemies) {
        super("ChainLightning", "assets/placeholder.png", AbilityType.ATTACK, 10, 7, 32, 32);
        maxJumps = 5;
        jumpRadius = 500;
        this.enemies = enemies;

    }

    public void cast(Enemy enemy) {
        Set<Enemy> alreadyHitEnemies = new HashSet<>();
        Enemy current = enemy;

        for (int i = 0; i < maxJumps && current != null; i++) { //om current == null så avbryter den (finns ingen nearby enemy)
            current.hit(getDamage());
            alreadyHitEnemies.add(current);
            hitPositions.add(current.getEnemyPos());
            current = getNextTarget(current, alreadyHitEnemies);
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

    public void draw(ShapeRenderer shapeRenderer, Vector2 playerPos) {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLUE);

        if (hitPositions.size > 0 ) {
            shapeRenderer.line(playerPos, hitPositions.get(0));

        }
        for (int i = 0; i < hitPositions.size-1; i++) {
            Vector2 from = hitPositions.get(i);
            Vector2 to = hitPositions.get(i+1);
            shapeRenderer.line(from, to);
        }
        shapeRenderer.end();
    }


    @Override
    public void use() {

    }

    @Override
    public void use(Player player) {

    }


    public Array<Vector2> getHitPositions() {
        return hitPositions;
    }

    public void clearArray() {
        hitPositions.clear();
    }

}
