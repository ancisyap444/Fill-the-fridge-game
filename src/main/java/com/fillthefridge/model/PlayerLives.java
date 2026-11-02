package com.fillthefridge.model;

import com.fillthefridge.util.GameEventListener;
import java.util.ArrayList;
import java.util.List;

public class PlayerLives {
    public static final int INITIAL_LIVES = 3;
    public static final int MAX_LIVES = 5;

    private int currentLives;
    private int maxLives;
    private final List<GameEventListener> listeners;

    public PlayerLives() {
        this.currentLives = INITIAL_LIVES;
        this.maxLives = MAX_LIVES;
        this.listeners = new ArrayList<>();
    }

    public void addListener(GameEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    public void loseLives(int count, String reason) {
        if (count <= 0 || currentLives <= 0) {
            return;
        }
        int previous = currentLives;
        currentLives = Math.max(0, currentLives - count);
        int delta = currentLives - previous;
        for (GameEventListener listener : new ArrayList<>(listeners)) {
            listener.onLivesChanged(currentLives, delta, reason);
        }
    }

    public void healLives(int count, String reason) {
        if (count <= 0) {
            return;
        }
        int previous = currentLives;
        currentLives = Math.min(maxLives, currentLives + count);
        int delta = currentLives - previous;
        if (delta > 0) {
            for (GameEventListener listener : new ArrayList<>(listeners)) {
                listener.onLivesChanged(currentLives, delta, reason);
            }
        }
    }

    public void reset() {
        this.currentLives = INITIAL_LIVES;
    }

    public boolean isDepleted() {
        return currentLives <= 0;
    }

    public int getCurrentLives() {
        return currentLives;
    }

    public int getMaxLives() {
        return maxLives;
    }
}
