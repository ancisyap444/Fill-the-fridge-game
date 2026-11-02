package com.fillthefridge.model;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fillthefridge.items.Item;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class Tray extends GameObject {
    private int maxVisibleCapacity;
    private final List<Item> activeItems;
    private final Deque<Item> pendingQueue;
    private Item selectedItem;

    public Tray(int maxVisibleCapacity) {
        super(0f, 0f, 820f, 165f);
        this.maxVisibleCapacity = Math.max(2, maxVisibleCapacity);
        this.activeItems = new ArrayList<>();
        this.pendingQueue = new ArrayDeque<>();
    }

    public void loadStageItems(List<Item> stageItems, int capacity, boolean revealTraps) {
        this.maxVisibleCapacity = Math.max(2, capacity);
        this.activeItems.clear();
        this.pendingQueue.clear();
        this.selectedItem = null;

        for (Item item : stageItems) {
            item.setInFridge(false);
            item.setConfirmed(false);
            item.setSelected(false);
            if (revealTraps && item.isTrap()) {
                item.setRevealedTrap(true);
            }
            pendingQueue.addLast(item);
        }
        refillVisibleSlots();
    }

    public void refillVisibleSlots() {
        while (activeItems.size() < maxVisibleCapacity && !pendingQueue.isEmpty()) {
            Item next = pendingQueue.removeFirst();
            next.setInFridge(false);
            activeItems.add(next);
        }
    }

    @Override
    public void update(float deltaTime) {
        for (Item item : activeItems) {
            item.update(deltaTime);
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font) {
    }

    public void selectItem(Item item) {
        if (selectedItem != null) {
            selectedItem.setSelected(false);
        }
        selectedItem = item;
        if (selectedItem != null) {
            selectedItem.setSelected(true);
        }
    }

    public void clearSelection() {
        if (selectedItem != null) {
            selectedItem.setSelected(false);
            selectedItem = null;
        }
    }

    public void removeActiveItem(Item item) {
        if (selectedItem == item) {
            clearSelection();
        }
        activeItems.remove(item);
        refillVisibleSlots();
    }

    public void returnItemToTray(Item item) {
        item.setInFridge(false);
        item.setConfirmed(false);
        item.setGridPosition(null);
        if (!activeItems.contains(item)) {
            activeItems.add(0, item);
        }
    }

    public void addBonusTimeToAll(float seconds) {
        for (Item item : activeItems) {
            item.addBonusTime(seconds);
        }
        for (Item item : pendingQueue) {
            item.addBonusTime(seconds);
        }
    }

    public void revealAllTraps() {
        for (Item item : activeItems) {
            if (item.isTrap()) {
                item.setRevealedTrap(true);
            }
        }
        for (Item item : pendingQueue) {
            if (item.isTrap()) {
                item.setRevealedTrap(true);
            }
        }
    }

    public Item findMostUrgentItem() {
        Item mostUrgent = null;
        float lowestTime = Float.MAX_VALUE;
        for (Item item : activeItems) {
            if (!item.isTrap() && item.getFreezeTimer() <= 0f && item.getRemainingTime() < lowestTime) {
                lowestTime = item.getRemainingTime();
                mostUrgent = item;
            }
        }
        if (mostUrgent == null && !activeItems.isEmpty()) {
            mostUrgent = activeItems.get(0);
        }
        return mostUrgent;
    }

    public boolean isCompletelyEmpty() {
        return activeItems.isEmpty() && pendingQueue.isEmpty();
    }

    public List<Item> getActiveItems() {
        return Collections.unmodifiableList(activeItems);
    }

    public int getPendingCount() {
        return pendingQueue.size();
    }

    public int getMaxVisibleCapacity() {
        return maxVisibleCapacity;
    }

    public Item getSelectedItem() {
        return selectedItem;
    }
}
