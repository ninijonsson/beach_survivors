package com.beachsurvivors.model;

import com.beachsurvivors.model.abilities.UpgradeType;

public class Upgrade {

    private UpgradeType type;
    private String description;

    public Upgrade(UpgradeType type, String description) {
        this.type = type;
        this.description = description;
    }

    public UpgradeType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }


}
