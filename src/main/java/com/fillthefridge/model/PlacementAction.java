package com.fillthefridge.model;

import com.fillthefridge.items.Item;
import com.fillthefridge.util.GridPosition;

public final class PlacementAction {
    private final Item item;
    private final GridPosition position;

    public PlacementAction(Item item, GridPosition position) {
        this.item = item;
        this.position = position;
    }

    public Item getItem() {
        return item;
    }

    public GridPosition getPosition() {
        return position;
    }
}
