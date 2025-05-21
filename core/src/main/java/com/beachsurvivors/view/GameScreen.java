package com.beachsurvivors.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.abilities.*;
import com.beachsurvivors.model.Map.Map;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.*;
import com.beachsurvivors.model.groundItems.*;
import com.beachsurvivors.utilities.CombatHelper;

import java.util.Random;

public class GameScreen extends Game implements Screen {
    private Array<BombAttack> activeBombs = new Array<>();

    private int baseEnemies = 2;

    // amount of enemies on screen gets multiplied by this number after every interval
    private float growthRate = 1.5f;
    // how often enemies get multiplied, in seconds.
    private int secondsBetweenIntervals = 10;
    ParticleEffectPoolManager poolManager;
    private ChestOverlay chestOverlay;
    private final int SCREEN_WIDTH = 1920;
    private final int SCREEN_HEIGHT = 1080;

    private Main main;
    private SpriteBatch spriteBatch;
    private final FitViewport gameViewport;
    private Stage stage;
    private final GameUI gameUI;
    private boolean isOverlayActive = false;

    private ShapeRenderer shapeRenderer;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    private Player player;
    private Array<PowerUp> droppedItems;

    private Array<Ability> abilities;
    private Array<PowerUp> currentPowerUps;
    private Boomerang boomerang;
    private BaseAttack bullet;
    private Shield shield;
    private ChainLightning chainLightning;
    private float bulletTimer = 0f;
    private int totalEnemiesKilled;
    private double totalPlayerDamageDealt;

    private BitmapFont font;
    private Array<DamageText> damageTexts = new Array<>();
    private Random random = new Random();


    private Array<GroundItem> groundItems = new Array<>();  //Array med alla groundItems som inte är powerUps. Kistor, exp o.s.v
    private Queue<Integer> miniBossSchedule = new Queue<>(10);
    /// /array med intervaller på när miniboss ska spawna

    private Array<Enemy> enemies = new Array<>();
    private Array<Ability> enemyAbilities = new Array<>();
    private Array<PowerUp> powerUpsToRemove = new Array<>();
    private Array<GroundItem> groundItemsToRemove = new Array<>();

    private boolean isPaused = false;

    //Boolean variables to toggle when testing the game with/without
    //some elements. Set all to true for testing everything.
    private boolean playerAbilitiesTestMode = true; //Toggles if player use abilities
    private boolean spawnEnemiesTestMode = true; //Toggles if enemies spawn
    private boolean spawnMinibossesTestMode = true; //Toggles if minibosses spawn

    public GameScreen(Main main) {
        this.main = main;

        gameViewport = new FitViewport(SCREEN_WIDTH * 1.5f, SCREEN_HEIGHT * 1.5f);
        gameUI = new GameUI(new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT), this);

        droppedItems = new Array<>();
        abilities = new Array<>();
        currentPowerUps = new Array<>();
        totalEnemiesKilled = 0;
        create();

