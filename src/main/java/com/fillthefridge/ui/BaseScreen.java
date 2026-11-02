package com.fillthefridge.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fillthefridge.FillTheFridgeGame;

public abstract class BaseScreen implements Screen {
    public static final float VIRTUAL_WIDTH = 1280f;
    public static final float VIRTUAL_HEIGHT = 720f;

    protected final FillTheFridgeGame game;
    protected final OrthographicCamera camera;
    protected final Viewport viewport;
    protected float transitionAlpha;
    private Screen pendingNextScreen;

    protected BaseScreen(FillTheFridgeGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        this.camera.position.set(VIRTUAL_WIDTH * 0.5f, VIRTUAL_HEIGHT * 0.5f, 0f);
        this.camera.update();
        this.transitionAlpha = 1.0f;
    }

    protected void transitionTo(Screen nextScreen) {
        if (pendingNextScreen == null) {
            this.pendingNextScreen = nextScreen;
        }
    }

    protected void updateTransition(float deltaTime) {
        if (pendingNextScreen != null) {
            transitionAlpha = Math.min(1.0f, transitionAlpha + deltaTime * 7.5f);
            if (transitionAlpha >= 1.0f) {
                Screen target = pendingNextScreen;
                pendingNextScreen = null;
                game.setScreen(target);
            }
        } else if (transitionAlpha > 0f) {
            transitionAlpha = Math.max(0f, transitionAlpha - deltaTime * 7.5f);
        }
    }

    protected void renderTransitionOverlay(ShapeRenderer shapeRenderer) {
        if (transitionAlpha <= 0.01f) {
            return;
        }
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.05f, 0.08f, 0.14f, transitionAlpha);
        shapeRenderer.rect(0f, 0f, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        shapeRenderer.end();
    }

    protected Vector2 getMouseWorldCoords() {
        Vector2 screenCoords = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        return viewport.unproject(screenCoords);
    }

    protected void drawStyledButtonShape(
        ShapeRenderer shapeRenderer,
        Rectangle bounds,
        Color baseColor,
        Color hoverColor,
        boolean hovered
    ) {
        shapeRenderer.setColor(0.05f, 0.08f, 0.14f, 0.35f);
        ProceduralFridgeRenderer.drawRoundedRect(
            shapeRenderer,
            bounds.x + 4f,
            bounds.y - 4f,
            bounds.width,
            bounds.height,
            14f
        );
        shapeRenderer.setColor(hovered ? hoverColor : baseColor);
        ProceduralFridgeRenderer.drawRoundedRect(
            shapeRenderer,
            bounds.x,
            bounds.y,
            bounds.width,
            bounds.height,
            14f
        );
    }

    protected void drawCenteredText(
        SpriteBatch batch,
        BitmapFont font,
        String text,
        Rectangle bounds,
        Color color
    ) {
        font.setColor(color);
        GlyphLayout layout = new GlyphLayout(font, text);
        float tx = bounds.x + (bounds.width - layout.width) * 0.5f;
        float ty = bounds.y + (bounds.height + layout.height) * 0.5f;
        font.draw(batch, text, tx, ty);
        font.setColor(Color.WHITE);
    }

    @Override
    public void show() {
        this.transitionAlpha = 1.0f;
        this.pendingNextScreen = null;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
