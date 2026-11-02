package com.fillthefridge.items;

import com.fillthefridge.model.Fridge;
import com.fillthefridge.model.ItemCategory;
import com.fillthefridge.model.PlacementResult;
import com.fillthefridge.util.GridSize;

public class TrapItem extends Item {
    public TrapItem(
        String id,
        String displayName,
        String textureName,
        GridSize size,
        float maxShelfLife,
        String warningPhrase
    ) {
        super(
            id,
            displayName,
            textureName,
            ItemCategory.TRAP,
            size,
            maxShelfLife,
            1,
            1,
            false,
            warningPhrase
        );
    }

    @Override
    public String onTimerWarning() {
        return getWarningPhrase();
    }

    @Override
    public boolean canSupport(Item aboveItem) {
        return aboveItem != null && aboveItem.getWeight() <= 1;
    }

    @Override
    public PlacementResult onStackedUpon(Item aboveItem, Fridge fridge) {
        if (!canSupport(aboveItem)) {
            return PlacementResult.UNSTABLE;
        }
        return PlacementResult.LEGAL;
    }

    @Override
    public boolean isTrap() {
        return true;
    }

    @Override
    public Item createFreshCopy() {
        return new TrapItem(
            getId(),
            getDisplayName(),
            getTextureName(),
            getSize(),
            getMaxShelfLife(),
            getWarningPhrase()
        );
    }
}
