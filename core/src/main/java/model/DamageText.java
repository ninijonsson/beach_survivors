package model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DamageText {
    private String text;
    private float x, y;
    private float duration;
    private float timer;
    private BitmapFont font;
    private Color color;
    private float moveSpeed = 50f;

    public DamageText(String text, float x, float y, float duration, boolean isCritical) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.duration = duration;
        this.timer = duration;
        this.font = new BitmapFont();
        this.color = new Color(Color.YELLOW);


        if (isCritical) {
            font.getData().setScale(3.0f);
        } else {
            font.getData().setScale(2.0f);
        }
    }

    public void update(float delta) {
        timer -= delta;
        y += moveSpeed * delta;
    }

    public boolean isActive() {
        return timer > 0;
    }

    public void draw(SpriteBatch spriteBatch) {
        font.setColor(color);
        font.draw(spriteBatch, text, x, y);
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
