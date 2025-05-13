package com.beachsurvivors.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.abilities.Ability;
import com.beachsurvivors.model.abilities.Shield;
import com.beachsurvivors.model.enemies.*;
import com.beachsurvivors.model.groundItems.Chest;
import com.beachsurvivors.model.groundItems.ExperienceOrb;
import com.beachsurvivors.model.groundItems.GroundItem;
import com.beachsurvivors.view.GameUI;

import java.util.Random;

public class EnemyController {
    private Array<Enemy> enemies;
    private Player player;
    private Array<Ability> enemyAbilities;
    private ExperienceOrb experienceOrb; // Fiende ska droppa -> EXP till spelaren
    private Chest chest;
    private Controller controller;
    private PlayerController playerController;
    private GameManager gameManager;
    private GameUI gameUI;
    private Queue<Integer> miniBossSchedule = new Queue<>(10);
    private Random random;

    public EnemyController(Controller controller) {
        this.enemies = new Array<>();
        this.enemyAbilities = new Array<>();
        this.controller = controller;
        this.playerController = controller.getPlayerController();
        this.gameManager = controller.getGameManager();
        this.player = controller.getPlayerController().getPlayer();
        this.gameUI = controller.getGameScreen().getGameUI();
        this.random = new Random();
    }

    public void logic() {}

    public void attack() {
        for (Enemy enemy : enemies) {
            enemy.attack(player, enemyAbilities);
        }

        for (Ability ability : enemyAbilities) {
            ability.updatePosition(player.getPlayerX(), player.getPlayerY());
        }
    }

    // Skillnad på attack och damagePlayer?
    public void damagePlayer(double damage) {
        Shield shield = controller.getAbilityController().getShield();

        int shieldStrength = shield.getCurrentShieldStrength();
        double remainingDamage = damage - shieldStrength;

        shield.damageShield(damage);

        if (remainingDamage >= 0) {
            player.takeDamage(remainingDamage);
            gameUI.setHealthBarValue(player.getHealthPoints());
            System.out.println("player HP : " + player.getHealthPoints());
        }

        if (!player.isAlive()) {
            System.out.println("You died");
            controller.getMain().gameOver(controller.getTotalEnemiesKilled(), 1 /*controller.getGameManager().getTotalPlayerDamageDealt()*/,
                    gameUI.getGameTimeSeconds(),
                    player.getDamageTaken(), player.getHealingReceived(),
                    shield.getTotalDamagePrevented());
        }
    }

    public void checkEnemyAbilitiesDamageOnPlayer() {
        for (int i = enemyAbilities.size - 1; i >= 0; i--) {
            if (enemyAbilities.get(i).getHitBox().overlaps(player.getHitBox())) {
                damagePlayer(enemyAbilities.get(i).getDamage());
                enemyAbilities.get(i).dispose();
                enemyAbilities.removeIndex(i);
            }
        }
    }

    public void spawn() {
        float gameTimeSeconds = gameUI.getGameTimeSeconds();
        int interval = (int) (gameTimeSeconds / controller.getSecondsBetweenGrowthRate());
        int maxEnemies = (int) (2 * Math.pow(controller.getGrowthRateOfSpawningEnemies(), interval));
        // Vet ej vad baseEnemies är, ändra 2 till variabel senare

        spawnMiniBoss(gameTimeSeconds);

        if (!(enemies.size < maxEnemies)) {
            return;
        }

        Enemy enemy = selectRandomEnemy();

        Vector2 randomPos = getRandomOffscreenPosition(150);
        enemy.getSprite().setPosition(randomPos.x, randomPos.y);
        enemy.getHitbox().setX(randomPos.x);
        enemy.getHitbox().setY(randomPos.y);

        System.out.println("Enemy added: " + enemy);
        enemies.add(enemy);
    }

    public void spawnMiniBoss(float gameTimeSeconds) {
        if (!(miniBossSchedule.isEmpty()) && miniBossSchedule.first() <= gameTimeSeconds) {
            Enemy miniBoss = new MiniBoss(controller.getPoolManager(), controller.getGameScreen());
            Vector2 randomPos = getRandomOffscreenPosition(miniBoss.getHeight());
            miniBoss.setEnemyPos(randomPos);
            miniBoss.setX(randomPos.x);
            miniBoss.setY(randomPos.y);
            enemies.add(miniBoss);
            miniBossSchedule.removeFirst();
        }
    }

    public void updatePosition() {
        for (int i = enemies.size - 1; i >= 0; i--) { //LOOP THROUGH ALL ENEMIES AND UPDATE RELATED POSITIONS.
            Enemy enemy = enemies.get(i);
            updateMovement(enemy); // MOVE TOWARDS PLAYER
            handleEnemyDeath(enemy, player, i); //IF THEY ARE DEAD
            playerController.checkIfPlayerAbilityHits(enemy); //IF THEY ARE HIT BY THE PLAYER
            playerController.checkIfDamageAgainstPlayer(enemy); //IF THEY DAMAGE THE PLAYER
        }
        checkEnemyAbilitiesDamageOnPlayer();
        resolveEnemyCollisions(enemies);
    }

