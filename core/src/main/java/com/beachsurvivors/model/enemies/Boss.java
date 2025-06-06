package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.Bullet;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.PuzzleOrb;
import com.beachsurvivors.model.abilities.BombAttack;
import com.beachsurvivors.utilities.ParticleEffectPoolManager;
import com.beachsurvivors.view.DamageText;
import com.beachsurvivors.view.GameScreen;
import com.beachsurvivors.view.Main;
import com.beachsurvivors.view.VictoryScreen;

public class Boss {

    private float phaseTimer;
    private int phase = 0;
    private boolean isAlive = false;

    private float scale = 1f;
    private boolean scaleUp = true;

    private boolean isVulnerable = false;
    private float vulnerableTimer = 0f;
    private float vulnerabilityDuration = 5f;
    private PuzzleOrb puzzleOrb;
    private float orbCooldown = 10f;
    private boolean orbPickedUp = false;

    private Color tint = Color.WHITE;
    private Texture walkSheet;
    private Animation<TextureRegion> walkAnimation;
    private float stateTime;
    private final Array<DamageText> damageText;

    private GameScreen game;
    private Camera camera;
    private Vector2 position;
    private float width;
    private float height;
    private double healthPoints;
    private Sprite sprite;
    private Rectangle hitbox;

    private Array<Bullet> bullets = new Array<>();
    private float bulletCooldown;
    private float bulletSpeed = 200f;
    private float spiralAngle = 0;
    private float patternAngleOffset = 0f;
    private float angleStep = 40f; // Amount to rotate pattern every time

    private Array<BombAttack> bombs = new Array<>();
    private float bombCooldown;
    private ParticleEffectPoolManager poolManager;

    private Player player; // to track player position
    private BitmapFont font = new BitmapFont();

    private Sprite arrowSprite;
    private Sound bossHit;
    private Sound cannonFireSound;
    private Sound kakaaw;
    private Sound hitSound;
    private Main main;
    private float damageCooldown = 0f;



    public Boss(Vector2 position, ParticleEffectPoolManager poolManager, Camera camera, GameScreen game, Main main, Array<DamageText> damageText) {
        this.position = position;
        width = 700;
        height = 700;
        healthPoints = 500;
        sprite = new Sprite(new Texture("entities/enemies/slutboss.png"));
        hitbox = new Rectangle(position.x, position.y, width, height);
        this.poolManager = poolManager;
        this.damageText = damageText;
        arrowSprite = new Sprite(new Texture("redarrow.png"));
        arrowSprite.setSize(1000, 1000);

        this.bossHit = AssetLoader.get().getSound("entities/abilities/boss_damage.wav");
        this.cannonFireSound = AssetLoader.get().getSound("entities/abilities/boss_cannon_fire.wav");
        this.kakaaw = AssetLoader.get().getSound("entities/abilities/boss_kakaaw.wav");
        this.hitSound = AssetLoader.get().getSound("sounds/shark_damage_2.wav");
        this.camera = camera;
        this.game = game;
        this.main = main;
        isAlive = true;
        createAnimation();
    }

    public void draw(SpriteBatch spriteBatch) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);

        spriteBatch.setColor(tint);
        spriteBatch.draw(currentFrame, position.x - width / 2, position.y - height / 2, width / 2, height / 2, width, height, scale, scale, 0f);
        spriteBatch.setColor(Color.WHITE);

        if (isVulnerable) {
            font.setColor(Color.RED);
            font.getData().setScale(3f);
            font.draw(spriteBatch, "BOSS VULNERABLE", position.x - 200, position.y + height / 2 + 50);
        }

        for (Bullet bullet : bullets) bullet.draw(spriteBatch);
        for (BombAttack bomb : bombs) bomb.draw(spriteBatch);
        if (puzzleOrb != null) puzzleOrb.draw(spriteBatch);

        if (camera != null && puzzleOrb != null) {
            Rectangle viewBounds = new Rectangle(camera.position.x - camera.viewportWidth / 2,
                camera.position.y - camera.viewportHeight / 2,
                camera.viewportWidth,
                camera.viewportHeight);
            if (!puzzleOrb.overlaps(viewBounds)) {
                Vector2 direction = new Vector2(puzzleOrb.getPosition()).sub(camera.position.x, camera.position.y).nor();
                float arrowDistanceFromCenter = 400f;
                Vector2 arrowPosition = new Vector2(camera.position.x, camera.position.y).add(direction.scl(arrowDistanceFromCenter));
                float angle = direction.angleDeg();

                arrowSprite.setOriginCenter();
                arrowSprite.setRotation(angle);
                arrowSprite.setPosition(arrowPosition.x - arrowSprite.getWidth() / 2, arrowPosition.y - arrowSprite.getHeight() / 2);
                arrowSprite.setScale(0.1f);
                arrowSprite.draw(spriteBatch);
            }
        }
    }


    public void update(float delta, Player player) {
        if (damageCooldown > 0f) {
            damageCooldown -= delta;
        }

        this.player = player;
        hitbox.setPosition(position.x - width / 2, position.y - height / 2);
        sprite.setPosition(position.x - width / 2, position.y - height / 2);

        // visual feedback when vulnerable
        if (isVulnerable) {
            if (scaleUp) {
                scale += 0.005f;
                if (scale > 1.05f) scaleUp = false;
            } else {
                scale -= 0.005f;
                if (scale < 0.95f) scaleUp = true;
            }
        } else {
            scale = 1f;
        }


        phaseTimer += delta;
        bulletCooldown -= delta;
        bombCooldown -= delta;

        if (isVulnerable) {
            vulnerableTimer -= delta;
            if (vulnerableTimer <= 0) {
                isVulnerable = false;
            }
        }

        handlePhases(delta, player);

        for (int i = 0; i < bullets.size; i++) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta);
            if (bullet.isExpired()) {
                bullets.removeIndex(i);
            }

            if (bullet.overlaps(player)) {
                game.damagePlayer(50);
            }
        }


        for (int i = 0; i < bombs.size; i++) {
            BombAttack bomb = bombs.get(i);
            bomb.update(delta);

            if (bomb.isFinished()) {
                bombs.removeIndex(i);
            }

        }

