package com.beachsurvivors.model.abilities;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.model.DamageText;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.SmokeParticle;

import java.util.Random;

public class Boomerang extends Ability {

    private float coconutSpeed = 280;
    private float angle;
    private float orbitRadius = 200;
    private float previousAngle = 0;
    private Array<SmokeParticle> smokeTrail = new Array<>();
    private boolean hasDamagedThisOrbit = false;
    private BitmapFont font;
    private Array<DamageText> damageTexts = new Array<>();
    private Random randomizeDirection = new Random();


    public Boomerang(){
        super("Boomerang", "entities/Boomerangmc.png", AbilityType.AoE, 10, 5, 32, 32);
        angle = 0;
    }

    public void createSmokeTrail() {
        smokeTrail.add(new SmokeParticle(getSprite().getX(), getSprite().getY()));

        for (int i = smokeTrail.size - 1; i >= 0; i--) {
            SmokeParticle s = smokeTrail.get(i);
            s.update(Gdx.graphics.getDeltaTime());
            if (s.isDead()) {
                smokeTrail.removeIndex(i);
            }
        }

    }

    public void showSmokeTrail(SpriteBatch spriteBatch) {
        createSmokeTrail();

        for (SmokeParticle s : smokeTrail) {
            s.getSprite().draw(spriteBatch);

        }
    }


    @Override
    public void use(Player player) {
        angle += coconutSpeed * Gdx.graphics.getDeltaTime();
        angle %= 360;

        if (angle < previousAngle) {
            hasDamagedThisOrbit = false;
        }
        previousAngle = angle;

        float radian = MathUtils.degreesToRadians * angle;
        float coconutX = player.getPlayerX() + player.getSprite().getWidth() / 2 + MathUtils.cos(radian) * orbitRadius - getSprite().getWidth() / 2;
        float coconutY = player.getPlayerY() + player.getSprite().getHeight() / 2 + MathUtils.sin(radian) * orbitRadius - getSprite().getHeight() / 2;

        updatePosition(coconutX, coconutY);
    }

    @Override
    public void use() {

    }

    public boolean hasDamagedThisOrbit() {
        return hasDamagedThisOrbit;
    }

    public void setHasDamagedThisOrbit(boolean hasDamagedThisOrbit) {
        this.hasDamagedThisOrbit = hasDamagedThisOrbit;
    }
}
