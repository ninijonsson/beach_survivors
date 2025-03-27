package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

    private Main main;

    Texture backgroundTexture;
    Texture logoTexture;
    Texture playButtonTexture;
    Texture exitButtonTexture;

    private TextureRegion playRegion; // Används för TextureRegionDrawable
    private TextureRegionDrawable playDrawable; // Används för ImageButton
    private ImageButton playButton;

    SpriteBatch spriteBatch;
    private Stage stage;

    //FitViewport viewport;
    ScreenViewport viewport;

    public MainMenuScreen(Main main) {
        this.main = main;

        backgroundTexture = new Texture("main_menu/menu_background.jpeg");
        logoTexture = new Texture("main_menu/logo_skiss_1.png");

        playButtonTexture = new Texture("main_menu/buttons/play_button.png");
        playRegion = new TextureRegion(playButtonTexture);
        playDrawable = new TextureRegionDrawable(playRegion);
        playButton = new ImageButton(playDrawable);

        exitButtonTexture = new Texture("main_menu/buttons/exit_button.png");

        // viewport = new FitViewport(16, 9);
        viewport = new ScreenViewport();

        spriteBatch = new SpriteBatch();
        stage = new Stage(viewport);
        stage.addActor(playButton);
        onPlayButtonPressed();
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

    public void draw(float delta) {
        ScreenUtils.clear(Color.BLACK); // Good practice to always clear the screen every frame
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        // Add lines to draw here

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float logoWidth = logoTexture.getWidth()*4;
        float logoHeight = logoTexture.getHeight()*4;
        playButton.setSize(128, 64);

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        spriteBatch.draw(logoTexture, ((worldWidth/2)-(logoWidth/2)), (worldHeight/2), logoWidth, logoHeight); // TODO: ändra så att värdena inte är hårdkodade
        playButton.setPosition((worldWidth/2)-(playButton.getWidth()), worldHeight/3);
        // spriteBatch.draw(playButtonTexture, 7, 3, 2, 1);
        // spriteBatch.draw(exitButtonTexture, 7, 1, 2, 1);

        spriteBatch.end();

        stage.act(delta);
        stage.draw();
    }
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage); // Lyssna på event listener (play-knappen)
    }

    @Override
    public void render(float v) {
        draw(v);

    }

    @Override
    public void hide() {

    }

    public void onPlayButtonPressed() {
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("onPlayButtonPressed");
                main.switchScreen();
            }
        });
    }
}
