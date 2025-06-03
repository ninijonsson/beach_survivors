package com.beachsurvivors.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.beachsurvivors.utilities.ParticleEffectPoolManager;
import com.beachsurvivors.model.abilities.*;
import com.beachsurvivors.model.Map.Map;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.*;
import com.beachsurvivors.model.groundItems.*;
import com.beachsurvivors.utilities.CombatHelper;
import com.beachsurvivors.model.abilities.AbilityDescription;

import java.util.*;

public class GameScreen extends Game implements Screen {
    private Array<BombAttack> activeBombs = new Array<>();

    private int baseEnemies = 2;

    // amount of enemies on screen gets multiplied by this number after every interval
    private float growthRate = 1.5f;
    // how often enemies get multiplied, in seconds.
    private int secondsBetweenIntervals = 10;

    private ChestOverlay chestOverlay;
    private final int SCREEN_WIDTH = 1920;
    private final int SCREEN_HEIGHT = 1080;

    private Main main;
    private SpriteBatch spriteBatch;
    private final FitViewport gameViewport;
    private Stage stage;
    private final GameUI gameUI;

    private ShapeRenderer shapeRenderer;
    private ParticleEffectPoolManager poolManager;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    private Player player;
    private Array<PowerUp> droppedItems;

    private Array<Ability> abilities;
    private Array<PowerUp> currentPowerUps;
    float numberOfBoomerangs;
    private BaseAttack bullet;
    private Shield shield;
    private ChainLightning chainLightning;
    private int totalEnemiesKilled;
    private double totalPlayerDamageDealt;
    private boolean hasWaterWaveAbility = false;
    private float waterWaveTimer = 0f;

    private HashMap<AbilityDescription, Integer> abilityLevels = new HashMap<>();

    private BitmapFont font;
    private Array<DamageText> damageTexts;
    private Random random = new Random();

    private Array<GroundItem> groundItems;  //Array med alla groundItems som inte är powerUps. Kistor, exp o.s.v
    private float nextMiniBossTime = 60f;
    private float nextEnemyBuffTime = 60f;
    private float enemyHpMultiplier = 1f;
    int minibossHP = 200;


    /// /array med intervaller på när miniboss ska spawna

    private Array<Enemy> enemies;
    private Array<Ability> enemyAbilities;
    private Array<PowerUp> powerUpsToRemove;
    private Array<GroundItem> groundItemsToRemove;
    private Array<Projectile> playerProjectiles;
    private Array<Projectile> enemyProjectiles;
    private float bossSpawnDelay = 60f; //seconds ;
    private Boss boss;
    private float bossSpawnTimer = 0f;
    private boolean warningGiven = false;
    private boolean showWarningVisual = false;
    private boolean bossSpawned = false;
    private Vector2 bossSpawnPos;
    private Sprite warningSprite;

    private Array<Enemy> enemies = new Array<>();
    private Array<Ability> enemyAbilities = new Array<>();
    private Array<PowerUp> powerUpsToRemove = new Array<>();
    private Array<GroundItem> groundItemsToRemove = new Array<>();
    private Vector2 playerPos;

    private boolean isPaused = false;
    private PauseOverlay pauseOverlay;

    private boolean isChestOverlayActive = false;

    //Boolean variables to toggle when testing the game with/without
    //some elements. Set all to true for testing everything.
    private boolean playerAbilitiesTestMode = true; //Toggles if player use abilities
    private boolean spawnEnemiesTestMode = true; //Toggles if enemies spawn
    private boolean spawnMinibossesTestMode = true; //Toggles if minibosses spawn
    private boolean testMode = false;
    private boolean splatterMode = false;



    public GameScreen(Main main) {
        this.main = main;

        gameViewport = new FitViewport(SCREEN_WIDTH * 1.5f, SCREEN_HEIGHT * 1.5f);
        gameUI = new GameUI(new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT), this);

        initializeArrays();

        totalEnemiesKilled = 0;
        create();

