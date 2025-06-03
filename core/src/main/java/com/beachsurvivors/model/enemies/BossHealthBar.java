package com.beachsurvivors.model.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class BossHealthBar extends Group {
    private final Image background;
    private final Image fill;
    private final float barWidth;
    private final float barHeight;

    public BossHealthBar(float width, float height, Skin skin) {
        this.barWidth = width;
        this.barHeight = height;

        // Background
        background = new Image(new TextureRegionDrawable(new TextureRegion(new Texture("ui/bar_bg.png"))));
        background.setSize(barWidth, barHeight);
        addActor(background);

        // Fill (red)
        fill = new Image(new TextureRegionDrawable(new TextureRegion(new Texture("ui/bar_fill.png"))));
        fill.setSize(barWidth, barHeight);
        addActor(fill);
    }

    public void update(float currentHP, float maxHP) {
        float percent = Math.max(0, currentHP / maxHP);
        fill.setWidth(barWidth * percent);
    }
}

