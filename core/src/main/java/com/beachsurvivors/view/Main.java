package com.beachsurvivors.view;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private MainMenuScreen menuScreen;
    private GameScreen gameScreen;

    @Override
    public void create() {
        System.out.println("test");
        menuScreen = new MainMenuScreen(this);

        setScreen(menuScreen);
        System.out.println("test2");

    }

    public void switchScreen() {
        if(this.getScreen() == menuScreen) {
            if (gameScreen == null) gameScreen = new GameScreen(this);
            setScreen(gameScreen);
        } else {
            setScreen(menuScreen);
        }

    }
}
