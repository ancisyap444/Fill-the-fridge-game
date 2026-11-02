package com.fillthefridge.items;

import com.fillthefridge.model.Fridge;
import com.fillthefridge.model.ItemCategory;
import com.fillthefridge.model.PlacementResult;
import com.fillthefridge.util.GridSize;

public class Meat extends Item {
    public Meat(
        String id,
        String displayName,
        String textureName,
        GridSize size,
        float maxShelfLife,
        int weight,
        int maxSupportWeight,
        String warningPhrase
    ) {
        super(
            id,
            displayName,
            textureName,
            ItemCategory.MEAT,
            size,
            maxShelfLife,
            weight,
            maxSupportWeight,
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
        return !isContaminated() && aboveItem != null && aboveItem.getWeight() <= getMaxSupportWeight();
    }

    @Override
    public PlacementResult onStackedUpon(Item aboveItem, Fridge fridge) {
        if (!canSupport(aboveItem)) {
            return PlacementResult.UNSTABLE;
        }
        return PlacementResult.LEGAL;
    }

    @Override
    public Item createFreshCopy() {
        return new Meat(
            getId(),
            getDisplayName(),
            getTextureName(),
            getSize(),
            getMaxShelfLife(),
            getWeight(),
            getMaxSupportWeight(),
            getWarningPhrase()
        );
    }
}
