package com.fillthefridge.powerups;

import com.fillthefridge.assets.AssetManager;
import com.fillthefridge.model.GameState;

public class SniffTestPowerUp extends PowerUp {
    public SniffTestPowerUp() {
        super(
            "sniff_test",
            "Sniff Test",
            "Reveal trap items immediately in the tray",
            AssetManager.WARNING_ICON,
            false
        );
    }

    @Override
    public void apply(GameState gameState) {
        gameState.getActivePowerUps().setSniffTestEnabled(true);
        gameState.getTray().revealAllTraps();
        gameState.getActivePowerUps().recordPowerUp(this);
    }
}
