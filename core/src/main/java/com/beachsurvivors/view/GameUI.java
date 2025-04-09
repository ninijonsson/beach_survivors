package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameUI {
    private Stage stage;
    private ProgressBar progressBar;
    private ProgressBar healthBar;
    private Label percentageLabel;

    private Label timerLabel;
    private float gameTime = 0f;

    public GameUI(FitViewport viewport) {
        stage = new Stage(viewport);
        createProgressBar(viewport);
        createPlayerHealthBar(viewport);
        stage.addActor(progressBar);
        stage.addActor(healthBar);
        stage.addActor(percentageLabel);
    }

    private void createPlayerHealthBar(FitViewport viewport) {
        Skin healthSkin = new Skin(Gdx.files.internal("SkinComposer/healthbutton.json"));
        healthBar = new ProgressBar(0, 100, 0.5f, false, healthSkin);
        healthBar.setValue(86);
        healthBar.setPosition(910, 620);
        healthBar.setSize(100, 50);
        healthBar.setScale(0.5f);

        percentageLabel = new Label(getHealthPercentage() + "%", healthSkin);
        percentageLabel.setColor(Color.BLACK);
        percentageLabel.setPosition(healthBar.getX()+37, healthBar.getY()+37); // Placera ovanför healthBar

        BitmapFont font = new BitmapFont();
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        timerLabel = new Label("00:00", style);
        timerLabel.setFontScale(5);
        timerLabel.setColor(Color.WHITE);
        timerLabel.setPosition(50, 980);
        stage.addActor(timerLabel);
    }

    public Stage getStage() {
        return stage;
    }

    public void setProgressBarValue(float value) {
        progressBar.setValue(value);
    }

    public void setHealthBarValue(float value) {
        healthBar.setValue(value);
        percentageLabel.setText(getHealthPercentage() + "%"); // Uppdatera labeln när healthBar ändras
    }

    private String getHealthPercentage() {
        double percentage = (healthBar.getValue() / healthBar.getMaxValue()) * 100;
        return String.format("%.0f", percentage);
    }

    public void createProgressBar(FitViewport viewport) {
        Skin skin = new Skin(Gdx.files.internal("SkinComposer/testbuttons.json"));
        progressBar = new ProgressBar(0, 100, 0.5f, false, skin);
        progressBar.setValue(0);
        progressBar.setPosition(viewport.getScreenWidth() * 0.5f + progressBar.getWidth() * 0.5f, viewport.getScreenHeight() + 300); // Sätt position längst upp på skärmen
        progressBar.setSize(500, 50);
        progressBar.setScale(2f);
    }

    public void update(float deltaTime) {
        gameTime += deltaTime;

        int minutes = (int)(gameTime / 60f);
        int seconds = (int)(gameTime % 60f);
        String timeText = String.format("%02d:%02d", minutes, seconds);

        timerLabel.setText(timeText);

        stage.act(deltaTime);
    }

    public void draw() {
        stage.draw();
    }

    public float getGameTimeSeconds() {
        return gameTime;
    }
}
