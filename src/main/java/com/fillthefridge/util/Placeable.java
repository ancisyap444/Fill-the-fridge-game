package com.fillthefridge.util;

public interface Placeable {
    GridPosition getGridPosition();
    void setGridPosition(GridPosition position);
    GridSize getSize();
    boolean isConfirmed();
    void setConfirmed(boolean confirmed);
}
