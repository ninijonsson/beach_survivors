package com.beachsurvivors.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.model.Map.Map;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.abilities.Ability;
import com.beachsurvivors.model.abilities.BaseAttack;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.view.DamageText;

import static com.badlogic.gdx.math.MathUtils.random;

public class PlayerController implements Screen {
    private Player player;
    private Vector2 position;
    private Controller controller;
    private FitViewport gameViewport;

    public PlayerController(Controller controller) {
        this.controller = controller;
        this.gameViewport = controller.getGameViewport();

        create();
    }

    public void create() {
        this.player = new Player(controller.getGameScreen().getSpriteBatch(), controller);
        this.position = new Vector2(player.getPlayerX(), player.getPlayerY());

        player.setPlayerX(controller.getMap().getStartingX());
        player.setPlayerY(controller.getMap().getStartingY());
        // player.setPosition(position.x, position.y);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // input();
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public void input() {
        player.playerInput();
    }

    public void logic() {}

    public void updateCameraPosition() {
        if (gameViewport != null) {
            gameViewport.getCamera().position.set(player.getPlayerX(), player.getPlayerY(), 0);
            gameViewport.getCamera().update();
            ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
            gameViewport.apply();

            player.drawAnimation();
        }
    }

    public void drawAnimation() {
        player.drawAnimation();
    }

    public void shoot() {
        float bulletCooldown = (float) controller.getAbilityController().getBullet().getCooldown();
        controller.getAbilityController().getBullet().addBulletTimer(Gdx.graphics.getDeltaTime());

        if (controller.getAbilityController().getBullet().getBulletTimer() >= bulletCooldown) {
            controller.getAbilityController().getBullet().setBulletTimer(0f);
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
            bullet.updatePosition(player.getPlayerX(), player.getPlayerY());

            controller.getAbilityController().addAbility(bullet);
        }
    }

    public Enemy getNearestEnemy() {
        Enemy nearest = null;
        float minDistance = 1000;
        position.set(player.getPlayerX(), player.getPlayerY());

        for (Enemy enemy : controller.getEnemyController().getEnemies()) {
            float distance = position.dst(enemy.getSprite().getX(), enemy.getSprite().getY());

            if (distance < minDistance) {
                minDistance = distance;
                nearest = enemy;
            }
        }
        return nearest;
    }

    public void checkIfPlayerAbilityHits(Enemy enemy) {
        Array<Ability> abilities = controller.getAbilityController().getAbilities();

        for (int j = abilities.size - 1; j >= 0; j--) {
            Ability ability = abilities.get(j);

            if (ability.getHitBox().overlaps(enemy.getHitbox())) {
                boolean isCritical = player.isCriticalHit();
                double damage = ability.getBaseDamage();
                if (isCritical) {
                    damage *= player.getCriticalHitDamage();
                }

                if (enemy.hit(damage)) {
                    //totalPlayerDamageDealt += damage;
                    /*damageTexts.add(new DamageText(String.valueOf((int) damage),
                            enemy.getSprite().getX() + random.nextInt(50),
                            enemy.getSprite().getY() + enemy.getSprite().getHeight() + 10 + random.nextInt(50),
                            1.0f,
                            isCritical));*/
                }

                if (!ability.isPersistent()) {
                    ability.dispose();
                    abilities.removeIndex(j);
                }
            }
        }
    }

    public void checkIfDamageAgainstPlayer(Enemy enemy) {
        if (enemy.getHitbox().overlaps(player.getHitBox())) {
            controller.getEnemyController().damagePlayer(enemy.getDamage());
        }
    }

    public void gainExp(int exp) {
        player.gainExp(exp);
    }

    // Getters & setters
    public Player getPlayer() {
        return player;
    }

    public Vector2 getPosition() { return position; }

    public void setPosition(Vector2 position) { player.setPosition(position.x, position.y); }
}
