package com.fillthefridge.model;

public enum PlacementResult {
    LEGAL("Legal Placement"),
    UNSTABLE("Unstable / Blocked"),
    SPILL_CAUSING("Warning: Will Cause Spill!");

    private final String description;

    PlacementResult(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
