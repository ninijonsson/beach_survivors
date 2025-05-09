package com.beachsurvivors.model.groundItems;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;

public class ExperienceOrb extends GroundItem implements PickUpAble {
    private int experience;

    ParticleEffectPool.PooledEffect xpEffect;

    public ExperienceOrb(float x, float y, int experience, ParticleEffectPoolManager poolManager) {
        super(AssetLoader.get().getTexture("assets/entities/abilities/xp_orb.png"), x,y, null);
        this.experience=experience;
        setParticleEffect(poolManager.obtain("assets/entities/particles/xp_orb.p"));

    }


    @Override
    public void onPickup(Player player) {
        System.out.println("current xp: "+player.getLevelController().getCurrentExp());
        player.gainExp(experience);
        System.out.println("gained xp");
        System.out.println("current xp: "+player.getLevelController().getCurrentExp());
    }

    @Override
    public void dispose() {
        super.dispose();
        xpEffect.free();
        xpEffect=null;
    }
}
