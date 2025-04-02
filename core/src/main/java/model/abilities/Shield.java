package model.abilities;

import model.Player;

public class Shield extends Ability {

    private int shieldStrength;  //Hur mycket HP skölden skyddar (ifall vi ska ha en vanlig HP-sköld)

    public Shield() {
        super("Shield" , "Placeholder", AbilityType.SHIELD, 0, 10, 30, 30);
        this.shieldStrength = 10;
    }

    public void onDepleted() {

    }

    @Override
    public void use() {

    }

    @Override
    public void use(Player player) {

    }
}
