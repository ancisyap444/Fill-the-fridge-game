package com.fillthefridge.powerups;

import com.fillthefridge.assets.AssetManager;
import com.fillthefridge.model.GameState;

public class SecondWindPowerUp extends PowerUp {
    public SecondWindPowerUp() {
        super(
            "second_wind",
            "Second Wind",
            "Heal 1 life immediately (Rare)",
            AssetManager.HEART,
            true
        );
    }

    @Override
    public void apply(GameState gameState) {
        gameState.getPlayerLives().healLives(1, "Second Wind healed +1 Life!");
        gameState.getActivePowerUps().recordPowerUp(this);
    }
}
