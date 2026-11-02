package com.fillthefridge.model;

public enum ItemCategory {
    VEGETABLE("Vegetable"),
    MEAT("Meat"),
    DAIRY("Dairy"),
    MILK("Milk"),
    BAKERY("Bakery"),
    PANTRY("Pantry"),
    LEFTOVER("Leftover"),
    TRAP("Trap");

    private final String displayName;

    ItemCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
