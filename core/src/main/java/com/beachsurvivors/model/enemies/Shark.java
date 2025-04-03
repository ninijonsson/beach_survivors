package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.powerUps.PowerUp;
import com.beachsurvivors.model.powerUps.SpeedBoost;

public class Shark extends Enemy {

    private float x;
    private float y;

    public Shark () {

        super("entities/Shark.png", 100, 100);
        createAnimation("assets/entities/Shark-Sheet.png" , 4, 1);

        setHealthPoints(20);

//        Random random = new Random();
//        x = random.nextInt(0, (int) (1920-getSprite().getWidth()));
//        y = random.nextInt(0, (int) (1080-getSprite().getHeight()));

//        getSprite().setPosition(getSprite().getX()+x,getSprite().getY()+y);
        getSprite().setPosition(x,y);
        getHitbox().setX(x);
        getHitbox().setY(y);

    }

    /*
    public void hit(double damageTaken){
        if(!justTookDamage()){
            setHealthPoints(-damageTaken);
            if(getHealthPoints()<=0){
                setAlive(false);
            }
            playSound();
        }

    }*/



    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void move() {

    }

    @Override
    public void attack() {

    }



    @Override
    public void onDeath() {

        dispose();
    }

    @Override
    public void dropItems() {

    }

    public void dropItems(Array<PowerUp> droppedItems) {
        SpeedBoost speedBoost = new SpeedBoost((getSprite().getWidth()/2)+x, (getSprite().getHeight()/2) + y);
        droppedItems.add(speedBoost);
        System.out.println("Item dropped");

    }

}
