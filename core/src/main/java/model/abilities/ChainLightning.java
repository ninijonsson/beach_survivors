package model.abilities;

public class ChainLightning extends Ability {

    public ChainLightning(String texturePath, String type, double damage, double cooldown, int width, int height) {
        super("ChainLightning" , "placeholder" , AbilityType.ATTACK , damage, cooldown, width, height);
    }

    public void chainEnemies() {

    }


    @Override
    public void use() {

    }
}
