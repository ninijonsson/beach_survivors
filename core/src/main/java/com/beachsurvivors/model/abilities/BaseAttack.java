package com.beachsurvivors.model.abilities;

import com.beachsurvivors.model.Player;

public class BaseAttack extends Ability{
    public BaseAttack(String name, String texturePath, AbilityType type, double baseDamage, double cooldown, int width, int height) {
        super(name, texturePath, type, baseDamage, cooldown, width, height);
    }

    @Override
    public void use() {

    }

    @Override
    public void use(Player player) {

    }
}
