package com.beachsurvivors.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Timer;
import com.beachsurvivors.utilities.MusicHandler;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class Main extends Game {

    private MainMenuScreen menuScreen;
    private GameScreen gameScreen;
    private PauseOverlay pauseOverlay;
    private LoadingScreen loadingScreen;
    private Screen previousScreen;
    private boolean isSoundOn = true;

    @Override
    public void create() {
        gameScreen = new GameScreen(this);
        menuScreen = new MainMenuScreen(this);

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
        if (gameScreen != null) gameScreen.dispose();
        gameScreen = null;
        setScreen(menuScreen);
    }

    public void restart() {
        MusicHandler.stop();    //Vi kanske skulle flytta playSound till gamescreen?
        Timer.instance().clear();

        if (gameScreen != null) gameScreen.dispose();

        gameScreen = new GameScreen(this);
        setScreen(gameScreen);

        Timer.instance().start();
        menuScreen.startGameMusic();
        previousScreen = gameScreen;
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

        if (gameScreen != null) {
            setScreen(new HelpScreen(gameScreen.getScreenWidth(), gameScreen.getScreenHeight(), this));
        } else {
            setScreen(new HelpScreen(1920, 1080, this));
        }
    }

    public void showPreviousScreen() {
        setScreen(previousScreen);
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
