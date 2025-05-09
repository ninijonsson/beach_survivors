package com.beachsurvivors.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.abilities.*;

public class AbilityController {
    private Array<Ability> abilities;
    private Boomerang boomerang;
    private BaseAttack bullet;
    private Shield shield;
    private PlayerController player;
    private float bulletTimer;

    public AbilityController(PlayerController player) {
        this.abilities = new Array<>();
        // Ska dessa flyttas till en create()-metod istället?
        this.boomerang = new Boomerang();
        this.bullet = new BaseAttack();
        this.shield = new Shield();
        this.player = player;
    }

    public void addAbility(Ability ability) {
        abilities.add(ability);
    }

    public void updatePosition() {
        float delta = Gdx.graphics.getDeltaTime();

        for (Ability ability : abilities) {
            // (ability.updatePosition(delta, player.getPlayerX(), player.getPlayerY());
        }
    }
}
