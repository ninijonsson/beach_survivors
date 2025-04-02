package com.beachsurvivors.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import model.*;
import model.abilities.Ability;
import model.abilities.Boomerang;
import model.abilities.Ability;
import model.enemies.Enemy;
import model.enemies.Shark;
import model.powerUps.PowerUp;

import java.util.Random;

public class GameScreen extends Game implements Screen {

    private Main main;

    private SpriteBatch spriteBatch;
    private FitViewport gameviewport;
    private Stage stage;

    private final int worldWidth = 1920;
    private final int worldHeight = 1080;
    private ShapeRenderer shapeRenderer;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    Shark shark;
    private Player player;
    private Array<PowerUp> droppedItems;
    private Array<Ability> equippedAbilities;

    private Boomerang boomerang;

    private Array<SmokeParticle> smokeTrail = new Array<>();

    private BitmapFont font;
    private Array<DamageText> damageTexts = new Array<>();
    private Random randomizeDirection = new Random();

    private Array<Enemy> enemies = new Array<>();
    private Vector2 playerPos;

    private int mapWidth;
    private int mapHeight;
    private float gameScale;

    public GameScreen(Main main) {
        this.main = main;
        gameviewport = new FitViewport(worldWidth, worldHeight);
        droppedItems = new Array<>();
        equippedAbilities = new Array<>();
        create();
    }

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        player = new Player();
        player.setPlayerX(worldWidth);
        player.setPlayerY(worldHeight);
        playerPos = new Vector2(player.getPlayerX(), player.getPlayerY());
        shark = new Shark();
        shapeRenderer = new ShapeRenderer();

        tiledMap = new TmxMapLoader().load("Map2/map2.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2f);

        stage = new Stage(gameviewport);
        stage.clear();

        boomerang = new Boomerang();
        equippedAbilities.add(boomerang);

        font = new BitmapFont();
        font.setColor(Color.YELLOW);
        font.getData().setScale(2);
        gameScale = 2f;
        mapWidth = tiledMap.getProperties().get("width", Integer.class) *
            tiledMap.getProperties().get("tilewidth", Integer.class);

        mapHeight = tiledMap.getProperties().get("height", Integer.class) *
            tiledMap.getProperties().get("tileheight", Integer.class);
        System.out.println("Map width: " + mapWidth + ", Map height: " + mapHeight);
        player.setPlayerX(mapWidth/2);
        player.setPlayerY(mapHeight/2);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float v) {
        input();
        logic();
        draw();
    }

    @Override
    public void resize(int width, int height) {
        gameviewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    private void draw() {
        gameviewport.getCamera().position.set(player.getPlayerX(), player.getPlayerY(), 0);
        gameviewport.getCamera().update();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        gameviewport.apply();

        mapRenderer.setView((OrthographicCamera) gameviewport.getCamera());

        mapRenderer.render();

        spriteBatch.setProjectionMatrix(gameviewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(gameviewport.getCamera().combined);

        // Enemies
        spriteBatch.begin();

        for (Enemy enemy : enemies) {
            enemy.getSprite().draw(spriteBatch);
        }

        player.getSprite().draw(spriteBatch);
        player.getHitBox().setPosition(player.getPlayerX(), player.getPlayerY());
        shark.getSprite().draw(spriteBatch);


        drawPlayer();

        stage.act();
        stage.draw();


        player.getSprite().draw(spriteBatch);
        player.getHitBox().setPosition(player.getPlayerX(), player.getPlayerY());
        shark.getSprite().draw(spriteBatch);
        boomerang.getSprite().draw(spriteBatch);

        // Rita alla DamageText-objekt
        for (DamageText dt : damageTexts) {
            dt.draw(spriteBatch);
        }
        for (PowerUp powerUp : droppedItems) {
            powerUp.getSprite().draw(spriteBatch);
        }


        boomerang.showSmokeTrail(spriteBatch);

        player.getSprite().draw(spriteBatch);

        spriteBatch.end();
    }

    private void drawPlayer() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CLEAR);
        shapeRenderer.rect(player.getSprite().getX(), player.getSprite().getY(), player.getSprite().getWidth(), player.getSprite().getHeight());
        shapeRenderer.rect(shark.getSprite().getX(), shark.getSprite().getY(), shark.getSprite().getWidth(), shark.getSprite().getHeight());
        shapeRenderer.end();
    }

    private void input() {
        player.playerInput();
    }

