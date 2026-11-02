package com.fillthefridge.model;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fillthefridge.util.Renderable;
import com.fillthefridge.util.Updatable;

public abstract class GameObject implements Updatable, Renderable {
    private float x;
    private float y;
    private float width;
    private float height;
    private boolean visible;

    protected GameObject(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visible = true;
    }

    public boolean containsPoint(float pointX, float pointY) {
        return visible
            && pointX >= x
            && pointX <= x + width
            && pointY >= y
            && pointY <= y + height;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setBounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public abstract void update(float deltaTime);

    @Override
    public abstract void render(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font);
}
