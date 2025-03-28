package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

    private Main main;

    private Stage stage;

    Texture backgroundTexture;
    Texture logoTexture;
    Texture playButtonTexture;
    Texture exitButtonTexture;

    Drawable drawablePlay;

    ImageButton playButton;
    ImageButton exitButton;

    SpriteBatch spriteBatch;

    FitViewport viewport;

    public MainMenuScreen(Main main) {
        this.main = main;
        backgroundTexture = new Texture("main_menu/menu_background.jpeg");
        logoTexture = new Texture("main_menu/logo.png");
        playButtonTexture = new Texture("main_menu/buttons/play_button.png");
        drawablePlay = new TextureRegionDrawable(new TextureRegion(playButtonTexture));
        playButton = new ImageButton(drawablePlay);
        //exitButtonTexture = new Texture("main_menu/buttons/exit_button.png");

        stage = new Stage(new ScreenViewport());
        stage.addActor(playButton);
        Gdx.input.setInputProcessor(stage);

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(16, 9);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    public void draw() {
        ScreenUtils.clear(Color.BLACK); // Good practice to always clear the screen every frame
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        // Add lines to draw here

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        spriteBatch.draw(logoTexture, 4, 5, (worldWidth/2), (worldHeight/3)); // TODO: ändra så att värdena inte är hårdkodade

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        //spriteBatch.draw(playButtonTexture, 7, 3, 2, 1);
        //spriteBatch.draw(exitButtonTexture, 7, 1, 2, 1);

        spriteBatch.end();
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        draw();
    }

    @Override
    public void hide() {

    }


}
