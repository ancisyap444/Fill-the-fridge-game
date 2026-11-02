package com.fillthefridge.powerups;

import com.fillthefridge.assets.AssetManager;
import com.fillthefridge.model.GameState;

public class GhostFitPowerUp extends PowerUp {
    public GhostFitPowerUp() {
        super(
            "ghost_fit",
            "Ghost Fit",
            "Exact placement preview and support highlights",
            AssetManager.SNOWFLAKE,
            false
        );
    }

    @Override
    public void apply(GameState gameState) {
        gameState.getActivePowerUps().setGhostFitEnabled(true);
        gameState.getActivePowerUps().recordPowerUp(this);
    }
}
