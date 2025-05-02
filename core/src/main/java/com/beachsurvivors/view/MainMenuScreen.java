package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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
import com.beachsurvivors.AssetLoader;

public class MainMenuScreen implements Screen {

    private Main main;
    private Stage stage;
    Sound playSound;
    Music mainTheme;

    Texture backgroundTexture;
    Texture logoTexture;

    Texture playButtonTexture;
    Texture playButtonHoverTexture;
    Texture playButtonPressedTexture;

    Texture exitButtonTexture;
    Texture exitButtonHoverTexture;
    Texture exitButtonPressedTexture;

    SpriteBatch spriteBatch;

    // TODO: Ändra via Skin Composer med knapparna
    // PLAY-KNAPP
    private TextureRegionDrawable playDrawable;
    private TextureRegionDrawable playHoverDrawable;
    private TextureRegionDrawable playPressedDrawable;

    private ImageButton playButton;
    private ImageButton.ImageButtonStyle playButtonStyle;

    // EXIT-KNAPP
    private TextureRegionDrawable exitDrawable;
    private TextureRegionDrawable exitHoverDrawable;
    private TextureRegionDrawable exitPressedDrawable;

    private ImageButton exitButton;
    private ImageButton.ImageButtonStyle exitButtonStyle;

    ScreenViewport viewport;

    public MainMenuScreen(Main main) {
        this.main = main;

        backgroundTexture = AssetLoader.get().manager.get("assets/main_menu/menu_background.jpeg");
        logoTexture = AssetLoader.get().manager.get("assets/main_menu/logo_skiss_1.png");

        playSound = AssetLoader.get().manager.get("assets/main_menu/sound/holiday.wav");
        mainTheme = AssetLoader.get().manager.get("assets/sounds/beach.mp3");
        mainTheme.play();
        mainTheme.setVolume(0.5f);
        mainTheme.setLooping(true);
        // PLAY
        playButtonTexture = AssetLoader.get().manager.get("assets/main_menu/buttons/play_button_2_scaled.png");
        playButtonHoverTexture = AssetLoader.get().manager.get("assets/main_menu/buttons/play_button_2_hover_scaled.png");
        playButtonPressedTexture = AssetLoader.get().manager.get("assets/main_menu/buttons/play_button_2_pressed_scaled.png");

        playDrawable = new TextureRegionDrawable(playButtonTexture);
        playHoverDrawable = new TextureRegionDrawable(playButtonHoverTexture);
        playPressedDrawable = new TextureRegionDrawable(playButtonPressedTexture);

        playButtonStyle = new ImageButton.ImageButtonStyle();
        playButtonStyle.up = playDrawable; // Standard
        playButtonStyle.over = playHoverDrawable; // Hover

        playButton = new ImageButton(playButtonStyle);

        // EXIT
        exitButtonTexture = new Texture("main_menu/buttons/exit_button_2_scaled.png");
        exitButtonHoverTexture = new Texture("main_menu/buttons/exit_button_2_hover_scaled.png");
        exitButtonPressedTexture = new Texture("main_menu/buttons/exit_button_2_pressed_scaled.png");

        exitDrawable = new TextureRegionDrawable(exitButtonTexture);
        exitHoverDrawable = new TextureRegionDrawable(exitButtonHoverTexture);
        exitPressedDrawable = new TextureRegionDrawable(exitButtonPressedTexture);

        exitButtonStyle = new ImageButton.ImageButtonStyle();
        exitButtonStyle.up = exitDrawable;
        exitButtonStyle.over = exitHoverDrawable;

        exitButton = new ImageButton(exitButtonStyle);

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
                mainTheme.stop();
               // playSound.play(0.1f);

                startGameMusic();
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
                playSound.play(1f);
                Gdx.app.exit();
            }
        });
    }

    public void startGameMusic() {
        playSound.setLooping(playSound.play(0.1f),true);
        playSound.setPitch(0,0.7f);
    }
}
