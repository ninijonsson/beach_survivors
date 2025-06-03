package com.beachsurvivors.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PuzzleOrb {
    private float width;
    private float height;
    private Sprite sprite;
    private Vector2 position;
    private Circle hitbox;
    private boolean active = true;

    public PuzzleOrb(Vector2 position) {
        this.position = position;
        width = 128;
        height = 128;
        sprite = new Sprite(new Texture("entities/waypoint.png"));
        this.hitbox = new Circle(position.x, position.y, 30);
    }

    public void update(float delta) {

    }

    public void draw(SpriteBatch batch) {
        batch.draw(sprite, position.x - width/2, position.y - height/2, width, height);
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public boolean overlaps(Rectangle rectangle) {
        float closestX = Math.max(rectangle.getX(), Math.min(hitbox.x, rectangle.getX() + rectangle.getWidth()));
        float closestY = Math.max(rectangle.getY(), Math.min(hitbox.y, rectangle.getY() + rectangle.getHeight()));

        float dx = hitbox.x - closestX;
        float dy = hitbox.y - closestY;

        return (dx * dx + dy * dy) < (hitbox.radius * hitbox.radius);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Circle getHitbox() {
        return hitbox;
    }
}

