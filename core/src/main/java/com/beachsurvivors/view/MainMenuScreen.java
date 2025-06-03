package com.beachsurvivors.view;

import com.badlogic.gdx.*;
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
import com.beachsurvivors.utilities.MusicHandler;

public class MainMenuScreen implements Screen {

    private Main main;
    private Stage stage;
    Sound playSound;
    Music mainTheme;
    Sound menuSwitch;
    Sound menuChoice;

    private int selectedIndex = 0;
    private Image selectorArrow;
    private ImageButton[] buttons;

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

        mainTheme = AssetLoader.get().manager.get("sounds/beach.mp3");
        menuSwitch = AssetLoader.get().manager.get("entities/abilities/menu_switch.wav");
        menuChoice = AssetLoader.get().manager.get("entities/abilities/menu_select.wav");

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

        buttons = new ImageButton[] { playButton, helpButton, exitButton };

        Texture arrowTexture = AssetLoader.get().getTexture("entities/icons/select_arrow.png");
        selectorArrow = new Image(arrowTexture);
        selectorArrow.setSize(32, 32);
        stage.addActor(selectorArrow);

        stage.act(0); // tvingar layout
        updateArrowPosition();
    }

    @Override
    public void resize(int width, int height) {
        fitViewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {}

    public void draw(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {
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
                }
                return false;
            }
        });

        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float v) {
        draw(v);
    }

    @Override
    public void hide() {}

    public void addListeners() {
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                mainTheme.stop();
                menuChoice.play(0.6f);
                startGameMusic();
                main.playGame();
            }
        });

        helpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                mainTheme.stop();
                menuChoice.play(0.6f);
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
        MusicHandler.play("main_menu/sound/holiday.wav", true);
    }

    private void updateArrowPosition() {
        ImageButton current = buttons[selectedIndex];

        float x = current.getX() - selectorArrow.getWidth() - 20;
        float y = current.getY() + current.getHeight() / 2f - selectorArrow.getHeight() / 2f;
        selectorArrow.setPosition(x, y);
    }
}
