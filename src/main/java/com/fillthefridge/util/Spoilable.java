package com.fillthefridge.util;

public interface Spoilable {
    float getRemainingTime();
    float getMaxShelfLife();
    float getShelfLifeRatio();
    void updateTimer(float deltaTime);
    boolean isWarningThresholdReached();
    boolean isCriticalThresholdReached();
    boolean isSpoiledAfterGrace();
    String onTimerWarning();
}
