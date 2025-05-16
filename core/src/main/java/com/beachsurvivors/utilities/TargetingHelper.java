package com.beachsurvivors.utilities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.Enemy;

public class TargetingHelper {

    public static Enemy getNearestEnemy(Player player, Array<Enemy> enemies) {

        Enemy nearest = null;
        float minDistance = 1000;

        for (Enemy enemy : enemies) {
            float distance = player.getPosition().dst(enemy.getSprite().getX(), enemy.getSprite().getY());

            if (distance < minDistance) {
                minDistance = distance;
                nearest = enemy;
            }
        }
        return nearest;
    }
}
