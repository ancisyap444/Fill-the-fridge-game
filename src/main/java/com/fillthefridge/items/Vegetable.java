package com.fillthefridge.items;

import com.fillthefridge.model.Fridge;
import com.fillthefridge.model.ItemCategory;
import com.fillthefridge.model.PlacementResult;
import com.fillthefridge.util.GridSize;

public class Vegetable extends Item {
    public Vegetable(
        String id,
        String displayName,
        String textureName,
        GridSize size,
        float maxShelfLife,
        int weight,
        int maxSupportWeight,
        boolean absorbent,
        String warningPhrase
    ) {
        super(
            id,
            displayName,
            textureName,
            ItemCategory.VEGETABLE,
            size,
            maxShelfLife,
            weight,
            maxSupportWeight,
            absorbent,
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
        return new Vegetable(
            getId(),
            getDisplayName(),
            getTextureName(),
            getSize(),
            getMaxShelfLife(),
            getWeight(),
            getMaxSupportWeight(),
            isAbsorbent(),
            getWarningPhrase()
        );
    }
}
