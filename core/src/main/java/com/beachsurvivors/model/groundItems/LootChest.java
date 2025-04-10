package com.beachsurvivors.model.groundItems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.abilities.Ability;

import java.util.List;

public class LootChest extends GroundItem implements PickUpAble {

    private List<Ability> abilitiesToChose;  //De 3 abilities man kan välja mellan ifall man tar upp en kista

    private Texture texture;
    private Sprite sprite;
    private Rectangle hitbox;
    private Vector2 position;


    public LootChest(float x, float y) {
        super("entities/chest.png", x, y);

    }

    @Override
    public void onPickup(Player player) {

    }
}
