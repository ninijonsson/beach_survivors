package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class DeathScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private GameScreen gameScreen;
    private Table table;

    private TextButton retryButton;
    private TextButton exitButton;
    private TextButton mainMenuButton;
    private Label textLabel;


    public DeathScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;

        stage = new Stage(new FitViewport(gameScreen.getScreenWidth(), gameScreen.getScreenHeight()));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("death_screen/aa.json"));

        createActors();

    }

    public void createActors() {
        table = new Table();

        textLabel = new Label("YOU DIED" , skin);
        textLabel.setFontScale(12);
        textLabel.setColor(Color.RED);
        skin.getFont("font-over").getData().setScale(3f);

        retryButton = new TextButton("Retry", skin, "wooden_sign");
        retryButton.setSize(256,128);

        mainMenuButton = new TextButton("Main menu" , skin, "wooden_sign");
        mainMenuButton.setSize(256,128);

        exitButton = new TextButton("Exit" , skin, "wooden_sign");
        exitButton.setSize(256,128);

        addActors();
        addListeners();
    }

    private void addActors() {
        table.add(textLabel).padBottom(30);
        table.row();
        table.add(retryButton).width(600).height(150).padBottom(50);
        table.row();
        table.add(mainMenuButton).width(600).height(150).padBottom(50);
        table.row();
        table.add(exitButton).width(600).height(150);

        table.setPosition(gameScreen.getScreenWidth()/2f, gameScreen.getScreenHeight()/2f);
        stage.addActor(table);
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
