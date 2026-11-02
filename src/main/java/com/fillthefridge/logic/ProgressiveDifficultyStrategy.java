package com.fillthefridge.logic;

import com.fillthefridge.model.StageConfig;

public class ProgressiveDifficultyStrategy implements DifficultyScalingStrategy {
    @Override
    public StageConfig createStageConfig(int stageNumber, long baseSeed) {
        int safeStage = Math.max(1, stageNumber);

        int gridCols = 4;
        int gridRows = 3;
        if (safeStage >= 3 && safeStage <= 4) {
            gridCols = 4;
            gridRows = 4;
        } else if (safeStage >= 5 && safeStage <= 6) {
            gridCols = 5;
            gridRows = 4;
        } else if (safeStage >= 7) {
            gridCols = 6;
            gridRows = 4;
        }

        int trapCount = Math.min(6, 1 + (safeStage / 2));

        float awkwardShapeProbability = Math.min(0.65f, 0.18f + (safeStage - 1) * 0.08f);
        float spillPuzzleProbability = Math.min(0.60f, 0.20f + (safeStage - 1) * 0.07f);

        int trayCapacity = safeStage <= 3 ? 4 : 3;
        int extraTrashBuffer = safeStage <= 4 ? 2 : 1;
        int trashSlots = trapCount + extraTrashBuffer;

        float timerMultiplier = 1.0f;
        if (safeStage >= 6) {
            timerMultiplier = Math.max(0.65f, 1.0f - (safeStage - 5) * 0.06f);
        }

        long stageSeed = baseSeed + safeStage * 982451653L;

        return new StageConfig(
            safeStage,
            gridCols,
            gridRows,
            trapCount,
            trayCapacity,
            trashSlots,
            timerMultiplier,
            awkwardShapeProbability,
            spillPuzzleProbability,
            stageSeed
        );
    }
}
