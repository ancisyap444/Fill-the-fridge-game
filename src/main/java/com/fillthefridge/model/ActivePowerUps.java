package com.fillthefridge.model;

import com.fillthefridge.powerups.PowerUp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivePowerUps {
    private float coldSnapBonusSeconds;
    private int deepFreezeCharges;
    private boolean ghostFitEnabled;
    private boolean sniffTestEnabled;
    private int extraTrashSlots;
    private int dripTrayCharges;
    private final List<PowerUp> acquiredPowerUps;

    public ActivePowerUps() {
        this.acquiredPowerUps = new ArrayList<>();
    }

    public void recordPowerUp(PowerUp powerUp) {
        if (powerUp != null) {
            acquiredPowerUps.add(powerUp);
        }
    }

    public void addColdSnapBonus(float seconds) {
        this.coldSnapBonusSeconds += seconds;
    }

    public float getColdSnapBonusSeconds() {
        return coldSnapBonusSeconds;
    }

    public void addDeepFreezeCharge(int charges) {
        this.deepFreezeCharges += charges;
    }

    public boolean consumeDeepFreezeCharge() {
        if (deepFreezeCharges > 0) {
            deepFreezeCharges--;
            return true;
        }
        return false;
    }

    public int getDeepFreezeCharges() {
        return deepFreezeCharges;
    }

    public boolean isGhostFitEnabled() {
        return ghostFitEnabled;
    }

    public void setGhostFitEnabled(boolean ghostFitEnabled) {
        this.ghostFitEnabled = ghostFitEnabled;
    }

    public boolean isSniffTestEnabled() {
        return sniffTestEnabled;
    }

    public void setSniffTestEnabled(boolean sniffTestEnabled) {
        this.sniffTestEnabled = sniffTestEnabled;
    }

    public void addExtraTrashSlots(int slots) {
        this.extraTrashSlots += slots;
    }

    public int getExtraTrashSlots() {
        return extraTrashSlots;
    }

    public void addDripTrayCharge(int charges) {
        this.dripTrayCharges += charges;
    }

    public boolean consumeDripTrayCharge() {
        if (dripTrayCharges > 0) {
            dripTrayCharges--;
            return true;
        }
        return false;
    }

    public int getDripTrayCharges() {
        return dripTrayCharges;
    }

    public List<PowerUp> getAcquiredPowerUps() {
        return Collections.unmodifiableList(acquiredPowerUps);
    }

    public void reset() {
        coldSnapBonusSeconds = 0f;
        deepFreezeCharges = 0;
        ghostFitEnabled = false;
        sniffTestEnabled = false;
        extraTrashSlots = 0;
        dripTrayCharges = 0;
        acquiredPowerUps.clear();
    }
}
