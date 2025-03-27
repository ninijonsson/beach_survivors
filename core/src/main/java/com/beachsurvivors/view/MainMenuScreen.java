package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

    private Main main;

    Sound playSound;

    Texture backgroundTexture;
    Texture logoTexture;
    Texture playButtonTexture;
    Texture exitButtonTexture;

    // PLAY-KNAPP
    private TextureRegion playRegion; // Används för TextureRegionDrawable
    private TextureRegionDrawable playDrawable; // Används för ImageButton
    private ImageButton playButton;

    // EXIT-KNAPP
    private TextureRegion exitRegion;
    private TextureRegionDrawable exitDrawable;
    private ImageButton exitButton;

    SpriteBatch spriteBatch;
    private Stage stage;

    //FitViewport viewport;
    ScreenViewport viewport;

    public MainMenuScreen(Main main) {
        this.main = main;

        backgroundTexture = new Texture("main_menu/menu_background.jpeg");
        logoTexture = new Texture("main_menu/logo_skiss_1.png");

        playSound = Gdx.audio.newSound(Gdx.files.internal("main_menu/sound/play_sound.wav"));

        // PLAY
        playButtonTexture = new Texture("main_menu/buttons/play_button_2_scaled.png");
        playRegion = new TextureRegion(playButtonTexture);
        playDrawable = new TextureRegionDrawable(playRegion);
        playButton = new ImageButton(playDrawable);

        // EXIT
        exitButtonTexture = new Texture("main_menu/buttons/exit_button_2_scaled.png");
        exitRegion = new TextureRegion(exitButtonTexture);
        exitDrawable = new TextureRegionDrawable(exitRegion);
        exitButton = new ImageButton(exitDrawable);

        // viewport = new FitViewport(16, 9);
        viewport = new ScreenViewport();

        spriteBatch = new SpriteBatch();
        stage = new Stage(viewport);
        stage.addActor(playButton); // Lägg in knapparna i en array istället?
        stage.addActor(exitButton);

        // EVENT LISTENERS
        onPlayButtonPressed();
        onExitButtonPressed();
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

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        spriteBatch.draw(logoTexture, ((worldWidth/2)-(logoWidth/2)), (worldHeight/2), logoWidth, logoHeight); // TODO: ändra så att värdena inte är hårdkodade
        spriteBatch.end();

        stage.act(delta);

        playButton.setPosition((worldWidth/2)-(playButton.getWidth()/2), worldHeight/3);
        exitButton.setPosition((worldWidth/2)-(exitButton.getWidth()/2), worldHeight/6);

        stage.draw();
    }
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage); // Lyssna på event listeners
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
                playSound.play();
                main.switchScreen();
            }
        });
    }

    public void onExitButtonPressed() {
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("onExitButtonPressed");
                Gdx.app.exit();
            }
        });
    }
}
