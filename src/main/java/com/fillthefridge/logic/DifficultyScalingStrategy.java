package com.fillthefridge.logic;

import com.fillthefridge.model.StageConfig;

public interface DifficultyScalingStrategy {
    StageConfig createStageConfig(int stageNumber, long baseSeed);
}