        gameUI.updateStats(player);
    }

    /**
     * This method is used by Libgdx for constructing and adding certain objects to the game.
     */
    @Override
    public void create() {
        stage = new Stage(gameViewport);
        stage.clear();

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        pauseOverlay = new PauseOverlay(this, main);

        tiledMap = new TmxMapLoader().load("map2/map2.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2f);
        assert tiledMap != null;
        Map map = new Map(tiledMap);


        addPoolManager();

        player = new Player(map, spriteBatch, this);


        bullet = new BaseAttack(poolManager);
        abilities.add(bullet);

        shield = new Shield();
        chainLightning = new ChainLightning(enemies, poolManager);
//        abilities.add(shield);
//        abilities.add(chainLightning);


        font = new BitmapFont();
        font.setColor(Color.YELLOW);
        font.getData().setScale(2);

        groundItems.add(new Chest(player.getPosition().x-300, player.getPosition().y, poolManager, this));
        groundItems.add(new Chest(player.getPosition().x-500, player.getPosition().y, poolManager, this));
        groundItems.add(new Chest(player.getPosition().x-700, player.getPosition().y, poolManager, this));
        groundItems.add(new Chest(player.getPosition().x-800, player.getPosition().y, poolManager, this));
        groundItems.add(new Chest(player.getPosition().x-900, player.getPosition().y, poolManager, this));
        groundItems.add(new Chest(player.getPosition().x-950, player.getPosition().y, poolManager, this));
        Vector2 startPos = new Vector2(player.getPosition());
        WaterWave wave = new WaterWave("WaterWave", 15, 1.2f, 32, 32, startPos, poolManager);
        player.setPlayerX(map.getStartingX());
        player.setPlayerY(map.getStartingY());

        poolManager = new ParticleEffectPoolManager();
        poolManager.register("entities/particles/blueFlame.p", 5, 20);
        poolManager.register("entities/particles/lootBeam.p", 5, 20);
        poolManager.register("entities/particles/lootPile.p", 5, 20);
        poolManager.register("entities/particles/xp_orb.p", 5, 20);
        poolManager.register("entities/particles/chestClosed.p", 5, 20);
        poolManager.register("entities/particles/chestOpen.p", 5, 20);
        poolManager.register("entities/particles/fire_trail.p", 5 , 20);
        Chest chest = new Chest(player.getPlayerX()-250,player.getPlayerY()-140, poolManager, this);
        groundItems.add(chest);

        Vector2 startPos = new Vector2(player.getPlayerX(), player.getPlayerY());
        WaterWave wave = new WaterWave("WaterWave", 15, 1.2, 32, 32, startPos, poolManager);
        abilities.add(wave);
    }

    private void initializeArrays() {
        groundItems = new Array<>();

        enemies = new Array<>();
        enemyAbilities = new Array<>();
        powerUpsToRemove = new Array<>();
        groundItemsToRemove = new Array<>();
        playerProjectiles = new Array<>();
        enemyProjectiles = new Array<>();

        droppedItems = new Array<>();
        abilities = new Array<>();
        currentPowerUps = new Array<>();

        damageTexts = new Array<>();
        warningSprite = new Sprite(new Texture("warningSign.png"));
        warningSprite.setSize(128*4,128*4);

    }


    /**
     * Override från Screen
     * Show kallas när en Screen blir "current screen"
     */
    @Override
    public void show() {
        isPaused = false;
        Gdx.input.setInputProcessor(stage); //Uppdaterar vilken stage inputProcessorn ska lyssna på
        Timer.instance().start();
    }

    /**
     * The rendering of the game. This method is called 60 times per second
     * (depending on the frame rate (FPS) that you can set in Lwjgl3Launcher.java)
     * by the rendering thread. This is the most important method in LibGdx.
     * It calls input(), logic() and draw() which handles all the logics and visuals
     * for the game.
     *
     * @param delta
     */
    @Override
    public void render(float delta) {


        input(); // hanterar ESC / TAB osv.

        // Rita alltid spelet
        if (!isPaused) {

            logicIfNotPaused(delta);
            draw();

            // Rita UI och ev. överlägg
            gameUI.getStage().act(delta);
            gameUI.update(delta);
            gameUI.draw();
        }

        if (isChestOverlayActive && chestOverlay != null) {
            chestOverlay.update(delta);  // inga world updates men ev. effekt
            chestOverlay.draw();
            if (chestOverlay.isClosed()) {
                chestOverlay.dispose();
                chestOverlay = null;
                isChestOverlayActive = false;
                resume();
            }
        }
        if (isPaused && pauseOverlay != null) {
            pauseOverlay.getStage().act(delta);
            pauseOverlay.getStage().draw();
            pauseOverlay.render(delta);
        }
    }


    /**
     * Method for keeping the correct width and height while resizing the game window.
     *
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        gameUI.getStage().getViewport().update(width, height, true);
    }

    /**
     * This method is used by libgdx to draw everything on the screen every gametic. This method is called upon every
     * single game tick and is used to update the visuals of the game.
     */
    private void draw() {
        gameViewport.getCamera().position.set(player.getPosition(), 0);
        gameViewport.getCamera().update();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        gameViewport.apply();

        mapRenderer.setView((OrthographicCamera) gameViewport.getCamera());
        mapRenderer.render();

        spriteBatch.setProjectionMatrix(gameViewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(gameViewport.getCamera().combined);

        spriteBatch.begin();
        stage.act();
        stage.draw();
        drawStuff();

        poolManager.updateAndDraw(Gdx.graphics.getDeltaTime(), spriteBatch);
        spriteBatch.end();

    }

    /**
     * Handles all the input and keypresses. Is called in render()
     */
    private void input() {
        if (!isPaused) {

            player.playerInput(isChestOverlayActive());
        }
        keyBinds();
    }

    /**
     * As the name suggests, Logic is where the game's logic is put.
     */
    private void logic(float delta) {
        pickUpPowerUp();
        pickUpGroundItem();
        updateShieldPos();
        gameUI.updateStats(player);
        gameUI.updateFpsLabel("FPS: " + Gdx.graphics.getFramesPerSecond());
        player.update(Gdx.graphics.getDeltaTime());

        enemyAttacks();
        updateProjectiles();


        if (playerAbilitiesTestMode) {
            updateAbilities();
            if (hasWaterWaveAbility) {
                castWaterWave();
            }

        }

        updateDamageText();

        //UPDATES EVERYTHING RELATED TO ENEMIES.
        if (spawnEnemiesTestMode) { //DOES NOT SPAWN IF THIS IS NOT TRUE, TOGGLES ENEMIES IN GAME.
            spawnEnemies();

            for (int i = enemies.size - 1; i >= 0; i--) { //LOOP THROUGH ALL ENEMIES AND UPDATE RELATED POSITIONS.
                Enemy enemy = enemies.get(i);
                updateEnemyMovement(enemy); //MOVE TOWARDS PLAYER
                handleEnemyDeaths(enemy, i); //IF THEY ARE DEAD
                checkPlayerAbilityHits(enemy); //IF THEY ARE HIT BY THE PLAYER
                checkDamageAgainstPlayer(enemy); //IF THEY DAMAGE THE PLAYER
                checkProjectileHits(enemy);
            }
            checkEnemyAbilitiesDamagePlayer();
            checkEnemyProjectilesHitPlayer();
        }

        bossSpawnTimer += delta;

        if (!warningGiven && bossSpawnTimer >= bossSpawnDelay - 10f) {
            bossSpawnPos = new Vector2(playerPos.x,playerPos.y+200);
            warningSprite.setPosition(bossSpawnPos.x, bossSpawnPos.y);
            showBossWarning();
        }

        if (!bossSpawned && bossSpawnTimer >= bossSpawnDelay) {
            hideBossWarning();
            spawnBoss(bossSpawnPos);
        }

        if (bossSpawned) {
            gameUI.setBossBarVisible(true);
            boss.update(delta, player);
            checkPlayerAbilityHits_boss();
            gameUI.updateBossHealth((float)boss.getHealthPoints(), 1000);
        } else {
            gameUI.setBossBarVisible(false);
        }

        resolveEnemyCollisions(enemies); //MOVE ENEMIES FROM EACH OTHER TO AVOID CLUTTERING

        updatePowerUps();

        vaccum();

        for (int i = activeBombs.size - 1; i >= 0; i--) {
            BombAttack bomb = activeBombs.get(i);
            bomb.update(Gdx.graphics.getDeltaTime());
            if (bomb.isFinished()) {
                bomb.dispose();
                activeBombs.removeIndex(i);
            }
        }
    }

    /**
     * This method is used to control enemy moving behaviour. Each enemy is given a radius in which other enemies tries
     * to avoid. This method prevents cluttering of enemies and having several enemies on the same game square.
     *
     * @param enemies
     */
    private void resolveEnemyCollisions(Array<Enemy> enemies) {
        for (int i = 0; i < enemies.size; i++) {
            Enemy enemyA = enemies.get(i);
            Circle circleA = enemyA.getCircle();

            for (int j = i + 1; j < enemies.size; j++) { // i + 1 avoids comparing an enemy with itself
                Enemy enemyB = enemies.get(j);
                Circle circleB = enemyB.getCircle();

                if (circleA.overlaps(circleB)) {
                    // Calculate push direction
                    // dx, dy is the vector from enemy 1 to enemy 2.
                    float deltaX = circleB.x - circleA.x;
                    float deltaY = circleB.y - circleA.y;
                    // dist is how far apart their centers are.
                    float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                    float minimumDistance = circleA.radius + circleB.radius;

                    if (distance == 0) distance = 0.01f; // avoid divide by zero

                    float overlap = minimumDistance - distance;

                    // Normalize direction, gives clean push, 0% wonky
                    float directionX = deltaX / distance;
                    float directionY = deltaY / distance;

                    // Push enemies apart
                    float pushAmount = overlap / 2f;

                    Vector2 positionA = enemyA.getPosition();
                    Vector2 positionB = enemyB.getPosition();

                    Vector2 newPositionA = new Vector2(positionA.x - directionX * pushAmount, positionA.y - directionY * pushAmount);

                    Vector2 newPositionB = new Vector2(positionB.x + directionX * pushAmount, positionB.y + directionY * pushAmount);
                    enemyA.setPosition(newPositionA);
                    enemyB.setPosition(newPositionB);
                }
            }
        }
    }

    private void keyBinds() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!isPaused) {
                main.pause();
                pause();
            } else {
                resume();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            main.gameOver(totalEnemiesKilled, totalPlayerDamageDealt, gameUI.getGameTimeSeconds(), player.getDamageTaken(), player.getHealingReceived(), shield.getTotalDamagePrevented());
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            main.victory(totalEnemiesKilled, totalPlayerDamageDealt,gameUI.getGameTimeSeconds(),
                player.getDamageTaken(), player.getHealingReceived(), shield.getTotalDamagePrevented());
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB) || Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            gameUI.showStatsTable();
        }
    }

    private void vaccum() {
        for (GroundItem xporb : groundItems) {
            if (xporb instanceof ExperienceOrb) {
                if (((ExperienceOrb) xporb).isAttracted()) {
                    ((ExperienceOrb) xporb).updateExperienceOrbMovement(player);
                    continue;
                }
                if (((ExperienceOrb) xporb).overlaps(player.getVaccumHitbox())) {
                    ((ExperienceOrb) xporb).updateExperienceOrbMovement(player);
                }
            }
        }
    }

    private void pickUpPowerUp() {
        for (PowerUp powerUp : droppedItems) {

            if (player.getHitBox().overlaps(powerUp.getHitbox())) {
                gameUI.addBuff(powerUp);
                currentPowerUps.add(powerUp);

                if (powerUp instanceof Berserk) {
                    powerUp.onPickup(player);
                    ((Berserk) powerUp).onPickupBullet(bullet);
                } else {
                    powerUp.onPickup(player);
                }

                powerUpsToRemove.add(powerUp);
                powerUp.dispose();
            }
        }
        gameUI.setHealthBarValue(player);
        droppedItems.removeAll(powerUpsToRemove, true);
        powerUpsToRemove.clear();
    }

    private void updatePowerUps() {
        for (PowerUp powerUp : currentPowerUps) {
            powerUp.updateDuration(Gdx.graphics.getDeltaTime(), player);
            if (powerUp.getRemainingDuration() <= 0) {
                currentPowerUps.removeValue(powerUp, true);
                gameUI.removeBuff(powerUp);
            }
        }
        gameUI.updateBuffIcons();
    }

    private void pickUpGroundItem() {
        for (GroundItem groundItem : groundItems) {

            if (player.getHitBox().overlaps(groundItem.getHitbox())) {
                groundItem.onPickup(player);
                groundItemsToRemove.add(groundItem);
                if (groundItem instanceof ExperienceOrb) {
                    gameUI.setProgressBarValue(player.getLevelSystem().getCurrentExp());
                }
            }
        }
        groundItems.removeAll(groundItemsToRemove, true);
        groundItemsToRemove.clear();
    }


    /**
     * Method for updating the position of damage text above enemies and then removing them after a certain amount of time.
     */
    private void updateDamageText() {
        for (int i = damageTexts.size - 1; i >= 0; i--) {
            DamageText dt = damageTexts.get(i);
            dt.update(Gdx.graphics.getDeltaTime() * 2);
            if (!dt.isActive()) {
                damageTexts.removeIndex(i);
            }
        }
    }


    private void castWaterWave() {
        float cooldown = CombatHelper.getActualCooldown(2f, player.getCooldownTime()); // 2s base
        waterWaveTimer += Gdx.graphics.getDeltaTime();

        if (waterWaveTimer >= cooldown) {
            waterWaveTimer = 0f;
            Vector2 direction = player.getLastDirection(); // vi behöver lägga till detta i Player
            if (direction.isZero()) return;

            WaterWave wave = new WaterWave("Water Wave", 15, 2f, 32, 32, player.getPosition().cpy(), poolManager);
            wave.setDirection(direction);
            abilities.add(wave);
            System.out.println("cast waterwave in direction " + direction);
        }
    }

    private void updateShieldPos() {
        if (!shield.getIsDepleted() && shield.getSprite() != null) {
            shield.updatePosition(0, player.getPosition());
            shield.rotate(-2);
        }
    }

    private void enemyAttacks() {
        for (Enemy enemy : enemies) {
            enemy.attack(player, enemyAbilities, enemyProjectiles);
        }
        for (Ability a : enemyAbilities) {
            a.updatePosition(Gdx.graphics.getDeltaTime(), player.getPosition());
        }
    }

    @Override
    public void resume() {
        setPaused(false);
        Gdx.input.setInputProcessor(gameUI.getStage());
        Timer.instance().start();
        pauseOverlay.hide();
        main.setScreen(this);
    }

    @Override
    public void pause() {
        if (isPaused) return;
        isPaused = true;
        Timer.instance().stop();
        pauseOverlay.show();
    }


    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        mapRenderer.dispose();
        tiledMap.dispose();
