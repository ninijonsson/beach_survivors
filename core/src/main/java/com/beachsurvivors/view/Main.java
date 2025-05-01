package com.beachsurvivors.view;

import com.badlogic.gdx.Game;

public class Main extends Game {

    private MainMenuScreen menuScreen;
    private GameScreen gameScreen;
    private LoadingScreen loadingScreen;

    @Override
    public void create() {
        menuScreen = new MainMenuScreen(this);
        setScreen(loadingScreen);
        setScreen(menuScreen);
    }

    public void startGame() {
        gameScreen = new GameScreen(this);
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
        menuScreen.playSound.stop();
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
