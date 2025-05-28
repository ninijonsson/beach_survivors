package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.utilities.AssetLoader;

public class DeathScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private GameScreen gameScreen;
    private Table rightTable;
    private Table leftTable;

    private ImageButton retryButton;
    private ImageButton exitButton;
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

    public DeathScreen(GameScreen gameScreen, int enemiesKilled, double damageDone,
                       float timeSurvived, double damageTaken, double healingReceived, double damagePrevented) {
        this.gameScreen = gameScreen;
        this.enemiesKilled = enemiesKilled;
        this.damageDone = damageDone;
        this.damageTaken = damageTaken;
        this.healingReceived = healingReceived;
        this.damagePrevented = damagePrevented;

        stage = new Stage(new FitViewport(gameScreen.getScreenWidth(), gameScreen.getScreenHeight()));
        Gdx.input.setInputProcessor(stage);

        skin = AssetLoader.get().manager.get("game_over_screen/deathscreen_skin.json");

        menuSwitch = AssetLoader.get().manager.get("entities/abilities/menu_switch.wav");
        menuChoice = AssetLoader.get().manager.get("entities/abilities/menu_select.wav");

        int minutes = (int)(timeSurvived / 60f);
        int seconds = (int)(timeSurvived % 60f);
        timeStamp = String.format("[%02d:%02d] ", minutes, seconds);

        createActors();

        buttons = new ImageButton[] { retryButton, mainMenuButton, exitButton };
        Texture arrowTexture = AssetLoader.get().getTexture("entities/icons/coin.png");
        selectorArrow = new Image(arrowTexture);
        selectorArrow.setSize(32, 32);
        stage.addActor(selectorArrow);

        stage.act(0);
        updateArrowPosition();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.W:
                    case Input.Keys.UP:
                        selectedIndex = (selectedIndex + buttons.length - 1) % buttons.length;
                        menuSwitch.play(0.1f);
                        updateArrowPosition();
                        return true;
                    case Input.Keys.S:
                    case Input.Keys.DOWN:
                        selectedIndex = (selectedIndex + 1) % buttons.length;
                        menuSwitch.play(0.1f);
                        updateArrowPosition();
                        return true;
                    case Input.Keys.SPACE:
                    case Input.Keys.ENTER:
                        menuChoice.play(0.2f);
                        buttons[selectedIndex].fire(new ChangeListener.ChangeEvent());
                        return true;
                }
                return false;
            }
        });
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void createActors() {
        rightTable = new Table();
        leftTable = new Table();

        Texture backgroundTexture = AssetLoader.get().manager.get("game_over_screen/you_died.png");
        Image background = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));

        Stack stack = new Stack();
        stack.setSize(1200,972);
        stack.setPosition(gameScreen.getScreenWidth()/2f-stack.getWidth()/2,
            gameScreen.getScreenHeight()/2f-stack.getHeight()/2);
        stack.add(background);
        stack.add(rightTable);
        stack.add(leftTable);
        stage.addActor(stack);

        createActorsLeftTable();
        createActorsRightTable();
        addListeners();
    }

    private void createActorsRightTable() {
        retryButton = new ImageButton(skin, "retry");
        exitButton = new ImageButton(skin, "exit");
        mainMenuButton = new ImageButton(skin, "menu");

        retryButton.getImageCell().grow();
        mainMenuButton.getImageCell().grow();
        exitButton.getImageCell().grow();

        addActorsRightTable();
    }

    private void addActorsRightTable() {
        int width = 333;
        int height = 83;

        rightTable.add(retryButton).width(width).height(height).padBottom(20);
        rightTable.row();
        rightTable.add(mainMenuButton).width(width).height(height).padBottom(20);
        rightTable.row();
        rightTable.add(exitButton).width(width).height(height);

        rightTable.setPosition(gameScreen.getScreenWidth()*0.65f, gameScreen.getScreenHeight()/2.5f);
        stage.addActor(rightTable);
    }

    private void createActorsLeftTable() {
        float fontscale = 1.15f;
        int bottomPadding = 10;

        Label enemiesKilledText = new Label("Enemies killed: " + enemiesKilled , skin);
        enemiesKilledText.setFontScale(fontscale);
        leftTable.add(enemiesKilledText).padBottom(bottomPadding).left();
        leftTable.row();

        Label timeSurvivedText = new Label("Time survived: " + timeStamp, skin);
        timeSurvivedText.setFontScale(fontscale);
        leftTable.add(timeSurvivedText).padBottom(bottomPadding).left();
        leftTable.row();

        Label totalDamageDone = new Label("Total damage done: " + damageDone, skin);
        totalDamageDone.setFontScale(fontscale);
        leftTable.add(totalDamageDone).padBottom(bottomPadding).left();
        leftTable.row();

        Label totalDamageTaken = new Label("Total damage taken: " + damageTaken, skin);
        totalDamageTaken.setFontScale(fontscale);
        leftTable.add(totalDamageTaken).padBottom(bottomPadding).left();
        leftTable.row();

        Label totalHealing = new Label("Healing received: " + String.format("%.0f", healingReceived), skin);
        totalHealing.setFontScale(fontscale);
        leftTable.add(totalHealing).padBottom(bottomPadding).left();
        leftTable.row();

        Label damageShielded = new Label("Damage absorbed: " + damagePrevented, skin);
        damageShielded.setFontScale(fontscale);
        leftTable.add(damageShielded).padBottom(bottomPadding).left();

        leftTable.setPosition(gameScreen.getScreenWidth()-1100, gameScreen.getScreenHeight()/2.4f);
        stage.addActor(leftTable);
    }

    public void addListeners() {
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                gameScreen.getMain().goToMainMenu();
            }
        });

        retryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                gameScreen.getMain().restart();
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    private void updateArrowPosition() {
        ImageButton current = buttons[selectedIndex];
        Vector2 localPos = new Vector2(current.getWidth() / 2f, current.getHeight() / 2f);
        Vector2 stagePos = current.localToStageCoordinates(localPos);

        float x = stagePos.x - current.getWidth() / 2f - selectorArrow.getWidth() - 10;
        float y = stagePos.y - selectorArrow.getHeight() / 2f;

        selectorArrow.setPosition(x, y);
    }

    @Override
    public void show() {}

    @Override
    public void render(float v) {
        stage.act();
        stage.draw();

        if (!arrowInitialized) {
            updateArrowPosition();
            arrowInitialized = true;
        }
    }

    @Override
    public void resize(int i, int i1) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
