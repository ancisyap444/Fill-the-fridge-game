package com.fillthefridge.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.fillthefridge.FillTheFridgeGame;
import com.fillthefridge.assets.AssetManager;
import com.fillthefridge.util.AssetLoadException;
import java.util.Collections;
import java.util.List;

public class CreditsScreen extends BaseScreen {
    private final Rectangle backButton;
    private List<String> creditsLines;
    private float scrollOffset;

    public CreditsScreen(FillTheFridgeGame game) {
        super(game);
        this.backButton = new Rectangle(500f, 32f, 280f, 52f);
        this.creditsLines = Collections.emptyList();
    }

    @Override
    public void show() {
        super.show();
        try {
            this.creditsLines = game.getAssetManager().loadCreditsLines();
        } catch (AssetLoadException e) {
            this.creditsLines = List.of(
                "Error loading credits file: " + e.getAssetPath(),
                e.getMessage()
            );
        }
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                scrollOffset = MathUtils.clamp(scrollOffset + amountY * 28f, 0f, Math.max(0f, creditsLines.size() * 24f - 420f));
                return true;
            }
        });
    }

    @Override
    public void hide() {
        super.hide();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        updateTransition(delta);
        Vector2 mouse = getMouseWorldCoords();

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            scrollOffset = MathUtils.clamp(scrollOffset + delta * 180f, 0f, Math.max(0f, creditsLines.size() * 24f - 420f));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            scrollOffset = MathUtils.clamp(scrollOffset - delta * 180f, 0f, Math.max(0f, creditsLines.size() * 24f - 420f));
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && backButton.contains(mouse.x, mouse.y)) {
            transitionTo(new MainMenuScreen(game));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            transitionTo(new MainMenuScreen(game));
        }

        Gdx.gl.glClearColor(0.08f, 0.14f, 0.24f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ShapeRenderer shapeRenderer = game.getShapeRenderer();
        SpriteBatch batch = game.getSpriteBatch();
        AssetManager assets = game.getAssetManager();

        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0f, 0f, VIRTUAL_WIDTH, VIRTUAL_HEIGHT,
            new Color(0.08f, 0.15f, 0.26f, 1f),
            new Color(0.08f, 0.15f, 0.26f, 1f),
            new Color(0.16f, 0.32f, 0.54f, 1f),
            new Color(0.16f, 0.32f, 0.54f, 1f)
        );

        shapeRenderer.setColor(0.09f, 0.16f, 0.28f, 0.94f);
        ProceduralFridgeRenderer.drawRoundedRect(shapeRenderer, 160f, 105f, 960f, 515f, 20f);

        drawStyledButtonShape(
            shapeRenderer,
            backButton,
            new Color(0.22f, 0.52f, 0.86f, 1f),
            new Color(0.30f, 0.62f, 0.96f, 1f),
            backButton.contains(mouse.x, mouse.y)
        );
        shapeRenderer.end();

        batch.begin();
        BitmapFont titleFont = assets.getTitleFont();
        BitmapFont bodyFont = assets.getBodyFont();
        BitmapFont smallFont = assets.getSmallFont();

        String header = "CREDITS & ATTRIBUTIONS";
        GlyphLayout headerLayout = new GlyphLayout(titleFont, header);
        titleFont.setColor(0.98f, 0.90f, 0.25f, 1f);
        titleFont.draw(batch, header, (VIRTUAL_WIDTH - headerLayout.width) * 0.5f, 678f);

        float startY = 590f + scrollOffset;
        for (int i = 0; i < creditsLines.size(); i++) {
            float lineY = startY - i * 24f;
            if (lineY < 125f || lineY > 602f) {
                continue;
            }
            String line = creditsLines.get(i);
            if (line.startsWith("=") || line.contains("CREDITS") || line.endsWith(":")) {
                smallFont.setColor(0.98f, 0.86f, 0.32f, 1f);
            } else {
                smallFont.setColor(0.88f, 0.94f, 1.0f, 1f);
            }
            smallFont.draw(batch, line, 195f, lineY);
        }

        smallFont.setColor(0.68f, 0.80f, 0.92f, 1f);
        smallFont.draw(batch, "Use Mouse Wheel or UP/DOWN Arrows to scroll (assets/credits.txt)", 440f, 98f);

        drawCenteredText(batch, bodyFont, "BACK TO MENU [ESC]", backButton, Color.WHITE);
        batch.end();

        renderTransitionOverlay(shapeRenderer);
    }
}
