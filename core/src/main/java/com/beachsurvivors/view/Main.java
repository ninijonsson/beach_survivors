package com.beachsurvivors.view;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private MainMenuScreen menuScreen;
    private GameScreen gameScreen;
    private LoadingScreen loadingScreen;
    private boolean isSoundOn = true;

    @Override
    public void create() {
        gameScreen = new GameScreen(this);
        menuScreen = new MainMenuScreen(this);
        setScreen(loadingScreen);
        setScreen(menuScreen);
    }

    public void startGame() {
        setScreen(gameScreen);
    }

    public void switchScreen() {
        if (this.getScreen() == menuScreen) {
            if (gameScreen == null) gameScreen = new GameScreen(this);
            setScreen(gameScreen);
        } else {
            setScreen(menuScreen);
        }
    }

    public void goToMainMenu() {
        menuScreen.mainTheme.play();
        menuScreen.playSound.stop();
        if (gameScreen != null) gameScreen.dispose();
        gameScreen = null;
        setScreen(menuScreen);
    }

    public void restart() {
        menuScreen.playSound.stop();    //Vi kanske skulle flytta playSound till gamescreen?
        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
        menuScreen.startGameMusic();
    }

    public void gameOver(int enemiesKilled, double damageDone, float gameTime,
                         double healingReceived, double damageTaken, double damagePrevented) {
        setScreen(new DeathScreen(gameScreen, enemiesKilled, damageDone, gameTime, healingReceived, damageTaken, damagePrevented));
    }

    public void levelUp() {
        setScreen(new LevelUpScreen(gameScreen, gameScreen.getPlayer()));
    }

    public void goToHelpScreen() {
        setScreen(new HelpScreen(gameScreen,this));
    }

    public void pause() {
        setScreen(new PauseScreen(gameScreen, this));
    }

    public void turnOffInGameMusic() {
        // TODO: Stäng av ljudeffekterna också
        menuScreen.playSound.stop();
    }

    public void turnOnInGameMusic() {
        menuScreen.playSound.play();
    }

    public boolean isSoundOn() {
        return isSoundOn;
    }

    public void setSoundOn(boolean isSoundOn) {
        this.isSoundOn = isSoundOn;
    }
    public GameScreen getGameScreen() {
        return gameScreen;
    }
}
