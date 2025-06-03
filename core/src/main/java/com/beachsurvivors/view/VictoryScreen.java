package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.utilities.AssetLoader;

public class VictoryScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private GameScreen gameScreen;
    private Main main;
    private Table table;

    private ImageButton restartButton;
    private ImageButton mainMenuButton;

    private int enemiesKilled;
    private double damageDone;
    private double damageTaken;
    private double healingReceived;
    private double damagePrevented;
    private String timeStamp;

    private Image selectorArrow;
    private int selectedIndex = 0;
    private ImageButton[] buttons;
    private boolean arrowInitialized = false;
    private Sound menuSwitch;
    private Sound menuChoice;

    public VictoryScreen(GameScreen gameScreen, int enemiesKilled, double damageDone,
                         float timeSurvived, double damageTaken, double healingReceived, double damagePrevented) {
        this.gameScreen = gameScreen;
        this.enemiesKilled = enemiesKilled;
        this.damageDone = damageDone;
        this.damageTaken = damageTaken;
        this.healingReceived = healingReceived;
        this.damagePrevented = damagePrevented;

        stage = new Stage(new FitViewport(gameScreen.getScreenWidth(), gameScreen.getScreenHeight()));
        Gdx.input.setInputProcessor(stage);

        skin = AssetLoader.get().manager.get("skin_composer/victory_screen/victory_screen.json");
        table = buildUI();

        // createText();
        // createButtons();
        buildListeners();

        menuSwitch = AssetLoader.get().manager.get("entities/abilities/menu_switch.wav");
        menuChoice = AssetLoader.get().manager.get("entities/abilities/menu_select.wav");
    }

    private Table buildUI() {
        Table victoryTable = new Window("", skin, "default");
        victoryTable.setSize(1200, 972);

        createText(victoryTable);
        createButtons(victoryTable);

        victoryTable.pack();

        stage.addActor(victoryTable);

        float x = stage.getViewport().getWorldWidth() / 2f;
        float y = stage.getViewport().getWorldHeight() / 2f;

        victoryTable.setPosition(x, y, Align.center);

        return victoryTable;
    }

    public void buildListeners() {
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                main.setSoundOn(true);
                menuChoice.play(0.6f);
                main.restart();
            }
        });

        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                main.goToMainMenu();
                menuChoice.play(0.6f);
            }
        });
    }

    private void createButtons(Table table) {
        restartButton = new ImageButton(skin, "restart");
        mainMenuButton = new ImageButton(skin, "exit");

        table.row();
        table.add(restartButton).padBottom(10);
        table.row();
        table.add(mainMenuButton);
    }

    private void createText(Table table) {
        float fontscale = 2f;
        int bottomPadding = 10;

        table.add().height(120);
        table.row();

        Label enemiesKilledText = new Label("Enemies killed: " + enemiesKilled , skin);
        enemiesKilledText.setFontScale(fontscale);
        table.add(enemiesKilledText).padBottom(bottomPadding).padTop(100).left();
        table.row();

        Label timeSurvivedText = new Label("Time survived: " + timeStamp, skin);
        timeSurvivedText.setFontScale(fontscale);
        table.add(timeSurvivedText).padBottom(bottomPadding).left();
        table.row();

        Label totalDamageDone = new Label("Total damage done: " + damageDone, skin);
        totalDamageDone.setFontScale(fontscale);
        table.add(totalDamageDone).padBottom(bottomPadding).left();
        table.row();

        Label totalDamageTaken = new Label("Total damage taken: " + damageTaken, skin);
        totalDamageTaken.setFontScale(fontscale);
        table.add(totalDamageTaken).padBottom(bottomPadding).left();
        table.row();

        Label totalHealing = new Label("Healing received: " + String.format("%.0f", healingReceived), skin);
        totalHealing.setFontScale(fontscale);
        table.add(totalHealing).padBottom(bottomPadding).left();
        table.row();

        Label damageShielded = new Label("Damage absorbed: " + damagePrevented, skin);
        damageShielded.setFontScale(fontscale);
        table.add(damageShielded).padBottom(bottomPadding).left();
        table.row();

        table.add().height(80);
        table.row();
    }

    public boolean isTableVisible() {
        return table.isVisible();
    }

    @Override
    public void show() {
        table.setVisible(true);
        stage.act(0);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        table.setVisible(false);
    }

    @Override
    public void dispose() {

    }
}
