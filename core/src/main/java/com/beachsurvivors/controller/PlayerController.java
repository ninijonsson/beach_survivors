package com.beachsurvivors.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.beachsurvivors.model.Map.Map;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.Enemy;

public class PlayerController extends Game {
    private Player player;
    private Vector2 position;
    private Controller controller;

    public PlayerController(Controller controller) {
        this.controller = controller;

        create();
    }

    @Override
    public void create() {
        this.player = new Player(new SpriteBatch());
        this.position = new Vector2(player.getX(), player.getY());
        player.setPosition(position.x, position.y);
    }

    public void logic() {}

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
