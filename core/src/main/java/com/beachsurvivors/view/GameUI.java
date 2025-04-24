package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.model.abilities.Ability;

public class GameUI {
    private final FitViewport viewport;
    private final GameScreen game;
    private final Stage stage;
    private ProgressBar progressBar;
    private ProgressBar healthBar;
    private Label percentageLabel;
    private Label nextLevel;
    private Label currentLevel;
    private Table abilityTable;
    private BitmapFont abilityFont;
    private Label.LabelStyle abilityLabelStyle;

    private Table progressbarTable;
    private Table healthTable;


    private Label timerLabel;
    private float gameTime = 0f;

    public GameUI(FitViewport viewport, GameScreen game) {
        this.viewport = viewport;
        this.game = game;
        stage = new Stage(viewport);

        createTables();
    }

    /**
     * Creates all UI-components and places them on the stage (screen).
     */
    private void createTables() {
        createProgressBar();
        createPlayerHealthBar();
        createTimerLabel();
        createAbilityTable();

        stage.addActor(healthTable);
        stage.addActor(progressbarTable);
        stage.addActor(timerLabel);
        stage.addActor(abilityTable);

    }

    private void createAbilityTable() {
        abilityFont = new BitmapFont(); // Skapas en gång
        abilityFont.getData().setScale(2);
        abilityLabelStyle = new Label.LabelStyle(abilityFont, Color.WHITE);

        abilityTable = new Table();
        abilityTable.setPosition(50, 50); // Justera position efter behov

        updateAbilityBar();
    }

    private void updateAbilityBar() {
        abilityTable.clearChildren();
        if(game.getAbilities()!=null){
            Array<Ability> abilities = game.getAbilities();
            Ability firstAbility = abilities.first();
            Sprite icon = firstAbility.getSprite();

            Image abilityIcon = new Image(icon);
            abilityIcon.setScaling(Scaling.fit);

            Container<Image> iconContainer = new Container<>(abilityIcon);
            iconContainer.setSize(50, 50);
            iconContainer.center();
            abilityTable.add(iconContainer).pad(5);
            //abilityTable.add(abilities.first().getSprite());
        }

    }




    private void createTimerLabel() {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2);

        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        Label timeLabelTop = new Label("Elapsed time:", style);

        timeLabelTop.setColor(Color.WHITE);
        timeLabelTop.setPosition(30, viewport.getWorldHeight() - 50);

        stage.addActor(timeLabelTop);

        timerLabel = new Label("00:00", style);
        timerLabel.setColor(Color.WHITE);
        timerLabel.setPosition(30, viewport.getWorldHeight() - timerLabel.getHeight() - 50);
    }

    private void createPlayerHealthBar() {
        Skin healthSkin = new Skin(Gdx.files.internal("SkinComposer/healthbutton.json"));
        healthBar = new ProgressBar(0, 100, 0.5f, false, healthSkin);
        healthBar.setValue(86);
        healthBar.setSize(100, 50);
        percentageLabel = new Label(getHealthPercentage() + "%", healthSkin);
        percentageLabel.setColor(Color.BLACK);

        healthTable = new Table();
        healthTable.add(healthBar);
        healthTable.add(percentageLabel).padLeft(10);
        healthTable.setPosition(viewport.getWorldWidth() / 2 - healthTable.getWidth() / 2, viewport.getWorldHeight() / 2 - healthTable.getHeight() / 2 + 100); // Placera healthBar i mitten längst ner
    }

    public Stage getStage() {
        return stage;
    }

    public void setProgressBarValue(float value) {
        progressBar.setValue(value);
    }

    public void setHealthBarValue(float value) {
        healthBar.setValue(value);
        percentageLabel.setText(getHealthPercentage() + "%");
    }

    private String getHealthPercentage() {
        double percentage = (healthBar.getValue() / healthBar.getMaxValue()) * 100;
        return String.format("%.0f", percentage);
    }

    public void createProgressBar() {
        progressbarTable = new Table();
        Skin skin = new Skin(Gdx.files.internal("SkinComposer/testbuttons.json"));

        progressBar = new ProgressBar(0, 100, 0.5f, false, skin);
        progressBar.setValue(0);
        progressBar.setSize(500, 50);


        BitmapFont font = new BitmapFont();
        font.getData().setScale(2f);
        font.setColor(Color.WHITE);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);


        currentLevel = new Label("Level: " +getPlayerLevel(), labelStyle);
        nextLevel = new Label(getPlayerLevel(), labelStyle);

        progressbarTable.add(currentLevel).padRight(20);
        progressbarTable.add(progressBar).expandX().fillX();
        progressbarTable.add(nextLevel).padLeft(20);

        progressbarTable.setSize(700, 100);
        progressbarTable.setPosition(viewport.getWorldWidth() / 2 - progressbarTable.getWidth()  / 2, viewport.getWorldHeight() - progressbarTable.getHeight() - 10);
    }

    private String getPlayerLevel() {
        if (game.getPlayer() == null) {
            return "0";
        } else return String.valueOf(game.getPlayer().getLevel());

    }

    private void updateLevelLabels() {
        currentLevel.setText("Level: " +getPlayerLevel());
        nextLevel.setText(String.valueOf(game.getPlayer().getLevel() + 1));
    }

    public void update(float deltaTime) {
        gameTime += deltaTime;

        int minutes = (int) (gameTime / 60f);
        int seconds = (int) (gameTime % 60f);

        String timeText = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText(timeText);

        updateLevelLabels();
        updateAbilityBar();

        stage.act(deltaTime);
    }

    public void draw() {
        stage.draw();
    }

    public float getGameTimeSeconds() {
        return gameTime;
    }
}
