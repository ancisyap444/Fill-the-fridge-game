package com.fillthefridge.model;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fillthefridge.items.Item;
import com.fillthefridge.util.InvalidPlacementException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrashBin extends GameObject {
    private int maxSlots;
    private final List<Item> trashedItems;
    private boolean hovered;
    private float bounceAnimationTimer;

    public TrashBin(int maxSlots) {
        super(0f, 0f, 130f, 180f);
        this.maxSlots = Math.max(1, maxSlots);
        this.trashedItems = new ArrayList<>();
    }

    @Override
    public void update(float deltaTime) {
        if (bounceAnimationTimer > 0f) {
            bounceAnimationTimer = Math.max(0f, bounceAnimationTimer - deltaTime);
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font) {
    }

    public boolean hasAvailableSlot() {
        return trashedItems.size() < maxSlots;
    }

    public void trashItem(Item item) throws InvalidPlacementException {
        if (item == null) {
            throw new InvalidPlacementException("No item selected to trash.");
        }
        if (!item.isTrap() && !item.isContaminated()) {
            throw new InvalidPlacementException("Fresh groceries belong in the fridge! Only trash traps or contaminated items.");
        }
        if (item.isTrap() && !hasAvailableSlot()) {
            throw new InvalidPlacementException("The trash bin is full! No slots remaining.");
        }
        if (item.isTrap()) {
            trashedItems.add(item);
        }
        bounceAnimationTimer = 0.35f;
    }

    public void addExtraSlots(int extraSlots) {
        if (extraSlots > 0) {
            this.maxSlots += extraSlots;
        }
    }

    public void resetForStage(int baseStageSlots, int bonusSlots) {
        this.trashedItems.clear();
        this.maxSlots = Math.max(1, baseStageSlots + bonusSlots);
        this.bounceAnimationTimer = 0f;
    }

    public int getUsedSlots() {
        return trashedItems.size();
    }

    public int getRemainingSlots() {
        return Math.max(0, maxSlots - trashedItems.size());
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public List<Item> getTrashedItems() {
        return Collections.unmodifiableList(trashedItems);
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public float getBounceAnimationTimer() {
        return bounceAnimationTimer;
    }
}
