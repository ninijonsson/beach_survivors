package com.beachsurvivors.model.groundItems;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.controller.Controller;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;

public class ExperienceOrb extends GroundItem implements PickUpAble {
    private int experience;
    private Controller controller;

    ParticleEffectPool.PooledEffect xpEffect;

    public ExperienceOrb(float x, float y, int experience,
                         ParticleEffectPoolManager poolManager, Controller controller) {
        super(AssetLoader.get().getTexture("entities/abilities/xp_orb.png"), x,y, null);
        this.experience=experience;
        setParticleEffect(poolManager.obtain("entities/particles/xp_orb.p"));
        this.controller = controller;
    }


    @Override
    public void onPickup(Player player) {
        System.out.println("current xp: "+ controller.getCurrentEXP());
        player.gainExp(experience);
        System.out.println("gained xp");
        System.out.println("current xp: " + controller.getCurrentEXP());
    }

    @Override
    public void dispose() {
        super.dispose();
        xpEffect.free();
        xpEffect=null;
    }
}
