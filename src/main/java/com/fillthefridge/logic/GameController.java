package com.fillthefridge.logic;

import com.fillthefridge.items.Item;
import com.fillthefridge.model.Fridge;
import com.fillthefridge.model.GamePhase;
import com.fillthefridge.model.GameState;
import com.fillthefridge.model.PlacementAction;
import com.fillthefridge.model.PlacementResult;
import com.fillthefridge.model.StageConfig;
import com.fillthefridge.model.TrashBin;
import com.fillthefridge.model.Tray;
import com.fillthefridge.powerups.PowerUp;
import com.fillthefridge.powerups.PowerUpPool;
import com.fillthefridge.util.GridPosition;
import com.fillthefridge.util.InvalidPlacementException;
import com.fillthefridge.util.Spillable;
import com.fillthefridge.util.StageGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameController {
    private final GameState gameState;
    private final DifficultyScalingStrategy difficultyStrategy;
    private final StageGenerator stageGenerator;
    private final PowerUpPool powerUpPool;
    private final long baseSeed;

    private GeneratedStage currentGeneratedStage;
    private List<PowerUp> currentPowerUpChoices;
    private float stageClearBannerTimer;

    public GameController(
        GameState gameState,
        DifficultyScalingStrategy difficultyStrategy,
        StageGenerator stageGenerator,
        long baseSeed
    ) {
        this.gameState = gameState;
        this.difficultyStrategy = difficultyStrategy;
        this.stageGenerator = stageGenerator;
        this.baseSeed = baseSeed;
        this.powerUpPool = new PowerUpPool(baseSeed);
        this.currentPowerUpChoices = new ArrayList<>();
    }

    public void startNewRun() {
        gameState.resetRun();
        startStage(1);
    }

    public void startStage(int stageNumber) {
        gameState.setCurrentStageNumber(stageNumber);
        gameState.getUndoStack().clear();

        StageConfig config = difficultyStrategy.createStageConfig(stageNumber, baseSeed);
        currentGeneratedStage = stageGenerator.generateStage(config, gameState.getActivePowerUps());

        gameState.getFridge().configureGrid(config.getGridCols(), config.getGridRows());
        gameState.getTrashBin().resetForStage(
            config.getTrashSlots(),
            gameState.getActivePowerUps().getExtraTrashSlots()
        );
        gameState.getTray().loadStageItems(
            currentGeneratedStage.getStageItems(),
            config.getTrayCapacity(),
            gameState.getActivePowerUps().isSniffTestEnabled()
        );
        gameState.setCurrentPhase(GamePhase.PLAYING);
        stageClearBannerTimer = 0f;
    }

    public void update(float deltaTime) {
        if (gameState.getCurrentPhase() == GamePhase.STAGE_CLEAR) {
            gameState.getFridge().update(deltaTime);
            stageClearBannerTimer -= deltaTime;
            if (stageClearBannerTimer <= 0f) {
                currentPowerUpChoices = powerUpPool.generateChoices(3);
                gameState.setCurrentPhase(GamePhase.POWERUP_SELECTION);
            }
            return;
        }

        if (gameState.getCurrentPhase() != GamePhase.PLAYING) {
            return;
        }

        gameState.getFridge().update(deltaTime);
        gameState.getTrashBin().update(deltaTime);
        gameState.getTray().update(deltaTime);

        checkTrayTimersAndSpoilage();

        if (gameState.getPlayerLives().isDepleted()) {
            gameState.setCurrentPhase(GamePhase.GAME_OVER);
            return;
        }

        if (gameState.isStageWinConditionMet()) {
            confirmPlacements();
        }
    }

    private void checkTrayTimersAndSpoilage() {
        Tray tray = gameState.getTray();
        List<Item> snapshot = new ArrayList<>(tray.getActiveItems());

        for (Item item : snapshot) {
            if (item.isWarningThresholdReached() && !item.isWarningAnnounced()) {
                item.setWarningAnnounced(true);
                String warning = item.onTimerWarning();
                gameState.notifyWarningMessage(warning);
            }

            if (item.isSpoiledAfterGrace()) {
                gameState.notifyItemSpoiled(item);
                gameState.getPlayerLives().loseLives(1, item.getDisplayName() + " spoiled outside the fridge!");

                if (gameState.getPlayerLives().isDepleted()) {
                    return;
                }

                if (item.isTrap()) {
                    tray.removeActiveItem(item);
                } else {
                    item.resetTimerToFull();
                    gameState.notifyWarningMessage("Fresh replacement added for spoiled " + item.getDisplayName() + "!");
                }
            }
        }
    }

    public void selectTrayItem(Item item) {
        if (gameState.getCurrentPhase() != GamePhase.PLAYING) {
            return;
        }
        Tray tray = gameState.getTray();
        if (tray.getSelectedItem() == item) {
            tray.clearSelection();
        } else {
            tray.selectItem(item);
        }
    }

    public void handleFridgeLeftClick(GridPosition clickedPosition) {
        if (gameState.getCurrentPhase() != GamePhase.PLAYING || clickedPosition == null) {
            return;
        }
        Tray tray = gameState.getTray();
        Fridge fridge = gameState.getFridge();
        Item selected = tray.getSelectedItem();

        if (selected == null) {
            Item existing = fridge.getItemAt(clickedPosition);
            if (existing != null) {
                if (existing.isContaminated() || existing.isTrap() || !existing.isConfirmed()) {
                    pickupItemFromFridge(existing);
                } else {
                    gameState.notifyWarningMessage(existing.getDisplayName() + " is locked in place!");
                }
            }
            return;
        }

        Item occupantAtClick = fridge.getItemAt(clickedPosition);
        if (occupantAtClick != null && (occupantAtClick.isContaminated() || occupantAtClick.isTrap())) {
            pickupItemFromFridge(occupantAtClick);
            return;
        }

        try {
            attemptPlaceSelectedItem(selected, clickedPosition);
        } catch (InvalidPlacementException e) {
            gameState.notifyWarningMessage(e.getMessage());
        }
    }

    private void pickupItemFromFridge(Item itemInFridge) {
        Fridge fridge = gameState.getFridge();
        Tray tray = gameState.getTray();

        fridge.removeItem(itemInFridge);
        returnUnsupportedItemsToTray();
        tray.returnItemToTray(itemInFridge);
        tray.selectItem(itemInFridge);

        if (itemInFridge.isContaminated()) {
            gameState.notifyWarningMessage("Selected contaminated " + itemInFridge.getDisplayName() + " - Click Trash Bin!");
        } else if (itemInFridge.isTrap()) {
            gameState.notifyWarningMessage("Selected trap " + itemInFridge.getDisplayName() + " - Click Trash Bin!");
        } else {
            gameState.notifyWarningMessage("Picked up " + itemInFridge.getDisplayName() + " from fridge.");
        }
    }

    private void returnUnsupportedItemsToTray() {
        Fridge fridge = gameState.getFridge();
        Tray tray = gameState.getTray();
        boolean changed = true;
        while (changed) {
            List<Item> unsupported = fridge.findUnsupportedItems();
            if (unsupported.isEmpty()) {
                changed = false;
            } else {
                for (Item floating : unsupported) {
                    fridge.removeItem(floating);
                    tray.returnItemToTray(floating);
                }
            }
        }
    }

    private void attemptPlaceSelectedItem(Item selected, GridPosition targetPos) throws InvalidPlacementException {
        if (selected.isContaminated()) {
            throw new InvalidPlacementException(
                selected.getDisplayName() + " is contaminated and must be trashed!",
                targetPos
            );
        }

        Fridge fridge = gameState.getFridge();
        PlacementResult result = fridge.evaluatePlacement(selected, targetPos);

        if (result == PlacementResult.UNSTABLE) {
            throw new InvalidPlacementException(
                "Unstable placement! Check grid bounds, empty tiles, and weight support below.",
                targetPos
            );
        }

        if (result == PlacementResult.SPILL_CAUSING) {
            if (gameState.getActivePowerUps().consumeDripTrayCharge()) {
                fridge.placeItem(selected, targetPos);
                gameState.getTray().removeActiveItem(selected);
                gameState.getUndoStack().push(new PlacementAction(selected, targetPos));
                gameState.notifyWarningMessage("Drip Tray negated the spill!");
                checkWinAfterAction();
                return;
            }

            List<Item> spillSources = fridge.getSpillableSupportingItems(selected, targetPos);
            fridge.placeItem(selected, targetPos);
            gameState.getTray().removeActiveItem(selected);
            gameState.getUndoStack().push(new PlacementAction(selected, targetPos));

            List<Item> allContaminated = new ArrayList<>();
            for (Item source : spillSources) {
                if (source instanceof Spillable spillableSource) {
                    List<Item> hit = spillableSource.spillDownward(fridge);
                    for (Item contaminated : hit) {
                        if (!allContaminated.contains(contaminated)) {
                            allContaminated.add(contaminated);
                        }
                    }
                }
            }

            if (!allContaminated.isEmpty()) {
                Item primarySource = spillSources.isEmpty() ? selected : spillSources.get(0);
                gameState.notifySpillOccurred(primarySource, allContaminated);
                gameState.getPlayerLives().loseLives(
                    allContaminated.size(),
                    primarySource.getDisplayName() + " spilled onto " + allContaminated.size() + " item(s)!"
                );
                if (gameState.getPlayerLives().isDepleted()) {
                    gameState.setCurrentPhase(GamePhase.GAME_OVER);
                }
            }
            return;
        }

        fridge.placeItem(selected, targetPos);
        gameState.getTray().removeActiveItem(selected);
        gameState.getUndoStack().push(new PlacementAction(selected, targetPos));

        if (selected.isTrap()) {
            gameState.notifyWarningMessage("Careful! That looks like trash and shouldn't stay in the fridge.");
        }
        checkWinAfterAction();
    }

    public void undoLastPlacement() {
        if (gameState.getCurrentPhase() != GamePhase.PLAYING) {
            return;
        }
        Optional<PlacementAction> popped = gameState.getUndoStack().pop();
        if (popped.isEmpty()) {
            gameState.notifyWarningMessage("No unconfirmed placement to undo.");
            return;
        }
        PlacementAction action = popped.get();
        Item item = action.getItem();
        if (item.isConfirmed()) {
            gameState.notifyWarningMessage("Placement is already locked!");
            return;
        }
        gameState.getFridge().removeItem(item);
        returnUnsupportedItemsToTray();
        gameState.getTray().returnItemToTray(item);
        gameState.getTray().selectItem(item);
        gameState.notifyWarningMessage("Undid placement of " + item.getDisplayName() + ".");
    }

    public void confirmPlacements() {
        if (gameState.getCurrentPhase() != GamePhase.PLAYING) {
            return;
        }
        gameState.getFridge().confirmAllPlacements();
        gameState.getUndoStack().clear();

        if (gameState.isStageWinConditionMet()) {
            stageClearBannerTimer = 1.85f;
            gameState.setCurrentPhase(GamePhase.STAGE_CLEAR);
            gameState.notifyStageCleared(gameState.getCurrentStageNumber());
        } else if (gameState.getFridge().hasTrapItems()) {
            gameState.notifyWarningMessage("Locked legal items! Remove and trash trap items in the fridge to finish.");
        } else if (gameState.getFridge().hasContaminatedItems()) {
            gameState.notifyWarningMessage("Click contaminated (green) items in the fridge and trash them!");
        } else {
            gameState.notifyWarningMessage("Placements locked!");
        }
    }

    public void trashSelectedItem() {
        if (gameState.getCurrentPhase() != GamePhase.PLAYING) {
            return;
        }
        Tray tray = gameState.getTray();
        TrashBin trashBin = gameState.getTrashBin();
        Item selected = tray.getSelectedItem();

        if (selected == null) {
            gameState.notifyWarningMessage("Select a trap or contaminated item first, then click the Trash Bin!");
            return;
        }

        try {
            boolean wasContaminatedGrocery = selected.isContaminated() && !selected.isTrap();
            trashBin.trashItem(selected);
            tray.removeActiveItem(selected);

            if (wasContaminatedGrocery) {
                Item freshReplacement = selected.createFreshCopy();
                tray.returnItemToTray(freshReplacement);
                gameState.notifyWarningMessage(
                    "Trashed contaminated " + selected.getDisplayName() + "! Fresh replacement added to tray."
                );
            } else {
                gameState.notifyWarningMessage("Trashed " + selected.getDisplayName() + "!");
            }
            checkWinAfterAction();
        } catch (InvalidPlacementException e) {
            gameState.notifyWarningMessage(e.getMessage());
        }
    }

    public void activateManualDeepFreeze() {
        if (gameState.getCurrentPhase() != GamePhase.PLAYING) {
            return;
        }
        if (gameState.getActivePowerUps().getDeepFreezeCharges() <= 0) {
            gameState.notifyWarningMessage("No Deep Freeze charges available!");
            return;
        }
        Item target = gameState.getTray().getSelectedItem();
        if (target == null) {
            target = gameState.getTray().findMostUrgentItem();
        }
        if (target != null && gameState.getActivePowerUps().consumeDeepFreezeCharge()) {
            target.applyDeepFreeze(3.0f);
            gameState.notifyWarningMessage("Deep Freeze paused " + target.getDisplayName() + " for 3s!");
        }
    }

    public void choosePowerUp(PowerUp chosenPowerUp) {
        if (gameState.getCurrentPhase() != GamePhase.POWERUP_SELECTION || chosenPowerUp == null) {
            return;
        }
        chosenPowerUp.apply(gameState);
        startStage(gameState.getCurrentStageNumber() + 1);
    }

    private void checkWinAfterAction() {
        if (gameState.isStageWinConditionMet()) {
            gameState.getFridge().confirmAllPlacements();
            gameState.getUndoStack().clear();
            stageClearBannerTimer = 1.85f;
            gameState.setCurrentPhase(GamePhase.STAGE_CLEAR);
            gameState.notifyStageCleared(gameState.getCurrentStageNumber());
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public GeneratedStage getCurrentGeneratedStage() {
        return currentGeneratedStage;
    }

    public List<PowerUp> getCurrentPowerUpChoices() {
        return currentPowerUpChoices;
    }

    public float getStageClearBannerTimer() {
        return stageClearBannerTimer;
    }
}
