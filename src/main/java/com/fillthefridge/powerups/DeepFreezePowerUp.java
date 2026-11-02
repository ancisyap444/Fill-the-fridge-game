package com.fillthefridge.powerups;

import com.fillthefridge.assets.AssetManager;
import com.fillthefridge.items.Item;
import com.fillthefridge.model.GameState;

public class DeepFreezePowerUp extends PowerUp {
    public DeepFreezePowerUp() {
        super(
            "deep_freeze",
            "Deep Freeze",
            "Pause one tray item for 3s (press [F] or auto-triggers)",
            AssetManager.SNOWFLAKE,
            false
        );
    }

    @Override
    public void apply(GameState gameState) {
        gameState.getActivePowerUps().addDeepFreezeCharge(1);
        Item urgent = gameState.getTray().findMostUrgentItem();
        if (urgent != null) {
            urgent.applyDeepFreeze(3.0f);
        }
        gameState.getActivePowerUps().recordPowerUp(this);
    }
}
