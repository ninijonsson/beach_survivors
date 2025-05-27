package com.beachsurvivors.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class Main extends Game {

    private MainMenuScreen menuScreen;
    private GameScreen gameScreen;
    private PauseScreen pauseScreen;
    private LoadingScreen loadingScreen;
    private Screen previousScreen;
    private boolean isSoundOn = true;

    @Override
    public void create() {
        gameScreen = new GameScreen(this);
        menuScreen = new MainMenuScreen(this);
        pauseScreen = new PauseScreen(gameScreen, this);
        setScreen(loadingScreen);
        setScreen(menuScreen);
        previousScreen = menuScreen;
    }

    public void startGame() {
        previousScreen = getScreen();
        setScreen(gameScreen);
    }

    public void playGame() {
        if (gameScreen != null) {
            setScreen(gameScreen);
        } else {
            gameScreen = new GameScreen(this);
            setScreen(gameScreen);
        }
        previousScreen = gameScreen;
    }

    public void goToMainMenu() {
        menuScreen.mainTheme.play();
        menuScreen.playSound.stop();
//        if (gameScreen != null) gameScreen.dispose();
//        gameScreen = null;
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
        previousScreen = getScreen();
        setScreen(new HelpScreen(gameScreen,this));
    }

    public void showPreviousScreen() {
        setScreen(previousScreen);
    }


    public void pause() {
        previousScreen = getScreen();
        setScreen(pauseScreen);
    }

    public void turnOffInGameMusic() {
        // TODO: Stäng av ljudeffekterna också
        menuScreen.playSound.stop();
    }

    public void turnOnInGameMusic() {
        menuScreen.playSound.play();
    }

    public void setPreviousScreen(Screen screen) {
        previousScreen = screen;
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
