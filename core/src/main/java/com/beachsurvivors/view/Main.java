package com.beachsurvivors.view;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private MainMenuScreen menuScreen;
    private GameScreen gameScreen;

    @Override
    public void create() {

        menuScreen = new MainMenuScreen(this);
        setScreen(menuScreen);

    }

    public void switchScreen() {
        if(this.getScreen() == menuScreen) {
            if (gameScreen == null) gameScreen = new GameScreen(this);
            setScreen(gameScreen);
        } else {
            setScreen(menuScreen);
        }

    }

    public void levelUp() {
        setScreen(new LevelUpScreen(gameScreen, gameScreen.getPlayer()));
    }

    public void pause() {
        setScreen(new PauseScreen(gameScreen));
    }
}
