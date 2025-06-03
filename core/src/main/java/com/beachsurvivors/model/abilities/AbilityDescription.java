package com.beachsurvivors.model.abilities;

public enum AbilityDescription {
    Chain_Lightning("Chain Lightning", "An attack that damages several enemies through jumps."),
    Shield("Shield", "Prevents damage from monsters"),
    Water_wave("Water wave", "Wave that attacks multiple enemies"),
    Boomerang_("Boomerang", "A boomerang that spins around the player");

    private final String name;
    private final String description;

    AbilityDescription(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