        gameUI.updateStats(player);
    }

    /**
     * This method is used by Libgdx for constructing and adding certain objects to the game.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        tiledMap = new TmxMapLoader().load("map2/map2.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2f);
        assert tiledMap != null;
        Map map = new Map(tiledMap);
        stage = new Stage(gameViewport);
        stage.clear();

        poolManager = new ParticleEffectPoolManager();
        poolManager.register("entities/particles/blueFlame.p", 5, 20);
        poolManager.register("entities/particles/lootBeam.p", 5, 20);
        poolManager.register("entities/particles/lootPile.p", 5, 20);
        poolManager.register("entities/particles/xp_orb.p", 5, 20);
        poolManager.register("entities/particles/chestClosed.p", 5, 20);
        poolManager.register("entities/particles/chestOpen.p", 5, 20);
        poolManager.register("entities/particles/water_trail.p", 5, 20);
        player = new Player(map, spriteBatch, this);

        boomerang = new Boomerang();
        bullet = new BaseAttack(poolManager);
        shield = new Shield();
        chainLightning = new ChainLightning(enemies);
        abilities.add(boomerang);
        abilities.add(bullet);
        abilities.add(shield);


        font = new BitmapFont();
        font.setColor(Color.YELLOW);
        font.getData().setScale(2);




        Chest chest = new Chest(player.getPosition().x -250,player.getPosition().y -140, poolManager, this);
        groundItems.add(chest);

        Vector2 startPos = new Vector2(player.getPosition());
        WaterWave wave = new WaterWave("WaterWave", 15, 1.2f, 32, 32, startPos, poolManager);
        abilities.add(wave);

        createMiniBossSchedule();

    }

    /**
     * Override från Screen
     * Show kallas när en Screen blir "current screen"
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage); //Uppdaterar vilken stage inputProcessorn ska lyssna på
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
        if (isPaused) return;

        input();

        if (!isPaused) {

            logic();
            draw();
            gameUI.getStage().act(delta);
            gameUI.update(Gdx.graphics.getDeltaTime());
            gameUI.draw();

        } else {

//            spriteBatch.begin();
//            main.pause();
//            spriteBatch.end();

            gameUI.getStage().act(0);
            gameUI.update(0);
            gameUI.draw();
        }

        if (chestOverlay != null) {
            chestOverlay.update(delta);
            chestOverlay.draw();

            if (chestOverlay.isClosed()) {
                chestOverlay.dispose();
                chestOverlay = null;
                isOverlayActive = false;
            }
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
        spriteBatch.end();

    }

    /**
     * Handles all the input and keypresses. Is called in render()
     */
    private void input() {
        if (!isPaused) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                activeBombs.add(new BombAttack(player.getPosition(), gameViewport.getCamera()));
                System.out.println(player.getPosition());
            }

            player.playerInput();
        }
        keyBinds();
    }

    /**
     * As the name suggests, Logic is where the game's logic is put.
     */
    private void logic() {
        pickUpPowerUp();
        pickUpGroundItem();
        updateShieldPos();
        gameUI.updateStats(player);
        player.update(Gdx.graphics.getDeltaTime());

        enemyAttacks();


        if (playerAbilitiesTestMode) {
            playerShoot();
            updateAbilities();
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
            }
            checkEnemyAbilitiesDamagePlayer();
        }
        resolveEnemyCollisions(enemies); //MOVE ENEMIES FROM EACH OTHER TO AVOID CLUTTERING

        castChainLightning();
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

                    Vector2 newPositionA = new Vector2(
                        positionA.x - directionX * pushAmount,
                        positionA.y - directionY * pushAmount
                    );

                    Vector2 newPositionB = new Vector2(
                        positionB.x + directionX * pushAmount,
                        positionB.y + directionY * pushAmount
                    );
                    enemyA.setPosition(newPositionA);
                    enemyB.setPosition(newPositionB);
                }
            }
        }
    }

    private void keyBinds() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            main.pause();
            pause();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            main.gameOver(totalEnemiesKilled, totalPlayerDamageDealt, gameUI.getGameTimeSeconds(),
                player.getDamageTaken(), player.getHealingReceived(), shield.getTotalDamagePrevented());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB) || Gdx.input.isKeyJustPressed(Input.Keys.V )) {
            gameUI.showStatsTable();
        }
