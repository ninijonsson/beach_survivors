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
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.beachsurvivors.model.abilities.Boomerang;
import com.beachsurvivors.model.Map.Map;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.abilities.Ability;
import com.beachsurvivors.model.abilities.BaseAttack;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.model.enemies.MiniBoss;
import com.beachsurvivors.model.enemies.NavySeal;
import com.beachsurvivors.model.enemies.Shark;
import com.beachsurvivors.model.groundItems.Berserk;
import com.beachsurvivors.model.groundItems.GroundItem;
import com.beachsurvivors.model.groundItems.PowerUp;

import java.util.Random;

public class GameScreen extends Game implements Screen {

    private int baseEnemies = 2;

    // amount of enemies on screen gets multiplied by this number after every interval
    private float growthRate = 1.5f;
    // how often enemies get multiplied, in seconds.
    private int secondsBetweenIntervals = 10;

    private int screenWidth = 1920;
    private int screenHeight = 1080;

    private Main main;
    private SpriteBatch spriteBatch;
    private final FitViewport gameViewport;
    private Stage stage;
    private final GameUI gameUI;

    private ShapeRenderer shapeRenderer;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    private Player player;
    private Array<PowerUp> droppedItems;

    private Array<Ability> abilities;
    private Boomerang boomerang;
    private BaseAttack bullet;
    private float bulletTimer = 0f;
    private int sharksKilled;

    private BitmapFont font;
    private Array<DamageText> damageTexts = new Array<>();
    private Random randomizeDirection = new Random();

    private Array<GroundItem> groundItems = new Array<>();  //Array med alla groundItems som inte är powerUps. Kistor, exp o.s.v
    private Queue<Integer> miniBossSchedule = new Queue<>(10); ////array med intervaller på när miniboss ska spawna

    private Array<Enemy> enemies = new Array<>();
    private Array<Ability> enemyAbilities = new Array<>();
    private Vector2 playerPos;

    private boolean isPaused = false;


    //Boolean variables to toggle when testing the game with/without
    //some elements. Set all to true for testing everything.
    private boolean playerAbilitiesTestMode = true; //Toggles if player use abilities
    private boolean spawnEnemiesTestMode = true; //Toggles if enemies spawn
    private boolean spawnMinibossesTestMode = true; //Toggles if minibosses spawn

    public GameScreen(Main main) {
        this.main = main;

        gameViewport = new FitViewport(screenWidth, screenHeight);
        gameUI = new GameUI(new FitViewport(screenWidth, screenHeight), this);

        droppedItems = new Array<>();
        abilities = new Array<>();
        sharksKilled = 0;
        create();


    }

