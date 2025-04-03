package model.abilities;

import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.view.GameScreen;
import model.Player;
import model.enemies.Enemy;
import model.enemies.Shark;

public class BaseAttack extends Ability{
    public BaseAttack(String name, String texturePath, AbilityType type, double baseDamage, double cooldown, int width, int height) {
        super(name, texturePath, type, baseDamage, cooldown, width, height);
    }

    @Override
    public void use() {

    }
}
