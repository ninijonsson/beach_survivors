package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.powerUps.PowerUp;
import com.beachsurvivors.model.powerUps.SpeedBoost;

public class Shark extends Enemy {



    public Shark () {

        super("entities/Shark.png", 100, 100);
        createAnimation("assets/entities/Shark-Sheet.png" , 4, 1);

        setHealthPoints(20);


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



//    public void setX(float x) {
//        this.x = x;
//    }
//
//    public void setY(float y) {
//        this.y = y;
//    }

    @Override
    public void move() {

    }

    @Override
    public void attack(Player player, Array enemyAbilities) {

    }



    @Override
    public void onDeath() {

        dispose();
    }

    @Override
    public void dropItems() {

    }



}
