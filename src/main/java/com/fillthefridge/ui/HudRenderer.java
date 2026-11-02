package com.fillthefridge.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.fillthefridge.assets.AssetManager;
import com.fillthefridge.items.Item;
import com.fillthefridge.model.ActivePowerUps;
import com.fillthefridge.model.GameState;
import com.fillthefridge.model.PlacementResult;
import com.fillthefridge.model.PlayerLives;
import com.fillthefridge.model.TrashBin;
import com.fillthefridge.model.Tray;
import com.fillthefridge.util.GridPosition;
import com.fillthefridge.util.GridSize;
import java.util.ArrayList;
import java.util.List;

public class HudRenderer {
    private final List<Rectangle> traySlotBounds;
    private final Rectangle confirmButtonBounds;
    private final Rectangle undoButtonBounds;
    private final Rectangle freezeButtonBounds;
    private final Rectangle menuButtonBounds;

    private String bannerMessage;
    private float bannerTimer;
    private Color bannerColor;
    private float lifeLossShakeTimer;
    private float uiClock;

    public HudRenderer() {
        this.traySlotBounds = new ArrayList<>();
        this.confirmButtonBounds = new Rectangle(1015f, 544f, 225f, 48f);
        this.undoButtonBounds = new Rectangle(1015f, 484f, 225f, 48f);
        this.freezeButtonBounds = new Rectangle(1015f, 424f, 225f, 48f);
        this.menuButtonBounds = new Rectangle(1140f, 662f, 120f, 42f);
        this.bannerMessage = "";
        this.bannerTimer = 0f;
        this.bannerColor = new Color(0.95f, 0.75f, 0.15f, 1f);
    }

    public void update(float deltaTime, GameState gameState) {
        uiClock += deltaTime;
        if (bannerTimer > 0f) {
            bannerTimer = Math.max(0f, bannerTimer - deltaTime);
        }
        if (lifeLossShakeTimer > 0f) {
            lifeLossShakeTimer = Math.max(0f, lifeLossShakeTimer - deltaTime);
        }
        updateTrayAndTrashBounds(gameState);
    }

    public void updateTrayAndTrashBounds(GameState gameState) {
        TrashBin trashBin = gameState.getTrashBin();
        trashBin.setBounds(96f, 250f, 162f, 214f);

        Tray tray = gameState.getTray();
        tray.setBounds(200f, 10f, 880f, 162f);

        traySlotBounds.clear();
        List<Item> activeItems = tray.getActiveItems();
        int count = activeItems.size();
        if (count == 0) {
            return;
        }

        float slotW = 192f;
        float slotH = 152f;
        float gap = 18f;
        float totalW = count * slotW + (count - 1) * gap;
        float startX = 640f - totalW * 0.5f;
        float slotY = 14f;

        for (int i = 0; i < count; i++) {
            Rectangle rect = new Rectangle(startX + i * (slotW + gap), slotY, slotW, slotH);
            traySlotBounds.add(rect);
            activeItems.get(i).setBounds(rect.x, rect.y, rect.width, rect.height);
        }
    }

    public void showNotification(String message, Color color, float duration) {
        this.bannerMessage = message;
        this.bannerColor = new Color(color);
        this.bannerTimer = duration;
    }

    public void triggerLifeShake() {
        this.lifeLossShakeTimer = 0.65f;
    }

    public void renderHudAndTray(
        ShapeRenderer shapeRenderer,
        SpriteBatch batch,
        AssetManager assetManager,
        GameState gameState,
        GridPosition hoverGridPos,
        float mouseX,
        float mouseY
    ) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderTopBarShapes(shapeRenderer, mouseX, mouseY);
        renderTrashBinShapes(shapeRenderer, gameState.getTrashBin(), gameState.getTray().getSelectedItem());
        renderTrayBasketShapes(shapeRenderer, gameState.getTray());
        renderSideActionButtonsShapes(shapeRenderer, gameState, mouseX, mouseY);
        if (bannerTimer > 0f && bannerMessage != null && !bannerMessage.isEmpty()) {
            renderToastBannerShape(shapeRenderer);
        }
        shapeRenderer.end();

