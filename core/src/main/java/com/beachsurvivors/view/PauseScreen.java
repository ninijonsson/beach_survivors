package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import javax.swing.event.ChangeEvent;

public class PauseScreen implements Screen {
    private Stage stage;
    private GameScreen game;
    private Skin skin;

    public PauseScreen(GameScreen game) {
        this.game = game;

        stage = new Stage(new FitViewport(game.getScreenWidth(), game.getScreenHeight()));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("SkinComposer/pause_menu/pause_menu.json"));

        buildUI();
    }

    private void buildUI() {
        Window pauseMenu = new Window("", skin, "default");
        pauseMenu.setSize(1200, 972); // Bildens dimensioner

        // Centrera pausmenyn
        pauseMenu.setPosition(game.getScreenWidth() / 5f,
            stage.getHeight() / 2f);

        ImageButton resumeButton = new ImageButton(skin, "resume");
        ImageButton helpButton = new ImageButton(skin, "help");
        ImageButton exitButton = new ImageButton(skin, "exit");

        pauseMenu.add(resumeButton).width(256).height(64).pad(10).padTop(420).row();
        pauseMenu.add(helpButton).width(256).height(64).pad(10).row();
        pauseMenu.add(exitButton).width(256).height(64).pad(10).row();

        stage.addActor(pauseMenu);

        // Återgå till spelet
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.resume();
            }
        });
    }


    @Override
    public void show() {

    }

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