//
    }

    private void nextPhase() {
        phase++;
        phaseTimer = 0;
        orbPickedUp = false;

        kakaaw.play(0.8f, MathUtils.random(0.95f, 1.05f), 0f);
        damageText.add(new DamageText("KAKAAAW! KAKAAAW!", position.x, position.y + 150, 6, true));
    }

    private float getHealthPercent() {
        return (float) healthPoints / 1000f; // or whatever max HP is
    }

    private void spawnPuzzleOrbIfNeeded(float delta, Player player) {
        orbCooldown -= delta;

        if (puzzleOrb == null && orbCooldown <= 0) {
            puzzleOrb = new PuzzleOrb(getSafeRandomOrbSpawnPosition(player));
            orbCooldown = 15f;
            orbPickedUp = false;
        }

        if (puzzleOrb != null) {
            puzzleOrb.update(delta);
            if (puzzleOrb.overlaps(player.getHitBox())) {
                puzzleOrb.deactivate();
                puzzleOrb = null;
                makeVulnerable();
                orbPickedUp = true;
            }
        }
    }

    private void phaseZero(float delta, Player player) {
        if (bulletCooldown <= 0) {
            fireExpandingSpiral();
            bulletCooldown = 0.05f;
        }
        if (bombCooldown <= 0) {
            dropBomb(new Vector2(player.getPosition()));
            bombCooldown = 5f;
        }
        spawnPuzzleOrbIfNeeded(delta, player);
    }

    private void phaseOne(float delta, Player player) {
        if (bulletCooldown <= 0) {
            fireRadial();
            bulletCooldown = 1.5f;
        }
        spawnPuzzleOrbIfNeeded(delta, player);
    }

    private void phaseTwo(float delta, Player player) {
        if (bulletCooldown <= 0) {
            fireSpiral();
            bulletCooldown = 0.75f;
        }
        if (bombCooldown <= 0) {
            dropBomb(new Vector2(player.getPosition()));
            bombCooldown = 4f;
        }
        spawnPuzzleOrbIfNeeded(delta, player);
    }

    private void phaseThree(float delta, Player player) {
        if (bulletCooldown <= 0) {
            fireRadial();
            fireSpiral();
            bulletCooldown = 1.5f;
        }
        if (bombCooldown <= 0) {
            dropBomb(new Vector2(player.getPosition()));
            bombCooldown = 3f;
        }
        spawnPuzzleOrbIfNeeded(delta, player);
    }


    private void handlePhases(float delta, Player player) {
        phaseTimer += delta;

        switch (phase) {
            case 0:
                if (!isVulnerable) phaseZero(delta, player);
                if (phaseTimer > 10f || getHealthPercent() < 0.9f) nextPhase();
                break;

            case 1:
                if (!isVulnerable) phaseOne(delta, player);
                if (orbPickedUp && (phaseTimer > 15f || getHealthPercent() < 0.65f)) nextPhase();
                break;

            case 2:
                if (!isVulnerable) phaseTwo(delta, player);
                if (orbPickedUp && (phaseTimer > 20f || getHealthPercent() < 0.35f)) nextPhase();
                break;

            case 3:
                if (!isVulnerable) phaseThree(delta, player);
                break;

            default:

                break;
        }
    }

    private void fireExpandingSpiral() {
        spiralAngle += 15f;
        if (spiralAngle >= 360f) spiralAngle -= 360f;

        spawnBullet(angleToVector(spiralAngle));
    }

    private void fireRadial() {
        for (int i = 0; i < 360; i += 15) {
            float angle = i + patternAngleOffset;
            spawnBullet(angleToVector(angle));
        }
        patternAngleOffset += angleStep;
        if (patternAngleOffset >= 360f) patternAngleOffset -= 360f;
    }

    private void fireSpiral() {
        float baseAngle = (System.currentTimeMillis() % 3600) / 10f;

        int spirals = 4; // 4 bullets spiraling outward
        for (int i = 0; i < spirals; i++) {
            float angle = baseAngle + (i * 90f); // spread them out evenly
            Vector2 velocity = angleToVector(angle);
            spawnBullet(velocity);
        }
    }


    private Vector2 angleToVector(float angle) {
        float radians = angle * MathUtils.degreesToRadians;

        float vx = MathUtils.cos(radians) * bulletSpeed;
        float vy = MathUtils.sin(radians) * bulletSpeed;

        return new Vector2(vx, vy);
    }

    private void spawnBullet(Vector2 velocity) {
        cannonFireSound.play(0.8f, MathUtils.random(0.95f, 1.05f), 0f);
        bullets.add(new Bullet(new Vector2(position.x + 320, position.y - 100), velocity, poolManager));
    }

    private void dropBomb(Vector2 position) {
        bombs.add(new BombAttack(position));
    }

    public void drawBulletHitboxes(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        for (Bullet bullet : bullets) {
            Circle hitbox = bullet.getHitbox();
            shapeRenderer.circle(hitbox.x, hitbox.y, hitbox.radius);
        }

        shapeRenderer.end();
    }

