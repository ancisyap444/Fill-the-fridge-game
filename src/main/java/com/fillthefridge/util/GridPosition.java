package com.fillthefridge.util;

import java.util.Objects;

public final class GridPosition {
    private final int col;
    private final int row;

    public GridPosition(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public GridPosition offset(int deltaCol, int deltaRow) {
        return new GridPosition(col + deltaCol, row + deltaRow);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GridPosition that)) {
            return false;
        }
        return col == that.col && row == that.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(col, row);
    }

    @Override
    public String toString() {
        return "(" + col + ", " + row + ")";
    }
}
