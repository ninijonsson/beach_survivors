package com.beachsurvivors.model.enemies;

public class NavySeal extends Enemy {


    public NavySeal() {
        super("", 100, 100);

        createAnimation("assets/entities/Seal rocket launcher-Sheet.png", 4, 1);

        setHealthPoints(30);

    }

    @Override
    public void move() {

    }

    @Override
    public void attack() {

    }

    @Override
    public void onDeath() {

    }

    @Override
    public void dropItems() {

    }
}
