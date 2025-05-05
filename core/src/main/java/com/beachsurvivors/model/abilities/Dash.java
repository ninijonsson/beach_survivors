package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.beachsurvivors.model.Player;

public class Dash extends Ability {

    private boolean dashOnCooldown;

    public Dash() {
        super("Dash" , "assets/placeholder.png" , AbilityType.MOVEMENT , 0, 0, 0 ,0 );

    }


    @Override
    public void use() {

    }

//    @Override
//    public void use(Player player) {
//        Vector2 playerDirection = player.getDirection();
//        int dashDistance = 400;
//        if (player.checkNewPositionValid(1,dashDistance)) {
//
//            if (playerDirection.x > 0) {
//                player.setPlayerX(player.getPlayerX() + dashDistance);
//
//            }
//            if (playerDirection.y > 0) {
//                player.setPlayerY(player.getPlayerY() + dashDistance);
//
//            }
//            if (playerDirection.x < 0) {
//                player.setPlayerX(player.getPlayerX() - dashDistance);
//
//            }
//            if (playerDirection.y < 0) {
//                player.setPlayerY(player.getPlayerY() - dashDistance);
//
//            }
//        }
//
//    }

    @Override
    public void use(Player player) {
        if (!dashOnCooldown) {
            dashOnCooldown = true;
            player.setSpeed(5000);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    player.setSpeed(400);
                }
            }, 0.1f);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    dashOnCooldown = false;
                }
            }, 5f);
        }

    }

}
