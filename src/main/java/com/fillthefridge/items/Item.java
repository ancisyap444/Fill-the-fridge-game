package com.fillthefridge.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.fillthefridge.assets.AssetManager;
import com.fillthefridge.model.Fridge;
import com.fillthefridge.model.GameObject;
import com.fillthefridge.model.ItemCategory;
import com.fillthefridge.model.PlacementResult;
import com.fillthefridge.util.GridPosition;
import com.fillthefridge.util.GridSize;
import com.fillthefridge.util.Placeable;
import com.fillthefridge.util.Spoilable;

public abstract class Item extends GameObject implements Placeable, Spoilable {
    public static final float GRACE_PERIOD_SECONDS = 0.5f;
    public static final float WARNING_THRESHOLD_RATIO = 0.40f;
    public static final float CRITICAL_THRESHOLD_RATIO = 0.20f;

    private final String id;
    private final String displayName;
    private final String textureName;
    private final ItemCategory category;
    private final GridSize size;
    private final int weight;
    private final int maxSupportWeight;
    private final boolean absorbent;
    private final String warningPhrase;

    private GridPosition gridPosition;
    private float maxShelfLife;
    private float remainingTime;
    private float graceTimeRemaining;
    private float freezeTimer;

    private boolean inFridge;
    private boolean confirmed;
    private boolean contaminated;
    private boolean revealedTrap;
    private boolean warningAnnounced;
    private boolean hovered;
    private boolean selected;

    private float hoverScale;
    private float snapAnimationProgress;
    private float shakePhase;
    private float splashAnimationTimer;

    protected Item(
        String id,
        String displayName,
        String textureName,
        ItemCategory category,
        GridSize size,
        float maxShelfLife,
        int weight,
        int maxSupportWeight,
        boolean absorbent,
        String warningPhrase
    ) {
        super(0f, 0f, 64f, 64f);
        this.id = id;
        this.displayName = displayName;
        this.textureName = textureName;
        this.category = category;
        this.size = size;
        this.maxShelfLife = maxShelfLife;
        this.remainingTime = maxShelfLife;
        this.graceTimeRemaining = GRACE_PERIOD_SECONDS;
        this.weight = weight;
        this.maxSupportWeight = maxSupportWeight;
        this.absorbent = absorbent;
        this.warningPhrase = warningPhrase;
        this.hoverScale = 1.0f;
        this.snapAnimationProgress = 1.0f;
    }

    @Override
    public void update(float deltaTime) {
        float targetScale = (hovered || selected) ? 1.12f : 1.0f;
        hoverScale = MathUtils.lerp(hoverScale, targetScale, Math.min(1f, deltaTime * 14f));

        if (snapAnimationProgress < 1.0f) {
            snapAnimationProgress = Math.min(1.0f, snapAnimationProgress + deltaTime * 8.0f);
        }

        if (splashAnimationTimer > 0f) {
            splashAnimationTimer = Math.max(0f, splashAnimationTimer - deltaTime);
        }

        if (!inFridge) {
            updateTimer(deltaTime);
            if (isCriticalThresholdReached()) {
                shakePhase += deltaTime * 36f;
            } else if (isWarningThresholdReached()) {
                shakePhase += deltaTime * 14f;
            }
        }
    }

    @Override
    public void updateTimer(float deltaTime) {
        if (inFridge) {
            return;
        }
        if (freezeTimer > 0f) {
            freezeTimer = Math.max(0f, freezeTimer - deltaTime);
            return;
        }
        if (remainingTime > 0f) {
            remainingTime = Math.max(0f, remainingTime - deltaTime);
        } else {
            graceTimeRemaining = Math.max(0f, graceTimeRemaining - deltaTime);
        }
    }

