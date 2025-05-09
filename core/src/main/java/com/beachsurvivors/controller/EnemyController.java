package com.beachsurvivors.controller;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.abilities.Ability;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.model.groundItems.Chest;
import com.beachsurvivors.model.groundItems.ExperienceOrb;

public class EnemyController {
    private Array<Enemy> enemies;
    private Array<Ability> enemyAbilities;
    private ExperienceOrb experienceOrb; // Fiende ska droppa -> EXP till spelaren
    private Chest chest;

    public EnemyController() {
        enemies = new Array<>();
    }

    public void logic() {}

    public void attack() {

    }

    // Skillnad på attack och damagePlayer?
    public void damagePlayer() {}

    public void spawn() {}

    public void spawnMiniBoss() {}

    public void updatePosition() {}

    public void selectRandomEnemy() {}

    public void handleEnemyDeath() {}

    public void resolveEnemyCollisions() {}

    // Getters & setters
    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public Vector2 getRandomOffscreenPosition() {
        // TODO: Lägg in kod från GameScreen hit istället
        return new Vector2(1,1);
    }
}
