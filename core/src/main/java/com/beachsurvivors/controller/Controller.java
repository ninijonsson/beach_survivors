package com.beachsurvivors.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.beachsurvivors.model.Map.Map;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.abilities.*;
import com.beachsurvivors.model.enemies.*;
import com.beachsurvivors.model.groundItems.*;
import com.beachsurvivors.view.DamageText;
import com.beachsurvivors.view.GameScreen;
import com.beachsurvivors.view.GameUI;
import com.beachsurvivors.view.Main;

import static com.badlogic.gdx.math.MathUtils.random;

public class Controller extends Game implements Screen {
    // Abilities
    private Array<Ability> abilities = new Array<>();
    private Array<Ability> enemyAbilities = new Array<>();
    private Boomerang boomerang;
    private BaseAttack bullet;
    private Shield shield;

    private Array<Enemy> enemies = new Array<>();
    private Array<GroundItem> groundItems = new Array<>();
    private Array<GroundItem> groundItemsToRemove = new Array<>();
    private Array<PowerUp> powerUps = new Array<>();
    private Array<PowerUp> powerUpsToRemove = new Array<>();
    private Queue<Integer> miniBossSchedule = new Queue<>();

    private Main main;
    private ParticleEffectPoolManager poolManager;
    private GameScreen gameScreen;
    private GameUI gameUI;
    private FitViewport gameViewport;
    private Map map;
    private Player player;
    private Vector2 playerPos;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    private int totalEnemiesKilled;
    private int totalPlayersKilled = 0; // Flytta till Player
    private int totalPlayerDamageDealt = 0;
    private float growthRateOfSpawningEnemies = 1.5f;
    private int secondsBetweenGrowthRate = 10;
    private float bulletTimer;
    private boolean isPaused;

    private int currentLevel;
    private int currentEXP;
    private int expToNextLevel;

    public Controller(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.gameViewport = gameScreen.getGameViewport();
        this.main = gameScreen.getMain();

        this.map = createMap();
        this.player = new Player(gameScreen.getSpriteBatch(), this);
        this.player.setPlayerX(map.getStartingX());
        this.player.setPlayerY(map.getStartingY());
        this.playerPos = new Vector2(player.getPlayerX(), player.getPlayerY());

        this.boomerang = new Boomerang();
        this.bullet = new BaseAttack();
        this.shield = new Shield();
        abilities.add(boomerang);
        abilities.add(bullet);
        abilities.add(shield);

        this.poolManager = new ParticleEffectPoolManager();
        this.poolManager.register("entities/particles/blueFlame.p", 5, 20);
        this.poolManager.register("entities/particles/lootBeam.p", 5, 20);
        this.poolManager.register("entities/particles/lootPile.p", 5, 20);
        this.poolManager.register("entities/particles/xp_orb.p", 5, 20);
        this.poolManager.register("entities/particles/chestClosed.p", 5, 20);
        this.poolManager.register("entities/particles/chestOpen.p", 5, 20);

        this.currentLevel = 1;
        this.currentEXP = 0;
        this.expToNextLevel = calculateExpForLevelUp(currentLevel);

        create();
    }

    @Override
    public void resume() {
        setPaused(false);
        main.setScreen(gameScreen);
        Timer.instance().start();
    }

    private int calculateExpForLevelUp(int level) {
        return 100 * level;
    }

    private void onLevelUp() {
        System.out.println("Level up!");
        gameUI.updateInfoTable("Congratulations, you are now level " + currentLevel);
        main.levelUp();
    }

    public void gainExp(int exp) {
        currentEXP += exp;
        while (currentEXP >= expToNextLevel) {
            currentEXP -= expToNextLevel;
            currentLevel++;
            expToNextLevel = calculateExpForLevelUp(currentLevel);
            onLevelUp();
        }
    }

    public void create() {
        // Any additional setup
    }

