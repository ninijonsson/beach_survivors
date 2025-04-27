package com.beachsurvivors.view;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class PauseMenu extends Group {

    public static class PauseMenuStyle {
        public Drawable background;
        public ImageButton.ImageButtonStyle resume;
        public ImageButton.ImageButtonStyle help;
        public ImageButton.ImageButtonStyle exit;
    }

    private final Image backgroundImage;
    private final ImageButton resumeButton;
    private final ImageButton helpButton;
    private final ImageButton exitButton;
    private PauseMenuStyle style;

    public PauseMenu(Skin skin) {
        this(skin.get(PauseMenuStyle.class));
    }

    public PauseMenu(PauseMenuStyle style) {
        if (style == null) throw new NullPointerException("PauseMenuStyle cannot be null");

        backgroundImage = new Image(style.background);
        addActor(backgroundImage);

        resumeButton = new ImageButton(style.resume);
        addActor(resumeButton);

        helpButton = new ImageButton(style.help);
        addActor(helpButton);

        exitButton = new ImageButton(style.exit);
        addActor(exitButton);

        layout();
    }

    public PauseMenu() {
        backgroundImage = new Image();
        resumeButton = new ImageButton(new ImageButton.ImageButtonStyle());
        helpButton = new ImageButton(new ImageButton.ImageButtonStyle());
        exitButton = new ImageButton(new ImageButton.ImageButtonStyle());

        addActor(backgroundImage);
        addActor(resumeButton);
        addActor(helpButton);
        addActor(exitButton);
    }


    public void layout() {
        float width = backgroundImage.getWidth();
        float height = backgroundImage.getHeight();

        backgroundImage.setPosition(0, 0);

        // Placera knapparna
        resumeButton.setPosition(width / 2f - resumeButton.getWidth() / 2f, height * 0.6f);
        helpButton.setPosition(width / 2f - helpButton.getWidth() / 2f, height * 0.4f);
        exitButton.setPosition(width / 2f - exitButton.getWidth() / 2f, height * 0.2f);

        setSize(width, height);
    }

    public void setStyle(PauseMenuStyle style) {
        this.style = style;

        backgroundImage.setDrawable(style.background);
        resumeButton.setStyle(style.resume);
        helpButton.setStyle(style.help);
        exitButton.setStyle(style.exit);

        //invalidateHierarchy();
    }
}
