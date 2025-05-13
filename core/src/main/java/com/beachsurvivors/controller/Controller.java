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
import com.beachsurvivors.view.GameScreen;
import com.beachsurvivors.view.GameUI;
import com.beachsurvivors.view.Main;

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
    private float growthRateOfSpawningEnemies = 1.5f;
    private int secondsBetweenGrowthRate = 10;
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
        this.poolManager.register("assets/entities/particles/blueFlame.p", 5, 20);
        this.poolManager.register("assets/entities/particles/lootBeam.p", 5, 20);
        this.poolManager.register("assets/entities/particles/lootPile.p", 5, 20);
        this.poolManager.register("assets/entities/particles/xp_orb.p", 5, 20);
        this.poolManager.register("assets/entities/particles/chestClosed.p", 5, 20);
        this.poolManager.register("assets/entities/particles/chestOpen.p", 5, 20);

        this.currentLevel = 1;
        this.currentEXP = 0;
        this.expToNextLevel = calculateExpForLevelUp(currentLevel);

        create();
    }

    private int calculateExpForLevelUp(int level) {
        return 100 * level;
    }

    private void onLevelUp() {
        System.out.println("Level up!");
        gameUI.updateInfoTable("Congratulations, you are now level " + currentLevel);
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
        updateAbilities();
        spawnEnemies();
        updateEnemyPositions();
        updateShieldPosition();
        pickUpPowerUp();
        pickUpGroundItem();
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
        float delta = Gdx.graphics.getDeltaTime();
        for (Ability a : abilities) {
            a.updatePosition(player.getPlayerX(), player.getPlayerY());
        }
    }

    private void updateShieldPosition() {
        if (!shield.getIsDepleted() && shield.getSprite() != null) {
            shield.updatePosition(player.getPlayerX() - shield.getSprite().getWidth() / 2,
                player.getPlayerY() - shield.getSprite().getHeight() / 2);
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
        switch (MathUtils.random(3)) {
            case 0: return new Shark();
            case 1: return new NavySeal();
            case 2: return new Crocodile();
            default: return new Crab();
        }
    }

    private Vector2 getRandomOffscreenPosition(float margin) {
        float w = gameViewport.getCamera().viewportWidth;
        float h = gameViewport.getCamera().viewportHeight;
        float cx = gameViewport.getCamera().position.x;
        float cy = gameViewport.getCamera().position.y;
        int edge = MathUtils.random(3);
        float x, y;
        switch (edge) {
            case 0: x = MathUtils.random(cx - w / 2, cx + w / 2); y = cy + h / 2 + margin; break;
            case 1: x = MathUtils.random(cx - w / 2, cx + w / 2); y = cy - h / 2 - margin; break;
            case 2: x = cx - w / 2 - margin; y = MathUtils.random(cy - h / 2, cy + h / 2); break;
            case 3: x = cx + w / 2 + margin; y = MathUtils.random(cy - h / 2, cy + h / 2); break;
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
}
