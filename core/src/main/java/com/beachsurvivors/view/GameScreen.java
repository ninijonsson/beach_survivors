package com.beachsurvivors.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.beachsurvivors.model.abilities.Boomerang;
import com.beachsurvivors.model.DamageText;
import com.beachsurvivors.model.Map.Map;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.SmokeParticle;
import com.beachsurvivors.model.abilities.Ability;
import com.beachsurvivors.model.abilities.AbilityType;
import com.beachsurvivors.model.abilities.BaseAttack;
import com.beachsurvivors.model.enemies.Crocodile;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.model.enemies.NavySeal;
import com.beachsurvivors.model.enemies.Shark;
import com.beachsurvivors.model.powerUps.Berserk;
import com.beachsurvivors.model.powerUps.PowerUp;

//import java.lang.classfile.attribute.BootstrapMethodsAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreen extends Game implements Screen {

    private Main main;
    private int enemiesToSpawn = 20;
    private SpriteBatch spriteBatch;
    private FitViewport gameviewport;
    private Stage stage;

    private final int screenWidth = 1920;
    private final int screenHeight = 1080;
    private ShapeRenderer shapeRenderer;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Map map;


    private Player player;
    private Array<PowerUp> droppedItems;

    private Array<Ability> abilities;
    private Boomerang boomerang;
    private Boomerang boomerang2;
    private Boomerang boomerang3;
    private Boomerang boomerang4;
    private BaseAttack bullet;
    private float bulletTimer = 0f;
    private int sharksKilled;

    private Array<SmokeParticle> smokeTrail = new Array<>();

    private BitmapFont font;
    private Array<DamageText> damageTexts = new Array<>();
    private Random randomizeDirection = new Random();


    private Array<Enemy> enemies = new Array<>();
    private Vector2 playerPos;


    public GameScreen(Main main) {
        this.main = main;

        gameviewport = new FitViewport(screenWidth, screenHeight);
        droppedItems = new Array<>();
        abilities = new Array<>();
        sharksKilled = 0;
        create();
    }

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();


        shapeRenderer = new ShapeRenderer();

        tiledMap = new TmxMapLoader().load("Map2/map2.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2f);
        this.map = new Map(tiledMap);
        stage = new Stage(gameviewport);
        stage.clear();
        player = new Player(map, spriteBatch);
        playerPos = new Vector2(player.getPlayerX(), player.getPlayerY());

        boomerang = new Boomerang();
        boomerang2 = new Boomerang();
        boomerang2.setAngle(90);
        boomerang3 = new Boomerang();
        boomerang3.setAngle(180);
        boomerang4 = new Boomerang();
        boomerang4.setAngle(270);

        bullet = new BaseAttack();


        abilities.add(boomerang);
        abilities.add(boomerang2);
        abilities.add(boomerang3);
        abilities.add(boomerang4);
        abilities.add(bullet);


        font = new BitmapFont();
        font.setColor(Color.YELLOW);
        font.getData().setScale(2);

//        mapWidth = tiledMap.getProperties().get("width", Integer.class) *
//            tiledMap.getProperties().get("tilewidth", Integer.class);
//
//        mapHeight = tiledMap.getProperties().get("height", Integer.class) *
//            tiledMap.getProperties().get("tileheight", Integer.class);

        player.setPlayerX(map.getStartingX());
        player.setPlayerY(map.getStartingY());
    }

    @Override
    public void show() {

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        input();
        logic();
        draw();
    }


    @Override
    public void resize(int width, int height) {
        gameviewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    private void draw() {
        gameviewport.getCamera().position.set(player.getPlayerX(), player.getPlayerY(), 0);
        gameviewport.getCamera().update();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        gameviewport.apply();

        mapRenderer.setView((OrthographicCamera) gameviewport.getCamera());

        mapRenderer.render();

        spriteBatch.setProjectionMatrix(gameviewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(gameviewport.getCamera().combined);

        player.runAnimation();

        spriteBatch.begin();

        drawPlayer();

        stage.act();
        stage.draw();

//        player.getSprite().draw(spriteBatch);
        player.getHitBox().setPosition(player.getPlayerX(), player.getPlayerY());


        drawStuff();

        spriteBatch.end();
    }

    private void drawStuff() {
        drawAbilities();
        drawDamageText();
        drawPowerUp();
        drawSmokeTrail();
        drawEnemies();
    }

    private void drawEnemies() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                // enemy.getSprite().draw(spriteBatch);
                enemy.drawAnimation(spriteBatch);
            }

        }
    }

    private void drawSmokeTrail() {
        for (SmokeParticle s : smokeTrail) {
            s.getSprite().draw(spriteBatch);
        }
    }

    private void drawPowerUp() {
        for (PowerUp powerUp : droppedItems) {
            powerUp.getSprite().draw(spriteBatch);
        }
    }

    private void drawDamageText() {
        for (DamageText dt : damageTexts) {
            dt.draw(spriteBatch);
        }
    }

    private void drawAbilities() {
        for (Ability a : abilities) {
            a.getSprite().draw(spriteBatch);
        }
    }

    /**
     * HITBOXES FOR PLAYER
     */
    private void drawPlayer() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CLEAR);
        Rectangle hitbox = player.getHitBox();
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        shapeRenderer.end();
    }

    private void input() {
        player.playerInput();
    }

    private void logic() {
        pickUpPowerUp();
        player.getSprite().setX(MathUtils.clamp(player.getSprite().getX(), 0, screenWidth - player.getSprite().getWidth()));
        player.getSprite().setY(MathUtils.clamp(player.getSprite().getY(), 0, screenHeight - player.getSprite().getHeight()));

        //ABILITIES
        for (Ability a : abilities) {
            a.updatePosition(Gdx.graphics.getDeltaTime(), player.getPlayerX(), player.getPlayerY());
        }

        //addSmokeTrails();


        // BULLET
        float bulletCooldown = (float) bullet.getCooldown(); // Gör om cooldown till float

        bulletTimer += Gdx.graphics.getDeltaTime();

        if (bulletTimer >= bulletCooldown) {
            bulletTimer = 0f;
            shootAtNearestEnemy();
        }

        for (int i = damageTexts.size - 1; i >= 0; i--) {
            DamageText dt = damageTexts.get(i);
            dt.update(Gdx.graphics.getDeltaTime() * 2);
            if (!dt.isActive()) {
                damageTexts.removeIndex(i);
            }
        }

        if (enemies.size < enemiesToSpawn) spawnEnemies();
        for (int i = enemies.size - 1; i >= 0; i--) {

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

            int randomPathX = randomizeDirection.nextInt(50);
            int randomPathY = randomizeDirection.nextInt(50);
            float damageTextX = enemy.getSprite().getX() + randomPathX;
            float damageTextY = enemy.getSprite().getY() + enemy.getSprite().getHeight() + 10 + randomPathY;

            if (!enemy.isAlive()) {
                enemy.dropItems(droppedItems);
                enemies.removeIndex(i);
                //System.out.println("Sharks killed: " + sharksKilled++);
            }

            for (int j = abilities.size - 1; j >= 0; j--) {
                Ability a = abilities.get(j);

                if (a.getHitBox().overlaps(enemy.getHitbox())) {
                    boolean isCritical = checkForCriticalStrike();
                    double damage = a.getBaseDamage();
                    if (isCritical) {
                        damage *= 2; // Dubblera skadan vid kritiskt slag
                    }

                    if (enemy.hit(damage)) {
                        damageTexts.add(new DamageText(String.valueOf((int) damage),
                            enemy.getSprite().getX() + randomizeDirection.nextInt(50),
                            enemy.getSprite().getY() + enemy.getSprite().getHeight() + 10 + randomizeDirection.nextInt(50),
                            1.0f,
                            isCritical));
                    }

                    // Om ability inte är en boomerang så tar vi bort spriten när den kolliderat
                    if (!(a instanceof Boomerang)) {
                        a.dispose();
                        abilities.removeIndex(j);
                    }
                }
            }
        }
        player.getSprite().setPosition(player.getPlayerX() - player.getSprite().getWidth() / 2, player.getPlayerY() - player.getSprite().getHeight() / 2);
        player.getHitBox().setPosition(player.getPlayerX(), player.getPlayerY());
    }

    private void addSmokeTrails() {
        smokeTrail.add(new SmokeParticle(boomerang.getSprite().getX(), boomerang.getSprite().getY()));
        smokeTrail.add(new SmokeParticle(boomerang2.getSprite().getX(), boomerang2.getSprite().getY()));
        smokeTrail.add(new SmokeParticle(boomerang3.getSprite().getX(), boomerang3.getSprite().getY()));
        smokeTrail.add(new SmokeParticle(boomerang4.getSprite().getX(), boomerang4.getSprite().getY()));

        for (int i = smokeTrail.size - 1; i >= 0; i--) {
            SmokeParticle s = smokeTrail.get(i);
            s.update(Gdx.graphics.getDeltaTime());
            if (s.isDead()) {
                smokeTrail.removeIndex(i);
            }
        }
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
                // Extra kontroll eftersom vi vill skicka med bullet-objektet
                // för att kunna justera damage och cooldown
                if (powerUp instanceof Berserk) {
                    powerUp.onPickup(player);
                    ((Berserk) powerUp).onPickupBullet(bullet);
                } else {
                    System.err.println("true");
                    powerUp.onPickup(player);
                }

                powerUpsToRemove.add(powerUp);
                powerUp.dispose();
            }
        }
        droppedItems.removeAll(powerUpsToRemove, true);
    }

    // Används för att skjuta på närmaste fienden
    private Enemy getNearestEnemy() {
        Enemy nearest = null;
        float minDistance = Float.MAX_VALUE;
        Vector2 playerPos = new Vector2(player.getPlayerX(), player.getPlayerY());

        for (Enemy enemy : enemies) {
            // Räkna ut avståndet mellan spelaren och fienden
            float distance = playerPos.dst(enemy.getSprite().getX(), enemy.getSprite().getY());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = enemy;
            }
        }

        return nearest;
    }

    private void shootAtNearestEnemy() {
        Enemy target = getNearestEnemy();

        if (target != null) {
            // Beräkna riktningen från spelaren till fienden
            Vector2 direction = new Vector2(
                target.getSprite().getX() - player.getPlayerX(),
                target.getSprite().getY() - player.getPlayerY()
            ).nor();

            // Skapa en ny bullet
            BaseAttack bullet = new BaseAttack();

            bullet.updatePosition(player.getPlayerX(), player.getPlayerY()); // Startpositionen är spelarens position
            bullet.setDirection(direction); // Mot fienden

            //bullet.getSprite().setOriginCenter(); // Sätt spritens ursprung till mitten så rotationen blir korrekt
            //float angleDeg = direction.angleDeg();
            //bullet.getSprite().setRotation(angleDeg); // Rotera spriten så att den pekar mot riktningen

            abilities.add(bullet);
        }
    }


    @Override
    public void resume() {
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
        //bullet.dispose();
    }

    // Returns random position off-screen. Parameter 'margin' is the off-screen distance between spawn point and screen edge. Put margin = 0 for enemies to spawn exactly outside screen.
    public Vector2 getRandomOffscreenPosition(float margin) {
        float viewWidth = gameviewport.getCamera().viewportWidth;
        float viewHeight = gameviewport.getCamera().viewportHeight;
        float camX = gameviewport.getCamera().position.x;
        float camY = gameviewport.getCamera().position.y;

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

        return direction;
    }

    private void spawnEnemies() {
        Random random = new Random();
        int enemyChoice = random.nextInt(0,3);
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
        }


//        Shark shark = new Shark();

        Vector2 randomPos = getRandomOffscreenPosition(100);
        enemy.getSprite().setPosition(randomPos.x, randomPos.y);
        enemy.getHitbox().setX(randomPos.x);
        enemy.getHitbox().setY(randomPos.y);
        enemies.add(enemy);
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }
}
