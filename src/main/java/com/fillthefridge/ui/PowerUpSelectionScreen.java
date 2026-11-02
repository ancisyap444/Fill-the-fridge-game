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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.fillthefridge.FillTheFridgeGame;
import com.fillthefridge.assets.AssetManager;
import com.fillthefridge.powerups.PowerUp;
import java.util.ArrayList;
import java.util.List;

public class PowerUpSelectionScreen extends BaseScreen {
    private final List<Rectangle> cardBounds;

    public PowerUpSelectionScreen(FillTheFridgeGame game) {
        super(game);
        this.cardBounds = new ArrayList<>();
        float cardW = 310f;
        float cardH = 360f;
        float gap = 42f;
        float totalW = 3 * cardW + 2 * gap;
        float startX = (VIRTUAL_WIDTH - totalW) * 0.5f;
        for (int i = 0; i < 3; i++) {
            cardBounds.add(new Rectangle(startX + i * (cardW + gap), 165f, cardW, cardH));
        }
    }

    @Override
    public void render(float delta) {
        updateTransition(delta);
        List<PowerUp> choices = game.getGameController().getCurrentPowerUpChoices();
        Vector2 mouse = getMouseWorldCoords();

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            for (int i = 0; i < choices.size() && i < cardBounds.size(); i++) {
                if (cardBounds.get(i).contains(mouse.x, mouse.y)) {
                    game.getGameController().choosePowerUp(choices.get(i));
                    transitionTo(new GameplayScreen(game));
                    break;
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) && choices.size() >= 1) {
            game.getGameController().choosePowerUp(choices.get(0));
            transitionTo(new GameplayScreen(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) && choices.size() >= 2) {
            game.getGameController().choosePowerUp(choices.get(1));
            transitionTo(new GameplayScreen(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) && choices.size() >= 3) {
            game.getGameController().choosePowerUp(choices.get(2));
            transitionTo(new GameplayScreen(game));
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

        for (int i = 0; i < choices.size() && i < cardBounds.size(); i++) {
            Rectangle card = cardBounds.get(i);
            PowerUp powerUp = choices.get(i);
            boolean hovered = card.contains(mouse.x, mouse.y);
            float lift = hovered ? 10f : 0f;

            shapeRenderer.setColor(0.04f, 0.07f, 0.12f, 0.45f);
            ProceduralFridgeRenderer.drawRoundedRect(
                shapeRenderer,
                card.x + 6f,
                card.y - 6f + lift,
                card.width,
                card.height,
                22f
            );

            if (powerUp.isRare()) {
                shapeRenderer.setColor(hovered ? new Color(0.98f, 0.80f, 0.20f, 1f) : new Color(0.86f, 0.66f, 0.14f, 1f));
            } else {
                shapeRenderer.setColor(hovered ? new Color(0.28f, 0.68f, 0.98f, 1f) : new Color(0.18f, 0.46f, 0.78f, 1f));
            }
            ProceduralFridgeRenderer.drawRoundedRect(
                shapeRenderer,
                card.x,
                card.y + lift,
                card.width,
                card.height,
                22f
            );

            shapeRenderer.setColor(0.12f, 0.20f, 0.34f, 1f);
            ProceduralFridgeRenderer.drawRoundedRect(
                shapeRenderer,
                card.x + 6f,
                card.y + 6f + lift,
                card.width - 12f,
                card.height - 12f,
                18f
            );
        }
        shapeRenderer.end();

        batch.begin();
        BitmapFont titleFont = assets.getTitleFont();
        BitmapFont subtitleFont = assets.getSubtitleFont();
        BitmapFont bodyFont = assets.getBodyFont();
        BitmapFont smallFont = assets.getSmallFont();

        String header = "CHOOSE 1 POWERUP FOR STAGE " + (game.getGameState().getCurrentStageNumber() + 1);
        GlyphLayout headerLayout = new GlyphLayout(titleFont, header);
        titleFont.setColor(0.98f, 0.90f, 0.26f, 1f);
        titleFont.draw(batch, header, (VIRTUAL_WIDTH - headerLayout.width) * 0.5f, 645f);

        String sub = "Click a card (or press 1, 2, 3) to upgrade your fridge run!";
        GlyphLayout subLayout = new GlyphLayout(subtitleFont, sub);
        subtitleFont.setColor(0.82f, 0.92f, 1.0f, 1f);
        subtitleFont.draw(batch, sub, (VIRTUAL_WIDTH - subLayout.width) * 0.5f, 588f);

        for (int i = 0; i < choices.size() && i < cardBounds.size(); i++) {
            Rectangle card = cardBounds.get(i);
            PowerUp powerUp = choices.get(i);
            boolean hovered = card.contains(mouse.x, mouse.y);
            float lift = hovered ? 10f : 0f;

            Texture icon = assets.getTexture(powerUp.getIconTextureName());
            float iconSize = 92f;
            batch.draw(
                icon,
                card.x + (card.width - iconSize) * 0.5f,
                card.y + 215f + lift,
                iconSize,
                iconSize
            );

            if (powerUp.isRare()) {
                smallFont.setColor(1.0f, 0.85f, 0.25f, 1f);
                smallFont.draw(batch, "RARE POWERUP", card.x + 98f, card.y + 336f + lift);
            } else {
                smallFont.setColor(0.58f, 0.86f, 1.0f, 1f);
                smallFont.draw(batch, "OPTION [" + (i + 1) + "]", card.x + 114f, card.y + 336f + lift);
            }

            GlyphLayout nameLayout = new GlyphLayout(subtitleFont, powerUp.getName());
            subtitleFont.setColor(Color.WHITE);
            subtitleFont.draw(
                batch,
                powerUp.getName(),
                card.x + (card.width - nameLayout.width) * 0.5f,
                card.y + 185f + lift
            );

            bodyFont.setColor(0.84f, 0.92f, 0.98f, 1f);
            bodyFont.draw(
                batch,
                powerUp.getDescription(),
                card.x + 24f,
                card.y + 125f + lift,
                card.width - 48f,
                1,
                true
            );

            smallFont.setColor(0.45f, 0.96f, 0.58f, 1f);
            smallFont.draw(batch, "Click to Select", card.x + 104f, card.y + 38f + lift);
        }

        smallFont.setColor(Color.WHITE);
        batch.end();

        renderTransitionOverlay(shapeRenderer);
    }
}