    public void logic() {
        updateCameraPosition();
        // gameUI.updateStats(player);

        playerShoot();
        updateShieldPosition();
        updateAbilities();

        spawnEnemies();
        for (int i = enemies.size - 1; i >= 0; i--) { //LOOP THROUGH ALL ENEMIES AND UPDATE RELATED POSITIONS.
            Enemy enemy = enemies.get(i);
            updateEnemyMovement(enemy); //MOVE TOWARDS PLAYER
            handleEnemyDeaths(enemy, i); //IF THEY ARE DEAD
            checkPlayerAbilityHits(enemy); //IF THEY ARE HIT BY THE PLAYER
            checkDamageAgainstPlayer(enemy); //IF THEY DAMAGE THE PLAYER
        }
        checkEnemyAbilitiesDamagePlayer();

        enemyAttacks();
        updateEnemyPositions();

        pickUpPowerUp();
        pickUpGroundItem();

        gameUI.updateDamageText();
    }

    /**
     * Updates the position of an enemy
     *
     * @param enemy - the enemy that's updated
     */
    private void updateEnemyMovement(Enemy enemy) {
        enemy.updateHealthBarPosition();
        enemy.addHealthBar();

        float delta = Gdx.graphics.getDeltaTime();
        playerPos.set(player.getPlayerX(), player.getPlayerY());
        Vector2 vector = enemy.moveTowardsPlayer(delta, playerPos, enemy.getEnemyPos());
        enemy.setMovingLeftRight(vector.x);

        //Uppdaterar Spritens X och Y position baserat på riktningen på fiendens vector2 * speed * tid.
        //vector.x/y är riktningen, movementSpeed är hastighet och delta är tid.
        enemy.getSprite().translateX(vector.x * enemy.getMovementSpeed() * delta);
        enemy.getSprite().translateY(vector.y * enemy.getMovementSpeed() * delta);
        enemy.getHitbox().set(enemy.getSprite().getX(), enemy.getSprite().getY(), enemy.getWidth(), enemy.getHeight());
    }

    /**
     * What happens when an enemy dies
     *
     * @param enemy - the enemy that is handled
     * @param i     - index for removing the correct enemy in the array
     */
    private void handleEnemyDeaths(Enemy enemy, int i) {
        if (!enemy.isAlive()) {
            totalEnemiesKilled++;
            gameUI.updateInfoTable("You gained " + enemy.getExp() + " exp.");

            // Om fienden är en miniboss ska den droppa en kista
            enemy.dropItems(powerUps, poolManager);
            groundItems.add(new ExperienceOrb(enemy.getX(), enemy.getY(), enemy.getExp(), poolManager, this));
            if (enemy instanceof MiniBoss) {
                ((MiniBoss) enemy).dropChest(groundItems);
            }

            enemies.removeIndex(i); // Ta bort från fiende-arrayen
            enemy.dispose(); // Ta även bort själva bilden på fienden

            // I denna metoden kontrollerar vi även om vi ska levela eller inte
            player.gainExp(enemy.getExp());

            // Uppdatera progress bar (exp)
            gameUI.setProgressBarValue(getCurrentEXP());
        }
    }

    /**
     * Checks if the player's abilities hitboxes overlaps with any of the enemies
     * hitboxes
     *
     * @param enemy
     */
    private void checkPlayerAbilityHits(Enemy enemy) {
        for (int j = abilities.size - 1; j >= 0; j--) {
            Ability ability = abilities.get(j);

            if (ability.getHitBox().overlaps(enemy.getHitbox())) {
                boolean isCritical = player.isCriticalHit();
                double damage = ability.getBaseDamage();
                if (isCritical) {
                    damage *= player.getCriticalHitDamage();
                }

                if (enemy.hit(damage)) {
                    totalPlayerDamageDealt += damage;
                    gameUI.getDamageTexts().add(new DamageText(String.valueOf((int) damage),
                        enemy.getSprite().getX() + random.nextInt(50),
                        enemy.getSprite().getY() + enemy.getSprite().getHeight() + 10 + random.nextInt(50),
                        1.0f,
                        isCritical));
                }

                if (!ability.isPersistent()) {
                    ability.dispose();
                    abilities.removeIndex(j);
                }
            }
        }
    }

    private void checkDamageAgainstPlayer(Enemy enemy) {
        if (enemy.getHitbox().overlaps(player.getHitBox())) {
            damagePlayer(enemy.getDamage());
        }
    }

