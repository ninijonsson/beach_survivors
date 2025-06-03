package com.beachsurvivors.model.groundItems;

import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.utilities.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.abilities.Ability;
import com.beachsurvivors.view.GameScreen;

import java.util.List;

public class Chest extends GroundItem implements PickUpAble {

    private List<Ability> abilitiesToChose;  //De 3 abilities man kan välja mellan ifall man tar upp en kista
    private GameScreen gameScreen;

    public Chest(float x, float y, ParticleEffectPoolManager poolManager, GameScreen gameScreen) {
        super(AssetLoader.get().getTexture("entities/chest.png"), x, y, gameScreen);
        this.gameScreen = gameScreen;
        setParticleEffect(poolManager.obtain("entities/particles/chestClosed.p"));
    }

    @Override
    public void onPickup(Player player) {
        gameScreen.showChestOverlay();

    }
}
