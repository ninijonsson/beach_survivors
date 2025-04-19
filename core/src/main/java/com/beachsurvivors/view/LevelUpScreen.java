package com.beachsurvivors.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class LevelUpScreen implements Screen {

    private Stage stage;
    private GameScreen game;
    private Skin skin;

    public LevelUpScreen(GameScreen game) {
        this.game = game;

        stage = new Stage(new FitViewport(game.getScreenWidth(), game.getScreenHeight()));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("levelUpScreen/uiskin.json"));

        buildUI();
    }

    private void buildUI() {
        Table table = new Table();
        table.setFillParent(true);
//        table.center();

        Label title = new Label("Level Up!", skin);
        TextButton upgrade1 = new TextButton("+1 BOOMERANG", skin);
        TextButton upgrade2 = new TextButton("+50% DAMAGE", skin);
        TextButton continueButton = new TextButton("Continue", skin);

        table.add(title).padBottom(40).row();
        table.add(upgrade1).pad(10).row();
        table.add(upgrade2).pad(10).row();
        table.add(continueButton).padTop(40);

        stage.addActor(table);

        // Button logic
        upgrade1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                applyUpgrade("damage");
            }
        });

        upgrade2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                applyUpgrade("cooldown");
            }
        });

        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resumeGame();
            }
        });
    }

    private void applyUpgrade(String type) {
        System.out.println("Upgrade chosen: " + type);
    }

    private void resumeGame() {
        game.resume();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