    public void renderItemSprite(
        SpriteBatch batch,
        AssetManager assetManager,
        BitmapFont font,
        float drawX,
        float drawY,
        float drawWidth,
        float drawHeight,
        float alpha
    ) {
        Texture texture = assetManager.getTexture(textureName);
        float fittedWidth;
        float fittedHeight;

        if (inFridge || alpha < 0.99f) {
            float padX = 8f;
            float padY = 8f;
            fittedWidth = Math.max(16f, drawWidth - padX * 2f);
            fittedHeight = Math.max(16f, drawHeight - padY * 2f);
        } else {
            float texW = texture.getWidth();
            float texH = texture.getHeight();
            float aspect = texW / Math.max(1f, texH);
            float boxAspect = drawWidth / Math.max(1f, drawHeight);
            if (aspect > boxAspect) {
                fittedWidth = drawWidth * 0.88f;
                fittedHeight = fittedWidth / aspect;
            } else {
                fittedHeight = drawHeight * 0.88f;
                fittedWidth = fittedHeight * aspect;
            }
        }

        float snapScale = 1.0f;
        if (snapAnimationProgress < 1.0f) {
            float t = snapAnimationProgress;
            snapScale = 0.88f + 0.12f * t + 0.08f * MathUtils.sin(t * MathUtils.PI);
        }

        float activeScale = inFridge ? snapScale : hoverScale;
        float finalW = fittedWidth * activeScale;
        float finalH = fittedHeight * activeScale;

        float shakeOffsetX = 0f;
        if (!inFridge && isCriticalThresholdReached()) {
            float amplitude = remainingTime <= 0f ? 5.5f : 3.0f;
            shakeOffsetX = MathUtils.sin(shakePhase) * amplitude;
        }

        float finalX = drawX + (drawWidth - finalW) * 0.5f + shakeOffsetX;
        float finalY = drawY + (drawHeight - finalH) * 0.5f;

        Color tint = new Color(1f, 1f, 1f, alpha);
        if (contaminated) {
            tint.set(0.55f, 0.78f, 0.32f, alpha);
        } else if (freezeTimer > 0f) {
            tint.set(0.68f, 0.90f, 1.0f, alpha);
        } else if (!inFridge && remainingTime <= 0f) {
            tint.set(1.0f, 0.45f, 0.45f, alpha);
        }

        batch.setColor(tint);
        batch.draw(texture, finalX, finalY, finalW, finalH);
        batch.setColor(Color.WHITE);

        if (assetManager.isPlaceholder(textureName) && font != null) {
            GlyphLayout layout = new GlyphLayout(font, displayName);
            font.setColor(Color.WHITE);
            font.draw(
                batch,
                displayName,
                drawX + (drawWidth - layout.width) * 0.5f,
                drawY + (drawHeight + layout.height) * 0.5f
            );
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font) {
    }

    public abstract String onTimerWarning();

    public abstract boolean canSupport(Item aboveItem);

    public abstract PlacementResult onStackedUpon(Item aboveItem, Fridge fridge);

    public abstract Item createFreshCopy();

    public boolean isTrap() {
        return false;
    }

    public boolean isSpillableItem() {
        return false;
    }

    public void triggerSnapAnimation() {
        this.snapAnimationProgress = 0.0f;
    }

    public void triggerSpillSplash() {
        this.splashAnimationTimer = 0.95f;
    }

    public float getSplashAnimationTimer() {
        return splashAnimationTimer;
    }

    public void applyDeepFreeze(float durationSeconds) {
        this.freezeTimer = Math.max(this.freezeTimer, durationSeconds);
    }

    public float getFreezeTimer() {
        return freezeTimer;
    }

    public void addBonusTime(float extraSeconds) {
        this.maxShelfLife += extraSeconds;
        this.remainingTime += extraSeconds;
    }

    public void applyTimerMultiplier(float multiplier, boolean isShortLifeItem) {
        float floor = isShortLifeItem ? 4.0f : 8.0f;
        float scaled = Math.max(floor, this.maxShelfLife * multiplier);
        this.maxShelfLife = scaled;
        this.remainingTime = scaled;
    }

    public void resetTimerToFull() {
        this.remainingTime = this.maxShelfLife;
        this.graceTimeRemaining = GRACE_PERIOD_SECONDS;
        this.warningAnnounced = false;
    }

    @Override
    public float getRemainingTime() {
        return remainingTime;
    }

    @Override
    public float getMaxShelfLife() {
        return maxShelfLife;
    }

    @Override
    public float getShelfLifeRatio() {
        if (maxShelfLife <= 0f) {
            return 0f;
        }
        return MathUtils.clamp(remainingTime / maxShelfLife, 0f, 1f);
    }

    @Override
    public boolean isWarningThresholdReached() {
        return getShelfLifeRatio() <= WARNING_THRESHOLD_RATIO;
    }

    @Override
    public boolean isCriticalThresholdReached() {
        return getShelfLifeRatio() <= CRITICAL_THRESHOLD_RATIO;
    }

    @Override
    public boolean isSpoiledAfterGrace() {
        return !inFridge && remainingTime <= 0f && graceTimeRemaining <= 0f;
    }

    public boolean isInGracePeriod() {
        return !inFridge && remainingTime <= 0f && graceTimeRemaining > 0f;
    }

    public float getGraceTimeRemaining() {
        return graceTimeRemaining;
    }

    public boolean isWarningAnnounced() {
        return warningAnnounced;
    }

    public void setWarningAnnounced(boolean warningAnnounced) {
        this.warningAnnounced = warningAnnounced;
    }

    @Override
    public GridPosition getGridPosition() {
        return gridPosition;
    }

    @Override
    public void setGridPosition(GridPosition position) {
        this.gridPosition = position;
    }

    @Override
    public GridSize getSize() {
        return size;
    }

    @Override
    public boolean isConfirmed() {
        return confirmed;
    }

    @Override
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTextureName() {
        return textureName;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public int getWeight() {
        return weight;
    }

    public int getMaxSupportWeight() {
        return maxSupportWeight;
    }

    public boolean isAbsorbent() {
        return absorbent;
    }

    public String getWarningPhrase() {
        return warningPhrase;
    }

    public boolean isInFridge() {
        return inFridge;
    }

    public void setInFridge(boolean inFridge) {
        this.inFridge = inFridge;
    }

    public boolean isContaminated() {
        return contaminated;
    }

    public void setContaminated(boolean contaminated) {
        this.contaminated = contaminated;
    }

    public boolean isRevealedTrap() {
        return revealedTrap;
    }

    public void setRevealedTrap(boolean revealedTrap) {
        this.revealedTrap = revealedTrap;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
