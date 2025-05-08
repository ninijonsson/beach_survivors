package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.Player;
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
    private Array<String> infoLog;
    private Array<Label> infoLabels;
    private BitmapFont levelFont;
    private Label.LabelStyle abilityLabelStyle;

    private Table progressBarTable;
    private Table healthTable;
    private Table xpTable;

    private Table statsTable;
    private Label healthPoints;
    private Label damage;
    private Label critChance;
    private Label critDamage;
    private Label cooldownReduction;
    private Label movementSpeed;

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
        createAbilityTable();
        createProgressBar();
        createPlayerHealthBar();
        createTimerLabel();
        createInfoTable();
        creatPlayerStats();

        addActors();
    }

    private void addActors(){
        stage.addActor(healthTable);

        stage.addActor(timerLabel);
        stage.addActor(abilityTable);
        stage.addActor(xpTable);
        stage.addActor(progressBarTable);
    }

    private void createAbilityTable() {
        abilityFont = new BitmapFont(Gdx.files.internal("fonts/timer.fnt"));
        abilityFont.getData().setScale(2);
        abilityLabelStyle = new Label.LabelStyle(abilityFont, Color.WHITE);

        this.abilityTable = new Table();
        Texture imageTexture = new Texture(Gdx.files.internal("entities/abilities/test_ability_bar.png"));

        Image abilityBackground = new Image(imageTexture);
        abilityBackground.setSize(400, 70);
        abilityBackground.setScale(1.5f);
        abilityTable.add(abilityBackground);
        abilityTable.bottom();
        abilityTable.center();
        abilityTable.pack();

        abilityTable.setPosition(
            ((viewport.getWorldWidth() - abilityTable.getWidth()*1.5f) / 2), 0
        );


        this.xpTable = new Table();
        Texture xpTexture = new Texture(Gdx.files.internal("entities/abilities/exp_bar.png"));
        Image xpBar = new Image(xpTexture);
        xpBar.setScale(1.5f);
        xpBar.setSize(400,70);
        xpTable.add(xpBar);
        xpTable.top();
        xpTable.center();
        xpTable.pack();
        xpTable.setPosition(
            ((viewport.getWorldWidth() - xpTable.getWidth()*1.5f) / 2), viewport.getWorldHeight()-xpTable.getHeight()*1.5f
        );
    }

    private void updateAbilityBar() {
            Array<Ability> abilities = game.getAbilities();
            for(Ability a : abilities){
                //a.getName()
            }
    }

    private void createInfoTable() {
        infoLog = new Array<>();
        infoLabels = new Array<>();

        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.5f);
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        float startY = 150;
        float spacing = 25;

        for (int i = 0; i < 5; i++) {
            Label label = new Label("", style);
            label.setPosition(40, startY - i * spacing);
            infoLabels.add(label);
            stage.addActor(label);
        }
        updateInfoTable("Welcome to Beach Survivors, try not to die");
    }

    public void updateInfoTable(String logMessage){
        if(infoLog.size == 5){
            infoLog.removeIndex(0);
        }

        int minutes = (int)(gameTime / 60f);
        int seconds = (int)(gameTime % 60f);
        String timestamp = String.format("[%02d:%02d] ", minutes, seconds);

        infoLog.add(timestamp + logMessage);

        for (int i = 0; i < infoLabels.size; i++) {
            if (i < infoLog.size) {
                infoLabels.get(i).setText(infoLog.get(i));
            } else {
                infoLabels.get(i).setText("");
            }
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
        Skin healthSkin = new Skin(Gdx.files.internal("skin_composer/healthbutton.json"));
        healthBar = new ProgressBar(0, 100, 0.5f, false, healthSkin);
        healthBar.setValue(100);
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
        progressBarTable = new Table();
        Skin skin = new Skin(Gdx.files.internal("skin_composer/testbuttons.json"));


        progressBar = new ProgressBar(0, 100, 0.5f, false, skin);
        progressBar.setValue(0);
        progressBar.setSize(700, 70);


        levelFont = new BitmapFont(Gdx.files.internal("fonts/level.fnt"));
        levelFont.setColor(Color.WHITE);
        Label.LabelStyle labelStyle = new Label.LabelStyle(levelFont, Color.WHITE);

        currentLevel = new Label("Level: " + getPlayerLevel(), labelStyle);
        nextLevel = new Label(getPlayerLevel(), labelStyle);

        progressBarTable.add(currentLevel).padRight(50);
        progressBarTable.add(progressBar).expandX().fillX();
        progressBarTable.add(nextLevel).padLeft(40);

        progressBarTable.setSize(720, 70);
        progressBarTable.setPosition(xpTable.getX()-currentLevel.getWidth(), xpTable.getY()+progressBar.getHeight()*0.55f);
    }

    private String getPlayerLevel() {
        if (game.getPlayer() == null) {
            return "1";
        } else {
            return String.valueOf(game.getPlayer().getLevelSystem().getCurrentLevel());
        }
    }



    private void creatPlayerStats() {
        float fontScale = 1.2f;
        Skin skin = AssetLoader.get().manager.get("game_over_screen/deathscreen_skin.json");
        statsTable = new Table();
        statsTable.setSize(600,400);

        healthPoints = new Label("HealthPoints" , skin, "stats");
        statsTable.add(healthPoints).left().row();
        healthPoints.setFontScale(fontScale);

        damage = new Label("Base Damage" , skin, "stats");
        statsTable.add(damage).left().row();
        damage.setFontScale(fontScale);

        critChance = new Label("Critical Chance" ,skin, "stats");
        statsTable.add(critChance).left().row();
        critChance.setFontScale(fontScale);

        critDamage = new Label("Critical Damage" , skin, "stats");
        statsTable.add(critDamage).left().row();
        critDamage.setFontScale(fontScale);

        cooldownReduction = new Label("Cooldown Reduction" , skin, "stats");
        statsTable.add(cooldownReduction).left().row();
        cooldownReduction.setFontScale(fontScale);

        movementSpeed = new Label("Movement Speed" , skin, "stats");
        statsTable.add(movementSpeed).left().row();
        movementSpeed.setFontScale(fontScale);

        statsTable.setPosition(-80, game.getScreenHeight()/2f);
        stage.addActor(statsTable);
        statsTable.setVisible(false);

    }

    public void updateStats(Player player) {
        healthPoints.setText("Health Points " +player.getCurrentHealthPoints() + "/"+player.getMaxHealthPoints());
        damage.setText("Base Damage: " + player.getBaseDamage());
        critChance.setText("CritHit Chance: " + String.format("%.0f", player.getCriticalHitChance()*100) + "%");
        critDamage.setText("CritHit Damage: " + String.format("%.0f", player.getCriticalHitDamage()*100) + "%");
        cooldownReduction.setText("Cooldown Reduction: " + player.getCooldown() + "%");
        movementSpeed.setText("Movement Speed: " + player.getSpeed());
    }

    public void showStatsTable() {
        statsTable.setVisible(!statsTable.isVisible());
    }


    private void updateLevelLabels() {
        currentLevel.setText("Level: " + String.valueOf(
            game.getPlayer().getLevelSystem().getCurrentLevel()));

        nextLevel.setText(String.valueOf(
            game.getPlayer().getLevelSystem().getCurrentLevel() + 1));
    }

    public void update(float deltaTime) {
        gameTime += deltaTime;

        int minutes = (int) (gameTime / 60f);
        int seconds = (int) (gameTime % 60f);

        String timeText = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText(timeText);

        updateLevelLabels();
        //updateAbilityBar();

        stage.act(deltaTime);
    }

    public void draw() {
        stage.draw();
    }

    public float getGameTimeSeconds() {
        return gameTime;
    }
}