    public void updateMovement(Enemy enemy) {
        enemy.updateHealthBarPosition();
        enemy.addHealthBar(controller.getGameScreen().getStage());

        float delta = Gdx.graphics.getDeltaTime();
        Vector2 playerPos = new Vector2(player.getPlayerX(), player.getPlayerY());
        Vector2 vector = enemy.moveTowardsPlayer(delta, playerPos, enemy.getEnemyPos());
        enemy.setMovingLeftRight(vector.x);

        //Uppdaterar Spritens X och Y position baserat på riktningen på fiendens vector2 * speed * tid.
        //vector.x/y är riktningen, movementSpeed är hastighet och delta är tid.
        enemy.getSprite().translateX(vector.x * enemy.getMovementSpeed() * delta);
        enemy.getSprite().translateY(vector.y * enemy.getMovementSpeed() * delta);
        enemy.getHitbox().set(enemy.getSprite().getX(), enemy.getSprite().getY(), enemy.getWidth(), enemy.getHeight());
    }

    public Enemy selectRandomEnemy() {
        int enemyChoice = random.nextInt(0, 4);
        Enemy enemy = null;

        switch (enemyChoice) {
            case 0:
                enemy = new Shark();
                break;
            case 1:
                enemy = new NavySeal();
                break;
            case 2:
                enemy = new Crocodile();
                break;
            case 3:
                enemy = new Crab();
                break;
        }
        return enemy;
    }

    public void handleEnemyDeath(Enemy enemy, Player player, int index) {
        if (!enemy.isAlive()) {
            Array<GroundItem> groundItems = controller.getGroundItems();
            ParticleEffectPoolManager poolManager = controller.getPoolManager();

            controller.increaseEnemiesKilledCounter();

            gameUI.updateInfoTable("You gained " + enemy.getExp() + " exp.");

            // Om fienden är en miniboss ska den droppa en kista
            enemy.dropItems(controller.getPowerUps(), poolManager);
            groundItems.add(new ExperienceOrb(enemy.getX(), enemy.getY(), enemy.getExp(), poolManager));
            if (enemy instanceof MiniBoss) {
                ((MiniBoss) enemy).dropChest(groundItems);
            }

            enemies.removeIndex(index); // Ta bort från fiende-arrayen
            enemy.dispose(); // Ta även bort själva bilden på fienden

            // I denna metoden kontrollerar vi även om vi ska levela eller inte
            player.gainExp(enemy.getExp());

            // Uppdatera progress bar (exp)
            gameUI.setProgressBarValue(player.getLevelController().getCurrentExp());
        }
    }

    public void resolveEnemyCollisions(Array<Enemy> enemies) {
        for (int i = 0; i < enemies.size; i++) {
            Enemy e1 = enemies.get(i);
            Circle c1 = e1.getCircle();

            for (int j = i + 1; j < enemies.size; j++) { // i + 1 avoids comparing an enemy with itself
                Enemy e2 = enemies.get(j);
                Circle c2 = e2.getCircle();

                if (c1.overlaps(c2)) {
                    // Calculate push direction
                    // dx, dy is the vector from enemy 1 to enemy 2.
                    float dx = c2.x - c1.x;
                    float dy = c2.y - c1.y;
                    // dist is how far apart their centers are.
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);
                    float minDist = c1.radius + c2.radius;

                    if (dist == 0) dist = 0.01f; // avoid divide by zero

                    float overlap = minDist - dist;

                    // Normalize direction, gives clean push, 0% wonky
                    float nx = dx / dist;
                    float ny = dy / dist;

                    // Push enemies apart
                    float push = overlap / 2f;
                    float e1X = e1.getX();
                    float e1Y = e1.getY();
                    float e2X = e2.getX();
                    float e2Y = e2.getY();
                    e1.setX(e1X -= nx * push);
                    e1.setY(e1Y -= ny * push);
                    e2.setX(e2X += nx * push);
                    e2.setY(e2Y += ny * push);
                }
            }
        }
    }

    // Getters & setters
    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public Vector2 getRandomOffscreenPosition(float margin) {
        FitViewport gameViewport = controller.getGameScreen().getGameViewport();

        float viewWidth = gameViewport.getCamera().viewportWidth;
        float viewHeight = gameViewport.getCamera().viewportHeight;
        float camX = gameViewport.getCamera().position.x;
        float camY = gameViewport.getCamera().position.y;

        int edge = MathUtils.random(3);

        float x, y;

        switch (edge) {
            case 0:
                x = MathUtils.random(camX - viewWidth / 2, camX + viewWidth / 2);
                y = camY + viewHeight / 2 + margin;
                break;
            case 1:
                x = MathUtils.random(camX - viewWidth / 2, camX + viewWidth / 2);
                y = camY - viewHeight / 2 - margin;
                break;
            case 2:
                x = camX - viewWidth / 2 - margin;
                y = MathUtils.random(camY - viewHeight / 2, camY + viewHeight / 2);
                break;
            case 3:
                x = camX + viewWidth / 2 + margin;
                y = MathUtils.random(camY - viewHeight / 2, camY + viewHeight / 2);
                break;
            default:
                x = camX;
                y = camY;
                break;
        }
        return new Vector2(x, y);
    }

    public Array<Ability> getEnemyAbilities() {
        return enemyAbilities;
    }
}
