package com.beachsurvivors.view;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.utilities.MusicHandler;

public class CharacterSelectScreen implements Screen {

    private Main main;
    private Stage stage;
    private Image selectorArrow;
    private ImageButton[] buttons;
    private int selectedIndex = 0;

    private Sound menuSwitch;
    private Sound menuChoice;

    public CharacterSelectScreen(Main main) {
        this.main = main;

        stage = new Stage(new FitViewport(1920, 1080));

        Texture bgTexture = AssetLoader.get().getTexture("main_menu/menu_background.jpeg");
        Image background = new Image(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        background.setFillParent(true);
        stage.addActor(background);

        menuSwitch = AssetLoader.get().manager.get("entities/abilities/menu_switch.wav");
        menuChoice = AssetLoader.get().manager.get("entities/abilities/menu_select.wav");

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fill();

        Texture darkPanelTexture = new Texture(pixmap);
        pixmap.dispose();

        Image darkPanel = new Image(new TextureRegionDrawable(new TextureRegion(darkPanelTexture)));
        darkPanel.setSize(800, stage.getHeight()+400);
        darkPanel.setPosition(
            stage.getWidth() / 2f - darkPanel.getWidth() / 2f,
            stage.getHeight() / 2f - darkPanel.getHeight() / 2f + 50
        );

        stage.addActor(darkPanel);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // === Karaktärsanimationer ===
        Texture girlSheet = AssetLoader.get().getTexture("entities/beach_girl_sheet.png");
        Texture guySheet = AssetLoader.get().getTexture("entities/beach_guy_sheet.png");

        CharacterPreview girl = new CharacterPreview(girlSheet, 2, 1, 0.25f, 300, 300);
        CharacterPreview guy = new CharacterPreview(guySheet, 2, 1, 0.25f, 300, 300);

        // === Lägg till i knappar ===
        ImageButton girlButton = new ImageButton(new TextureRegionDrawable(new Texture("placeholder.png")));
        girlButton.getImageCell().size(300, 300);
        girlButton.getImage().setVisible(false);
        girlButton.addActor(girl);

        ImageButton guyButton = new ImageButton(new TextureRegionDrawable(new Texture("placeholder.png")));
        guyButton.getImageCell().size(300, 300);
        guyButton.getImage().setVisible(false);
        guyButton.addActor(guy);

        girlButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuChoice.play(0.6f);
                main.setSelectedCharacterType(1);
                main.playGame();
            }
        });

        guyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuChoice.play(0.6f);
                main.setSelectedCharacterType(2);
                main.playGame();
            }
        });

        buttons = new ImageButton[] { girlButton, guyButton };

        // === Layout ===
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2.5f);

        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        Label title = new Label("SELECT CHAMPION", style);

        table.add(title).padBottom(100).colspan(2).row();

        table.add(girlButton).padRight(100);
        table.add(guyButton).row();

        // === Pil under ===
        Texture arrowTexture = AssetLoader.get().getTexture("entities/icons/select_arrow.png");

        selectorArrow = new Image(arrowTexture);
        selectorArrow.setSize(32, 32);
        selectorArrow.setOrigin(selectorArrow.getWidth() / 2f, selectorArrow.getHeight() / 2f);
        selectorArrow.setRotation(90f);
        stage.addActor(selectorArrow);

        stage.act(0); // tvinga layout
        updateArrowPosition();
    }


    private void updateArrowPosition() {
        ImageButton current = buttons[selectedIndex];
        float x = current.getX() + current.getWidth() / 2f - selectorArrow.getWidth() / 2f;
        float y = current.getY() - selectorArrow.getHeight() - 40;
        selectorArrow.setPosition(x, y);
    }


    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();

        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.LEFT:
                    case Input.Keys.A:
                        selectedIndex = (selectedIndex + buttons.length - 1) % buttons.length;
                        menuSwitch.play(0.4f);
                        updateArrowPosition();
                        return true;
                    case Input.Keys.RIGHT:
                    case Input.Keys.D:
                        selectedIndex = (selectedIndex + 1) % buttons.length;
                        menuSwitch.play(0.4f);
                        updateArrowPosition();
                        return true;
                    case Input.Keys.ENTER:
                    case Input.Keys.SPACE:
                        MusicHandler.stop();
                        MusicHandler.play("main_menu/sound/holiday.wav", true);
                        menuChoice.play(0.6f);
                        main.setSelectedCharacterType(selectedIndex + 1);
                        main.playGame();
                        return true;
                    case Input.Keys.ESCAPE:
                        main.goToMainMenu();
                        return true;
                }
                return false;
            }
        });

        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
