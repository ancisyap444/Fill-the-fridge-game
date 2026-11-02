package com.fillthefridge.model;

import com.fillthefridge.items.Item;
import com.fillthefridge.util.GridPosition;

public class Tile {
    private final GridPosition position;
    private TileState state;
    private Item occupyingItem;

    public Tile(GridPosition position) {
        this.position = position;
        this.state = TileState.EMPTY;
    }

    public boolean isEmpty() {
        return occupyingItem == null && state == TileState.EMPTY;
    }

    public void setOccupyingItem(Item item) {
        this.occupyingItem = item;
        refreshState();
    }

    public void clear() {
        this.occupyingItem = null;
        this.state = TileState.EMPTY;
    }

    public void refreshState() {
        if (occupyingItem == null) {
            state = TileState.EMPTY;
        } else if (occupyingItem.isContaminated()) {
            state = TileState.CONTAMINATED;
        } else if (occupyingItem.isTrap()) {
            state = TileState.TRAP_OCCUPIED;
        } else if (occupyingItem.isConfirmed()) {
            state = TileState.OCCUPIED_CONFIRMED;
        } else {
            state = TileState.OCCUPIED_UNCONFIRMED;
        }
    }

    public GridPosition getPosition() {
        return position;
    }

    public TileState getState() {
        return state;
    }

    public void setState(TileState state) {
        this.state = state;
    }

    public Item getOccupyingItem() {
        return occupyingItem;
    }
}
