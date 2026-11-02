package com.fillthefridge.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.fillthefridge.assets.AssetManager;
import com.fillthefridge.items.Item;
import com.fillthefridge.model.ActivePowerUps;
import com.fillthefridge.model.Fridge;
import com.fillthefridge.model.PlacementResult;
import com.fillthefridge.model.Shelf;
import com.fillthefridge.model.Tile;
import com.fillthefridge.model.TileState;
import com.fillthefridge.util.GridPosition;
import com.fillthefridge.util.GridSize;
import java.util.List;

public class ProceduralFridgeRenderer {
    private final Rectangle interiorBounds;
    private float tileWidth;
    private float tileHeight;
    private float animationClock;

    public ProceduralFridgeRenderer() {
        this.interiorBounds = new Rectangle(360f, 200f, 560f, 420f);
    }

    public void updateLayout(Fridge fridge, float screenWidth, float screenHeight) {
        float maxInteriorWidth = 620f;
        float maxInteriorHeight = 424f;

        int cols = fridge.getCols();
        int rows = fridge.getRows();

        float rawTileW = maxInteriorWidth / cols;
        float rawTileH = maxInteriorHeight / rows;
        float cellSize = Math.min(rawTileW, rawTileH);

        this.tileWidth = cellSize;
        this.tileHeight = cellSize;

        float actualWidth = tileWidth * cols;
        float actualHeight = tileHeight * rows;

        float centerX = screenWidth * 0.50f;
        float interiorX = centerX - actualWidth * 0.5f;
        float interiorY = 198f + (maxInteriorHeight - actualHeight) * 0.5f;

        interiorBounds.set(interiorX, interiorY, actualWidth, actualHeight);
        fridge.setBounds(interiorX - 24f, interiorY - 24f, actualWidth + 48f, actualHeight + 48f);
    }

    public void update(float deltaTime) {
        animationClock += deltaTime;
    }

    public GridPosition screenToGridPosition(Fridge fridge, float worldX, float worldY) {
        if (!interiorBounds.contains(worldX, worldY)) {
            return null;
        }
        int col = (int) ((worldX - interiorBounds.x) / tileWidth);
        int row = (int) ((worldY - interiorBounds.y) / tileHeight);
        col = MathUtils.clamp(col, 0, fridge.getCols() - 1);
        row = MathUtils.clamp(row, 0, fridge.getRows() - 1);
        return new GridPosition(col, row);
    }

    public void renderKitchenBackground(ShapeRenderer shapeRenderer, float width, float height) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.rect(
            0f,
            182f,
            width,
            height - 182f,
            new Color(0.24f, 0.36f, 0.52f, 1f),
            new Color(0.24f, 0.36f, 0.52f, 1f),
            new Color(0.32f, 0.46f, 0.64f, 1f),
            new Color(0.32f, 0.46f, 0.64f, 1f)
        );

        shapeRenderer.setColor(0.20f, 0.30f, 0.44f, 1f);
        shapeRenderer.rect(0f, 176f, width, 6f);

        shapeRenderer.rect(
            0f,
            0f,
            width,
            176f,
            new Color(0.14f, 0.22f, 0.34f, 1f),
            new Color(0.14f, 0.22f, 0.34f, 1f),
            new Color(0.18f, 0.28f, 0.42f, 1f),
            new Color(0.18f, 0.28f, 0.42f, 1f)
        );

