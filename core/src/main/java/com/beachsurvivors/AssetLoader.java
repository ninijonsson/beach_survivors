package com.beachsurvivors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
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
        addMusic();
        manager.finishLoading();
    }

    private void addMusic() {
        manager.load("sounds/beach.mp3", Music.class);
    }

    private void addSounds() {
        manager.load("sounds/shark_damage_2.wav", Sound.class);
        manager.load("sounds/Seal_Damage.wav", Sound.class);
        manager.load("main_menu/sound/play_sound.wav", Sound.class);
        manager.load("main_menu/sound/holiday.wav", Sound.class);
        manager.load("assets/sounds/chain_lightning.wav", Sound.class);
    }

    private void addTextures() {
        manager.load("placeholder.png", Texture.class);
        manager.load("entities/particles/bullet.png", Texture.class);
        manager.load("entities/particles/particle.png", Texture.class);
        manager.load("entities/beer.png", Texture.class);
        manager.load("entities/burger.png", Texture.class);
        manager.load("entities/chest.png", Texture.class);
        manager.load("entities/icons/ability_icon.png", Texture.class);
        manager.load("entities/icons/chest_open.png", Texture.class);
        manager.load("entities/coconut.png", Texture.class);
        manager.load("entities/death.png", Texture.class);
        manager.load("entities/how_to_play.png", Texture.class);

        manager.load("game_over_screen/you died screen.png", Texture.class);
        manager.load("game_over_screen/you_died.png", Texture.class);

        manager.load("main_menu/logo_skiss_1.png", Texture.class);
        manager.load("main_menu/menu_background.jpeg", Texture.class);
        manager.load("main_menu/buttons/play_button_2_scaled.png", Texture.class);
        manager.load("main_menu/buttons/play_button_2_hover_scaled.png", Texture.class);
        manager.load("main_menu/buttons/play_button_2_pressed_scaled.png", Texture.class);
        manager.load("main_menu/buttons/exit_button_2_scaled.png", Texture.class);
        manager.load("main_menu/buttons/exit_button_2_hover_scaled.png", Texture.class);
        manager.load("main_menu/buttons/exit_button_2_pressed_scaled.png", Texture.class);

        manager.load("entities/abilities/exp_bar.png", Texture.class);
        manager.load("entities/abilities/xp_orb.png", Texture.class);

        //manager.load("entities/abilities/ability_bar.png", Texture.class);
        manager.load("entities/abilities/bullet.png", Texture.class);
        manager.load("entities/abilities/boomerangmc.png", Texture.class);
        manager.load("entities/abilities/fireball.png", Texture.class);
        manager.load("entities/abilities/shield_bubble.png", Texture.class);
        manager.load("entities/abilities/lightning.png", Texture.class);

        manager.load("entities/smoke.png", Texture.class);

        manager.load("entities/enemies/shark.png", Texture.class);
        manager.load("entities/enemies/shark_sheet.png", Texture.class);
        manager.load("entities/enemies/crocodile1.png", Texture.class);
        manager.load("entities/enemies/crocodile1_sheet.png", Texture.class);
        manager.load("entities/enemies/navy_seal_sheet.png", Texture.class);
        manager.load("entities/enemies/crocodile2.png", Texture.class);
        manager.load("entities/enemies/crab_sheet.png", Texture.class);

        manager.load("entities/beach_girl_sheet.png", Texture.class);
        manager.load("entities/beach_guy_sheet.png", Texture.class);

        manager.load("entities/power_ups/freeze.png", Texture.class);
        manager.load("entities/power_ups/health_boost.png", Texture.class);
        manager.load("entities/power_ups/health_heart.png", Texture.class);
        manager.load("entities/power_ups/lucky_clover.png", Texture.class);
        manager.load("entities/power_ups/speed_boost.png", Texture.class);
        manager.load("entities/icons/blank.png", Texture.class);
        manager.load("entities/icons/coin.png", Texture.class);

    }

    private void addParticles() {
        ParticleEffectLoader.ParticleEffectParameter blueFlame = new ParticleEffectLoader.ParticleEffectParameter();
        blueFlame.imagesDir = Gdx.files.internal("entities/particles");
        manager.load("entities/particles/blueFlame.p", ParticleEffect.class, blueFlame);

        ParticleEffectLoader.ParticleEffectParameter lootBeam = new ParticleEffectLoader.ParticleEffectParameter();
        manager.load("entities/particles/lootBeam.p", ParticleEffect.class, lootBeam);

        ParticleEffectLoader.ParticleEffectParameter lootPile = new ParticleEffectLoader.ParticleEffectParameter();
        manager.load("entities/particles/lootPile.p", ParticleEffect.class, lootPile);

        ParticleEffectLoader.ParticleEffectParameter xpOrb = new ParticleEffectLoader.ParticleEffectParameter();
        manager.load("entities/particles/xp_orb.p", ParticleEffect.class, xpOrb);

        ParticleEffectLoader.ParticleEffectParameter chestEffect = new ParticleEffectLoader.ParticleEffectParameter();
        manager.load("entities/particles/chestClosed.p", ParticleEffect.class, chestEffect);

        ParticleEffectLoader.ParticleEffectParameter chestOpen = new ParticleEffectLoader.ParticleEffectParameter();
        manager.load("entities/particles/chestOpen.p", ParticleEffect.class, chestEffect);
    }

    private void addSkins() {
        manager.load("game_over_screen/gameover_skin.json", Skin.class);
        manager.load("skin_composer/healthbutton.json", Skin.class);
        manager.load("level_up_screen/uiskin.json", Skin.class);
        manager.load("skin_composer/pause_menu/pause_menu.json", Skin.class);
        manager.load("game_over_screen/deathscreen_skin.json", Skin.class);
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
