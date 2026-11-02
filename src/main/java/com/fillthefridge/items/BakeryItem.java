package com.fillthefridge.items;

import com.fillthefridge.model.Fridge;
import com.fillthefridge.model.ItemCategory;
import com.fillthefridge.model.PlacementResult;
import com.fillthefridge.util.GridSize;
import com.fillthefridge.util.Spillable;
import java.util.Collections;
import java.util.List;

public class BakeryItem extends Item implements Spillable {
    private final boolean spillable;

    public BakeryItem(
        String id,
        String displayName,
        String textureName,
        ItemCategory category,
        GridSize size,
        float maxShelfLife,
        int weight,
        int maxSupportWeight,
        boolean absorbent,
        boolean spillable,
        String warningPhrase
    ) {
        super(
            id,
            displayName,
            textureName,
            category,
            size,
            maxShelfLife,
            weight,
            maxSupportWeight,
            absorbent,
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
        return new BakeryItem(
            getId(),
            getDisplayName(),
            getTextureName(),
            getCategory(),
            getSize(),
            getMaxShelfLife(),
            getWeight(),
            getMaxSupportWeight(),
            isAbsorbent(),
            spillable,
            getWarningPhrase()
        );
    }
}
