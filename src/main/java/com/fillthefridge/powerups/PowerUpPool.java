package com.fillthefridge.powerups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PowerUpPool {
    private final Random random;

    public PowerUpPool(long seed) {
        this.random = new Random(seed);
    }

    public List<PowerUp> generateChoices(int count) {
        List<PowerUp> standardChoices = new ArrayList<>();
        standardChoices.add(new ColdSnapPowerUp());
        standardChoices.add(new DeepFreezePowerUp());
        standardChoices.add(new GhostFitPowerUp());
        standardChoices.add(new SniffTestPowerUp());
        standardChoices.add(new TrashCompactorPowerUp());
        standardChoices.add(new DripTrayPowerUp());

        Collections.shuffle(standardChoices, random);

        List<PowerUp> selected = new ArrayList<>();
        boolean includeRare = random.nextFloat() < 0.28f;
        if (includeRare) {
            selected.add(new SecondWindPowerUp());
        }

        for (PowerUp powerUp : standardChoices) {
            if (selected.size() >= count) {
                break;
            }
            selected.add(powerUp);
        }

        Collections.shuffle(selected, random);
        return selected;
    }
}
