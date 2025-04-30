package com.beachsurvivors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;

public class AssetLoader {
    private static final AssetLoader instance = new AssetLoader();
    public final AssetManager manager;

    private AssetLoader() {
        manager = new AssetManager();
        manager.setLoader(ParticleEffect.class, new ParticleEffectLoader(new InternalFileHandleResolver()));
        addSkins();
        addTextures();
        addSounds();
        addParticles();
        manager.finishLoading();
    }

    private void addSounds() {
        manager.load("assets/sounds/Shark_Damage2.wav", Sound.class);
        manager.load("assets/sounds/Seal_Damage.wav", Sound.class);
    }

    private void addTextures() {
        manager.load("assets/placeholder.png", Texture.class);
        manager.load("assets/entities/particles/bullet.png", Texture.class);
        manager.load("assets/entities/beer.png", Texture.class);
        manager.load("assets/entities/burger.png", Texture.class);
        manager.load("assets/entities/chest.png", Texture.class);
        manager.load("assets/entities/coconut.png", Texture.class);
        manager.load("assets/entities/death.png", Texture.class);
        manager.load("assets/entities/howToPlay.png", Texture.class);
        manager.load("assets/entities/abilities/xpBar.png", Texture.class);
        manager.load("assets/entities/abilities/abilityBar.png", Texture.class);
        manager.load("assets/entities/abilities/bullet.png", Texture.class);
        manager.load("assets/entities/abilities/boomerangmc.png", Texture.class);
        manager.load("assets/entities/abilities/fireball.png", Texture.class);
        manager.load("assets/entities/smoke.png", Texture.class);
        manager.load("assets/entities/enemies/shark.png", Texture.class);
        manager.load("assets/entities/enemies/shark_sheet.png", Texture.class);
        manager.load("assets/entities/enemies/crocodile1.png", Texture.class);
        manager.load("assets/entities/enemies/crocodile1_sheet.png", Texture.class);
        manager.load("assets/entities/enemies/navy_seal_sheet.png", Texture.class);
        manager.load("assets/entities/enemies/crocodile2.png", Texture.class);
        manager.load("assets/entities/beach_girl_sheet.png", Texture.class);
        manager.load("assets/entities/beach_guy_sheet.png", Texture.class);
        manager.load("assets/entities/power_ups/freeze.png", Texture.class);
        manager.load("assets/entities/power_ups/health_boost.png", Texture.class);
        manager.load("assets/entities/power_ups/health_heart.png", Texture.class);
        manager.load("assets/entities/power_ups/lucky_clover.png", Texture.class);
        manager.load("assets/entities/power_ups/speed_boost.png", Texture.class);
    }

    private void addParticles() {
        ParticleEffectLoader.ParticleEffectParameter params = new ParticleEffectLoader.ParticleEffectParameter();
        params.imagesDir = Gdx.files.internal("assets/entities/particles");
        manager.load("assets/entities/particles/blueFlame.p", ParticleEffect.class, params);
    }

    private void addSkins() {
        manager.load("SkinComposer/healthbutton.json", Skin.class);
    }

    public Texture getTexture(String path) {
        return manager.get(path, Texture.class);
    }

    public Sound getSound(String path) {
        return manager.get(path, Sound.class);
    }

    public Skin getSkin(String path) {
        return manager.get(path, Skin.class);
    }

    public ParticleEffect getParticleEffect(String path) {
        return manager.get(path, ParticleEffect.class);
    }

    public void dispose() {
        manager.dispose();
    }

    public static AssetLoader get() {
        return instance;
    }
}