//        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
//            Vector3 screenCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
//            gameViewport.getCamera().unproject(screenCoords); // camera = your game's OrthographicCamera
//
//            Vector2 clickPos = new Vector2(screenCoords.x, screenCoords.y);
//            groundItems.add(new ExperienceOrb(clickPos.x, clickPos.y, 0, poolManager));
//        }

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
        gameUI.setHealthBarValue(player.getCurrentHealthPoints(), player.getMaxHealthPoints());
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
                    gameUI.updateInfoTable("You gained " + ((ExperienceOrb) groundItem).getExp() + " exp.");
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

    private void playerShoot() {  //Flytta alla player-shoot metoder till player i stället?
        float cooldown = CombatHelper.getActualCooldown(bullet.getCooldown(), player.getCooldownReduction());
        bulletTimer += Gdx.graphics.getDeltaTime();

        if (bulletTimer >= cooldown) {
            bulletTimer = 0f;
            shootAtNearestEnemy();
        }
    }

    private void shootAtNearestEnemy() {
        Enemy target = getNearestEnemy();

        if (target != null) {
            Vector2 targetCenter = target.getCenter();
            Vector2 direction = new Vector2(
                targetCenter.x - player.getPosition().x,
                targetCenter.y - player.getPosition().y
            ).nor();

            BaseAttack bullet = new BaseAttack(poolManager);
            bullet.setDirection(direction);
            bullet.setPosition(player.getPosition().cpy());

            abilities.add(bullet);
        }
    }

    private void updateShieldPos() {
        if (!shield.getIsDepleted() && shield.getSprite() != null) {
            shield.updatePosition(0, player.getPosition());
        }
    }

    private Enemy getNearestEnemy() {
        Enemy nearest = null;
        float minDistance = 1000;

        for (Enemy enemy : enemies) {
            float distance = player.getPosition().dst(enemy.getPosition());


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
            a.updatePosition(Gdx.graphics.getDeltaTime(), player.getPosition());
        }
    }

    private void castChainLightning() {
        chainLightning.update(Gdx.graphics.getDeltaTime());
        chainLightning.use(Gdx.graphics.getDeltaTime(), player, enemies, abilities, damageTexts);
    }

    @Override
    public void resume() {
        setPaused(false);
        main.setScreen(this);
        Timer.instance().start();
    }

    @Override
    public void pause() {
        isPaused = !isPaused;
        Timer.instance().stop();
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
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

    private void spawnEnemies() {
        float gameTimeSeconds = gameUI.getGameTimeSeconds();
        int interval = (int) (gameTimeSeconds / secondsBetweenIntervals);
        int maxEnemies = (int) (baseEnemies * Math.pow(growthRate, interval));

        if (spawnMinibossesTestMode) {
            spawnMiniBoss(gameTimeSeconds);
        }

        if (!(enemies.size < maxEnemies)) {
            return;
        }

        Enemy enemy = selectRandomEnemy();

        Vector2 randomPos = getRandomOffscreenPosition(150);
        enemy.setPosition(randomPos);
        enemies.add(enemy);
    }

    private Enemy selectRandomEnemy() {
        int enemyChoice = random.nextInt(0, 11);
        Enemy enemy = null;

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

        if (!(miniBossSchedule.isEmpty()) && miniBossSchedule.first() <= gameTimeSeconds) {
            Enemy miniBoss = new MiniBoss(poolManager, this);
            Vector2 randomPos = getRandomOffscreenPosition(miniBoss.getHeight());
            miniBoss.setPosition(randomPos);
            enemies.add(miniBoss);
            miniBossSchedule.removeFirst();
        }
    }

    /**
     * Method for creating a schedule for when minibosses should spawn
     * var i is for seconds
     */
    private void createMiniBossSchedule() {
        for (int i = 10; i <= 100; i += 20) {
            miniBossSchedule.addLast(i);
        }
    }

    /**
     * Updates position of all abilities that enemies use
     */
    private void updateAbilities() {
        for (Ability ability : abilities) {
            ability.updatePosition(Gdx.graphics.getDeltaTime(), player.getPosition().cpy());
            ability.update(Gdx.graphics.getDeltaTime(), player, enemies, abilities);
            ability.use(Gdx.graphics.getDeltaTime(), player, enemies, abilities, damageTexts);
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
            gameUI.updateInfoTable("You gained " + enemy.getExp() + " exp.");

            // Om fienden är en miniboss ska den droppa en kista
            enemy.dropItems(droppedItems, poolManager);
            groundItems.add(new ExperienceOrb(enemy.getPosition().x, enemy.getPosition().y, enemy.getExp(), poolManager));
            if (enemy instanceof MiniBoss) {
                ((MiniBoss) enemy).dropChest(groundItems);
            }

            enemies.removeIndex(i); // Ta bort från fiende-arrayen
            enemy.dispose(); // Ta även bort själva bilden på fienden

            //TODO DETTA SKA CHECKAS NÄR MAN TAR UPP EN ORB INTE NÄR MAN DÖDAR EN ENEMY
            // Uppdatera progress bar (exp)
//            gameUI.setProgressBarValue(player.getLevelSystem().getCurrentExp());
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
                double damage = player.getDamage() * ability.getDamageMultiplier();
                if (isCritical) {
                    damage *= player.getCriticalHitDamage();
                }

                if (enemy.hit(damage)) {
                    totalPlayerDamageDealt += damage;
                    damageTexts.add(new DamageText(String.valueOf((int) damage),
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
                damagePlayer(enemyAbilities.get(i).getDamageMultiplier());
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

    /**
     * Decluttering method for keeping the draw-method simple.
     */
    private void drawStuff() {

        drawGroundItems();
        drawPowerUps();
        drawEnemies();
        drawEnemyAbilities();
        drawDamageText();
        player.drawAnimation();
        drawPlayerAbilities();
        drawChainLightning();

        //drawEnemyHitboxes();
        //drawPlayerHitbox();

//        xpOrbDebug(player);
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

    private void drawPlayerAbilities() {
        for (Ability a : abilities) {

            if (a instanceof Shield) {
                a.updatePosition(Gdx.graphics.getDeltaTime(), player.getPosition());

                if (!shield.getIsDepleted()) {
                    a.getSprite().draw(spriteBatch);
                }
            } else {
                a.getSprite().draw(spriteBatch);
            }

            if( a instanceof BaseAttack){
                ((BaseAttack) a).drawTrail(spriteBatch);
            }
        }

        for (BombAttack bomb : activeBombs) {
            bomb.draw(spriteBatch);
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


    public void showChestOverlay() {
        if (chestOverlay == null) {
            chestOverlay = new ChestOverlay(this);
            isOverlayActive = true;
        }
    }

    public void addBoomerang() {
        abilities.add(new Boomerang());
        gameUI.addAbilityIcon("entities/abilities/boomerangmc.png");
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

}
