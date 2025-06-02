package com.beachsurvivors.utilities;

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

    public static void registerParticles(ParticleEffectPoolManager poolManager) {
        poolManager.register("entities/particles/blueFlame.p", 5, 20);
        poolManager.register("entities/particles/lootBeam.p", 5, 20);
        poolManager.register("entities/particles/lootPile.p", 5, 20);
        poolManager.register("entities/particles/xp_orb.p", 5, 20);
        poolManager.register("entities/particles/chestClosed.p", 5, 20);
        poolManager.register("entities/particles/chestOpen.p", 5, 20);
        poolManager.register("entities/particles/water_trail.p", 5, 20);
        poolManager.register("entities/particles/electric_trail.p", 5, 20);
        poolManager.register("entities/particles/death_effect.p", 5, 20);
        poolManager.register("entities/particles/bomb_explosion.p", 5, 20);
        poolManager.register("entities/particles/arrow_effect.p", 5, 20);
    }

    private void addMusic() {
        manager.load("sounds/beach.mp3", Music.class);
        manager.load("entities/abilities/bomb_explosion.mp3", Sound.class);
        manager.load("entities/abilities/bomb_fall.wav", Sound.class);
        manager.load("entities/abilities/water_gun_fire.wav", Sound.class);
        manager.load("sounds/item_pickup.wav", Sound.class);
        manager.load("sounds/crab_rave.mp3", Sound.class);
        manager.load("sounds/footstep.mp3", Sound.class);
        manager.load("sounds/level_up.mp3", Sound.class);


    }

    private void addSounds() {
        manager.load("sounds/shark_damage_2.wav", Sound.class);
        manager.load("sounds/Seal_Damage.wav", Sound.class);
        manager.load("main_menu/sound/play_sound.wav", Sound.class);
        manager.load("main_menu/sound/holiday.wav", Sound.class);
        manager.load("sounds/chain_lightning.wav", Sound.class);
        manager.load("entities/abilities/death_splatter.wav", Sound.class);
        manager.load("entities/abilities/menu_select.wav", Sound.class);
        manager.load("entities/abilities/menu_switch.wav", Sound.class);
    }

    private void addTextures() {
        manager.load("placeholder.png", Texture.class);
        manager.load("entities/particles/bullet.png", Texture.class);
        manager.load("entities/particles/particle.png", Texture.class);
        manager.load("entities/beer.png", Texture.class);
        manager.load("entities/burger.png", Texture.class);
        manager.load("entities/chest.png", Texture.class);
        manager.load("entities/icons/ability_icon.png", Texture.class);
        manager.load("entities/icons/ability_icon_selected.png", Texture.class);
        manager.load("entities/icons/select_arrow.png", Texture.class);
        manager.load("entities/icons/chest_open.png", Texture.class);
        manager.load("entities/coconut.png", Texture.class);
        manager.load("entities/death.png", Texture.class);
        manager.load("entities/how_to_play.png", Texture.class);
        manager.load("entities/abilities/bomb_shadow.png", Texture.class);

        manager.load("entities/abilities/bomb_ring.png", Texture.class);
        manager.load("entities/abilities/bomb.png", Texture.class);
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
        manager.load("entities/enemies/crocodile2_walk_sheet.png", Texture.class);

        manager.load("entities/beach_girl_sheet.png", Texture.class);
        manager.load("entities/beach_guy_sheet.png", Texture.class);

        manager.load("entities/power_ups/freeze.png", Texture.class);
        manager.load("entities/power_ups/health_boost.png", Texture.class);
        manager.load("entities/power_ups/health_heart.png", Texture.class);
        manager.load("entities/power_ups/lucky_clover.png", Texture.class);
        manager.load("entities/power_ups/speed_boost.png", Texture.class);
        manager.load("entities/power_ups/beer.png", Texture.class);
        manager.load("entities/icons/blank.png", Texture.class);
        manager.load("entities/icons/coin.png", Texture.class);

        manager.load("entities/ui/ability_bar_placeholder.png", Texture.class);
        manager.load("entities/ui/ability_slot_placeholder.png", Texture.class);
        manager.load("entities/ui/ability_bar.png", Texture.class);
        manager.load("entities/ui/buff_bar.png", Texture.class);
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
        manager.load("entities/particles/chestOpen.p", ParticleEffect.class, chestOpen);

        ParticleEffectLoader.ParticleEffectParameter bombExplosion = new ParticleEffectLoader.ParticleEffectParameter();
        manager.load("entities/particles/bomb_explosion.p", ParticleEffect.class, bombExplosion);

        ParticleEffectLoader.ParticleEffectParameter impactZone = new ParticleEffectLoader.ParticleEffectParameter();
        manager.load("entities/particles/impact_zone.p", ParticleEffect.class, impactZone);

        ParticleEffectLoader.ParticleEffectParameter waterTrail = new ParticleEffectLoader.ParticleEffectParameter();
        manager.load("entities/particles/water_trail.p", ParticleEffect.class, waterTrail);

        ParticleEffectLoader.ParticleEffectParameter electricTrail = new ParticleEffectLoader.ParticleEffectParameter();
        manager.load("entities/particles/electric_trail.p", ParticleEffect.class, electricTrail);

        ParticleEffectLoader.ParticleEffectParameter deathEffect = new ParticleEffectLoader.ParticleEffectParameter();
        manager.load("entities/particles/death_effect.p", ParticleEffect.class, deathEffect);

        ParticleEffectLoader.ParticleEffectParameter safeDeathEffect = new ParticleEffectLoader.ParticleEffectParameter();
        manager.load("entities/particles/death_effect_safe.p", ParticleEffect.class, safeDeathEffect);

        ParticleEffectLoader.ParticleEffectParameter arrowEffect = new ParticleEffectLoader.ParticleEffectParameter();
        manager.load("entities/particles/arrow_effect.p", ParticleEffect.class, arrowEffect);
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
