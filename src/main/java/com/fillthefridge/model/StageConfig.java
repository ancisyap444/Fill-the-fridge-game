package com.fillthefridge.model;

public final class StageConfig {
    private final int stageNumber;
    private final int gridCols;
    private final int gridRows;
    private final int trapCount;
    private final int trayCapacity;
    private final int trashSlots;
    private final float timerMultiplier;
    private final float awkwardShapeProbability;
    private final float spillPuzzleProbability;
    private final long randomSeed;

    public StageConfig(
        int stageNumber,
        int gridCols,
        int gridRows,
        int trapCount,
        int trayCapacity,
        int trashSlots,
        float timerMultiplier,
        float awkwardShapeProbability,
        float spillPuzzleProbability,
        long randomSeed
    ) {
        this.stageNumber = stageNumber;
        this.gridCols = gridCols;
        this.gridRows = gridRows;
        this.trapCount = trapCount;
        this.trayCapacity = trayCapacity;
        this.trashSlots = trashSlots;
        this.timerMultiplier = timerMultiplier;
        this.awkwardShapeProbability = awkwardShapeProbability;
        this.spillPuzzleProbability = spillPuzzleProbability;
        this.randomSeed = randomSeed;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public int getGridCols() {
        return gridCols;
    }

    public int getGridRows() {
        return gridRows;
    }

    public int getTrapCount() {
        return trapCount;
    }

    public int getTrayCapacity() {
        return trayCapacity;
    }

    public int getTrashSlots() {
        return trashSlots;
    }

    public float getTimerMultiplier() {
        return timerMultiplier;
    }

    public float getAwkwardShapeProbability() {
        return awkwardShapeProbability;
    }

    public float getSpillPuzzleProbability() {
        return spillPuzzleProbability;
    }

    public long getRandomSeed() {
        return randomSeed;
    }
}
