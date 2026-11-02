package com.fillthefridge.items;

import com.fillthefridge.model.Fridge;
import com.fillthefridge.model.ItemCategory;
import com.fillthefridge.model.PlacementResult;
import com.fillthefridge.util.GridSize;
import com.fillthefridge.util.Spillable;
import java.util.List;

public class Milk extends Item implements Spillable {
    public Milk(
        String id,
        String displayName,
        String textureName,
        GridSize size,
        float maxShelfLife,
        int weight,
        String warningPhrase
    ) {
        super(
            id,
            displayName,
            textureName,
            ItemCategory.MILK,
            size,
            maxShelfLife,
            weight,
            0,
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
        return false;
    }

    @Override
    public PlacementResult onStackedUpon(Item aboveItem, Fridge fridge) {
        return PlacementResult.SPILL_CAUSING;
    }

    @Override
    public boolean isSpillable() {
        return true;
    }

    @Override
    public boolean isSpillableItem() {
        return true;
    }

    @Override
    public List<Item> spillDownward(Fridge fridge) {
        triggerSpillSplash();
        return fridge.propagateSpillFrom(this);
    }

    @Override
    public Item createFreshCopy() {
        return new Milk(
            getId(),
            getDisplayName(),
            getTextureName(),
            getSize(),
            getMaxShelfLife(),
            getWeight(),
            getWarningPhrase()
        );
    }
}