//    private void puzzleOrbLogic(float delta, Player player) {
//        // Puzzle orb logic
//        orbCooldown -= delta;
//        if (puzzleOrb == null && orbCooldown <= 0) {
//            puzzleOrb = new PuzzleOrb(getSafeRandomOrbSpawnPosition(player));
//            orbCooldown = 15f; // next orb in 15 seconds
//        }
//
//        if (puzzleOrb != null) {
//            puzzleOrb.update(delta);
//            if (puzzleOrb.overlaps(player.getHitBox())) {
//                puzzleOrb.deactivate();
//                puzzleOrb = null;
//                makeVulnerable();
//            }
//        }
//
//    }

    private void makeVulnerable() {
        isVulnerable = true;
        vulnerableTimer = vulnerabilityDuration;

        // Automatically turn off vulnerability after X seconds
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Gdx.app.postRunnable(() -> {
                    isVulnerable = false;
                });
            }
        }, vulnerabilityDuration);
    }

    public void takeDamage(float amount) {
        if (!isVulnerable) return;

        healthPoints -= amount;
        if (healthPoints <= 0) {
            onDeath();
        }
    }

    public boolean hit(double damage) {
        if (!isVulnerable || damageCooldown > 0f) return false;

        healthPoints -= damage;
        damageCooldown = 0.1f;

        float pitch = MathUtils.random(0.9f, 1.1f);
        bossHit.play(0.9f, pitch, 0f);
        hitSound.play(0.8f, MathUtils.random(0.95f, 1.05f), 0f);
        if (healthPoints <= 0) {
            isAlive = false;
            onDeath();
            return true;
        } else {
            tint = Color.RED;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Gdx.app.postRunnable(() -> tint = Color.WHITE);
                }
            }, 0.1f);
            return true;
        }
    }


    private Vector2 getSafeRandomOrbSpawnPosition(Player player) {
        Vector2 spawnPos;
        do {
            spawnPos = getRandomOrbSpawnPosition();
        } while (
            spawnPos.dst(position) < 400f ||  // Too close to boss
                spawnPos.dst(new Vector2(player.getPosition())) < 800f  // Too close to player
        );
        return spawnPos;
    }


    private Vector2 getRandomOrbSpawnPosition() {
        float minDistance = 1000f;
        float maxDistance = 2000f;

        float angle = MathUtils.random(0f, 360f);
        float distance = MathUtils.random(minDistance, maxDistance);

        float offsetX = MathUtils.cosDeg(angle) * distance;
        float offsetY = MathUtils.sinDeg(angle) * distance;

        return new Vector2(position.x + offsetX, position.y + offsetY);
    }


    private void createAnimation() {
        Texture texture = new Texture("entities/enemies/slutboss_sheet.png");
        int sheetColumns = 5;
        int sheetRows = 1;

        walkSheet = texture;
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth() / sheetColumns, walkSheet.getHeight() / sheetRows);
        TextureRegion[] walkFrames = new TextureRegion[sheetColumns * sheetRows];
        int index = 0;

        for (int j = 0; j < sheetColumns; j++) {
            walkFrames[index++] = tmp[0][j];
        }

        walkAnimation = new Animation<>(0.15f, walkFrames);
        stateTime = 0f;
    }

    /*public void drawAnimation(SpriteBatch spriteBatch) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        if (isMovingLeft() && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (isMovingRight() && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        spriteBatch.setColor(tint);
        spriteBatch.draw(currentFrame, sprite.getX(), sprite.getY(), getWidth(), getHeight());
        spriteBatch.setColor(Color.WHITE);
    }*/

    private void clearScreen() {
        bullets.clear();
        bombs.clear();
        puzzleOrb = null;
    }

    private void onDeath() {
        isAlive = false;
        System.out.println("boss dead");
        main.setScreen(new VictoryScreen(game, game.getTotalEnemiesKilled(), game.getTotalPlayerDamageDealt(), 0, 0, 0, 0));
//        clearScreen();
    }

    public boolean isAlive() {
        return isAlive;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public double getHealthPoints() {
        return healthPoints;
    }
}