    private void logic() {
        player.getSprite().setX(MathUtils.clamp(player.getSprite().getX(), 0, worldWidth - player.getSprite().getWidth()));
        player.getSprite().setY(MathUtils.clamp(player.getSprite().getY(), 0, worldHeight - player.getSprite().getHeight()));


        pickUpPowerUp();

        // COCONUT SPIN SKIT
        boomerang.use(player);

        // SMOKE TRAIL
        //boomerang.createSmokeTrail(); //flyttade till draw, vet ej var den ska vara.

        if (boomerang.getHitbox().overlaps(shark.getHitbox()) && !boomerang.hasDamagedThisOrbit()) {
            double damage = boomerang.getDamage();
            boolean isCritical = checkForCriticalStrike();

            if (isCritical) {
                damage *= 2; // Dubblera skadan vid kritiskt slag
            }
            shark.hit(damage);

            if (!shark.isAlive()) {
                shark.dropItems(droppedItems);
                shark = new Shark();
            }
            boomerang.setHasDamagedThisOrbit(true);
        }

        drawDamageText();

        spawnEnemies();

//        Sound sharkDeath = Gdx.audio.newSound(Gdx.files.internal("Thud.mp3"));
//        long SharkDeathID = sharkDeath.play();

        for (int i = enemies.size - 1; i >= 0; i--) {
            //
            Enemy enemy = enemies.get(i);

            float delta = Gdx.graphics.getDeltaTime();
            playerPos.set(player.getPlayerX(), player.getPlayerY());
            Vector2 enemyPos = new Vector2(enemy.getSprite().getX(), enemy.getSprite().getY());
            Vector2 vector = moveTowardsPlayer(delta, playerPos, enemyPos);
            float speed = 300f;

            enemy.getSprite().translateX(vector.x * speed * delta);
            enemy.getSprite().translateY(vector.y * speed * delta);

            enemy.getHitbox().set(enemy.getSprite().getX(), enemy.getSprite().getY(), enemy.getWidth(), enemy.getHeight());
            //
            double damage = boomerang.getDamage();
            int randomPathX = randomizeDirection.nextInt(50);
            int randomPathY = randomizeDirection.nextInt(50);
            float damageTextX = enemy.getSprite().getX() + randomPathX;
            float damageTextY = enemy.getSprite().getY() + enemy.getSprite().getHeight() + 10 + randomPathY;


            if (enemy.isAlive() == false) {
                enemies.removeIndex(i);
            }

            if (boomerang.getHitbox().overlaps(enemy.getHitbox())) {
//                sharkDeath.setPitch(SharkDeathID, 2f);
//                sharkDeath.resume(SharkDeathID);

                boolean isCritical = checkForCriticalStrike();

                if (isCritical) {
                    damage *= 2; // Dubblera skadan vid kritiskt slag
                }


                if(enemy.hit(boomerang.getDamage())){
                    damageTexts.add(new DamageText(String.valueOf((int) damage),
                        damageTextX,
                        damageTextY,
                        3.0f, // damageText visas i 3 sekunder
                        isCritical));
                }


            }
        }

        //kolla position
        player.setPlayerX(MathUtils.clamp(player.getPlayerX(), 0, mapWidth*gameScale - player.getSprite().getWidth()));
        player.setPlayerY(MathUtils.clamp(player.getPlayerY(), 0, mapHeight*gameScale - player.getSprite().getHeight()));

        player.getSprite().setPosition(player.getPlayerX(), player.getPlayerY());
        player.getHitBox().setPosition(player.getPlayerX(), player.getPlayerY());
    }

    private boolean checkForCriticalStrike() {
        float critChance = player.getCritChance();
        return randomizeDirection.nextFloat() < critChance;
    }


    public void pickUpPowerUp() {
        Array<PowerUp> powerUpsToRemove = new Array<>();
        for (PowerUp powerUp : droppedItems) {
            if (player.getHitBox().overlaps(powerUp.getHitbox())) {
                powerUp.onPickup(player);
                powerUp.dispose();
                powerUpsToRemove.add(powerUp);
            }
        }
        droppedItems.removeAll(powerUpsToRemove, true);
    }

    public void drawDamageText() {
        for (int i = damageTexts.size - 1; i >= 0; i--) {
            DamageText dt = damageTexts.get(i);
            dt.update(Gdx.graphics.getDeltaTime());
            if (!dt.isActive()) {
                damageTexts.removeIndex(i);
            }
        }
    }


    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        spriteBatch.dispose();
        mapRenderer.dispose();
        tiledMap.dispose();
        player.dispose();
        boomerang.dispose();
        font.dispose();
    }

    // Returns random position off-screen. Parameter 'margin' is the off-screen distance between spawn point and screen edge. Put margin = 0 for enemies to spawn exactly outside screen.
    public Vector2 getRandomOffscreenPosition(float margin) {
        float viewWidth = gameviewport.getCamera().viewportWidth;
        float viewHeight = gameviewport.getCamera().viewportHeight;
        float camX = gameviewport.getCamera().position.x;
        float camY = gameviewport.getCamera().position.y;

        System.out.println(camX);
        System.out.println(camY);

        // Get random edge: 0 = Top, 1 = Bottom, 2 = Left, 3 = Right
        int edge = MathUtils.random(3);

        float x, y;

        switch (edge) {
            case 0: // Top, dividing by 2 to find center
                x = MathUtils.random(camX - viewWidth / 2, camX + viewWidth / 2);
                y = camY + viewHeight / 2 + margin; // Above the screen
                break;
            case 1: // Bottom
                x = MathUtils.random(camX - viewWidth / 2, camX + viewWidth / 2);
                y = camY - viewHeight / 2 - margin; // Below the screen
                break;
            case 2: // Left
                x = camX - viewWidth / 2 - margin; // Left of the screen
                y = MathUtils.random(camY - viewHeight / 2, camY + viewHeight / 2);
                break;
            case 3: // Right
                x = camX + viewWidth / 2 + margin; // Right of the screen
                y = MathUtils.random(camY - viewHeight / 2, camY + viewHeight / 2);
                break;
            default:
                System.out.println("Unexpected edge value: " + edge);

                // Spawns at center of screen if something goes wroNg
                x = camX;
                y = camY;
                break;
        }

        return new Vector2(x, y);
    }

    private Vector2 moveTowardsPlayer(float delta, Vector2 playerPosition, Vector2 enemyPosition) {
        // Calculate direction towards the player
        Vector2 direction = new Vector2(playerPosition.x - enemyPosition.x, playerPosition.y - enemyPosition.y);
        direction.nor();

        return direction ;
    }

    private void spawnEnemies() {
        Shark shark = new Shark();
        Vector2 randomPos = getRandomOffscreenPosition(100);
        shark.getSprite().setPosition(randomPos.x, randomPos.y);
        shark.getHitbox().setX(randomPos.x);
        shark.getHitbox().setY(randomPos.y);
        enemies.add(shark);
    }
}
