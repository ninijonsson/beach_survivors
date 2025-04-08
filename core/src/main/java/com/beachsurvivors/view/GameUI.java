package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameUI {
    private Stage stage;
    private ProgressBar progressBar;
    private ProgressBar healthBar;

    public GameUI(FitViewport viewport) {
        stage = new Stage(viewport);
        createProgressBar(viewport);
        createPlayerHealthBar(viewport);
        stage.addActor(progressBar);
        stage.addActor(healthBar);
    }

    private void createPlayerHealthBar(FitViewport viewport) {
        Skin health = new Skin(Gdx.files.internal("assets/SkinComposer/healthbutton.json"));
        healthBar = new ProgressBar(0, 100, 0.5f, false, health);
        healthBar.setValue(86);
        healthBar.setPosition(910, 620);
        healthBar.setSize(100, 50);
        healthBar.setScale(0.5f);
    }

    public Stage getStage() {
        return stage;
    }

    public void setProgressBarValue(float value){
        progressBar.setValue(value);
    }

    public void setHealthBarValue(float value){
        healthBar.setValue(value);
    }

    public void createProgressBar(FitViewport viewport){
        Skin skin = new Skin(Gdx.files.internal("assets/SkinComposer/testbuttons.json"));
        progressBar = new ProgressBar(0, 100, 0.5f, false, skin);
        progressBar.setValue(0);
        progressBar.setPosition(viewport.getScreenWidth()*0.5f+ progressBar.getWidth()*0.5f, viewport.getScreenHeight()+300); // Sätt position längst upp på skärmen
        progressBar.setSize(500, 50);
        progressBar.setScale(2f);
    }
}
