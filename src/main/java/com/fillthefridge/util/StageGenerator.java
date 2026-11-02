package com.fillthefridge.util;

import com.fillthefridge.logic.GeneratedStage;
import com.fillthefridge.model.ActivePowerUps;
import com.fillthefridge.model.StageConfig;

public interface StageGenerator {
    GeneratedStage generateStage(StageConfig config, ActivePowerUps activePowerUps);
    boolean verifySolvability(GeneratedStage generatedStage, StageConfig config);
}
