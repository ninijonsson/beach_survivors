package com.beachsurvivors.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.model.Map.Map;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.Enemy;

public class PlayerController extends Game implements Screen {
    private Player player;
    private Vector2 position;
    private Controller controller;
    private FitViewport gameViewport;

    public PlayerController(Controller controller) {
        this.controller = controller;
        this.gameViewport = controller.getGameViewport();

        create();
    }

    @Override
    public void create() {
        this.player = new Player(new SpriteBatch(), controller);
        this.position = new Vector2(player.getPlayerX(), player.getPlayerY());

        player.setPlayerX(controller.getGameManager().getMap().getStartingX());
        player.setPlayerY(controller.getGameManager().getMap().getStartingY());
        // player.setPosition(position.x, position.y);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        input();
    }

    @Override
    public void hide() {

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

    public void shoot() {}

    public void shootAtNearestEnemy() {}

    public void checkIfPlayerAbilityHits(Enemy enemy) {}

    public void checkIfDamageAgainstPlayer(Enemy enemy) {}

    public void gainExp(int exp) {
        player.gainExp(exp);
    }

    public void pickUpPowerUp() {}

    public void pickUpGroundItem() {}

    // Getters & setters
    public Player getPlayer() {
        return player;
    }

    public Vector2 getPosition() { return position; }

    public void setPosition(Vector2 position) { this.position = position; }
}