//        player.dispose();  //dessa borde inte disposas
//        boomerang.dispose();
        font.dispose();
    }

    /**
     * Method for generating a random position outside of the visible screen, this position could then be used to summon
     * enemies outside of the game screen.
     *
     * @param margin
     * @return a vector for the enemy to move.
     */
    public Vector2 getRandomOffscreenPosition(float margin) {
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

    public void hideBossWarning() {
        showWarningVisual = false;
    }

    private void showBossWarning() {
        gameUI.updateInfoTable_color("BOSS INCOMING IN 10 SECONDS", Color.RED);
        showWarningVisual = true;
        warningGiven = true;
    }

    private void spawnBoss(Vector2 position) {
        boss = new Boss(position, poolManager, gameViewport.getCamera(), this);
        bossSpawned = true;
        spawnEnemiesTestMode = false;
        spawnMinibossesTestMode = false;
    }

    private void spawnEnemies() {

        float gameTimeSeconds = gameUI.getGameTimeSeconds();
        buffEnemies(gameTimeSeconds);
        int interval = (int) (gameTimeSeconds / secondsBetweenIntervals);
        int maxEnemies = (int) (baseEnemies * Math.pow(growthRate, interval));
        if (maxEnemies > 100) {
            maxEnemies = 100;
        }

        if (spawnMinibossesTestMode) {
            spawnMiniBoss(gameTimeSeconds);
        }

        if (!(enemies.size < maxEnemies)) {
            return;
        }

        Enemy enemy = selectRandomEnemy();
        enemy.setHealthPoints((float) (enemy.getHealthPoints() * enemyHpMultiplier));

        Vector2 randomPos = getRandomOffscreenPosition(150);
        enemy.setPosition(randomPos);
        enemies.add(enemy);
    }

    private void buffEnemies(float gameTimeSeconds) {
        if (gameTimeSeconds >= nextEnemyBuffTime) {
            enemyHpMultiplier *= 1.25f;
            nextEnemyBuffTime += 60f;
            gameUI.updateInfoTable("Enemies got stronger!");
        }
    }

    private Enemy selectRandomEnemy() {
        int enemyChoice = random.nextInt(0, 11);
        Enemy enemy = null;
        //enemyChoice = 0;

        switch (enemyChoice) {
            case 0:
                enemy = new NavySeal();
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                enemy = new Shark();
                break;
            case 5:
            case 6:
            case 7:
                enemy = new Crocodile();
                break;
            case 8:
            case 9:
            case 10:
                enemy = new Crab();
                break;
        }
        return enemy;
    }

    private void spawnMiniBoss(float gameTimeSeconds) {

        if (gameTimeSeconds >= nextMiniBossTime) {
            Enemy miniBoss = new MiniBoss(poolManager, this);
            miniBoss.setHealthPoints(minibossHP);
            gameUI.updateInfoTable("Spawned a miniboss! Watch out!");
            Vector2 randomPos = getRandomOffscreenPosition(miniBoss.getHeight());
            miniBoss.setPosition(randomPos);
            enemies.add(miniBoss);

            nextMiniBossTime += 60f;
            minibossHP += 100;
        }
    }


    /**
     * Updates position of all abilities that enemies use
     */
    private void updateAbilities() {
        for (Ability ability : abilities) {
            ability.updatePosition(Gdx.graphics.getDeltaTime(), player.getPosition().cpy());
            ability.update(Gdx.graphics.getDeltaTime(), player, enemies, abilities);
            if (ability.isOffCooldown()) {
                ability.use(Gdx.graphics.getDeltaTime(), player, enemies, abilities, damageTexts, playerProjectiles);
                ability.setOffCooldown(false);
            }
        }
    }

    private void updateProjectiles() {
        float delta = Gdx.graphics.getDeltaTime();

        for (int i = playerProjectiles.size - 1; i >= 0; i--) {
            Projectile projectile = playerProjectiles.get(i);
            projectile.updatePosition(delta);

            if (projectile.hasExpired()) {
                playerProjectiles.removeValue(projectile, true);
            }
        }
        for (int i = enemyProjectiles.size - 1; i >= 0; i--) {
            Projectile projectile = enemyProjectiles.get(i);
            projectile.updatePosition(delta);

            if (projectile.hasExpired()) {
                enemyProjectiles.removeValue(projectile, true);
            }
        }
    }


    /**
     * Updates the position of an enemy
     *
     * @param enemy - the enemy that's updated
     */
    private void updateEnemyMovement(Enemy enemy) {
        enemy.updateHealthBarPosition();
        enemy.addHealthBar(stage);

        float delta = Gdx.graphics.getDeltaTime();

        Vector2 direction = enemy.moveTowardsPlayer(delta, player.getPosition(), enemy.getPosition());
        enemy.setMovingLeftRight(direction.x);

        //Uppdaterar Spritens X och Y position baserat på riktningen på fiendens vector2 * speed * tid.
        //vector.x/y är riktningen, movementSpeed är hastighet och delta är tid.
        float speed = enemy.getMovementSpeed();
        Vector2 movement = new Vector2(direction).scl(speed * delta);

        Vector2 newPosition = enemy.getPosition().cpy().add(movement);
        enemy.setPosition(newPosition);
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

            enemy.dropItems(droppedItems, poolManager);
            groundItems.add(new ExperienceOrb(enemy.getPosition().x, enemy.getPosition().y, enemy.getExp(), poolManager));

            if (enemy instanceof MiniBoss) {
                ((MiniBoss) enemy).dropChest(groundItems);
            }

            // Lägg till dödseffekt innan dispose

            if(splatterMode){ //Bloding effekt
                ParticleEffectPool.PooledEffect deathEffect = poolManager.obtain("entities/particles/death_effect.p");
                deathEffect.setPosition(enemy.getPosition().x + enemy.getSprite().getWidth() * 0.5f, enemy.getPosition().y + enemy.getSprite().getHeight() * 0.5f);
                poolManager.addActiveEffect(deathEffect);
                enemy.playDeathSound();
            } else { //Rökig effect
                ParticleEffectPool.PooledEffect deathEffect = poolManager.obtain("entities/particles/death_effect_safe.p");
                deathEffect.setPosition(enemy.getPosition().x + enemy.getSprite().getWidth() * 0.5f, enemy.getPosition().y + enemy.getSprite().getHeight() * 0.5f);
                poolManager.addActiveEffect(deathEffect);
            }

            enemies.removeIndex(i);
            enemy.dispose();
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

            if (!(ability instanceof Shield)) {
                if (ability.getHitBox().overlaps(enemy.getHitbox())) {
                    boolean isCritical = player.isCriticalHit();
                    double damage = player.getDamage() * ability.getDamageMultiplier();
                    if (isCritical) {
                        damage *= player.getCriticalHitDamage();
                    }

                    if (enemy.hit(damage)) {
                        totalPlayerDamageDealt += damage;
                        damageTexts.add(new DamageText(String.valueOf((int) damage), enemy.getSprite().getX() + random.nextInt(50), enemy.getSprite().getY() + enemy.getSprite().getHeight() + 10 + random.nextInt(50), 1.0f, isCritical));
                    }
                }
            }
        }
    }

    private void checkPlayerAbilityHits_boss() {
        for (int j = abilities.size - 1; j >= 0; j--) {
            Ability ability = abilities.get(j);

            if (ability.getHitBox().overlaps(boss.getHitbox())) {
                boolean isCritical = player.isCriticalHit();
                double damage = ability.getBaseDamage();
                if (isCritical) {
                    damage *= player.getCriticalHitDamage();
                }

                if (boss.hit(damage)) {
                    totalPlayerDamageDealt += damage;
                    damageTexts.add(new DamageText(String.valueOf((int) damage),
                        boss.getSprite().getX() + random.nextInt(50),
                        boss.getSprite().getY() + boss.getSprite().getHeight() + 10 + random.nextInt(50),
                        1.0f,
                        isCritical));
                }
            }
        }
    }

    private void checkProjectileHits(Enemy enemy) {
        for (Projectile projectile : playerProjectiles) {
            if(projectile.getHitBox().overlaps(enemy.getHitbox())) {
                boolean isCritical = player.isCriticalHit();
                double damage = projectile.getDamage();
                if (isCritical) {
                    damage *= player.getCriticalHitDamage();
                }

                if (enemy.hit(damage)) {
                    totalPlayerDamageDealt += damage;
                    damageTexts.add(new DamageText(String.valueOf((int) damage), enemy.getSprite().getX() + random.nextInt(50),
                        enemy.getSprite().getY() + enemy.getSprite().getHeight() + 10 + random.nextInt(50), 1.0f, isCritical));
                }

                projectile.dispose();
                playerProjectiles.removeValue(projectile, true);
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
                damagePlayer(enemyAbilities.get(i).getDamageMultiplier());
                enemyAbilities.get(i).dispose();
                enemyAbilities.removeIndex(i);
            }
        }

    }

    private void checkEnemyProjectilesHitPlayer() {
        for (int i = enemyProjectiles.size - 1; i >= 0; i--) {
            if (enemyProjectiles.get(i).getHitBox().overlaps(player.getHitBox())) {
                damagePlayer(enemyProjectiles.get(i).getDamage());
                enemyProjectiles.get(i).dispose();
                enemyProjectiles.removeIndex(i);
            }
        }
    }

    public void damagePlayer(double damage) {
        if (player.isImmune()) return;

        float shieldStrength = shield.getCurrentShieldStrength();

        if (shieldStrength > 0) {
            double damageAbsorbed = Math.min(shieldStrength, damage);
            shield.damageShield(damageAbsorbed);
            double remainingDamage = damage - damageAbsorbed;


            player.triggerImmunity();

            if (remainingDamage > 0) {
                player.takeDamage(remainingDamage);
                gameUI.setHealthBarValue(player);
            }

        } else {
            player.takeDamage(damage);
            gameUI.setHealthBarValue(player);
            player.triggerImmunity();
        }

        if (!player.isAlive()) {
            System.out.println("You died");
            main.gameOver(totalEnemiesKilled, totalPlayerDamageDealt, gameUI.getGameTimeSeconds(), player.getDamageTaken(), player.getHealingReceived(), shield.getTotalDamagePrevented());
        }
    }



    private void logicIfNotPaused(float delta) {
        if (isPaused || isChestOverlayActive) return;
        logic(); // endast körs om spelet inte är pausat eller overlay är aktiv
    }


    /**
     * Decluttering method for keeping the draw-method simple.
     */
    private void drawStuff() {

        drawBoss();
        drawGroundItems();
        drawPowerUps();
        drawEnemies();
        drawEnemyAbilities();
        drawDamageText();
        player.drawAnimation();
        drawPlayerAbilities();
        drawProjectiles();
        drawChainLightning();

        if(testMode){
            drawMapCollisions();
            drawEnemyHitboxes();
            drawPlayerHitbox();
        }

    }

    private void addPoolManager() {
        poolManager = new ParticleEffectPoolManager();
        poolManager.register("entities/particles/blueFlame.p", 5, 20);
        poolManager.register("entities/particles/lootBeam.p", 5, 20);
        poolManager.register("entities/particles/lootPile.p", 5, 20);
        poolManager.register("entities/particles/xp_orb.p", 5, 20);
        poolManager.register("entities/particles/chestClosed.p", 5, 20);
        poolManager.register("entities/particles/chestOpen.p", 5, 20);
        poolManager.register("entities/particles/water_trail.p", 5, 20);
        poolManager.register("entities/particles/electric_trail.p", 5, 20);
        poolManager.register("entities/particles/death_effect.p", 5, 20);
        poolManager.register("entities/particles/death_effect_safe.p", 5, 20);
        poolManager.register("entities/particles/bomb_explosion.p", 5, 20);
    }

    private void xpOrbDebug(Player player) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.circle(player.getVaccumHitbox().x, player.getVaccumHitbox().y, player.getVaccumHitbox().radius); // XP orb's range
        shapeRenderer.rect(player.getHitBox().x, player.getHitBox().y, player.getHitBox().width, player.getHitBox().height); // Player's box
        shapeRenderer.end();
    }

    /**
     * This method draws a rectangle around the player, used in play testing for checking various collisions.
     */
    private void drawPlayerHitbox() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        Rectangle hitbox = player.getHitBox();
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        shapeRenderer.end();
    }

    private void drawBoss() {
        if (bossSpawned && boss.isAlive()) boss.draw(spriteBatch);


        if (showWarningVisual) {
            font.draw(spriteBatch, "⚠️ BOSS INCOMING IN 10 SECONDS ⚠️", 300, 400); // adjust position as needed
            warningSprite.draw(spriteBatch);
        }
    }

    private void drawEnemyHitboxes() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (Enemy enemy : enemies) {
            Rectangle hitbox = enemy.getHitbox();
            shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);

        }
        shapeRenderer.end();
    }

    /**
     * Draws living enemies every game tic
     */
    private void drawEnemies() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.drawAnimation(spriteBatch);
            } else {
                enemy.dispose();
            }
        }
    }

    private void drawPowerUps() {
        for (PowerUp powerUp : droppedItems) {
            powerUp.update(Gdx.graphics.getDeltaTime());
            powerUp.drawParticles(spriteBatch); // RITA EFFEKT FÖRST
            powerUp.getSprite().draw(spriteBatch); // RITA SPRITE OVANPÅ
        }

    }

    private void drawGroundItems() {
        for (GroundItem groundItem : groundItems) {
            groundItem.getSprite().draw(spriteBatch); // RITA SPRITE OVANPÅ
            groundItem.update(Gdx.graphics.getDeltaTime());
            groundItem.drawParticles(spriteBatch); // RITA EFFEKT FÖRST

        }
    }

    private void drawDamageText() {
        for (DamageText dt : damageTexts) {
            dt.draw(spriteBatch);
        }
    }

    public void showPlayerDamageText(String text, boolean isCritical) {
        float x = player.getPosition().x;
        float y = player.getPosition().y + 100;
        damageTexts.add(new DamageText(text, x, y, 1.0f, isCritical));
    }


    private void drawPlayerAbilities() {
        for (Ability a : abilities) {

            if (a instanceof Shield) {
                a.updatePosition(Gdx.graphics.getDeltaTime(), player.getPosition());
                if (!shield.getIsDepleted()) {
                    a.getSprite().draw(spriteBatch);
                }

            } else if (a instanceof WaterWave) {
                ((WaterWave) a).draw(spriteBatch);

            } else {
                a.getSprite().draw(spriteBatch);
            }
        }

        for (BombAttack bomb : activeBombs) {
            bomb.draw(spriteBatch);
        }
    }

    private void drawProjectiles() {
        for (Projectile projectile : playerProjectiles) {
            projectile.draw(spriteBatch);
        }
        for (Projectile projectile : enemyProjectiles) {
            projectile.draw(spriteBatch);
        }
    }


    private void drawEnemyAbilities() {
        for (Ability a : enemyAbilities) {
            a.getSprite().draw(spriteBatch);
        }
    }

    private void drawChainLightning() {
        //chainLightning.draw(shapeRenderer, playerPos);
        chainLightning.drawLightning(spriteBatch, player.getPosition());
    }

    public int getScreenWidth() {
        return SCREEN_WIDTH;
    }

    public int getScreenHeight() {
        return SCREEN_HEIGHT;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isChestOverlayActive() {
        return isChestOverlayActive;
    }

    public void showChestOverlay() {
        if (chestOverlay == null) {
            chestOverlay = new ChestOverlay(this);
            isChestOverlayActive = true;
        }
    }

    public void enableWaterWave() {
        hasWaterWaveAbility = true;
        //gameUI.addAbilityIcon("entities/abilities/water_wave_icon.png"); // byt ikon om du vill
    }

    public void addBoomerang() {
        if (numberOfBoomerangs >= 4) return;

        numberOfBoomerangs++;

        Array<Boomerang> newBoomerangs = new Array<>();

        for (int i = 0; i < numberOfBoomerangs; i++) {
            Boomerang b = new Boomerang();
            b.setOrbitRadius(player.getAreaRange());
            float angle = i * (360f / numberOfBoomerangs);
            b.setAngle(angle);
            newBoomerangs.add(b);
        }

        // Ta bort gamla boomerangs (endast dessa)
        for (int i = abilities.size - 1; i >= 0; i--) {
            if (abilities.get(i) instanceof Boomerang) {
                abilities.removeIndex(i);
            }
        }

        abilities.addAll(newBoomerangs);
        gameUI.addAbilityIcon("entities/abilities/boomerangmc.png");
    }

    public void increaseBoomerangRadius(float increase) {
        for (Ability ability : abilities) {
            if (ability instanceof Boomerang) {
                ((Boomerang) ability).increaseOrbitRadius(increase);
            }
        }
    }


    public Player getPlayer() {
        return player;
    }

    public Array<Ability> getAbilities() {
        return abilities;
    }


    public Main getMain() {
        return main;
    }

    public void printLog(String s) {
        gameUI.updateInfoTable(s);

    }

    public GameUI getGameUI() {
        return gameUI;
    }

    public void addOrUpgradeAbility(AbilityDescription ability) {
        int currentLevel = abilityLevels.getOrDefault(ability, 0);

        switch (ability) {
            case Water_wave:
                if (currentLevel == 0) {
                    enableWaterWave();
                    gameUI.addAbilityIcon("entities/icons/water_wave_icon.png");
                    printLog("Unlocked Water Wave!");
                } else {
                    // Öka piercing här om du har stöd för det i klassen
                    printLog("Water Wave upgraded! Increased piercing.");
                }
                break;

            case Boomerang_:
                if (currentLevel < 4) {
                    addBoomerang();
                    printLog("Boomerang upgraded! Now: " + (currentLevel + 1));
                } else {
                    printLog("Boomerang is already maxed!");
                }
                break;

            case Shield:
                if (currentLevel == 0) {
                    abilities.add(shield);
                    gameUI.addAbilityIcon("entities/abilities/shield_bubble.png");
                } else {
                    shield.increaseHealth(25); // Exempel: ge extra sköld
                    printLog("Shield reinforced! Current strength: "+ shield.getInitialShieldStrength());
                }

                break;

            case Chain_Lightning:
                if (currentLevel == 0) {
                    abilities.add(chainLightning);
                    chainLightning.setEnabled(true);
                    printLog("Unlocked Chain Lightning!");
                    gameUI.addAbilityIcon("entities/icons/chain_lightning_icon.png");
                } else {
                    chainLightning.increaseMaxJumps(1);
                    printLog("Chain Lightning upgraded! +1 bounce.");
                }
                break;
        }

        abilityLevels.put(ability, currentLevel + 1);
    }



    //FÖR TESTNING - GER KOLLISION MED KARTAN
    private void drawMapCollisions() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        for (Polygon polygon : player.getMap().getMapBoundary()) {
            shapeRenderer.polygon(polygon.getTransformedVertices());
        }

        shapeRenderer.setColor(Color.BLUE);
        for (Polygon polygon : player.getMap().getCollisionObjects()) {
            shapeRenderer.polygon(polygon.getTransformedVertices());
        }

        shapeRenderer.end();
    }

    public List<AbilityDescription> getRandomAvailableAbilities() {
        List<AbilityDescription> available = new ArrayList<>();

        for (AbilityDescription desc : AbilityDescription.values()) {
            int level = abilityLevels.getOrDefault(desc, 0);

            switch (desc) {
                case Boomerang_:
                    if (level < 4) available.add(desc);
                    break;
                case Water_wave:
                case Chain_Lightning:
                    available.add(desc); // hanteras i addOrUpgradeAbility
                    break;
                case Shield:
                    available.add(desc); // alltid tillåten
                    break;
            }
        }

        Collections.shuffle(available);
        return available.subList(0, Math.min(3, available.size()));
    }


}
