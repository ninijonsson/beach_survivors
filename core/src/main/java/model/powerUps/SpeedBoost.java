package model.powerUps;

import com.badlogic.gdx.utils.Timer;
import model.PickUpAble;
import model.Player;

public class SpeedBoost extends PowerUp implements PickUpAble {

    private int speedIncrease = 300;
    private Timer timer;

    public SpeedBoost(int duration) {
        super("" ,duration);
    }

    @Override
    public void onPickup(Player player) {
        System.out.println("You picked up SpeedBoost");
        applyAffect(player);
    }

    @Override
    public void applyAffect(Player player) {
        player.increaseSpeed(speedIncrease);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                player.increaseSpeed(-speedIncrease);
            }
        }, 10);
        
    }
}