    private void checkEnemyAbilitiesDamagePlayer() {
        for (int i = enemyAbilities.size - 1; i >= 0; i--) {
            if (enemyAbilities.get(i).getHitBox().overlaps(player.getHitBox())) {
                damagePlayer(enemyAbilities.get(i).getDamage());
                enemyAbilities.get(i).dispose();
                enemyAbilities.removeIndex(i);
            }
        }
    }


    private void damagePlayer(double damage) {
        int shieldStrength = shield.getCurrentShieldStrength();
        double remainingDamage = damage - shieldStrength;

        shield.damageShield(damage);

        if (remainingDamage >= 0) {
            player.takeDamage(remainingDamage);
            gameUI.setHealthBarValue(player.getCurrentHealthPoints(), player.getMaxHealthPoints());
            System.out.println("player HP : " + player.getCurrentHealthPoints());
        }

        if (!player.isAlive()) {
            System.out.println("You died");
            main.gameOver(totalEnemiesKilled, totalPlayerDamageDealt, gameUI.getGameTimeSeconds(),
                player.getDamageTaken(), player.getHealingReceived(), shield.getTotalDamagePrevented());
        }
    }

    public void input() {
        if (!isPaused) {
            player.playerInput();
        }

        keyBinds();
    }

    private void keyBinds() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            main.pause();
            pause();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB) || Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            // gameUI.showStatsTable();
        }
    }

    private void updateCameraPosition() {
        gameViewport.getCamera().position.set(player.getPlayerX(), player.getPlayerY(), 0);
        gameViewport.getCamera().update();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        gameViewport.apply();
    }

    private void updateAbilities() {
        for (Ability a : abilities) {
            a.updatePosition(player.getPlayerX(), player.getPlayerY());
        }
    }

    private void updateShieldPosition() {
        if (!shield.getIsDepleted() && shield.getSprite() != null) {
            float playerCenterX = player.getPlayerX();
            float playerCenterY = player.getPlayerY();

            float shieldWidth = shield.getSprite().getWidth();
            float shieldHeight = shield.getSprite().getHeight();

            shield.updatePosition(
                playerCenterX - shieldWidth / 2f,
                playerCenterY - shieldHeight / 2f
            );
        }
    }

    private void spawnEnemies() {
        float gameTimeSeconds = gameUI.getGameTimeSeconds();
        int interval = (int) (gameTimeSeconds / secondsBetweenGrowthRate);
        int maxEnemies = (int) (2 * Math.pow(growthRateOfSpawningEnemies, interval));

        if (enemies.size >= maxEnemies) return;

        Enemy enemy = selectRandomEnemy();
        Vector2 pos = getRandomOffscreenPosition(150);
        enemy.getSprite().setPosition(pos.x, pos.y);
        enemy.getHitbox().setPosition(pos);
        enemies.add(enemy);
    }

    private void updateEnemyPositions() {
        float delta = Gdx.graphics.getDeltaTime();
        Vector2 target = new Vector2(player.getPlayerX(), player.getPlayerY());
        for (Enemy e : enemies) {
            Vector2 move = e.moveTowardsPlayer(delta, target, e.getEnemyPos());
            e.getSprite().translate(move.x * e.getMovementSpeed() * delta, move.y * e.getMovementSpeed() * delta);
            e.getHitbox().setPosition(e.getSprite().getX(), e.getSprite().getY());
        }
    }

    private Enemy selectRandomEnemy() {
        switch (random(3)) {
            case 0: return new Shark(this);
            case 1: return new NavySeal(this);
            case 2: return new Crocodile(this);
            default: return new Crab(this);
        }
    }

    private Vector2 getRandomOffscreenPosition(float margin) {
        float w = gameViewport.getCamera().viewportWidth;
        float h = gameViewport.getCamera().viewportHeight;
        float cx = gameViewport.getCamera().position.x;
        float cy = gameViewport.getCamera().position.y;
        int edge = random(3);
        float x, y;
        switch (edge) {
            case 0: x = random(cx - w / 2, cx + w / 2); y = cy + h / 2 + margin; break;
            case 1: x = random(cx - w / 2, cx + w / 2); y = cy - h / 2 - margin; break;
            case 2: x = cx - w / 2 - margin; y = random(cy - h / 2, cy + h / 2); break;
            case 3: x = cx + w / 2 + margin; y = random(cy - h / 2, cy + h / 2); break;
            default: x = cx; y = cy; break;
        }
        return new Vector2(x, y);
    }

    public void pickUpPowerUp() {
        for (PowerUp p : powerUps) {
            if (player.getHitBox().overlaps(p.getHitbox())) {
                p.onPickup(player);
                if (p instanceof Berserk) ((Berserk) p).onPickupBullet(bullet);
                powerUpsToRemove.add(p);
                p.dispose();
            }
        }
        powerUps.removeAll(powerUpsToRemove, true);
        powerUpsToRemove.clear();
    }

    public void pickUpGroundItem() {
        for (GroundItem g : groundItems) {
            if (player.getHitBox().overlaps(g.getHitbox())) {
                g.onPickup(player);
                groundItemsToRemove.add(g);
            }
        }
        groundItems.removeAll(groundItemsToRemove, true);
        groundItemsToRemove.clear();
    }

    public Map createMap() {
        tiledMap = new TmxMapLoader().load("map2/map2.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2f);
        this.map = new Map(tiledMap);
        return map;
    }

    public void playerShoot() {
        float bulletCooldown = (float) bullet.getCooldown();
        bulletTimer += Gdx.graphics.getDeltaTime();

        if (bulletTimer >= bulletCooldown) {
            bulletTimer = 0f;
            shootAtNearestEnemy();
        }
    }

    public void shootAtNearestEnemy() {
        Enemy target = getNearestEnemy();

        if (target != null) {
            Vector2 direction = new Vector2(
                target.getSprite().getX() - player.getPlayerX(),
                target.getSprite().getY() - player.getPlayerY()
            ).nor();

            BaseAttack bullet = new BaseAttack();
            bullet.setDirection(direction);
            bullet.getSprite().setPosition(player.getPlayerX(), player.getPlayerY());
            bullet.getHitBox().setPosition(player.getPlayerX(), player.getPlayerY());

            bullet.updatePosition(player.getPlayerX(), player.getPlayerY());

            abilities.add(bullet);
        }
    }

    public Enemy getNearestEnemy() {
        Enemy nearest = null;
        float minDistance = 1000;
        playerPos.set(player.getPlayerX(), player.getPlayerY());

        for (Enemy enemy : enemies) {
            float distance = playerPos.dst(enemy.getSprite().getX(), enemy.getSprite().getY());

            if (distance < minDistance) {
                minDistance = distance;
                nearest = enemy;
            }
        }
        return nearest;
    }

    private void enemyAttacks() {
        for (Enemy enemy : enemies) {
            enemy.attack(player, enemyAbilities);
        }
        for (Ability a : enemyAbilities) {
            a.updatePosition(player.getPlayerX(), player.getPlayerY());
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void hide() {

    }

    // Getters & setters
    public Player getPlayer() {
        return player;
    }
    public Array<Ability> getAbilities() {
        return abilities;
    }

    public Array<Ability> getEnemyAbilities() {
        return enemyAbilities;
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public Array<GroundItem> getGroundItems() {
        return groundItems;
    }

    public Array<GroundItem> getGroundItemsToRemove() {
        return groundItemsToRemove;
    }

    public Array<PowerUp> getPowerUps() {
        return powerUps;
    }

    public Array<PowerUp> getPowerUpsToRemove() {
        return powerUpsToRemove;
    }

    public Queue<Integer> getMiniBossSchedule() {
        return miniBossSchedule;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentEXP() {
        return currentEXP;
    }

    public int getExpToNextLevel() {
        return expToNextLevel;
    }

    public Map getMap() { return map; }

    public GameUI getGameUI() { return gameUI; }

    public void setGameUI(GameUI gameUI) { this.gameUI = gameUI; }

    public OrthogonalTiledMapRenderer getTiledMapRenderer() { return tiledMapRenderer; }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }
}
