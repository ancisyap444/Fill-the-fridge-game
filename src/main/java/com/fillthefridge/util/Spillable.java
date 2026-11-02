package com.fillthefridge.util;

import com.fillthefridge.items.Item;
import com.fillthefridge.model.Fridge;
import java.util.List;

public interface Spillable {
    boolean isSpillable();
    List<Item> spillDownward(Fridge fridge);
}
