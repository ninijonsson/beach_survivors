package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.utilities.MusicHandler;

public class PauseOverlay {
    private Stage stage;
    private Table table;
    private GameScreen game;
    private Main main;
    private Skin skin;
    private boolean isSoundOn;
    private Image dimBackground;
    private int selectedIndex = 0;
    private Image selectorArrow;
    private ImageButton[] buttons;
    private boolean arrowInitialized = false;
    private SpriteBatch spriteBatch;


    private Sound menuSwitch;
    private Sound menuChoice;




    public PauseOverlay(GameScreen game, Main main) {
        this.game = game;
        this.main = main;
        isSoundOn = main.isSoundOn();

        stage = new Stage(new FitViewport(game.getScreenWidth(), game.getScreenHeight()));
        skin = AssetLoader.get().getSkin("skin_composer/pause_menu/pause_menu.json");

        menuSwitch = AssetLoader.get().manager.get("entities/abilities/menu_switch.wav");
        menuChoice = AssetLoader.get().manager.get("entities/abilities/menu_select.wav");

        table = buildUI(); // Lägg till tabellen
        table.setVisible(true); // Viktigt – gör table synlig så layout fungerar
        stage.act(0);           // Uppdaterar layouten direkt
        this.spriteBatch = new SpriteBatch();

    }


    private Table buildUI() {
        Table pauseMenu = new Window("", skin, "default");
        pauseMenu.setSize(1200, 972);

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

        // Select icon - går att byta ut
        buttons = new ImageButton[] { resumeButton, helpButton, exitButton };


        // Runda knappar
        pauseMenu.add(soundButton).size(96, 96).left().padTop(260);
        pauseMenu.add(restartButton).size(96, 96).padTop(260);
        pauseMenu.add().row(); // Ny rad

        // Avlånga knappar
        pauseMenu.add(resumeButton).size(333, 83).pad(10).padTop(70).padLeft(100).row();
        pauseMenu.add(helpButton).size(333, 83).pad(10).padLeft(100).row();
        pauseMenu.add(exitButton).size(333, 83).pad(10).padLeft(100).row();

        stage.addActor(pauseMenu);
        Texture arrowTexture = AssetLoader.get().getTexture("entities/icons/select_arrow.png");
        selectorArrow = new Image(arrowTexture);
        selectorArrow.setSize(32, 32);
        selectorArrow.setPosition(-50,-50);


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
                menuChoice.play(0.6f);
                main.restart();
            }
        });

        // Återgå till spelet
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.resume();
                menuChoice.play(0.6f);
            }
        });

        // How to play
        helpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                main.goToHelpScreen();
                menuChoice.play(0.6f);
            }
        });

        // Återgå till huvudmenyn
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                main.goToMainMenu();
                menuChoice.play(0.6f);
            }
        });
        stage.addActor(selectorArrow);
        stage.act(0); // tvingar layout så att knapparna får position
        //updateArrowPosition(); // sätter coinen vid första knappen

        this.table = pauseMenu;
        return pauseMenu;
    }


    public void toggleSound() {
        if (!main.isSoundOn()) {
            main.turnOffInGameMusic();
        } else {
            main.turnOnInGameMusic();
        }
    }

    public boolean isVisible() {
        return table.isVisible();
    }

    public void show() {
        table.setVisible(true);
        Gdx.input.setInputProcessor(stage);
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.W:
                    case Input.Keys.UP:
                        selectedIndex = (selectedIndex + buttons.length - 1) % buttons.length;
                        menuSwitch.play(0.4f);
                        updateArrowPosition();
                        return true;
                    case Input.Keys.S:
                    case Input.Keys.DOWN:
                        selectedIndex = (selectedIndex + 1) % buttons.length;
                        menuSwitch.play(0.4f);
                        updateArrowPosition();
                        return true;
                    case Input.Keys.SPACE:
                    case Input.Keys.ENTER:
                        menuChoice.play(0.6f);
                        buttons[selectedIndex].fire(new ChangeListener.ChangeEvent());
                        return true;
                    case Input.Keys.ESCAPE:
                        game.resume();
                        return true;
                }
                return false;
            }
        });


        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);

        stage.act(0);        // Uppdatera layout så vi får rätt positioner
    }

    public void hide() {
        table.setVisible(false);
    }

    public Stage getStage() {
        return stage;
    }

    private void updateArrowPosition() {
        ImageButton current = buttons[selectedIndex];
        Vector2 localPos = new Vector2(current.getWidth() / 2f, current.getHeight() / 2f);
        Vector2 stagePos = current.localToStageCoordinates(localPos);

        float x = stagePos.x - current.getWidth() / 2f - selectorArrow.getWidth() - 10;
        float y = stagePos.y - selectorArrow.getHeight() / 2f;

        selectorArrow.setPosition(x, y);
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();

        if (!arrowInitialized) {
            updateArrowPosition();
            arrowInitialized = true;
        }
    }






//    @Override
//    public void show() {
//        table.setVisible(false);
//        Gdx.input.setInputProcessor(stage); // Lyssna på event listeners
//    }
//
//    @Override
//    public void hide() {
//        Gdx.input.setInputProcessor(stage);
//    }


//    @Override
//    public void render(float delta) {
//        stage.act(delta);
//        stage.draw();
//
//        // Återgå till spelet ifall du trycker på ESC
//        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
//            game.resume();
//        }
//    }
//
//    @Override
//    public void resize(int width, int height) {
//        stage.getViewport().update(width, height, true);
//    }
//
//    @Override
//    public void pause() {
//
//    }
//
//    @Override
//    public void resume() {
//
//    }
//
//
//
public void dispose() {
    spriteBatch.dispose();
    stage.dispose();
}


}
