package com.fillthefridge.util;

public class InvalidPlacementException extends Exception {
    private final GridPosition attemptedPosition;

    public InvalidPlacementException(String message) {
        super(message);
        this.attemptedPosition = null;
    }

    public InvalidPlacementException(String message, GridPosition attemptedPosition) {
        super(message);
        this.attemptedPosition = attemptedPosition;
    }

    public GridPosition getAttemptedPosition() {
        return attemptedPosition;
    }
}