    /**
     * This method is used by Libgdx for constructing and adding certain objects to the game.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        tiledMap = new TmxMapLoader().load("Map2/map2.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2f);
        assert tiledMap != null;
        Map map = new Map(tiledMap);
        stage = new Stage(gameViewport);
        stage.clear();
        player = new Player(map, spriteBatch);
        playerPos = new Vector2(player.getPlayerX(), player.getPlayerY());

        boomerang = new Boomerang();
        bullet = new BaseAttack();
        abilities.add(boomerang);
        abilities.add(bullet);


        font = new BitmapFont();
        font.setColor(Color.YELLOW);
        font.getData().setScale(2);

        player.setPlayerX(map.getStartingX());
        player.setPlayerY(map.getStartingY());

        createMiniBossSchedule();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float delta) {
        input();

        if (!isPaused) {
            logic();
            draw();


        gameUI.getStage().act(delta);
        gameUI.update(Gdx.graphics.getDeltaTime());
        gameUI.draw();
        }
        if (isPaused) {
            spriteBatch.begin();
            font.draw(spriteBatch, "PAUSED", player.getPlayerX() - 60, player.getPlayerY() + 200);
            spriteBatch.end();
        }
    }

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
        gameViewport.getCamera().position.set(player.getPlayerX(), player.getPlayerY(), 0);
        gameViewport.getCamera().update();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        gameViewport.apply();

        mapRenderer.setView((OrthographicCamera) gameViewport.getCamera());
        mapRenderer.render();

        spriteBatch.setProjectionMatrix(gameViewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(gameViewport.getCamera().combined);

        player.runAnimation();

        spriteBatch.begin();
        stage.act();
        stage.draw();
        drawStuff();
        spriteBatch.end();

    }

    private void input() {
        if (!isPaused) {
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

        enemyAttacks();

        if (playerAbilitiesTestMode) {
            playerShoot();
            updateAbilityMovement();
        }

        updateDamageText();

        if (spawnEnemiesTestMode) {
            spawnEnemies();

            for (int i = enemies.size - 1; i >= 0; i--) {
                Enemy enemy = enemies.get(i);
                updateEnemyMovement(enemy);
                handleEnemyDeaths(enemy, i);
                checkPlayerAbilityHits(enemy);
                checkDamageAgainstPlayer(enemy);
            }
            checkEnemyAbilitiesDamagePlayer();
        }
        resolveEnemyCollisions(enemies);

    }

    /**
     * This method is used to control enemy moving behaviour. Each enemy is given a radius in which other enemies tries
     * to avoid. This method prevents cluttering of enemies and having several enemies on the same game square.
     * @param enemies
     */
    private void resolveEnemyCollisions(Array<Enemy> enemies) {
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
                    float dist = (float)Math.sqrt(dx * dx + dy * dy);
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

    private void keyBinds() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
        }
    }

    private boolean checkForCriticalStrike() {
        float critChance = player.getCritChance();
        return randomizeDirection.nextFloat() < critChance;
    }

    private void pickUpPowerUp() {
        Array<PowerUp> powerUpsToRemove = new Array<>();
        for (PowerUp powerUp : droppedItems) {
            if (player.getHitBox().overlaps(powerUp.getHitbox())) {
                powerUp.onPickup(player);
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
        droppedItems.removeAll(powerUpsToRemove, true);
    }


    private void pickUpGroundItem() {
        Array<GroundItem> groundItemsToRemove = new Array<>();
        for (GroundItem groundItem : groundItems) {
            if (player.getHitBox().overlaps(groundItem.getHitbox())) {
                groundItem.onPickup(player);
                groundItemsToRemove.add(groundItem);
                groundItem.dispose();
            }
        }
        groundItems.removeAll(groundItemsToRemove, true);
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
        float bulletCooldown = (float) bullet.getCooldown();
        bulletTimer += Gdx.graphics.getDeltaTime();

        if (bulletTimer >= bulletCooldown) {
            bulletTimer = 0f;
            shootAtNearestEnemy();
        }
    }


    private void shootAtNearestEnemy() {
        Enemy target = getNearestEnemy();

        if (target != null) {
            Vector2 direction = new Vector2(
                target.getSprite().getX() - player.getPlayerX(),
                target.getSprite().getY() - player.getPlayerY()
            ).nor();

            BaseAttack bullet = new BaseAttack();
            bullet.updatePosition(player.getPlayerX(), player.getPlayerY());
            bullet.setDirection(direction);

            abilities.add(bullet);
        }
    }

    private Enemy getNearestEnemy() {
        Enemy nearest = null;
        float minDistance = 1000;
        Vector2 playerPos = new Vector2(player.getPlayerX(), player.getPlayerY());

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
            a.updatePosition(Gdx.graphics.getDeltaTime(), player.getPlayerX(), player.getPlayerY());
        }
    }

    @Override
    public void resume() {
        setPaused(false);
        main.setScreen(this);
    }

    @Override
    public void pause() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        mapRenderer.dispose();
        tiledMap.dispose();
        player.dispose();
        boomerang.dispose();
        font.dispose();
    }

    /**
     * Method for generating a random position outside of the visible screen, this position could then be used to summon
     * enemies outside of the game screen.
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
        Random random = new Random();
        int enemyChoice = random.nextInt(0, 3);
        Enemy enemy = null;


//        switch (enemyChoice) {
//            case 0:
   //             enemy = new Shark();
//                break;
//            case 1:
                enemy = new NavySeal();
//                break;
//            case 2:
//                enemy = new Crocodile();
//                break;
//        }

        Vector2 randomPos = getRandomOffscreenPosition(150);
        enemy.getSprite().setPosition(randomPos.x, randomPos.y);
        enemy.getHitbox().setX(randomPos.x);
        enemy.getHitbox().setY(randomPos.y);
        enemies.add(enemy);
    }

    private void spawnMiniBoss(float gameTimeSeconds) {

        if (!(miniBossSchedule.isEmpty()) && miniBossSchedule.first() <= gameTimeSeconds) {
            Enemy miniBoss = new MiniBoss();
            Vector2 randomPos = getRandomOffscreenPosition(miniBoss.getHeight());
            miniBoss.setEnemyPos(randomPos);
            miniBoss.setX(randomPos.x);
            miniBoss.setY(randomPos.y);
            enemies.add(miniBoss);
            miniBossSchedule.removeFirst();
        }
    }

    /**
     * Method for creating a schedule for when minibosses should spawn
     * var i is for seconds
     */
    private void createMiniBossSchedule() {
        for (int i = 10; i <= 100; i+=20) {
            miniBossSchedule.addLast(i);
        }
    }

    /**
     * Updates position of all abilities that enemies use
     */
    private void updateAbilityMovement() {
        for (Ability a : abilities) {
            a.updatePosition(Gdx.graphics.getDeltaTime(), player.getPlayerX(), player.getPlayerY());
        }
    }

    /**
     * Updates the position of an enemy
     * @param enemy - the enemy that's updated
     */
    private void updateEnemyMovement(Enemy enemy) {
        enemy.updateHealthBarPosition();
        enemy.addHealthBar(stage);
        float delta = Gdx.graphics.getDeltaTime();
        playerPos.set(player.getPlayerX(), player.getPlayerY());
        Vector2 vector = enemy.moveTowardsPlayer(delta, playerPos, enemy.getEnemyPos());
        enemy.setMovingLeftRight(vector.x);

        enemy.getSprite().translateX(vector.x * enemy.getMovementSpeed() * delta);
        enemy.getSprite().translateY(vector.y * enemy.getMovementSpeed() * delta);
        enemy.getHitbox().set(enemy.getSprite().getX(), enemy.getSprite().getY(), enemy.getWidth(), enemy.getHeight());


    }


    /**
     * What happens when an enemy dies
     * @param enemy - the enemy that is handled
     * @param i - index for removing the correct enemy in the array
     */
    private void handleEnemyDeaths(Enemy enemy, int i) {
        if (!enemy.isAlive()) {
            enemy.dropItems(droppedItems);
            if (enemy instanceof MiniBoss) {
                ((MiniBoss) enemy).dropChest(groundItems);
            }
            enemies.removeIndex(i);
            sharksKilled = sharksKilled + 3;
            checkLevelUp(); // kommentera bort detta för att avaktivera levelup systemet

            gameUI.setProgressBarValue(sharksKilled/player.getLevel());
        }
    }

    /**
     * Checks if the player's abilities hitboxes overlaps with any of the enemies
     * hitboxes
     * @param enemy
     */
    private void checkPlayerAbilityHits(Enemy enemy) {
        for (int j = abilities.size - 1; j >= 0; j--) {
            Ability a = abilities.get(j);

            if (a.getHitBox().overlaps(enemy.getHitbox())) {
                boolean isCritical = checkForCriticalStrike();
                double damage = a.getBaseDamage();
                if (isCritical) {
                    damage *= 2;
                }

                if (enemy.hit(damage)) {
                    damageTexts.add(new DamageText(String.valueOf((int) damage),
                        enemy.getSprite().getX() + randomizeDirection.nextInt(50),
                        enemy.getSprite().getY() + enemy.getSprite().getHeight() + 10 + randomizeDirection.nextInt(50),
                        1.0f,
                        isCritical));
                }

                if (!(a instanceof Boomerang)) {
                    a.dispose();
                    abilities.removeIndex(j);
                }
            }
        }
    }

    private void checkDamageAgainstPlayer(Enemy enemy) {
        if (enemy.getHitbox().overlaps(player.getHitBox())) {
            player.takeDamage(enemy.getDamage());
            gameUI.setHealthBarValue(player.getHealthPoints());
            System.out.println("player HP : " + player.getHealthPoints());

            if (!player.isAlive()) {
                System.out.println("You died");
                isPaused = true;
            }
        }
    }

    private void checkEnemyAbilitiesDamagePlayer() {
        for (int i = 0; i < enemyAbilities.size; i++) {

            if (enemyAbilities.get(i).getHitBox().overlaps(player.getHitBox())) {
                gameUI.setHealthBarValue(player.getHealthPoints());
                System.out.println("player HP : " + player.getHealthPoints());
                enemyAbilities.get(i).dispose();
                enemyAbilities.removeIndex(i);

                if (!player.isAlive()) {
                    System.out.println("You died");
                    isPaused = true;
                }
            }

        }
    }

    /**
     * Decluttering method for keeping the draw-method simple.
     */
    private void drawStuff() {
        drawPlayerAbilities();
        drawGroundItems();
        drawPowerUps();
        drawEnemies();
        drawEnemyAbilities();
        drawDamageText();

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
            powerUp.getSprite().draw(spriteBatch);
        }
    }

    private void drawGroundItems() {
        for (GroundItem groundItem : groundItems) {
            groundItem.getSprite().draw(spriteBatch);
        }
    }

    private void drawDamageText() {
        for (DamageText dt : damageTexts) {
            dt.draw(spriteBatch);
        }
    }

    private void drawPlayerAbilities() {
        for (Ability a : abilities) {
            a.getSprite().draw(spriteBatch);
        }
    }

    private void drawEnemyAbilities() {
        for (Ability a : enemyAbilities) {
            a.getSprite().draw(spriteBatch);
        }
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    private void checkLevelUp() {
        if (sharksKilled >= 100 * player.getLevel()) {
            System.out.println("levelup");
            setPaused(true);
            main.levelUp();
            player.setLevel(player.getLevel() + 1);
            sharksKilled = 0;
        }
    }

    public void addBoomerang() {
        abilities.add(new Boomerang());
    }

    public Player getPlayer() {
        return player;
    }

    public Array<Ability> getAbilities() {
        return abilities;
    }
}
