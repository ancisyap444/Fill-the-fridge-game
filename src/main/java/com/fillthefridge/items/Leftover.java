package com.fillthefridge.items;

import com.fillthefridge.model.Fridge;
import com.fillthefridge.model.ItemCategory;
import com.fillthefridge.model.PlacementResult;
import com.fillthefridge.util.GridSize;
import com.fillthefridge.util.Spillable;
import java.util.Collections;
import java.util.List;

public class Leftover extends Item implements Spillable {
    private final boolean spillable;

    public Leftover(
        String id,
        String displayName,
        String textureName,
        GridSize size,
        float maxShelfLife,
        int weight,
        int maxSupportWeight,
        boolean spillable,
        String warningPhrase
    ) {
        super(
            id,
            displayName,
            textureName,
            ItemCategory.LEFTOVER,
            size,
            maxShelfLife,
            weight,
            maxSupportWeight,
            false,
            warningPhrase
        );
        this.spillable = spillable;
    }

    @Override
    public String onTimerWarning() {
        return getWarningPhrase();
    }

    @Override
    public boolean canSupport(Item aboveItem) {
        if (spillable || isContaminated() || aboveItem == null) {
            return false;
        }
        return aboveItem.getWeight() <= getMaxSupportWeight();
    }

    @Override
    public PlacementResult onStackedUpon(Item aboveItem, Fridge fridge) {
        if (spillable) {
            return PlacementResult.SPILL_CAUSING;
        }
        if (!canSupport(aboveItem)) {
            return PlacementResult.UNSTABLE;
        }
        return PlacementResult.LEGAL;
    }

    @Override
    public boolean isSpillable() {
        return spillable;
    }

    @Override
    public boolean isSpillableItem() {
        return spillable;
    }

    @Override
    public List<Item> spillDownward(Fridge fridge) {
        if (!spillable) {
            return Collections.emptyList();
        }
        triggerSpillSplash();
        return fridge.propagateSpillFrom(this);
    }

    @Override
    public Item createFreshCopy() {
        return new Leftover(
            getId(),
            getDisplayName(),
            getTextureName(),
            getSize(),
            getMaxShelfLife(),
            getWeight(),
            getMaxSupportWeight(),
            spillable,
            getWarningPhrase()
        );
    }
}
