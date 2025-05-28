package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.beachsurvivors.utilities.AssetLoader;

public class MainMenuScreen implements Screen {

    private Main main;
    private Stage stage;
    Sound playSound;
    Music mainTheme;

    Texture backgroundTexture;
    Texture logoTexture;

    private Skin skin;

    private Table table;
    private ImageButton playButton;
    private ImageButton helpButton;
    private ImageButton exitButton;

    ScreenViewport viewport;
    FitViewport fitViewport;

    public MainMenuScreen(Main main) {
        this.main = main;

        playSound = AssetLoader.get().manager.get("main_menu/sound/holiday.wav");
        mainTheme = AssetLoader.get().manager.get("sounds/beach.mp3");
        mainTheme.play();
        mainTheme.setVolume(0.5f);
        mainTheme.setLooping(true);

        backgroundTexture = AssetLoader.get().manager.get("main_menu/menu_background.jpeg");
        Image background = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        logoTexture = AssetLoader.get().manager.get("main_menu/logo_skiss_1.png");
        Image logo = new Image(new TextureRegionDrawable(new TextureRegion(logoTexture)));
        logo.setScaling(Scaling.fit);

        skin = AssetLoader.get().manager.get("game_over_screen/deathscreen_skin.json");

        table = new Table();
        table.setFillParent(true);

        int width = 400;
        int height = 120;

        playButton = new ImageButton(skin, "play");
        playButton.getImageCell().size(width,height);

        helpButton = new ImageButton(skin, "help");
        helpButton.getImageCell().size(width,height);

        exitButton = new ImageButton(skin, "exit");
        exitButton.getImageCell().size(width,height);

        viewport = new ScreenViewport();
        fitViewport = new FitViewport(main.getGameScreen().getScreenWidth(), main.getGameScreen().getScreenHeight());

        stage = new Stage(fitViewport);

        int bottomPad = 20;
        table.add(logo).padBottom(60).padTop(100).height(400).width(800).row();
        table.add(playButton).padBottom(bottomPad).row();
        table.add(helpButton).padBottom(bottomPad).row();
        table.add(exitButton).padBottom(bottomPad).row();

        Stack stack = new Stack();
        stack.setSize(1200,972);
        stack.add(background);
        stack.add(table);
        stack.setPosition(0,0);
        stack.setFillParent(true);
        stage.addActor(stack);

        addListeners();
    }

    @Override
    public void resize(int width, int height) {
        fitViewport.update(width, height, true);
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
        stage.act(delta);
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

    public void addListeners() {

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                mainTheme.stop();
                startGameMusic();
                main.playGame();
            }
        });

        helpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                mainTheme.stop();
                main.goToHelpScreen();
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    public void startGameMusic() {
        playSound.setLooping(playSound.play(0.1f),true);
        playSound.setPitch(0,0.7f);
    }

}
