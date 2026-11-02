package com.fillthefridge.model;

public class Shelf {
    private final int rowIndex;
    private final int columnSpan;
    private final boolean baseShelf;

    public Shelf(int rowIndex, int columnSpan, boolean baseShelf) {
        this.rowIndex = rowIndex;
        this.columnSpan = columnSpan;
        this.baseShelf = baseShelf;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnSpan() {
        return columnSpan;
    }

    public boolean isBaseShelf() {
        return baseShelf;
    }
}
