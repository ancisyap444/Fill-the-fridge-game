package com.fillthefridge.powerups;

import com.fillthefridge.model.GameState;

public abstract class PowerUp {
    private final String id;
    private final String name;
    private final String description;
    private final String iconTextureName;
    private final boolean rare;

    protected PowerUp(String id, String name, String description, String iconTextureName, boolean rare) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconTextureName = iconTextureName;
        this.rare = rare;
    }

    public abstract void apply(GameState gameState);

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIconTextureName() {
        return iconTextureName;
    }

    public boolean isRare() {
        return rare;
    }
}
