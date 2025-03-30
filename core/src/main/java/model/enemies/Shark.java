package model.enemies;

import java.util.Random;

public class Shark extends Enemy {

    private int x;
    private int y;

    public Shark () {

        super("entities/Shark.png", 100, 100);

        Random random = new Random();
        x = random.nextInt(0, (int) (1920-getSprite().getWidth()));
        y = random.nextInt(0, (int) (1080-getSprite().getHeight()));

        getSprite().setPosition(getSprite().getX()+x,getSprite().getY()+y);
        getSprite().setPosition(x,y);
        getHitbox().setX(x);
        getHitbox().setY(y);

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

}
