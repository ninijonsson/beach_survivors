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
import model.enemies.Enemy;
import model.enemies.Shark;
import model.powerUps.PowerUp;

import java.util.ArrayList;
import java.util.List;
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
    private List<PowerUp> droppedItems;

    private Coconut coconut;
    private float coconutSpeed = 280;
    private float angle;
    private float orbitRadius = 200;
    private float previousAngle = 0;
    private Array<SmokeParticle> smokeTrail = new Array<>();
    private boolean hasDamagedThisOrbit = false;
    private BitmapFont font;
    private Array<DamageText> damageTexts = new Array<>();
    private Random randomizeDirection = new Random();

    private Array<Shark> enemies = new Array<>();
    private Vector2 playerPos;

    private int mapWidth;
    private int mapHeight;
    private float gameScale;

    public GameScreen(Main main) {
        this.main = main;
        gameviewport = new FitViewport(worldWidth, worldHeight);
        droppedItems = new ArrayList<>();
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

        tiledMap = new TmxMapLoader().load("Maps/beachTest2.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2f);

        stage = new Stage(gameviewport);
        stage.clear();

        coconut = new Coconut();
        angle = 0;

        font = new BitmapFont();
        font.setColor(Color.YELLOW);
        font.getData().setScale(2);
        gameScale = 2f;
        mapWidth = tiledMap.getProperties().get("width", Integer.class) *
            tiledMap.getProperties().get("tilewidth", Integer.class);

        mapHeight = tiledMap.getProperties().get("height", Integer.class) *
            tiledMap.getProperties().get("tileheight", Integer.class);
        System.out.println("Map width: " + mapWidth + ", Map height: " + mapHeight);
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

        spriteBatch.end();

        spriteBatch.begin();
        player.getSprite().draw(spriteBatch);
        player.getHitBox().setPosition(player.getPlayerX(), player.getPlayerY());
        shark.getSprite().draw(spriteBatch);
        spriteBatch.end();

        drawPlayer();

        stage.act();
        stage.draw();

        spriteBatch.begin();
        player.getSprite().draw(spriteBatch);
        player.getHitBox().setPosition(player.getPlayerX(), player.getPlayerY());
        shark.getSprite().draw(spriteBatch);
        coconut.getSprite().draw(spriteBatch);

        // Rita alla DamageText-objekt
        for (DamageText dt : damageTexts) {
            dt.draw(spriteBatch);
        }
        for (PowerUp powerUp : droppedItems) {
            powerUp.getSprite().draw(spriteBatch);
        }

        spriteBatch.end();

        spriteBatch.begin();

        for (SmokeParticle s : smokeTrail) {
            s.getSprite().draw(spriteBatch);
        }

        player.getSprite().draw(spriteBatch);
        shark.getSprite().draw(spriteBatch);
        coconut.getSprite().draw(spriteBatch);

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

        if (player.getHitBox().overlaps(shark.getHitbox())) {
            shark.dropItems(droppedItems);
            shark = null;
            shark = new Shark();
            player.increaseSpeed(50);
        }

        pickUpPowerUp();

        // COCONUT SPIN SKIT
        angle += coconutSpeed * Gdx.graphics.getDeltaTime();
        angle %= 360;


        if (angle < previousAngle) {
            hasDamagedThisOrbit = false;
        }

        previousAngle = angle;

        float radian = MathUtils.degreesToRadians * angle;

        float coconutX = player.getPlayerX() + player.getSprite().getWidth() / 2 + MathUtils.cos(radian) * orbitRadius - coconut.getSprite().getWidth() / 2;
        float coconutY = player.getPlayerY() + player.getSprite().getHeight() / 2 + MathUtils.sin(radian) * orbitRadius - coconut.getSprite().getHeight() / 2;

        coconut.updatePosition(coconutX, coconutY);

        // SMOKE TRAIL
        smokeTrail.add(new SmokeParticle(coconut.getSprite().getX(), coconut.getSprite().getY()));

        for (int i = smokeTrail.size - 1; i >= 0; i--) {
            SmokeParticle s = smokeTrail.get(i);
            s.update(Gdx.graphics.getDeltaTime());
            if (s.isDead()) {
                smokeTrail.removeIndex(i);
            }
        }

        if (coconut.getHitBox().overlaps(shark.getHitbox()) && !hasDamagedThisOrbit) {
            double damage = coconut.getDamage();
            boolean isCritical = checkForCriticalStrike();

            if (isCritical) {
                damage *= 2; // Dubblera skadan vid kritiskt slag
            }

            shark.hit(damage);


            int randomPathX = randomizeDirection.nextInt(50);
            int randomPathY = randomizeDirection.nextInt(50);

            float damageTextX = shark.getSprite().getX() + randomPathX;
            float damageTextY = shark.getSprite().getY() + shark.getSprite().getHeight() + 10 + randomPathY;

            damageTexts.add(new DamageText(String.valueOf((int) damage),
                damageTextX,
                damageTextY,
                3.0f, // damageText visas i 3 sekunder
                isCritical));
            if (!shark.isAlive()) {
                shark.dropItems(droppedItems);
                shark = new Shark();
            }
            hasDamagedThisOrbit = true;
        }


        for (int i = damageTexts.size - 1; i >= 0; i--) {
            DamageText dt = damageTexts.get(i);
            dt.update(Gdx.graphics.getDeltaTime());
            if (!dt.isActive()) {
                damageTexts.removeIndex(i);
            }
        }

        spawnEnemies();

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
        List<PowerUp> powerUpsToRemove = new ArrayList<>();
        for (PowerUp powerUp : droppedItems) {
            if (player.getHitBox().overlaps(powerUp.getHitbox())) {
                powerUp.onPickup(player);
                powerUp.dispose();
                powerUpsToRemove.add(powerUp);
            }
        }
        droppedItems.removeAll(powerUpsToRemove);
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
        coconut.dispose();
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
//        shark.getHitbox().setX(randomPos.x);
//        shark.getHitbox().setY(randomPos.y);
        enemies.add(shark);
    }
}
