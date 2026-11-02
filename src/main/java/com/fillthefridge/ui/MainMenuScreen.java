package com.fillthefridge.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.fillthefridge.FillTheFridgeGame;
import com.fillthefridge.assets.AssetManager;

public class MainMenuScreen extends BaseScreen {
    private final Rectangle playButton;
    private final Rectangle creditsButton;
    private final Rectangle exitButton;
    private float menuTimer;

    public MainMenuScreen(FillTheFridgeGame game) {
        super(game);
        this.playButton = new Rectangle(490f, 290f, 300f, 62f);
        this.creditsButton = new Rectangle(490f, 210f, 300f, 56f);
        this.exitButton = new Rectangle(490f, 136f, 300f, 52f);
    }

    @Override
    public void render(float delta) {
        menuTimer += delta;
        updateTransition(delta);

        Vector2 mouse = getMouseWorldCoords();
        if (Gdx.input.justTouched()) {
            if (playButton.contains(mouse.x, mouse.y)) {
                game.getGameController().startNewRun();
                transitionTo(new GameplayScreen(game));
            } else if (creditsButton.contains(mouse.x, mouse.y)) {
                transitionTo(new CreditsScreen(game));
            } else if (exitButton.contains(mouse.x, mouse.y)) {
                Gdx.app.exit();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.getGameController().startNewRun();
            transitionTo(new GameplayScreen(game));
        }

        Gdx.gl.glClearColor(0.10f, 0.18f, 0.30f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ShapeRenderer shapeRenderer = game.getShapeRenderer();
        SpriteBatch batch = game.getSpriteBatch();
        AssetManager assets = game.getAssetManager();

        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0f, 0f, VIRTUAL_WIDTH, VIRTUAL_HEIGHT,
            new Color(0.12f, 0.24f, 0.42f, 1f),
            new Color(0.12f, 0.24f, 0.42f, 1f),
            new Color(0.28f, 0.56f, 0.84f, 1f),
            new Color(0.28f, 0.56f, 0.84f, 1f)
        );

        shapeRenderer.setColor(0.10f, 0.18f, 0.30f, 0.85f);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, 310f, 80f, 660f, 560f, 28f);

        shapeRenderer.setColor(0.18f, 0.42f, 0.78f, 1f);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, 105f, 180f, 160f, 320f, 20f);
        shapeRenderer.setColor(0.92f, 0.96f, 1.0f, 1f);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, 120f, 198f, 130f, 284f, 12f);
        shapeRenderer.setColor(0.46f, 0.64f, 0.84f, 1f);
        shapeRenderer.rect(120f, 292f, 130f, 5f);
        shapeRenderer.rect(120f, 386f, 130f, 5f);

        drawStyledButtonShape(
            shapeRenderer,
            playButton,
            new Color(0.18f, 0.72f, 0.38f, 1f),
            new Color(0.24f, 0.84f, 0.46f, 1f),
            playButton.contains(mouse.x, mouse.y)
        );
        drawStyledButtonShape(
            shapeRenderer,
            creditsButton,
            new Color(0.20f, 0.50f, 0.86f, 1f),
            new Color(0.28f, 0.60f, 0.96f, 1f),
            creditsButton.contains(mouse.x, mouse.y)
        );
        drawStyledButtonShape(
            shapeRenderer,
            exitButton,
            new Color(0.75f, 0.25f, 0.30f, 1f),
            new Color(0.88f, 0.32f, 0.38f, 1f),
            exitButton.contains(mouse.x, mouse.y)
        );
        shapeRenderer.end();

        batch.begin();
        BitmapFont titleFont = assets.getTitleFont();
        BitmapFont subtitleFont = assets.getSubtitleFont();
        BitmapFont bodyFont = assets.getBodyFont();
        BitmapFont smallFont = assets.getSmallFont();

        String title = "FILL THE FRIDGE";
        GlyphLayout titleLayout = new GlyphLayout(titleFont, title);
        titleFont.setColor(0.98f, 0.90f, 0.24f, 1f);
        titleFont.draw(batch, title, (VIRTUAL_WIDTH - titleLayout.width) * 0.5f, 585f);

        String subtitle = "Roguelike Grocery Sorting & Shelf-Stacking Puzzle";
        GlyphLayout subLayout = new GlyphLayout(subtitleFont, subtitle);
        subtitleFont.setColor(0.86f, 0.94f, 1.0f, 1f);
        subtitleFont.draw(batch, subtitle, (VIRTUAL_WIDTH - subLayout.width) * 0.5f, 528f);

        String[] showcaseItems = new String[]{
            AssetManager.MILK,
            AssetManager.WHOLE_CHICKEN,
            AssetManager.PIZZA_BOX,
            AssetManager.CHEESE,
            AssetManager.BROCCOLI,
            AssetManager.JUICE_BOTTLE
        };
        for (int i = 0; i < showcaseItems.length; i++) {
            Texture tex = assets.getTexture(showcaseItems[i]);
            float bob = MathUtils.sin(menuTimer * 3.0f + i * 0.9f) * 8f;
            float iconX = 375f + i * 92f;
            float iconY = 405f + bob;
            batch.draw(tex, iconX, iconY, 64f, 64f);
        }

        drawCenteredText(batch, subtitleFont, "PLAY GAME", playButton, Color.WHITE);
        drawCenteredText(batch, bodyFont, "CREDITS", creditsButton, Color.WHITE);
        drawCenteredText(batch, bodyFont, "EXIT DESKTOP", exitButton, Color.WHITE);

        smallFont.setColor(0.76f, 0.88f, 0.98f, 1f);
        String hint = "Pack every fridge tile before groceries spoil outside! Trash traps & avoid spills.";
        GlyphLayout hintLayout = new GlyphLayout(smallFont, hint);
        smallFont.draw(batch, hint, (VIRTUAL_WIDTH - hintLayout.width) * 0.5f, 106f);
        smallFont.setColor(Color.WHITE);
        batch.end();

        renderTransitionOverlay(shapeRenderer);
    }
}
