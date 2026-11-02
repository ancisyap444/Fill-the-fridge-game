package com.fillthefridge.logic;

import com.fillthefridge.items.Item;
import com.fillthefridge.model.PlacementAction;
import com.fillthefridge.model.StageConfig;
import java.util.Collections;
import java.util.List;

public final class GeneratedStage {
    private final StageConfig config;
    private final List<PlacementAction> solutionPlacements;
    private final List<Item> stageItems;

    public GeneratedStage(
        StageConfig config,
        List<PlacementAction> solutionPlacements,
        List<Item> stageItems
    ) {
        this.config = config;
        this.solutionPlacements = List.copyOf(solutionPlacements);
        this.stageItems = List.copyOf(stageItems);
    }

    public StageConfig getConfig() {
        return config;
    }

    public List<PlacementAction> getSolutionPlacements() {
        return Collections.unmodifiableList(solutionPlacements);
    }

    public List<Item> getStageItems() {
        return Collections.unmodifiableList(stageItems);
    }
}
