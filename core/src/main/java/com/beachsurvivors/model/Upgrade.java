package com.beachsurvivors.model;

import com.beachsurvivors.model.abilities.UpgradeType;

public class Upgrade {

    private UpgradeType type;
    private String description;
    private float value;

    public Upgrade(UpgradeType type, float value) {
        this.type = type;
        this.value = value;
    }

    public UpgradeType getType() {
        return type;
    }

    public float getValue() {
        return value;
    }

    public String getDescription() {

        switch (type) {
            case Health: return "Gain " + (int)value + " to \n maximum health";
            case Speed: return "Gain " + (int)value + "\n movement speed";
            case Damage: return "Increases your \n damage by " + String.format("%.0f", value);
            case CriticalHitChance: return "Increases your \n critical  hit \n chance by " + (int)(value * 100) + "%";
            case CriticalHitDamage: return "Increases your \n critical hit \n damage by " + (int)(value * 100) +"%";
            case CooldownTime: return "Reduces your \n cooldown time \n by " + (int)((1 - value) * 100) + "%";
            case AreaRange: return "Increases your \n area by \n " + String.format("%.0f", value) + " units";
            case HpRegen: return "Increases your \n Hp Regen by \n " + value + " HP/s";
            case LifeSteal: return "Increases your \n life steal \n by " + value;

            default: return "N/A";

        }
    }


}
