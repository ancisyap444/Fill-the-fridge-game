package com.fillthefridge.powerups;

import com.fillthefridge.assets.AssetManager;
import com.fillthefridge.model.GameState;

public class DripTrayPowerUp extends PowerUp {
    public DripTrayPowerUp() {
        super(
            "drip_tray",
            "Drip Tray",
            "Negate the next spill completely",
            AssetManager.MILK,
            false
        );
    }

    @Override
    public void apply(GameState gameState) {
        gameState.getActivePowerUps().addDripTrayCharge(1);
        gameState.getActivePowerUps().recordPowerUp(this);
    }
}
