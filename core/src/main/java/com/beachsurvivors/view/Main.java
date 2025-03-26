package com.beachsurvivors.view;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game implements ApplicationListener {

    private MainMenuScreen menuScreen;
    private GameScreen gameScreen;

    @Override
    public void create() {
        menuScreen = new MainMenuScreen(this);
        gameScreen = new GameScreen(this);

        setScreen(gameScreen);

    }

    public void switchScreen() {
        if(this.getScreen() == menuScreen) {
            setScreen(gameScreen);
        }
    }
}
