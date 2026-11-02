package com.fillthefridge.model;

import com.fillthefridge.items.Item;
import com.fillthefridge.util.GameEventListener;
import com.fillthefridge.util.UndoStack;
import java.util.ArrayList;
import java.util.List;

public class GameState {
    private final Fridge fridge;
    private final Tray tray;
    private final TrashBin trashBin;
    private final PlayerLives playerLives;
    private final ActivePowerUps activePowerUps;
    private final UndoStack<PlacementAction> undoStack;
    private final List<GameEventListener> eventListeners;

    private int currentStageNumber;
    private GamePhase currentPhase;

    public GameState() {
        this.fridge = new Fridge(4, 3);
        this.tray = new Tray(4);
        this.trashBin = new TrashBin(3);
        this.playerLives = new PlayerLives();
        this.activePowerUps = new ActivePowerUps();
        this.undoStack = new UndoStack<>();
        this.eventListeners = new ArrayList<>();
        this.currentStageNumber = 1;
        this.currentPhase = GamePhase.MAIN_MENU;
    }

    public void addEventListener(GameEventListener listener) {
        if (listener != null && !eventListeners.contains(listener)) {
            eventListeners.add(listener);
            playerLives.addListener(listener);
        }
    }

    public void removeEventListener(GameEventListener listener) {
        eventListeners.remove(listener);
        playerLives.removeListener(listener);
    }

    public void notifyStageCleared(int stageNumber) {
        for (GameEventListener listener : new ArrayList<>(eventListeners)) {
            listener.onStageCleared(stageNumber);
        }
    }

    public void notifyItemSpoiled(Item item) {
        for (GameEventListener listener : new ArrayList<>(eventListeners)) {
            listener.onItemSpoiled(item);
        }
    }

    public void notifySpillOccurred(Item sourceItem, List<Item> contaminatedItems) {
        for (GameEventListener listener : new ArrayList<>(eventListeners)) {
            listener.onSpillOccurred(sourceItem, contaminatedItems);
        }
    }

    public void notifyWarningMessage(String message) {
        for (GameEventListener listener : new ArrayList<>(eventListeners)) {
            listener.onWarningMessage(message);
        }
    }

    public void resetRun() {
        this.currentStageNumber = 1;
        this.playerLives.reset();
        this.activePowerUps.reset();
        this.undoStack.clear();
        this.currentPhase = GamePhase.PLAYING;
    }

    public boolean isStageWinConditionMet() {
        return fridge.isEveryTileOccupiedByLegalItem()
            && tray.isCompletelyEmpty()
            && !fridge.hasTrapItems()
            && !fridge.hasContaminatedItems();
    }

    public Fridge getFridge() {
        return fridge;
    }

    public Tray getTray() {
        return tray;
    }

    public TrashBin getTrashBin() {
        return trashBin;
    }

    public PlayerLives getPlayerLives() {
        return playerLives;
    }

    public ActivePowerUps getActivePowerUps() {
        return activePowerUps;
    }

    public UndoStack<PlacementAction> getUndoStack() {
        return undoStack;
    }

    public int getCurrentStageNumber() {
        return currentStageNumber;
    }

    public void setCurrentStageNumber(int currentStageNumber) {
        this.currentStageNumber = currentStageNumber;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(GamePhase currentPhase) {
        this.currentPhase = currentPhase;
    }
}
