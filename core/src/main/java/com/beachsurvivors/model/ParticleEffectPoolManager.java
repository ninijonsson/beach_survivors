package com.beachsurvivors.model;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.AssetLoader;

import java.util.HashMap;
import java.util.Map;

public class ParticleEffectPoolManager {

    private final Map<String, ParticleEffectPool> pools = new HashMap<>();
    private final Array<ParticleEffectPool.PooledEffect> activeEffects = new Array<>();

    public void register(String effectPath, int min, int max) {
        ParticleEffect base = AssetLoader.get().getParticleEffect(effectPath);
        ParticleEffectPool pool = new ParticleEffectPool(base, min, max);
        pools.put(effectPath, pool);
    }

    public ParticleEffectPool.PooledEffect obtain(String effectPath, float x, float y) {
        ParticleEffectPool pool = pools.get(effectPath);
        if (pool == null) return null;
        ParticleEffectPool.PooledEffect effect = pool.obtain();
        effect.setPosition(x, y);
        effect.start();
        activeEffects.add(effect);
        return effect;
    }

    public void updateAndDraw(SpriteBatch batch, float delta) {
        for (int i = activeEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = activeEffects.get(i);
            effect.draw(batch, delta);
            if (effect.isComplete()) {
                effect.free();
                activeEffects.removeIndex(i);
            }
        }
    }

    public void clear() {
        for (ParticleEffectPool.PooledEffect effect : activeEffects) {
            effect.free();
        }
        activeEffects.clear();
    }
}