        shapeRenderer.end();
    }

    public void renderFridgeStructure(
        ShapeRenderer shapeRenderer,
        SpriteBatch batch,
        BitmapFont smallFont,
        AssetManager assetManager,
        Fridge fridge,
        Item selectedItem,
        GridPosition hoverGridPos,
        ActivePowerUps activePowerUps
    ) {
        float cabX = interiorBounds.x - 24f;
        float cabY = interiorBounds.y - 24f;
        float cabW = interiorBounds.width + 48f;
        float cabH = interiorBounds.height + 48f;

        Gdx.glEnableBlend();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.06f, 0.10f, 0.18f, 0.38f);
        drawRoundedRect(shapeRenderer, cabX + 8f, cabY - 8f, cabW, cabH, 24f);

        shapeRenderer.setColor(0.18f, 0.42f, 0.78f, 1f);
        drawRoundedRect(shapeRenderer, cabX, cabY, cabW, cabH, 22f);

        shapeRenderer.setColor(0.24f, 0.52f, 0.90f, 1f);
        drawRoundedRect(shapeRenderer, cabX + 4f, cabY + 4f, cabW - 8f, cabH - 8f, 19f);

        shapeRenderer.setColor(0.74f, 0.82f, 0.90f, 1f);
        drawRoundedRect(
            shapeRenderer,
            interiorBounds.x - 6f,
            interiorBounds.y - 6f,
            interiorBounds.width + 12f,
            interiorBounds.height + 12f,
            10f
        );

        shapeRenderer.rect(
            interiorBounds.x,
            interiorBounds.y,
            interiorBounds.width,
            interiorBounds.height,
            new Color(0.85f, 0.90f, 0.95f, 1f),
            new Color(0.85f, 0.90f, 0.95f, 1f),
            new Color(0.95f, 0.98f, 1.00f, 1f),
            new Color(0.95f, 0.98f, 1.00f, 1f)
        );

        renderGridCellsAndShelves(shapeRenderer, fridge, selectedItem, activePowerUps);
        renderPlacedItemFootprintPads(shapeRenderer, fridge);

        if (selectedItem != null && hoverGridPos != null) {
            renderPlacementPreview(shapeRenderer, fridge, selectedItem, hoverGridPos);
        }

        shapeRenderer.end();

        batch.begin();
        renderPlacedItems(batch, smallFont, assetManager, fridge);
        if (selectedItem != null && hoverGridPos != null) {
            renderGhostSpritePreview(batch, smallFont, assetManager, selectedItem, hoverGridPos);
        }
        batch.end();

        Gdx.glEnableBlend();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderSpillAnimationsAndBadges(shapeRenderer, fridge);
        shapeRenderer.end();
    }

    private void renderGridCellsAndShelves(
        ShapeRenderer shapeRenderer,
        Fridge fridge,
        Item selectedItem,
        ActivePowerUps activePowerUps
    ) {
        int cols = fridge.getCols();
        int rows = fridge.getRows();
        boolean ghostFit = activePowerUps != null && activePowerUps.isGhostFitEnabled();

        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                float cellX = interiorBounds.x + c * tileWidth;
                float cellY = interiorBounds.y + r * tileHeight;
                Tile tile = fridge.getTile(c, r);

                if (ghostFit && selectedItem != null && tile.isEmpty()) {
                    PlacementResult fit = fridge.evaluatePlacement(selectedItem, new GridPosition(c, r));
                    if (fit == PlacementResult.LEGAL) {
                        shapeRenderer.setColor(0.20f, 0.85f, 0.45f, 0.22f);
                        shapeRenderer.rect(cellX + 3f, cellY + 3f, tileWidth - 6f, tileHeight - 6f);
                    }
                }

                shapeRenderer.setColor(0.76f, 0.83f, 0.90f, 0.70f);
                shapeRenderer.rect(cellX, cellY, 2f, tileHeight);
                shapeRenderer.rect(cellX + tileWidth - 2f, cellY, 2f, tileHeight);
            }
        }

        for (Shelf shelf : fridge.getShelves()) {
            float shelfY = interiorBounds.y + shelf.getRowIndex() * tileHeight;
            shapeRenderer.setColor(0.76f, 0.86f, 0.94f, 0.90f);
            shapeRenderer.rect(interiorBounds.x, shelfY, interiorBounds.width, 5f);

            if (shelf.isBaseShelf()) {
                shapeRenderer.setColor(0.56f, 0.66f, 0.78f, 1f);
                shapeRenderer.rect(interiorBounds.x, shelfY - 4f, interiorBounds.width, 6f);
            } else {
                shapeRenderer.setColor(0.46f, 0.64f, 0.84f, 0.95f);
                shapeRenderer.rect(interiorBounds.x, shelfY - 3f, interiorBounds.width, 5f);
            }
        }
    }

    private void renderPlacedItemFootprintPads(ShapeRenderer shapeRenderer, Fridge fridge) {
        for (Item item : fridge.getPlacedItems()) {
            GridPosition pos = item.getGridPosition();
            if (pos == null) {
                continue;
            }
            GridSize size = item.getSize();
            float drawX = interiorBounds.x + pos.getCol() * tileWidth + 3f;
            float drawY = interiorBounds.y + pos.getRow() * tileHeight + 3f;
            float drawW = size.getWidth() * tileWidth - 6f;
            float drawH = size.getHeight() * tileHeight - 6f;

            if (item.isContaminated()) {
                shapeRenderer.setColor(0.38f, 0.65f, 0.18f, 0.90f);
                drawRoundedRect(shapeRenderer, drawX, drawY, drawW, drawH, 10f);
                shapeRenderer.setColor(0.62f, 0.88f, 0.35f, 0.92f);
                drawRoundedRect(shapeRenderer, drawX + 2f, drawY + 2f, drawW - 4f, drawH - 4f, 8f);
            } else if (item.isTrap()) {
                shapeRenderer.setColor(0.78f, 0.22f, 0.28f, 0.90f);
                drawRoundedRect(shapeRenderer, drawX, drawY, drawW, drawH, 10f);
                shapeRenderer.setColor(0.96f, 0.68f, 0.72f, 0.92f);
                drawRoundedRect(shapeRenderer, drawX + 2f, drawY + 2f, drawW - 4f, drawH - 4f, 8f);
            } else if (item.isConfirmed()) {
                shapeRenderer.setColor(0.52f, 0.72f, 0.90f, 0.92f);
                drawRoundedRect(shapeRenderer, drawX, drawY, drawW, drawH, 10f);
                shapeRenderer.setColor(0.88f, 0.94f, 0.99f, 0.95f);
                drawRoundedRect(shapeRenderer, drawX + 2f, drawY + 2f, drawW - 4f, drawH - 4f, 8f);
            } else {
                shapeRenderer.setColor(0.60f, 0.76f, 0.90f, 0.90f);
                drawRoundedRect(shapeRenderer, drawX, drawY, drawW, drawH, 10f);
                shapeRenderer.setColor(0.92f, 0.96f, 1.00f, 0.95f);
                drawRoundedRect(shapeRenderer, drawX + 2f, drawY + 2f, drawW - 4f, drawH - 4f, 8f);
            }
        }
    }

    private void renderPlacementPreview(
        ShapeRenderer shapeRenderer,
        Fridge fridge,
        Item selectedItem,
        GridPosition hoverPos
    ) {
        PlacementResult result = fridge.evaluatePlacement(selectedItem, hoverPos);
        GridSize size = selectedItem.getSize();

        Color fillColor;
        Color borderColor;
        float pulse = 0.5f + 0.5f * MathUtils.sin(animationClock * 8f);

        if (result == PlacementResult.LEGAL) {
            fillColor = new Color(0.18f, 0.86f, 0.38f, 0.32f + pulse * 0.10f);
            borderColor = new Color(0.10f, 0.72f, 0.26f, 0.95f);
        } else if (result == PlacementResult.SPILL_CAUSING) {
            fillColor = new Color(0.98f, 0.62f, 0.08f, 0.40f + pulse * 0.15f);
            borderColor = new Color(0.95f, 0.38f, 0.05f, 0.98f);
        } else {
            fillColor = new Color(0.92f, 0.20f, 0.24f, 0.36f + pulse * 0.10f);
            borderColor = new Color(0.82f, 0.10f, 0.14f, 0.95f);
        }

        for (int c = hoverPos.getCol(); c < hoverPos.getCol() + size.getWidth(); c++) {
            for (int r = hoverPos.getRow(); r < hoverPos.getRow() + size.getHeight(); r++) {
                float cellX = interiorBounds.x + c * tileWidth;
                float cellY = interiorBounds.y + r * tileHeight;
                shapeRenderer.setColor(fillColor);
                shapeRenderer.rect(cellX + 2f, cellY + 2f, tileWidth - 4f, tileHeight - 4f);

                shapeRenderer.setColor(borderColor);
                shapeRenderer.rect(cellX + 2f, cellY + 2f, tileWidth - 4f, 3f);
                shapeRenderer.rect(cellX + 2f, cellY + tileHeight - 5f, tileWidth - 4f, 3f);
                shapeRenderer.rect(cellX + 2f, cellY + 2f, 3f, tileHeight - 4f);
                shapeRenderer.rect(cellX + tileWidth - 5f, cellY + 2f, 3f, tileHeight - 4f);
            }
        }
    }

    private void renderPlacedItems(
        SpriteBatch batch,
        BitmapFont smallFont,
        AssetManager assetManager,
        Fridge fridge
    ) {
        List<Item> items = fridge.getPlacedItems();
        for (Item item : items) {
            GridPosition pos = item.getGridPosition();
            if (pos == null) {
                continue;
            }
            GridSize size = item.getSize();
            float drawX = interiorBounds.x + pos.getCol() * tileWidth;
            float drawY = interiorBounds.y + pos.getRow() * tileHeight;
            float drawW = size.getWidth() * tileWidth;
            float drawH = size.getHeight() * tileHeight;

            item.renderItemSprite(batch, assetManager, smallFont, drawX, drawY, drawW, drawH, 1.0f);

            if (item.isContaminated()) {
                Texture warnTex = assetManager.getTexture(AssetManager.WARNING_ICON);
                batch.draw(warnTex, drawX + drawW - 28f, drawY + drawH - 28f, 24f, 24f);
                smallFont.setColor(0.95f, 1.0f, 0.45f, 1f);
                smallFont.draw(batch, "SPOILED!", drawX + 8f, drawY + 20f);
            } else if (item.isTrap()) {
                Texture warnTex = assetManager.getTexture(AssetManager.WARNING_ICON);
                batch.draw(warnTex, drawX + drawW - 28f, drawY + drawH - 28f, 24f, 24f);
                smallFont.setColor(1.0f, 0.55f, 0.55f, 1f);
                smallFont.draw(batch, "TRAP!", drawX + 8f, drawY + 20f);
            }
        }
        smallFont.setColor(Color.WHITE);
    }

    private void renderGhostSpritePreview(
        SpriteBatch batch,
        BitmapFont smallFont,
        AssetManager assetManager,
        Item selectedItem,
        GridPosition hoverPos
    ) {
        GridSize size = selectedItem.getSize();
        float drawX = interiorBounds.x + hoverPos.getCol() * tileWidth;
        float drawY = interiorBounds.y + hoverPos.getRow() * tileHeight;
        float drawW = size.getWidth() * tileWidth;
        float drawH = size.getHeight() * tileHeight;

        selectedItem.renderItemSprite(batch, assetManager, smallFont, drawX, drawY, drawW, drawH, 0.55f);
    }

    private void renderSpillAnimationsAndBadges(ShapeRenderer shapeRenderer, Fridge fridge) {
        for (Item item : fridge.getPlacedItems()) {
            float splashTimer = item.getSplashAnimationTimer();
            GridPosition pos = item.getGridPosition();
            if (splashTimer > 0f && pos != null) {
                float progress = 1.0f - (splashTimer / 0.95f);
                float centerX = interiorBounds.x + (pos.getCol() + item.getSize().getWidth() * 0.5f) * tileWidth;
                float centerY = interiorBounds.y + (pos.getRow() + item.getSize().getHeight() * 0.5f) * tileHeight;

                shapeRenderer.setColor(0.45f, 0.82f, 0.22f, Math.max(0f, 0.85f - progress * 0.8f));
                for (int i = 0; i < 8; i++) {
                    float angle = i * MathUtils.PI2 / 8f;
                    float dist = 14f + progress * 56f;
                    float dropX = centerX + MathUtils.cos(angle) * dist;
                    float dropY = centerY + MathUtils.sin(angle) * dist - progress * 24f;
                    shapeRenderer.circle(dropX, dropY, 6f * (1f - progress * 0.5f));
                }
            }
        }
    }

    public static void drawRoundedRect(
        ShapeRenderer shapeRenderer,
        float x,
        float y,
        float width,
        float height,
        float radius
    ) {
        float r = Math.min(radius, Math.min(width, height) * 0.5f);
        shapeRenderer.rect(x + r, y, width - 2f * r, height);
        shapeRenderer.rect(x, y + r, r, height - 2f * r);
        shapeRenderer.rect(x + width - r, y + r, r, height - 2f * r);
        shapeRenderer.arc(x + r, y + r, r, 180f, 90f, 12);
        shapeRenderer.arc(x + width - r, y + r, r, 270f, 90f, 12);
        shapeRenderer.arc(x + width - r, y + height - r, r, 0f, 90f, 12);
        shapeRenderer.arc(x + r, y + height - r, r, 90f, 90f, 12);
    }

    public Rectangle getInteriorBounds() {
        return interiorBounds;
    }

    private static final class Gdx {
        static void glEnableBlend() {
            com.badlogic.gdx.Gdx.gl.glEnable(GL20.GL_BLEND);
            com.badlogic.gdx.Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
    }
}
