package com.beachsurvivors.model;

import com.beachsurvivors.model.abilities.Ability;

import java.util.List;

public class Chest implements PickUpAble {

    private List<Ability> abilitiesToChose;  //De 3 abilities man kan välja mellan ifall man tar upp en kista

    @Override
    public void onPickup(Player player) {

    }
}
