package com.fillthefridge.util;

import com.fillthefridge.items.Item;
import java.util.List;

public interface GameEventListener {
    void onLivesChanged(int currentLives, int changeAmount, String reason);
    void onStageCleared(int stageNumber);
    void onItemSpoiled(Item item);
    void onSpillOccurred(Item sourceItem, List<Item> contaminatedItems);
    void onWarningMessage(String message);
}
