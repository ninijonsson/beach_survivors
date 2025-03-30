package model.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import model.powerUps.PowerUp;
import model.powerUps.SpeedBoost;

import java.util.List;
import java.util.Random;

public class Shark extends Enemy {

    private int x;
    private int y;

    public Shark () {

        super("entities/Shark.png", 100, 100);

        setHealthPoints(10);

        Random random = new Random();
        x = random.nextInt(0, (int) (1920-getSprite().getWidth()));
        y = random.nextInt(0, (int) (1080-getSprite().getHeight()));

        getSprite().setPosition(getSprite().getX()+x,getSprite().getY()+y);
        getSprite().setPosition(x,y);
        getHitbox().setX(x);
        getHitbox().setY(y);

    }

    public void hit(double damageTaken){
        setHealthPoints(-damageTaken);
        if(getHealthPoints()<=0){
            setAlive(false);
        }
    }

    @Override
    public void move() {

    }

    @Override
    public void attack() {

    }

    @Override
    public void spawnEnemy() {

    }

    @Override
    public void onDeath() {

    }

    @Override
    public void dropItems() {

    }

    public void dropItems(List<PowerUp> droppedItems) {
        SpeedBoost speedBoost = new SpeedBoost((getSprite().getWidth()/2)+x, (getSprite().getHeight()/2) + y);
        droppedItems.add(speedBoost);
        System.out.println("Item dropped");

    }

}