        batch.begin();
        renderTopBarTextAndIcons(batch, assetManager, gameState);
        renderTrashBinSpriteAndText(batch, assetManager, gameState.getTrashBin());
        renderTrayItemsAndTimers(batch, assetManager, gameState.getTray());
        renderSideActionButtonsText(batch, assetManager, gameState, hoverGridPos);
        if (bannerTimer > 0f && bannerMessage != null && !bannerMessage.isEmpty()) {
            renderToastBannerText(batch, assetManager);
        }
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderTrayTimerBars(shapeRenderer, gameState.getTray());
        shapeRenderer.end();
    }

    private void renderTopBarShapes(ShapeRenderer shapeRenderer, float mouseX, float mouseY) {
        shapeRenderer.setColor(0.09f, 0.16f, 0.28f, 0.92f);
        shapeRenderer.rect(0f, 652f, 1280f, 68f);

        shapeRenderer.setColor(0.18f, 0.48f, 0.92f, 1f);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, 18f, 661f, 196f, 48f, 12f);

        shapeRenderer.setColor(0.14f, 0.24f, 0.40f, 0.95f);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, 224f, 661f, 220f, 48f, 12f);

        shapeRenderer.setColor(0.14f, 0.24f, 0.40f, 0.95f);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, 454f, 661f, 668f, 48f, 12f);

        boolean menuHover = menuButtonBounds.contains(mouseX, mouseY);
        shapeRenderer.setColor(menuHover ? new Color(0.88f, 0.32f, 0.36f, 1f) : new Color(0.72f, 0.24f, 0.28f, 1f));
        ProceduralFridgeRenderer.drawRoundedRect(
            shapeRenderer,
            menuButtonBounds.x,
            menuButtonBounds.y,
            menuButtonBounds.width,
            menuButtonBounds.height,
            10f
        );
    }

    private void renderTopBarTextAndIcons(SpriteBatch batch, AssetManager assetManager, GameState gameState) {
        BitmapFont subtitleFont = assetManager.getSubtitleFont();
        BitmapFont bodyFont = assetManager.getBodyFont();
        BitmapFont smallFont = assetManager.getSmallFont();

        subtitleFont.setColor(Color.WHITE);
        subtitleFont.draw(batch, "STAGE " + gameState.getCurrentStageNumber(), 28f, 694f);
        smallFont.setColor(0.82f, 0.92f, 1.0f, 1f);
        smallFont.draw(
            batch,
            "(" + gameState.getFridge().getCols() + "x" + gameState.getFridge().getRows() + ")",
            152f,
            689f
        );

        PlayerLives lives = gameState.getPlayerLives();
        Texture heartTex = assetManager.getTexture(AssetManager.HEART);
        float shakeX = lifeLossShakeTimer > 0f ? MathUtils.sin(uiClock * 45f) * 5f : 0f;

        bodyFont.setColor(Color.WHITE);
        bodyFont.draw(batch, "LIVES:", 236f + shakeX, 691f);

        for (int i = 0; i < Math.max(PlayerLives.INITIAL_LIVES, lives.getCurrentLives()); i++) {
            float hx = 300f + i * 36f + shakeX;
            float hy = 669f;
            if (i < lives.getCurrentLives()) {
                batch.setColor(1f, 1f, 1f, 1f);
            } else {
                batch.setColor(0.25f, 0.28f, 0.32f, 0.55f);
            }
            batch.draw(heartTex, hx, hy, 30f, 30f);
        }
        batch.setColor(Color.WHITE);

        ActivePowerUps powerUps = gameState.getActivePowerUps();
        StringBuilder powerSummary = new StringBuilder("Powerups: ");
        List<String> tags = new ArrayList<>();
        if (powerUps.getColdSnapBonusSeconds() > 0f) {
            tags.add("Cold Snap (+" + (int) powerUps.getColdSnapBonusSeconds() + "s)");
        }
        if (powerUps.getDeepFreezeCharges() > 0) {
            tags.add("Deep Freeze x" + powerUps.getDeepFreezeCharges() + " [F]");
        }
        if (powerUps.isGhostFitEnabled()) {
            tags.add("Ghost Fit");
        }
        if (powerUps.isSniffTestEnabled()) {
            tags.add("Sniff Test");
        }
        if (powerUps.getDripTrayCharges() > 0) {
            tags.add("Drip Tray x" + powerUps.getDripTrayCharges());
        }
        if (powerUps.getExtraTrashSlots() > 0) {
            tags.add("Compactor (+" + powerUps.getExtraTrashSlots() + ")");
        }
        if (tags.isEmpty()) {
            powerSummary.append("None yet (clear stage to earn!)");
        } else {
            powerSummary.append(String.join(" | ", tags));
        }

        smallFont.setColor(0.92f, 0.96f, 1.0f, 1f);
        smallFont.draw(batch, powerSummary.toString(), 468f, 690f);

        bodyFont.setColor(Color.WHITE);
        bodyFont.draw(batch, "MENU [ESC]", menuButtonBounds.x + 15f, menuButtonBounds.y + 27f);
    }

    private void renderTrashBinShapes(ShapeRenderer shapeRenderer, TrashBin trashBin, Item selectedItem) {
        float x = trashBin.getX();
        float y = trashBin.getY();
        float w = trashBin.getWidth();
        float h = trashBin.getHeight();

        boolean highlightTrash = selectedItem != null && (selectedItem.isTrap() || selectedItem.isContaminated());
        Color bgColor = trashBin.isHovered()
            ? new Color(0.18f, 0.30f, 0.46f, 0.96f)
            : new Color(0.12f, 0.20f, 0.34f, 0.94f);

        shapeRenderer.setColor(bgColor);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, x, y, w, h, 18f);

        if (highlightTrash) {
            float pulse = 0.5f + 0.5f * MathUtils.sin(uiClock * 8f);
            shapeRenderer.setColor(0.98f, 0.58f, 0.16f, 0.65f + pulse * 0.3f);
            shapeRenderer.rect(x + 3f, y + 3f, w - 6f, 4f);
            shapeRenderer.rect(x + 3f, y + h - 7f, w - 6f, 4f);
            shapeRenderer.rect(x + 3f, y + 3f, 4f, h - 6f);
            shapeRenderer.rect(x + w - 7f, y + 3f, 4f, h - 6f);
        }
    }

    private void renderTrashBinSpriteAndText(SpriteBatch batch, AssetManager assetManager, TrashBin trashBin) {
        Texture trashTex = assetManager.getTexture(AssetManager.TRASH_BIN);
        BitmapFont bodyFont = assetManager.getBodyFont();
        BitmapFont smallFont = assetManager.getSmallFont();

        float bounce = trashBin.getBounceAnimationTimer() > 0f
            ? MathUtils.sin(trashBin.getBounceAnimationTimer() * 18f) * 8f
            : 0f;

        float iconSize = 98f + bounce;
        float iconX = trashBin.getX() + (trashBin.getWidth() - iconSize) * 0.5f;
        float iconY = trashBin.getY() + 66f;
        batch.draw(trashTex, iconX, iconY, iconSize, iconSize);

        GlyphLayout titleLayout = new GlyphLayout(bodyFont, "TRASH BIN");
        bodyFont.setColor(Color.WHITE);
        bodyFont.draw(
            batch,
            "TRASH BIN",
            trashBin.getX() + (trashBin.getWidth() - titleLayout.width) * 0.5f,
            trashBin.getY() + 194f
        );

        String slotStr = "Slots: " + trashBin.getUsedSlots() + " / " + trashBin.getMaxSlots();
        GlyphLayout slotLayout = new GlyphLayout(smallFont, slotStr);
        Color slotColor = trashBin.hasAvailableSlot()
            ? new Color(0.55f, 0.95f, 0.62f, 1f)
            : new Color(1.0f, 0.45f, 0.45f, 1f);
        smallFont.setColor(slotColor);
        smallFont.draw(
            batch,
            slotStr,
            trashBin.getX() + (trashBin.getWidth() - slotLayout.width) * 0.5f,
            trashBin.getY() + 46f
        );

        String hintStr = "Click to Trash";
        GlyphLayout hintLayout = new GlyphLayout(smallFont, hintStr);
        smallFont.setColor(0.78f, 0.86f, 0.96f, 1f);
        smallFont.draw(
            batch,
            hintStr,
            trashBin.getX() + (trashBin.getWidth() - hintLayout.width) * 0.5f,
            trashBin.getY() + 24f
        );
        smallFont.setColor(Color.WHITE);
    }

    private void renderTrayBasketShapes(ShapeRenderer shapeRenderer, Tray tray) {
        List<Item> activeItems = tray.getActiveItems();
        for (int i = 0; i < traySlotBounds.size() && i < activeItems.size(); i++) {
            Rectangle slot = traySlotBounds.get(i);
            Item item = activeItems.get(i);

            shapeRenderer.setColor(0.05f, 0.09f, 0.16f, 0.38f);
            ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, slot.x + 4f, slot.y - 4f, slot.width, slot.height, 16f);

            Color cardOuter = item.isSelected()
                ? new Color(0.98f, 0.76f, 0.16f, 1f)
                : new Color(0.20f, 0.46f, 0.82f, 1f);
            shapeRenderer.setColor(cardOuter);
            ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, slot.x, slot.y, slot.width, slot.height, 16f);

            Color cardInner = item.isHovered() || item.isSelected()
                ? new Color(0.96f, 0.98f, 1.00f, 1f)
                : new Color(0.90f, 0.94f, 0.98f, 1f);
            shapeRenderer.setColor(cardInner);
            ProceduralFridgeRenderer.drawRoundedRect(
                shapeRenderer,
                slot.x + 5f,
                slot.y + 5f,
                slot.width - 10f,
                slot.height - 10f,
                12f
            );

            renderMiniFootprintGrid(shapeRenderer, slot, item.getSize());

            if (item.isRevealedTrap() || item.isContaminated()) {
                shapeRenderer.setColor(0.92f, 0.22f, 0.28f, 0.92f);
                ProceduralFridgeRenderer.drawRoundedRect(
                    shapeRenderer,
                    slot.x + 10f,
                    slot.y + slot.height - 30f,
                    68f,
                    20f,
                    6f
                );
            }
        }

        if (tray.getPendingCount() > 0) {
            shapeRenderer.setColor(0.12f, 0.20f, 0.34f, 0.94f);
            ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, 1065f, 38f, 190f, 104f, 16f);
        }
    }

    private void renderMiniFootprintGrid(ShapeRenderer shapeRenderer, Rectangle slot, GridSize size) {
        int cols = size.getWidth();
        int rows = size.getHeight();
        float cellSq = 8.5f;
        float gap = 2.5f;
        float totalW = cols * cellSq + (cols - 1) * gap;
        float totalH = rows * cellSq + (rows - 1) * gap;

        float badgePad = 5f;
        float badgeW = totalW + badgePad * 2f;
        float badgeH = totalH + badgePad * 2f;
        float badgeX = slot.x + slot.width - badgeW - 10f;
        float badgeY = slot.y + slot.height - badgeH - 10f;

        shapeRenderer.setColor(0.14f, 0.24f, 0.40f, 0.88f);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, badgeX, badgeY, badgeW, badgeH, 5f);

        shapeRenderer.setColor(0.45f, 0.82f, 1.00f, 1f);
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                float sqX = badgeX + badgePad + c * (cellSq + gap);
                float sqY = badgeY + badgePad + r * (cellSq + gap);
                shapeRenderer.rect(sqX, sqY, cellSq, cellSq);
            }
        }
    }

    private void renderTrayItemsAndTimers(SpriteBatch batch, AssetManager assetManager, Tray tray) {
        BitmapFont smallFont = assetManager.getSmallFont();
        BitmapFont bodyFont = assetManager.getBodyFont();
        List<Item> activeItems = tray.getActiveItems();

        for (int i = 0; i < traySlotBounds.size() && i < activeItems.size(); i++) {
            Rectangle slot = traySlotBounds.get(i);
            Item item = activeItems.get(i);

            item.renderItemSprite(
                batch,
                assetManager,
                smallFont,
                slot.x + 28f,
                slot.y + 42f,
                slot.width - 56f,
                slot.height - 62f,
                1.0f
            );

            GlyphLayout nameLayout = new GlyphLayout(smallFont, item.getDisplayName());
            smallFont.setColor(0.14f, 0.22f, 0.34f, 1f);
            smallFont.draw(
                batch,
                item.getDisplayName(),
                slot.x + (slot.width - nameLayout.width) * 0.5f,
                slot.y + 36f
            );

            if (item.isRevealedTrap()) {
                smallFont.setColor(Color.WHITE);
                smallFont.draw(batch, "TRAP!", slot.x + 20f, slot.y + slot.height - 15f);
            } else if (item.isContaminated()) {
                smallFont.setColor(Color.WHITE);
                smallFont.draw(batch, "SPOILED!", slot.x + 14f, slot.y + slot.height - 15f);
            }

            if (item.getFreezeTimer() > 0f) {
                Texture snowTex = assetManager.getTexture(AssetManager.SNOWFLAKE);
                batch.draw(snowTex, slot.x + 12f, slot.y + 46f, 26f, 26f);
            } else if (item.isCriticalThresholdReached()) {
                float pulse = 0.8f + 0.25f * MathUtils.sin(uiClock * 14f);
                Texture warnTex = assetManager.getTexture(AssetManager.WARNING_ICON);
                float iconSize = 26f * pulse;
                batch.draw(warnTex, slot.x + 12f, slot.y + 46f, iconSize, iconSize);
            }
        }

        if (tray.getPendingCount() > 0) {
            bodyFont.setColor(Color.WHITE);
            bodyFont.draw(batch, "CART QUEUE", 1098f, 118f);
            smallFont.setColor(0.75f, 0.90f, 1.0f, 1f);
            smallFont.draw(batch, "+" + tray.getPendingCount() + " more item(s)", 1102f, 90f);
            smallFont.draw(batch, "waiting to pack", 1102f, 66f);
        }

        smallFont.setColor(Color.WHITE);
    }

    private void renderTrayTimerBars(ShapeRenderer shapeRenderer, Tray tray) {
        List<Item> activeItems = tray.getActiveItems();
        for (int i = 0; i < traySlotBounds.size() && i < activeItems.size(); i++) {
            Rectangle slot = traySlotBounds.get(i);
            Item item = activeItems.get(i);

            float barX = slot.x + 14f;
            float barY = slot.y + 11f;
            float barW = slot.width - 28f;
            float barH = 9f;

            shapeRenderer.setColor(0.20f, 0.26f, 0.34f, 1f);
            shapeRenderer.rect(barX, barY, barW, barH);

            float ratio = item.getShelfLifeRatio();
            if (item.getFreezeTimer() > 0f) {
                shapeRenderer.setColor(0.32f, 0.82f, 0.98f, 1f);
            } else if (item.isInGracePeriod()) {
                float flash = 0.5f + 0.5f * MathUtils.sin(uiClock * 28f);
                shapeRenderer.setColor(1.0f, 0.15f, 0.15f, flash);
                ratio = item.getGraceTimeRemaining() / Item.GRACE_PERIOD_SECONDS;
            } else if (ratio <= Item.CRITICAL_THRESHOLD_RATIO) {
                shapeRenderer.setColor(0.95f, 0.20f, 0.20f, 1f);
            } else if (ratio <= Item.WARNING_THRESHOLD_RATIO) {
                shapeRenderer.setColor(0.96f, 0.68f, 0.12f, 1f);
            } else {
                shapeRenderer.setColor(0.22f, 0.80f, 0.36f, 1f);
            }

            shapeRenderer.rect(barX, barY, barW * MathUtils.clamp(ratio, 0f, 1f), barH);
        }
    }

    private void renderSideActionButtonsShapes(
        ShapeRenderer shapeRenderer,
        GameState gameState,
        float mouseX,
        float mouseY
    ) {
        boolean confirmHover = confirmButtonBounds.contains(mouseX, mouseY);
        shapeRenderer.setColor(confirmHover ? new Color(0.22f, 0.78f, 0.40f, 1f) : new Color(0.16f, 0.64f, 0.32f, 1f));
        ProceduralFridgeRenderer.drawRoundedRect(
            shapeRenderer,
            confirmButtonBounds.x,
            confirmButtonBounds.y,
            confirmButtonBounds.width,
            confirmButtonBounds.height,
            12f
        );

        boolean undoHover = undoButtonBounds.contains(mouseX, mouseY);
        shapeRenderer.setColor(undoHover ? new Color(0.94f, 0.58f, 0.18f, 1f) : new Color(0.80f, 0.46f, 0.14f, 1f));
        ProceduralFridgeRenderer.drawRoundedRect(
            shapeRenderer,
            undoButtonBounds.x,
            undoButtonBounds.y,
            undoButtonBounds.width,
            undoButtonBounds.height,
            12f
        );

        int freezeCharges = gameState.getActivePowerUps().getDeepFreezeCharges();
        boolean freezeHover = freezeButtonBounds.contains(mouseX, mouseY) && freezeCharges > 0;
        Color freezeColor = freezeCharges > 0
            ? (freezeHover ? new Color(0.24f, 0.72f, 0.96f, 1f) : new Color(0.18f, 0.58f, 0.84f, 1f))
            : new Color(0.26f, 0.34f, 0.44f, 0.85f);
        shapeRenderer.setColor(freezeColor);
        ProceduralFridgeRenderer.drawRoundedRect(
            shapeRenderer,
            freezeButtonBounds.x,
            freezeButtonBounds.y,
            freezeButtonBounds.width,
            freezeButtonBounds.height,
            12f
        );

        shapeRenderer.setColor(0.12f, 0.20f, 0.34f, 0.94f);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, 1015f, 224f, 225f, 182f, 16f);
    }

    private void renderSideActionButtonsText(
        SpriteBatch batch,
        AssetManager assetManager,
        GameState gameState,
        GridPosition hoverGridPos
    ) {
        BitmapFont bodyFont = assetManager.getBodyFont();
        BitmapFont smallFont = assetManager.getSmallFont();

        bodyFont.setColor(Color.WHITE);
        bodyFont.draw(batch, "Lock [SPACE]", confirmButtonBounds.x + 44f, confirmButtonBounds.y + 31f);
        bodyFont.draw(batch, "Undo [R-Click]", undoButtonBounds.x + 42f, undoButtonBounds.y + 31f);

        int freezeCharges = gameState.getActivePowerUps().getDeepFreezeCharges();
        bodyFont.draw(
            batch,
            "Freeze [F] (" + freezeCharges + ")",
            freezeButtonBounds.x + 40f,
            freezeButtonBounds.y + 31f
        );

        smallFont.setColor(0.95f, 0.88f, 0.42f, 1f);
        smallFont.draw(batch, "CONTROLS & RULES", 1042f, 386f);

        smallFont.setColor(0.86f, 0.92f, 0.98f, 1f);
        smallFont.draw(batch, "L-Click: Select / Place", 1032f, 358f);
        smallFont.draw(batch, "R-Click: Undo unconfirmed", 1032f, 334f);
        smallFont.draw(batch, "Space: Lock placements", 1032f, 310f);
        smallFont.draw(batch, "Trash Bin: Trash traps", 1032f, 286f);

        Item selected = gameState.getTray().getSelectedItem();
        if (selected != null && hoverGridPos != null) {
            PlacementResult eval = gameState.getFridge().evaluatePlacement(selected, hoverGridPos);
            if (eval == PlacementResult.LEGAL) {
                smallFont.setColor(0.35f, 0.96f, 0.52f, 1f);
            } else if (eval == PlacementResult.SPILL_CAUSING) {
                smallFont.setColor(1.0f, 0.68f, 0.16f, 1f);
            } else {
                smallFont.setColor(1.0f, 0.42f, 0.42f, 1f);
            }
            smallFont.draw(batch, "Preview: " + eval.name(), 1032f, 252f);
        } else if (selected != null) {
            smallFont.setColor(0.65f, 0.90f, 1.0f, 1f);
            smallFont.draw(batch, "Holding: " + selected.getDisplayName(), 1032f, 252f);
        } else {
            smallFont.setColor(0.72f, 0.78f, 0.86f, 1f);
            smallFont.draw(batch, "Select a tray item!", 1032f, 252f);
        }
        smallFont.setColor(Color.WHITE);
    }

    private void renderToastBannerShape(ShapeRenderer shapeRenderer) {
        float alpha = Math.min(1f, bannerTimer * 2.5f);
        shapeRenderer.setColor(0.09f, 0.15f, 0.26f, 0.94f * alpha);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, 310f, 604f, 660f, 40f, 12f);

        shapeRenderer.setColor(bannerColor.r, bannerColor.g, bannerColor.b, alpha);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, 314f, 607f, 8f, 34f, 4f);
    }

    private void renderToastBannerText(SpriteBatch batch, AssetManager assetManager) {
        BitmapFont bodyFont = assetManager.getBodyFont();
        float alpha = Math.min(1f, bannerTimer * 2.5f);
        bodyFont.setColor(bannerColor.r, bannerColor.g, bannerColor.b, alpha);

        GlyphLayout layout = new GlyphLayout(bodyFont, bannerMessage);
        float textX = 640f - layout.width * 0.5f;
        bodyFont.draw(batch, bannerMessage, textX, 630f);
        bodyFont.setColor(Color.WHITE);
    }

    public void renderStageClearBanner(
        ShapeRenderer shapeRenderer,
        SpriteBatch batch,
        AssetManager assetManager,
        int stageNumber,
        float remainingBannerTimer
    ) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        float progress = 1.0f - (remainingBannerTimer / 1.85f);
        float scale = MathUtils.clamp(0.75f + progress * 3.0f, 0.75f, 1f);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.05f, 0.10f, 0.18f, 0.68f);
        shapeRenderer.rect(0f, 0f, 1280f, 720f);

        for (int i = 0; i < 28; i++) {
            float cx = (i * 97f + progress * 240f * ((i % 2 == 0) ? 1f : -1f)) % 1280f;
            if (cx < 0f) {
                cx += 1280f;
            }
            float cy = 720f - ((i * 63f + progress * 520f) % 720f);
            shapeRenderer.setColor(
                0.3f + (i % 3) * 0.3f,
                0.4f + ((i + 1) % 3) * 0.25f,
                0.35f + ((i + 2) % 3) * 0.3f,
                0.9f
            );
            shapeRenderer.circle(cx, cy, 7f);
        }

        float boxW = 620f * scale;
        float boxH = 180f * scale;
        float boxX = 640f - boxW * 0.5f;
        float boxY = 360f - boxH * 0.5f;

        shapeRenderer.setColor(0.16f, 0.58f, 0.34f, 0.98f);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, boxX, boxY, boxW, boxH, 24f);
        shapeRenderer.setColor(0.98f, 0.84f, 0.18f, 1f);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, boxX + 8f, boxY + 8f, boxW - 16f, boxH - 16f, 18f);
        shapeRenderer.setColor(0.12f, 0.24f, 0.42f, 1f);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, boxX + 14f, boxY + 14f, boxW - 28f, boxH - 28f, 14f);
        shapeRenderer.end();

        if (scale > 0.7f) {
            batch.begin();
            BitmapFont titleFont = assetManager.getTitleFont();
            BitmapFont subtitleFont = assetManager.getSubtitleFont();

            String headline = "STAGE " + stageNumber + " CLEARED!";
            GlyphLayout hlLayout = new GlyphLayout(titleFont, headline);
            titleFont.setColor(0.98f, 0.90f, 0.25f, 1f);
            titleFont.draw(batch, headline, 640f - hlLayout.width * 0.5f, 395f);

            String sub = "Every fridge tile packed to perfection!";
            GlyphLayout subLayout = new GlyphLayout(subtitleFont, sub);
            subtitleFont.setColor(Color.WHITE);
            subtitleFont.draw(batch, sub, 640f - subLayout.width * 0.5f, 338f);
            batch.end();
        }
    }

    public List<Rectangle> getTraySlotBounds() {
        return traySlotBounds;
    }

    public Rectangle getConfirmButtonBounds() {
        return confirmButtonBounds;
    }

    public Rectangle getUndoButtonBounds() {
        return undoButtonBounds;
    }

    public Rectangle getFreezeButtonBounds() {
        return freezeButtonBounds;
    }

    public Rectangle getMenuButtonBounds() {
        return menuButtonBounds;
    }
}
