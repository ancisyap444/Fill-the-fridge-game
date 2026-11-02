package com.fillthefridge.powerups;

import com.fillthefridge.assets.AssetManager;
import com.fillthefridge.model.GameState;

public class TrashCompactorPowerUp extends PowerUp {
    public TrashCompactorPowerUp() {
        super(
            "trash_compactor",
            "Trash Compactor",
            "+2 trash bin slots per stage",
            AssetManager.TRASH_BIN,
            false
        );
    }

    @Override
    public void apply(GameState gameState) {
        gameState.getActivePowerUps().addExtraTrashSlots(2);
        gameState.getTrashBin().addExtraSlots(2);
        gameState.getActivePowerUps().recordPowerUp(this);
    }
}
