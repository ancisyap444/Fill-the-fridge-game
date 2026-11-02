package com.fillthefridge.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.fillthefridge.FillTheFridgeGame;
import com.fillthefridge.assets.AssetManager;
import com.fillthefridge.powerups.PowerUp;
import java.util.List;

public class GameOverScreen extends BaseScreen {
    private final Rectangle retryButton;
    private final Rectangle mainMenuButton;

    public GameOverScreen(FillTheFridgeGame game) {
        super(game);
        this.retryButton = new Rectangle(480f, 200f, 320f, 60f);
        this.mainMenuButton = new Rectangle(480f, 120f, 320f, 54f);
    }

    @Override
    public void render(float delta) {
        updateTransition(delta);
        Vector2 mouse = getMouseWorldCoords();

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (retryButton.contains(mouse.x, mouse.y)) {
                game.getGameController().startNewRun();
                transitionTo(new GameplayScreen(game));
            } else if (mainMenuButton.contains(mouse.x, mouse.y)) {
                transitionTo(new MainMenuScreen(game));
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.getGameController().startNewRun();
            transitionTo(new GameplayScreen(game));
        }

        Gdx.gl.glClearColor(0.14f, 0.08f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ShapeRenderer shapeRenderer = game.getShapeRenderer();
        SpriteBatch batch = game.getSpriteBatch();
        AssetManager assets = game.getAssetManager();

        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0f, 0f, VIRTUAL_WIDTH, VIRTUAL_HEIGHT,
            new Color(0.16f, 0.08f, 0.12f, 1f),
            new Color(0.16f, 0.08f, 0.12f, 1f),
            new Color(0.28f, 0.12f, 0.18f, 1f),
            new Color(0.28f, 0.12f, 0.18f, 1f)
        );

        shapeRenderer.setColor(0.10f, 0.12f, 0.18f, 0.90f);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, 320f, 85f, 640f, 550f, 26f);

        drawStyledButtonShape(
            shapeRenderer,
            retryButton,
            new Color(0.20f, 0.72f, 0.38f, 1f),
            new Color(0.26f, 0.84f, 0.46f, 1f),
            retryButton.contains(mouse.x, mouse.y)
        );
        drawStyledButtonShape(
            shapeRenderer,
            mainMenuButton,
            new Color(0.24f, 0.48f, 0.78f, 1f),
            new Color(0.32f, 0.58f, 0.90f, 1f),
            mainMenuButton.contains(mouse.x, mouse.y)
        );
        shapeRenderer.end();

        batch.begin();
        BitmapFont titleFont = assets.getTitleFont();
        BitmapFont subtitleFont = assets.getSubtitleFont();
        BitmapFont bodyFont = assets.getBodyFont();
        BitmapFont smallFont = assets.getSmallFont();

        String title = "GAME OVER";
        GlyphLayout titleLayout = new GlyphLayout(titleFont, title);
        titleFont.setColor(1.0f, 0.36f, 0.36f, 1f);
        titleFont.draw(batch, title, (VIRTUAL_WIDTH - titleLayout.width) * 0.5f, 575f);

        int stageReached = game.getGameState().getCurrentStageNumber();
        int stagesCleared = Math.max(0, stageReached - 1);
        String stats = "Reached Stage " + stageReached + "  |  Stages Cleared: " + stagesCleared;
        GlyphLayout statsLayout = new GlyphLayout(subtitleFont, stats);
        subtitleFont.setColor(0.98f, 0.90f, 0.35f, 1f);
        subtitleFont.draw(batch, stats, (VIRTUAL_WIDTH - statsLayout.width) * 0.5f, 495f);

        List<PowerUp> powerUps = game.getGameState().getActivePowerUps().getAcquiredPowerUps();
        bodyFont.setColor(0.84f, 0.92f, 1.0f, 1f);
        bodyFont.draw(batch, "Powerups Collected (" + powerUps.size() + "):", 410f, 425f);

        if (powerUps.isEmpty()) {
            smallFont.setColor(0.68f, 0.74f, 0.82f, 1f);
            smallFont.draw(batch, "None collected on this run.", 410f, 392f);
        } else {
            float py = 392f;
            for (int i = 0; i < Math.min(5, powerUps.size()); i++) {
                PowerUp p = powerUps.get(i);
                smallFont.setColor(0.55f, 0.92f, 0.68f, 1f);
                smallFont.draw(batch, "- " + p.getName() + ": " + p.getDescription(), 410f, py);
                py -= 24f;
            }
        }

        drawCenteredText(batch, subtitleFont, "PLAY AGAIN", retryButton, Color.WHITE);
        drawCenteredText(batch, bodyFont, "MAIN MENU", mainMenuButton, Color.WHITE);
        batch.end();

        renderTransitionOverlay(shapeRenderer);
    }
}
