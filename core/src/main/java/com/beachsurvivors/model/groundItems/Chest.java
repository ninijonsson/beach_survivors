package com.beachsurvivors.model.groundItems;

import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.abilities.Ability;

import java.util.List;

public class Chest extends GroundItem implements PickUpAble {

    private List<Ability> abilitiesToChose;  //De 3 abilities man kan välja mellan ifall man tar upp en kista

    public Chest(float x, float y) {
        super("entities/chest.png", x, y);
    }

    @Override
    public void onPickup(Player player) {
        System.out.println("You picked up a chest");
    }
}
