package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.AssetLoader;

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

        int minutes = (int)(timeSurvived / 60f);
        int seconds = (int)(timeSurvived % 60f);
        timeStamp = String.format("[%02d:%02d] ", minutes, seconds);

        createActors();

    }

    public void createActors() {
        rightTable = new Table();
        leftTable = new Table();

        Texture backgroundTexture = AssetLoader.get().manager.get("assets/game_over_screen/you_died.png");
        Image background = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));

        //Stackar actors, background underst, tables ovanpå
        Stack stack = new Stack();
        stack.setSize(1200,972);
        stack.setPosition(gameScreen.getScreenWidth()/2f-stack.getWidth()/2,
            gameScreen.getScreenHeight()/2f-stack.getHeight()/2 );
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

        //Grow() för att bilden ska fylla ut knapparna
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

        Label totalHealing = new Label("Healing received: " + healingReceived, skin);
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
                System.out.println("Main menu");
                gameScreen.getMain().goToMainMenu();
            }
        });

        retryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("Retry");
                gameScreen.getMain().restart();
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("Exit");
                Gdx.app.exit();
            }
        });

    }


    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        stage.act();
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

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
