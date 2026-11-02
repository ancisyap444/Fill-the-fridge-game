package com.fillthefridge;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fillthefridge.assets.AssetManager;
import com.fillthefridge.logic.DifficultyScalingStrategy;
import com.fillthefridge.logic.GameController;
import com.fillthefridge.logic.ProgressiveDifficultyStrategy;
import com.fillthefridge.logic.SolvableStageGenerator;
import com.fillthefridge.model.GameState;
import com.fillthefridge.ui.GameplayScreen;
import com.fillthefridge.ui.MainMenuScreen;
import com.fillthefridge.util.StageGenerator;

public class FillTheFridgeGame extends Game {
    private static final long DEFAULT_SEED = 20261102L;

    private final boolean smokeTestMode;
    private int smokeTestFrames;

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private AssetManager assetManager;
    private GameState gameState;
    private GameController gameController;

    public FillTheFridgeGame() {
        this(false);
    }

    public FillTheFridgeGame(boolean smokeTestMode) {
        this.smokeTestMode = smokeTestMode;
    }

    @Override
    public void create() {
        this.spriteBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.assetManager = new AssetManager();
        this.assetManager.loadAll();

        this.gameState = new GameState();
        DifficultyScalingStrategy strategy = new ProgressiveDifficultyStrategy();
        StageGenerator generator = new SolvableStageGenerator();
        this.gameController = new GameController(gameState, strategy, generator, DEFAULT_SEED);

        if (smokeTestMode) {
            this.gameController.startNewRun();
            setScreen(new GameplayScreen(this));
        } else {
            setScreen(new MainMenuScreen(this));
        }
    }

    @Override
    public void render() {
        super.render();
        if (smokeTestMode) {
            smokeTestFrames++;
            if (smokeTestFrames == 5) {
                for (int stage = 1; stage <= 8; stage++) {
                    gameController.startStage(stage);
                }
            }
            if (smokeTestFrames >= 12) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void dispose() {
        if (screen != null) {
            screen.dispose();
        }
        if (assetManager != null) {
            assetManager.dispose();
        }
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameController getGameController() {
        return gameController;
    }
}
