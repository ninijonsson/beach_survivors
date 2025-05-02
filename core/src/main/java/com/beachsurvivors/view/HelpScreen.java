package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class HelpScreen implements Screen {
    private Stage stage;
    private GameScreen game;
    private Main main;

    public HelpScreen(GameScreen game, Main main) {
        this.game = game;
        this.main = main;

        stage = new Stage(new FitViewport(game.getScreenWidth(), game.getScreenHeight()));
        Gdx.input.setInputProcessor(stage);

        buildUI();
    }

    // TODO: Ändra från Image till Skin
    private void buildUI() {
        Texture background = new Texture(Gdx.files.internal("entities/how_to_play.png"));
        Image backgroundImage = new Image(background);

        backgroundImage.setScale(2.0f);

        // Sätt positionen så att bilden blir centrerad på skärmen
        float x = stage.getWidth() / 2f - backgroundImage.getWidth() * backgroundImage.getScaleX() / 2f;
        float y = stage.getHeight() / 2f - backgroundImage.getHeight() * backgroundImage.getScaleY() / 2f;

        backgroundImage.setPosition(x, y);

        stage.addActor(backgroundImage);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f); // Svart bakgrundsfärg
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.resume();
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
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
