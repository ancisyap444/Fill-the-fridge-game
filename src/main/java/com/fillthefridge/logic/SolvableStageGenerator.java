package com.fillthefridge.logic;

import com.fillthefridge.items.Item;
import com.fillthefridge.items.ItemCatalog;
import com.fillthefridge.items.TrapItem;
import com.fillthefridge.model.ActivePowerUps;
import com.fillthefridge.model.Fridge;
import com.fillthefridge.model.PlacementAction;
import com.fillthefridge.model.PlacementResult;
import com.fillthefridge.model.StageConfig;
import com.fillthefridge.util.GridPosition;
import com.fillthefridge.util.InvalidPlacementException;
import com.fillthefridge.util.StageGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SolvableStageGenerator implements StageGenerator {
    @Override
    public GeneratedStage generateStage(StageConfig config, ActivePowerUps activePowerUps) {
        Random random = new Random(config.getRandomSeed());
        int cols = config.getGridCols();
        int rows = config.getGridRows();

        Fridge buildFridge = new Fridge(cols, rows);
        List<PlacementAction> solutionPlacements = new ArrayList<>();
        List<Item> allTemplates = ItemCatalog.getAllGroceryTemplates();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!buildFridge.getTile(c, r).isEmpty()) {
                    continue;
                }
                GridPosition targetPos = new GridPosition(c, r);
                Item chosenTemplate = selectCompatibleItem(
                    buildFridge,
                    allTemplates,
                    targetPos,
                    rows,
                    config,
                    random
                );
                Item placedInstance = chosenTemplate.createFreshCopy();
                try {
                    buildFridge.placeItem(placedInstance, targetPos);
                    solutionPlacements.add(new PlacementAction(placedInstance, targetPos));
                } catch (InvalidPlacementException e) {
                    throw new IllegalStateException("Failed to tile guaranteed stage solution", e);
                }
            }
        }

        List<Item> stageItems = new ArrayList<>();
        float coldSnapBonus = activePowerUps != null ? activePowerUps.getColdSnapBonusSeconds() : 0f;
        boolean sniffTest = activePowerUps != null && activePowerUps.isSniffTestEnabled();

        for (PlacementAction action : solutionPlacements) {
            Item itemCopy = action.getItem().createFreshCopy();
            boolean shortLife = ItemCatalog.isShortLifeItem(itemCopy);
            itemCopy.applyTimerMultiplier(config.getTimerMultiplier(), shortLife);
            if (coldSnapBonus > 0f) {
                itemCopy.addBonusTime(coldSnapBonus);
            }
            stageItems.add(itemCopy);
        }

        List<TrapItem> trapTemplates = ItemCatalog.getAllTrapTemplates();
        for (int i = 0; i < config.getTrapCount(); i++) {
            TrapItem trapTemplate = trapTemplates.get(random.nextInt(trapTemplates.size()));
            TrapItem trapInstance = (TrapItem) trapTemplate.createFreshCopy();
            if (coldSnapBonus > 0f) {
                trapInstance.addBonusTime(coldSnapBonus);
            }
            if (sniffTest) {
                trapInstance.setRevealedTrap(true);
            }
            int insertIndex = stageItems.isEmpty() ? 0 : random.nextInt(stageItems.size() + 1);
            stageItems.add(insertIndex, trapInstance);
        }

        GeneratedStage generatedStage = new GeneratedStage(config, solutionPlacements, stageItems);
        if (!verifySolvability(generatedStage, config)) {
            throw new IllegalStateException("Generated stage failed solvability verification!");
        }
        return generatedStage;
    }

    private Item selectCompatibleItem(
        Fridge fridge,
        List<Item> templates,
        GridPosition targetPos,
        int totalRows,
        StageConfig config,
        Random random
    ) {
        List<Item> weightedCandidates = new ArrayList<>();
        List<Item> fallbackCandidates = new ArrayList<>();

        for (Item candidate : templates) {
            if (fridge.evaluatePlacement(candidate, targetPos) != PlacementResult.LEGAL) {
                continue;
            }
            int topRowAfterPlacement = targetPos.getRow() + candidate.getSize().getHeight();
            boolean reachesCeiling = topRowAfterPlacement == totalRows;

            if (!reachesCeiling) {
                if (candidate.isSpillableItem() || candidate.getMaxSupportWeight() < 1) {
                    continue;
                }
                if (targetPos.getRow() == 0 && totalRows >= 3 && candidate.getMaxSupportWeight() < 2
                    && !candidate.getId().equals("pizza_box") && !candidate.isAbsorbent()) {
                    continue;
                }
            }

            fallbackCandidates.add(candidate);

            int weightCopies = 2;
            if (candidate.getSize().getArea() > 1 && random.nextFloat() < config.getAwkwardShapeProbability()) {
                weightCopies += 4;
            }
            if (reachesCeiling && candidate.isSpillableItem() && random.nextFloat() < config.getSpillPuzzleProbability()) {
                weightCopies += 5;
            }
            if (!reachesCeiling && candidate.isAbsorbent() && random.nextFloat() < config.getSpillPuzzleProbability()) {
                weightCopies += 3;
            }
            for (int i = 0; i < weightCopies; i++) {
                weightedCandidates.add(candidate);
            }
        }

        if (!weightedCandidates.isEmpty()) {
            return weightedCandidates.get(random.nextInt(weightedCandidates.size()));
        }
        if (!fallbackCandidates.isEmpty()) {
            return fallbackCandidates.get(random.nextInt(fallbackCandidates.size()));
        }

        for (Item template : templates) {
            if (template.getSize().getWidth() == 1
                && template.getSize().getHeight() == 1
                && template.getWeight() == 1
                && !template.isSpillableItem()
                && template.getMaxSupportWeight() >= 2) {
                return template;
            }
        }
        return templates.get(0);
    }

    @Override
    public boolean verifySolvability(GeneratedStage generatedStage, StageConfig config) {
        Fridge testFridge = new Fridge(config.getGridCols(), config.getGridRows());
        for (PlacementAction action : generatedStage.getSolutionPlacements()) {
            Item testItem = action.getItem().createFreshCopy();
            PlacementResult eval = testFridge.evaluatePlacement(testItem, action.getPosition());
            if (eval != PlacementResult.LEGAL) {
                return false;
            }
            try {
                testFridge.placeItem(testItem, action.getPosition());
            } catch (InvalidPlacementException e) {
                return false;
            }
        }
        return testFridge.isEveryTileOccupiedByLegalItem()
            && !testFridge.hasTrapItems()
            && !testFridge.hasContaminatedItems();
    }
}
