package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Boomerang {

    private Sprite sprite;
    private Rectangle hitBox;
    private double damage;

    public Boomerang() {
        sprite = new Sprite(new Texture(Gdx.files.internal("assets/entities/Boomerangmc.png")));
        sprite.setSize(32, 32);
        hitBox = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        this.damage=10;
    }

    public void updatePosition(float x, float y) {
        sprite.setPosition(x, y);
        hitBox.setPosition(x, y);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public void dispose() {
        sprite.getTexture().dispose();
    }

    public double getDamage() {
        return damage;
    }
}
