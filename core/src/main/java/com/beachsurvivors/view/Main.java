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

    public void goToMainMenu() {
        menuScreen.mainTheme.play();
        menuScreen.playSound.stop();
        gameScreen.dispose();
        gameScreen = null;
        setScreen(menuScreen);
    }

    public void restart() {
        menuScreen.playSound.stop();    //Vi kanske skulle flytta playSound till gamescreen?
        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
        menuScreen.startGameMusic();
    }

    public void gameOver(int enemiesKilled) {
        setScreen(new DeathScreen(gameScreen, enemiesKilled));
    }

    public void levelUp() {
        setScreen(new LevelUpScreen(gameScreen, gameScreen.getPlayer()));
    }
}
