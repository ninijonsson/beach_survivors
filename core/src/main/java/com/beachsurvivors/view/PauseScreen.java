package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.utilities.AssetLoader;

public class PauseScreen implements Screen {
    private Stage stage;
    private GameScreen game;
    private Main main;
    private Skin skin;
    private boolean isSoundOn;
    private Image dimBackground;

    public PauseScreen(GameScreen game, Main main) {
        this.game = game;
        this.main = main;
        isSoundOn = main.isSoundOn();

        stage = new Stage(new FitViewport(game.getScreenWidth(), game.getScreenHeight()));
        Gdx.input.setInputProcessor(stage);

        skin = AssetLoader.get().getSkin("skin_composer/pause_menu/pause_menu.json");

        //createDarkerBackground();
        buildUI();
    }

    private void buildUI() {
        Window pauseMenu = new Window("", skin, "default");
        pauseMenu.setSize(1200, 972); // Bildens dimensioner
        //pauseMenu.setDebug(true); // Rutnät för debugging

        // Centrera pausmenyn
        pauseMenu.setPosition(game.getScreenWidth() / 5f,
            stage.getHeight() / 2f);

        ImageButton soundOnButton = new ImageButton(skin, "sound_on");
        ImageButton soundOffButton = new ImageButton(skin, "sound_off");

        // För att toggla stilarna på ljudknappen
        ImageButton.ImageButtonStyle soundOnStyle = skin.get("sound_on", ImageButton.ImageButtonStyle.class);
        ImageButton.ImageButtonStyle soundOffStyle = skin.get("sound_off", ImageButton.ImageButtonStyle.class);

        // Deklaration av knappar
        ImageButton soundButton = new ImageButton(isSoundOn ? soundOnStyle : soundOffStyle);
        ImageButton restartButton = new ImageButton(skin, "restart");
        ImageButton resumeButton = new ImageButton(skin, "resume");
        ImageButton helpButton = new ImageButton(skin, "help");
        ImageButton exitButton = new ImageButton(skin, "exit");

        // Runda knappar
        pauseMenu.add(soundButton).size(96, 96).left().padTop(260);
        pauseMenu.add(restartButton).size(96, 96).padTop(260);
        pauseMenu.add().row(); // Ny rad

        // Avlånga knappar
        pauseMenu.add(resumeButton).size(333, 83).pad(10).padTop(70).padLeft(100).row();
        pauseMenu.add(helpButton).size(333, 83).pad(10).padLeft(100).row();
        pauseMenu.add(exitButton).size(333, 83).pad(10).padLeft(100).row();

        stage.addActor(pauseMenu);

        // Stänga av/sätta på musik
        soundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (isSoundOn) {
                    soundButton.setStyle(soundOffStyle);
                    main.setSoundOn(false);
                } else {
                    soundButton.setStyle(soundOnStyle);
                    main.setSoundOn(true);
                }

                toggleSound();
            }
        });

        // Börja om spel
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                main.setSoundOn(true);
                main.restart();
            }
        });

        // Återgå till spelet
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.resume();
            }
        });

        // How to play
        helpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                main.goToHelpScreen();
            }
        });

        // Återgå till huvudmenyn
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                main.goToMainMenu();
            }
        });
    }

    public void toggleSound() {
        if (!main.isSoundOn()) {
            main.turnOffInGameMusic();
        } else {
            main.turnOnInGameMusic();
        }
    }

    public void createDarkerBackground() {
        if (dimBackground == null) {
            // Skapa en svart transparent rektangel
            Pixmap pixmap = new Pixmap((int) stage.getWidth(), (int) stage.getHeight(), Pixmap.Format.RGBA8888);
            pixmap.setColor(0, 0, 0, 0.5f); // 50 % svart
            pixmap.fill();

            Texture texture = new Texture(pixmap);
            pixmap.dispose();

            dimBackground = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
            dimBackground.setSize(stage.getWidth(), stage.getHeight());

            if (!stage.getActors().contains(dimBackground, true)) {
                stage.addActor(dimBackground);
            }
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();

        // Återgå till spelet ifall du trycker på ESC
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
