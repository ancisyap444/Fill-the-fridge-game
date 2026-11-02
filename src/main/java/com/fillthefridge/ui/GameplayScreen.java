package com.fillthefridge.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.fillthefridge.FillTheFridgeGame;
import com.fillthefridge.items.Item;
import com.fillthefridge.logic.GameController;
import com.fillthefridge.model.GamePhase;
import com.fillthefridge.model.GameState;
import com.fillthefridge.util.GameEventListener;
import com.fillthefridge.util.GridPosition;
import java.util.List;

public class GameplayScreen extends BaseScreen implements GameEventListener {
    private final ProceduralFridgeRenderer fridgeRenderer;
    private final HudRenderer hudRenderer;
    private GridPosition currentHoverGridPosition;

    public GameplayScreen(FillTheFridgeGame game) {
        super(game);
        this.fridgeRenderer = new ProceduralFridgeRenderer();
        this.hudRenderer = new HudRenderer();
    }

    @Override
    public void show() {
        super.show();
        game.getGameState().addEventListener(this);
        fridgeRenderer.updateLayout(game.getGameState().getFridge(), VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        hudRenderer.updateTrayAndTrashBounds(game.getGameState());
    }

    @Override
    public void hide() {
        super.hide();
        game.getGameState().removeEventListener(this);
    }

    @Override
    public void render(float delta) {
        updateTransition(delta);

        GameController controller = game.getGameController();
        GameState state = game.getGameState();

        fridgeRenderer.updateLayout(state.getFridge(), VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        fridgeRenderer.update(delta);
        hudRenderer.update(delta, state);

        Vector2 mouse = getMouseWorldCoords();
        updateHoverStates(state, mouse.x, mouse.y);
        handleUserInput(controller, state, mouse.x, mouse.y);

        controller.update(delta);

        if (state.getCurrentPhase() == GamePhase.POWERUP_SELECTION) {
            transitionTo(new PowerUpSelectionScreen(game));
        } else if (state.getCurrentPhase() == GamePhase.GAME_OVER) {
            transitionTo(new GameOverScreen(game));
        }

        Gdx.gl.glClearColor(0.45f, 0.78f, 0.96f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ShapeRenderer shapeRenderer = game.getShapeRenderer();
        SpriteBatch batch = game.getSpriteBatch();

        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        fridgeRenderer.renderKitchenBackground(shapeRenderer, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        fridgeRenderer.renderFridgeStructure(
            shapeRenderer,
            batch,
            game.getAssetManager().getSmallFont(),
            game.getAssetManager(),
            state.getFridge(),
            state.getTray().getSelectedItem(),
            currentHoverGridPosition,
            state.getActivePowerUps()
        );

        hudRenderer.renderHudAndTray(
            shapeRenderer,
            batch,
            game.getAssetManager(),
            state,
            currentHoverGridPosition,
            mouse.x,
            mouse.y
        );

        if (state.getCurrentPhase() == GamePhase.STAGE_CLEAR) {
            hudRenderer.renderStageClearBanner(
                shapeRenderer,
                batch,
                game.getAssetManager(),
                state.getCurrentStageNumber(),
                controller.getStageClearBannerTimer()
            );
        }

        renderTransitionOverlay(shapeRenderer);
    }

    private void updateHoverStates(GameState state, float mouseX, float mouseY) {
        currentHoverGridPosition = fridgeRenderer.screenToGridPosition(state.getFridge(), mouseX, mouseY);
        state.getTrashBin().setHovered(state.getTrashBin().containsPoint(mouseX, mouseY));

        List<Item> activeItems = state.getTray().getActiveItems();
        List<Rectangle> slotRects = hudRenderer.getTraySlotBounds();
        for (int i = 0; i < activeItems.size(); i++) {
            Item item = activeItems.get(i);
            boolean hovered = i < slotRects.size() && slotRects.get(i).contains(mouseX, mouseY);
            item.setHovered(hovered);
        }
    }

    private void handleUserInput(GameController controller, GameState state, float mouseX, float mouseY) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            transitionTo(new MainMenuScreen(game));
            return;
        }
        if (state.getCurrentPhase() != GamePhase.PLAYING) {
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            controller.confirmPlacements();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            controller.activateManualDeepFreeze();
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            controller.undoLastPlacement();
            return;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (hudRenderer.getMenuButtonBounds().contains(mouseX, mouseY)) {
                transitionTo(new MainMenuScreen(game));
                return;
            }
            if (hudRenderer.getConfirmButtonBounds().contains(mouseX, mouseY)) {
                controller.confirmPlacements();
                return;
            }
            if (hudRenderer.getUndoButtonBounds().contains(mouseX, mouseY)) {
                controller.undoLastPlacement();
                return;
            }
            if (hudRenderer.getFreezeButtonBounds().contains(mouseX, mouseY)) {
                controller.activateManualDeepFreeze();
                return;
            }
            if (state.getTrashBin().containsPoint(mouseX, mouseY)) {
                controller.trashSelectedItem();
                return;
            }

            List<Item> activeItems = state.getTray().getActiveItems();
            List<Rectangle> slotRects = hudRenderer.getTraySlotBounds();
            for (int i = 0; i < activeItems.size() && i < slotRects.size(); i++) {
                if (slotRects.get(i).contains(mouseX, mouseY)) {
                    controller.selectTrayItem(activeItems.get(i));
                    return;
                }
            }

            if (currentHoverGridPosition != null) {
                controller.handleFridgeLeftClick(currentHoverGridPosition);
            }
        }
    }

    @Override
    public void onLivesChanged(int currentLives, int changeAmount, String reason) {
        if (changeAmount < 0) {
            hudRenderer.triggerLifeShake();
            hudRenderer.showNotification(reason + " (" + currentLives + " lives left)", new Color(1.0f, 0.35f, 0.35f, 1f), 2.8f);
        } else {
            hudRenderer.showNotification(reason, new Color(0.35f, 0.95f, 0.55f, 1f), 2.5f);
        }
    }

    @Override
    public void onStageCleared(int stageNumber) {
        hudRenderer.showNotification("Stage " + stageNumber + " Complete!", new Color(0.35f, 0.98f, 0.55f, 1f), 2.0f);
    }

    @Override
    public void onItemSpoiled(Item item) {
        hudRenderer.triggerLifeShake();
    }

    @Override
    public void onSpillOccurred(Item sourceItem, List<Item> contaminatedItems) {
        hudRenderer.triggerLifeShake();
        hudRenderer.showNotification(
            "SPILL! " + sourceItem.getDisplayName() + " contaminated " + contaminatedItems.size() + " item(s)!",
            new Color(0.98f, 0.45f, 0.18f, 1f),
            3.2f
        );
    }

    @Override
    public void onWarningMessage(String message) {
        hudRenderer.showNotification(message, new Color(0.98f, 0.84f, 0.24f, 1f), 2.4f);
    }
}
