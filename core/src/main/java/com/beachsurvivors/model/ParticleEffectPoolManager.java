package com.beachsurvivors.model;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.utils.ObjectMap;
import com.beachsurvivors.utilities.AssetLoader;

public class ParticleEffectPoolManager {

    private final ObjectMap<String, ParticleEffectPool> pools = new ObjectMap<>();

    public void register(String effectPath, int min, int max) {
        ParticleEffect base = AssetLoader.get().getParticleEffect(effectPath);
        if (base == null) {
            throw new IllegalArgumentException("Effect not found in AssetLoader: " + effectPath);
        }
        ParticleEffectPool pool = new ParticleEffectPool(base, min, max);
        pools.put(effectPath, pool);
    }

    public PooledEffect obtain(String effectPath) {
        ParticleEffectPool pool = pools.get(effectPath);
        if (pool == null) {
            throw new IllegalArgumentException("Effect not registered: " + effectPath);
        }
        return pool.obtain();
    }

    public PooledEffect obtain(String effectPath, float x, float y) {
        PooledEffect effect = obtain(effectPath);
        if (effect != null) {
            effect.setPosition(x, y);
        }
        return effect;
    }
}
