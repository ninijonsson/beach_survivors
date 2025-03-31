package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
    private Stage stage;
    Sound playSound;

    Texture backgroundTexture;
    Texture logoTexture;

    Texture playButtonTexture;
    Texture playButtonHoverTexture;
    Texture playButtonPressedTexture;

    Texture exitButtonTexture;
    Texture exitButtonHoverTexture;
    Texture exitButtonPressedTexture;

    SpriteBatch spriteBatch;
    // PLAY-KNAPP
    //private TextureRegion playRegion; // Används för TextureRegionDrawable
    private TextureRegionDrawable playDrawable; // Används för ImageButton
    private TextureRegionDrawable playHoverDrawable;
    private TextureRegionDrawable playPressedDrawable;

    private ImageButton playButton;
    //private TextureRegion playHoverRegion;
    private ImageButton.ImageButtonStyle playButtonStyle;

    // EXIT-KNAPP
    //private TextureRegion exitRegion;
    private TextureRegionDrawable exitDrawable;
    private TextureRegionDrawable exitHoverDrawable;
    private TextureRegionDrawable exitPressedDrawable;
    private ImageButton exitButton;
    private ImageButton.ImageButtonStyle exitButtonStyle;

    //FitViewport viewport;
    ScreenViewport viewport;

    public MainMenuScreen(Main main) {
        this.main = main;

        backgroundTexture = new Texture("main_menu/menu_background.jpeg");
        logoTexture = new Texture("main_menu/logo_skiss_1.png");
        playButtonTexture = new Texture("main_menu/buttons/play_button.png");
        playDrawable = new TextureRegionDrawable(new TextureRegion(playButtonTexture));
        //exitButtonTexture = new Texture("main_menu/buttons/exit_button.png");

        playSound = Gdx.audio.newSound(Gdx.files.internal("main_menu/sound/play_sound.wav"));


        // PLAY
        playButtonTexture = new Texture("main_menu/buttons/play_button_2_scaled.png");
        playButtonHoverTexture = new Texture("main_menu/buttons/play_button_2_hover_scaled.png");
        playButtonPressedTexture = new Texture("main_menu/buttons/play_button_2_pressed_scaled.png");

        //playRegion = new TextureRegion(playButtonTexture);
        playDrawable = new TextureRegionDrawable(playButtonTexture);
        playHoverDrawable = new TextureRegionDrawable(playButtonHoverTexture);
        playPressedDrawable = new TextureRegionDrawable(playButtonPressedTexture);
        //playButton = new ImageButton(playDrawable);

        playButtonStyle = new ImageButton.ImageButtonStyle();
        playButtonStyle.up = playDrawable; // Standard
        playButtonStyle.over = playHoverDrawable; // Hover

        playButton = new ImageButton(playButtonStyle);

        // EXIT
        exitButtonTexture = new Texture("main_menu/buttons/exit_button_2_scaled.png");
        exitButtonHoverTexture = new Texture("main_menu/buttons/exit_button_2_hover_scaled.png");
        exitButtonPressedTexture = new Texture("main_menu/buttons/exit_button_2_pressed_scaled.png");
        //exitRegion = new TextureRegion(exitButtonTexture);
        exitDrawable = new TextureRegionDrawable(exitButtonTexture);
        exitHoverDrawable = new TextureRegionDrawable(exitButtonHoverTexture);
        exitPressedDrawable = new TextureRegionDrawable(exitButtonPressedTexture);

        exitButtonStyle = new ImageButton.ImageButtonStyle();
        exitButtonStyle.up = exitDrawable;
        exitButtonStyle.over = exitHoverDrawable;

        exitButton = new ImageButton(exitButtonStyle);

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

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

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        //spriteBatch.draw(playButtonTexture, 7, 3, 2, 1);
        //spriteBatch.draw(exitButtonTexture, 7, 1, 2, 1);

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
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                playButtonStyle.over = playPressedDrawable;
                return true; // Viktigt! returnera true så att touchUp() aktiveras
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                playButtonStyle.up = playDrawable;
                playSound.play(0.1f);
                main.switchScreen();
            }
        });
    }

    public void onExitButtonPressed() {
        exitButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                exitButtonStyle.over = exitPressedDrawable;
                return true; // Viktigt! returnera true så att touchUp() aktiveras
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                exitButtonStyle.up = exitDrawable;
                playSound.play(0.1f);
                Gdx.app.exit();
            }
        });
    }
}
