package com.fillthefridge.model;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fillthefridge.items.Item;
import com.fillthefridge.util.GridPosition;
import com.fillthefridge.util.GridSize;
import com.fillthefridge.util.InvalidPlacementException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Fridge extends GameObject {
    private int cols;
    private int rows;
    private Tile[][] tiles;
    private final List<Shelf> shelves;
    private final List<Item> placedItems;

    public Fridge(int cols, int rows) {
        super(0f, 0f, 440f, 440f);
        this.shelves = new ArrayList<>();
        this.placedItems = new ArrayList<>();
        configureGrid(cols, rows);
    }

    public void configureGrid(int newCols, int newRows) {
        this.cols = Math.max(2, newCols);
        this.rows = Math.max(2, newRows);
        this.tiles = new Tile[this.cols][this.rows];
        this.shelves.clear();
        this.placedItems.clear();

        for (int c = 0; c < this.cols; c++) {
            for (int r = 0; r < this.rows; r++) {
                tiles[c][r] = new Tile(new GridPosition(c, r));
            }
        }
        for (int r = 0; r < this.rows; r++) {
            shelves.add(new Shelf(r, this.cols, r == 0));
        }
    }

    @Override
    public void update(float deltaTime) {
        for (Item item : placedItems) {
            item.update(deltaTime);
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font) {
    }

    public boolean isWithinBounds(GridPosition position, GridSize size) {
        if (position == null || size == null) {
            return false;
        }
        int startCol = position.getCol();
        int startRow = position.getRow();
        return startCol >= 0
            && startRow >= 0
            && startCol + size.getWidth() <= cols
            && startRow + size.getHeight() <= rows;
    }

    public boolean areTilesEmpty(GridPosition position, GridSize size) {
        if (!isWithinBounds(position, size)) {
            return false;
        }
        for (int c = position.getCol(); c < position.getCol() + size.getWidth(); c++) {
            for (int r = position.getRow(); r < position.getRow() + size.getHeight(); r++) {
                if (!tiles[c][r].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public PlacementResult evaluatePlacement(Item item, GridPosition targetPosition) {
        if (item == null || targetPosition == null) {
            return PlacementResult.UNSTABLE;
        }
        GridSize size = item.getSize();
        if (!isWithinBounds(targetPosition, size) || !areTilesEmpty(targetPosition, size)) {
            return PlacementResult.UNSTABLE;
        }
        int row = targetPosition.getRow();
        if (row == 0) {
            return PlacementResult.LEGAL;
        }

        Set<Item> supportingItems = new LinkedHashSet<>();
        for (int c = targetPosition.getCol(); c < targetPosition.getCol() + size.getWidth(); c++) {
            Tile belowTile = tiles[c][row - 1];
            if (belowTile.isEmpty() || belowTile.getOccupyingItem() == null) {
                return PlacementResult.UNSTABLE;
            }
            supportingItems.add(belowTile.getOccupyingItem());
        }

        boolean causesSpill = false;
        for (Item belowItem : supportingItems) {
            PlacementResult result = belowItem.onStackedUpon(item, this);
            if (result == PlacementResult.SPILL_CAUSING) {
                causesSpill = true;
            } else if (result == PlacementResult.UNSTABLE) {
                return PlacementResult.UNSTABLE;
            }
        }

        return causesSpill ? PlacementResult.SPILL_CAUSING : PlacementResult.LEGAL;
    }

    public List<Item> getSpillableSupportingItems(Item item, GridPosition targetPosition) {
        List<Item> spillables = new ArrayList<>();
        if (item == null || targetPosition == null || targetPosition.getRow() <= 0) {
            return spillables;
        }
        int belowRow = targetPosition.getRow() - 1;
        GridSize size = item.getSize();
        for (int c = targetPosition.getCol(); c < targetPosition.getCol() + size.getWidth(); c++) {
            if (c >= 0 && c < cols && belowRow >= 0 && belowRow < rows) {
                Item below = tiles[c][belowRow].getOccupyingItem();
                if (below != null && below.isSpillableItem() && !spillables.contains(below)) {
                    spillables.add(below);
                }
            }
        }
        return spillables;
    }

    public void placeItem(Item item, GridPosition targetPosition) throws InvalidPlacementException {
        if (!isWithinBounds(targetPosition, item.getSize())) {
            throw new InvalidPlacementException("Item placement is outside the fridge grid!", targetPosition);
        }
        if (!areTilesEmpty(targetPosition, item.getSize())) {
            throw new InvalidPlacementException("Target fridge tiles are already occupied!", targetPosition);
        }
        item.setGridPosition(targetPosition);
        item.setInFridge(true);
        item.setSelected(false);
        item.triggerSnapAnimation();

        for (int c = targetPosition.getCol(); c < targetPosition.getCol() + item.getSize().getWidth(); c++) {
            for (int r = targetPosition.getRow(); r < targetPosition.getRow() + item.getSize().getHeight(); r++) {
                tiles[c][r].setOccupyingItem(item);
            }
        }
        if (!placedItems.contains(item)) {
            placedItems.add(item);
        }
    }

    public void removeItem(Item item) {
        if (item == null) {
            return;
        }
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                if (tiles[c][r].getOccupyingItem() == item) {
                    tiles[c][r].clear();
                }
            }
        }
        placedItems.remove(item);
        item.setInFridge(false);
        item.setConfirmed(false);
        item.setGridPosition(null);
    }

    public List<Item> findUnsupportedItems() {
        List<Item> unsupported = new ArrayList<>();
        for (Item item : new ArrayList<>(placedItems)) {
            GridPosition pos = item.getGridPosition();
            if (pos != null && pos.getRow() > 0) {
                boolean supported = true;
                for (int c = pos.getCol(); c < pos.getCol() + item.getSize().getWidth(); c++) {
                    if (tiles[c][pos.getRow() - 1].isEmpty()) {
                        supported = false;
                        break;
                    }
                }
                if (!supported) {
                    unsupported.add(item);
                }
            }
        }
        return unsupported;
    }

    public List<Item> propagateSpillFrom(Item sourceItem) {
        List<Item> newlyContaminated = new ArrayList<>();
        if (sourceItem == null || sourceItem.getGridPosition() == null) {
            return newlyContaminated;
        }
        GridPosition sourcePos = sourceItem.getGridPosition();
        GridSize sourceSize = sourceItem.getSize();

        if (sourcePos.getRow() == 0) {
            if (!sourceItem.isContaminated()) {
                sourceItem.setContaminated(true);
                sourceItem.triggerSpillSplash();
                newlyContaminated.add(sourceItem);
            }
            refreshAllTileStates();
            return newlyContaminated;
        }

        collectDownwardSpillTargets(
            sourcePos.getCol(),
            sourcePos.getCol() + sourceSize.getWidth() - 1,
            sourcePos.getRow() - 1,
            newlyContaminated
        );

        if (newlyContaminated.isEmpty() && !sourceItem.isContaminated()) {
            sourceItem.setContaminated(true);
            sourceItem.triggerSpillSplash();
            newlyContaminated.add(sourceItem);
        }

        refreshAllTileStates();
        return newlyContaminated;
    }

    private void collectDownwardSpillTargets(
        int startCol,
        int endCol,
        int targetRow,
        List<Item> newlyContaminated
    ) {
        if (targetRow < 0 || targetRow >= rows) {
            return;
        }
        Set<Item> hitItemsAtRow = new LinkedHashSet<>();
        for (int c = Math.max(0, startCol); c <= Math.min(cols - 1, endCol); c++) {
            Item occupant = tiles[c][targetRow].getOccupyingItem();
            if (occupant != null) {
                hitItemsAtRow.add(occupant);
            }
        }

        for (Item hitItem : hitItemsAtRow) {
            if (!hitItem.isContaminated()) {
                hitItem.setContaminated(true);
                hitItem.triggerSpillSplash();
                newlyContaminated.add(hitItem);
            }
            if (hitItem.isAbsorbent() && hitItem.getGridPosition() != null) {
                GridPosition hitPos = hitItem.getGridPosition();
                GridSize hitSize = hitItem.getSize();
                collectDownwardSpillTargets(
                    hitPos.getCol(),
                    hitPos.getCol() + hitSize.getWidth() - 1,
                    hitPos.getRow() - 1,
                    newlyContaminated
                );
            }
        }
    }

    public void confirmAllPlacements() {
        for (Item item : placedItems) {
            if (!item.isTrap() && !item.isContaminated()) {
                item.setConfirmed(true);
            }
        }
        refreshAllTileStates();
    }

    public void refreshAllTileStates() {
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                tiles[c][r].refreshState();
            }
        }
    }

    public boolean isEveryTileOccupiedByLegalItem() {
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                Item occupant = tiles[c][r].getOccupyingItem();
                if (occupant == null || occupant.isTrap() || occupant.isContaminated()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean hasTrapItems() {
        for (Item item : placedItems) {
            if (item.isTrap()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasContaminatedItems() {
        for (Item item : placedItems) {
            if (item.isContaminated()) {
                return true;
            }
        }
        return false;
    }

    public Item getItemAt(GridPosition position) {
        if (position == null || position.getCol() < 0 || position.getCol() >= cols
            || position.getRow() < 0 || position.getRow() >= rows) {
            return null;
        }
        return tiles[position.getCol()][position.getRow()].getOccupyingItem();
    }

    public Tile getTile(int col, int row) {
        return tiles[col][row];
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public List<Shelf> getShelves() {
        return Collections.unmodifiableList(shelves);
    }

    public List<Item> getPlacedItems() {
        return Collections.unmodifiableList(placedItems);
    }
}
