package com.beachsurvivors.model.abilities;

import com.beachsurvivors.model.Player;

public class ChainLightning extends Ability {

    public ChainLightning(String texturePath, String type, double damage, double cooldown, int width, int height) {
        super("ChainLightning" , "placeholder" , AbilityType.ATTACK , damage, cooldown, width, height);
    }

    public void chainEnemies() {

    }


    @Override
    public void use() {

    }

    @Override
    public void use(Player player) {

    }
}
