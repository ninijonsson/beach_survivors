package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.utilities.AssetLoader;

public class HelpScreen implements Screen {
    private Stage stage;
    private GameScreen game;
    private Main main;
    private TextButton backButton;
    private Skin skin;

    private int screenWidth;
    private int screenHeight;

    public HelpScreen(int screenWidth, int screenHeight, Main main) {
        this.main = main;

        stage = new Stage(new FitViewport(screenWidth, screenHeight));
        Gdx.input.setInputProcessor(stage);

        buildUI();
    }

    // TODO: Ändra från Image till Skin
    private void buildUI() {
        Texture background = new Texture(Gdx.files.internal("entities/how_to_play.png"));
        Image backgroundImage = new Image(background);

        skin = AssetLoader.get().getSkin("game_over_screen/deathscreen_skin.json");
        backButton = new TextButton("RETURN [ESC]", skin);
        backButton.setSize(440,100);

        backgroundImage.setScale(2.0f);

        // Sätt positionen så att bilden blir centrerad på skärmen
        float x = stage.getWidth() / 2f - backgroundImage.getWidth() * backgroundImage.getScaleX() / 2f;
        float y = stage.getHeight() / 2f - backgroundImage.getHeight() * backgroundImage.getScaleY() / 2f;

        backgroundImage.setPosition(x, y);
        backButton.setPosition((stage.getWidth() - backButton.getWidth()) /2, 30);

        stage.addActor(backgroundImage);
        stage.addActor(backButton);

        addListeners();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage); // Lyssna på event listeners
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f); // Svart bakgrundsfärg
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            main.showPreviousScreen();

        }
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
        Timer.instance().start();
       // dispose();
    }

    @Override
    public void dispose() {
        //stage.dispose();
    }

    public void addListeners() {
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                main.showPreviousScreen();
            }
        });

    }
}
