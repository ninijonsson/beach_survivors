package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.model.abilities.Ability;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.abilities.AbilityDescription;
import com.beachsurvivors.utilities.MusicHandler;

import java.util.ArrayList;
import java.util.List;


public class ChestOverlay {
    private GameScreen game;
    private Stage stage;
    private SpriteBatch spriteBatch;
    private ParticleEffect chestEffect;
    private boolean isClosed = false;
    private Image chestImage;
    private Sound crabRave;
    private int selectedIndex = 0;
    private Image selectorArrow;
    private Label description;
    private Stack[] abilityButtons = new Stack[3];
    private Image[] abilityImages = new Image[3];
    private boolean effectStarted = false;
    private List<AbilityDescription> offeredAbilities = new ArrayList<>();


    public ChestOverlay(GameScreen game) {
        this.game = game;
        this.spriteBatch = new SpriteBatch();
        this.stage = new Stage(new FitViewport(game.getScreenWidth(), game.getScreenHeight()));
        Gdx.input.setInputProcessor(stage);
        crabRave = AssetLoader.get().getSound("sounds/crab_rave.mp3");
        crabRave.setVolume(crabRave.play(), 0.05f);
        createTable();
        createEffect();
        createDescriptionLabel();
        createButtons();

        MusicHandler.pause();
    }

    private void createTable() {
        Table table = new Table();
        //table.setDebug(true);
        table.setFillParent(true);
        table.top().padTop(100);
        BitmapFont font = new BitmapFont();
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);
        Label title = new Label("You opened a chest!", labelStyle);
        title.setFontScale(4f);


        Texture chestTexture = AssetLoader.get().getTexture("entities/icons/chest_open.png");
        Image chestImage = new Image(new TextureRegionDrawable(new TextureRegion(chestTexture)));
        chestImage.setScale(10);
        table.add(chestImage).padTop(480);
        this.chestImage = chestImage;
        table.row();
        table.add(title).colspan(3).center();
        table.row();
        stage.addActor(table);
    }

    private Stack createImageButton(String texturePath, String text, int width, int height, Runnable onClick, int index) {
        Texture texture = AssetLoader.get().getTexture(texturePath);
        Image image = new Image(texture);
        image.setSize(width, height);

        abilityImages[index] = image;

        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label label = new Label(text, labelStyle);
        label.setFontScale(1.5f);
        label.setAlignment(Align.center);

        Stack stack = new Stack();
        stack.setSize(width, height);
        stack.add(image);
        stack.add(label);

        stack.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onClick.run();
                return true;
            }
        });

        return stack;
    }

    private void createDescriptionLabel() {
        BitmapFont font = new BitmapFont();
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        description = new Label("", labelStyle);
        description.setWrap(true);
        description.setAlignment(Align.center);

        description.setWidth(600);
        description.setFontScale(2.2f);

        description.setPosition(
            (stage.getViewport().getWorldWidth() - description.getWidth()) / 2f,
            150
        );
        Pixmap bg = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bg.setColor(0, 0, 0, 0.5f);
        bg.fill();
        Texture bgTex = new Texture(bg);
        Drawable bgDrawable = new Image(bgTex).getDrawable();

        labelStyle.background = bgDrawable;


        stage.addActor(description);
    }



    private void createButtons() {
        Table table = new Table();
        table.setFillParent(true);
        table.bottom().padBottom(220);

        BitmapFont font = new BitmapFont();
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label title = new Label("Select Ability", labelStyle);
        title.setFontScale(2f);
        table.add(title).colspan(3).padBottom(10).row();

        // Hämta slumpade abilities från GameScreen
        offeredAbilities = game.getRandomAvailableAbilities();

        for (int i = 0; i < offeredAbilities.size(); i++) {
            final int choiceIndex = i;
            AbilityDescription desc = offeredAbilities.get(i);
            String buttonText = "[" + (i + 1) + "]\n" + desc.getName();
            abilityButtons[i] = createImageButton("entities/icons/ability_icon.png", buttonText, 200, 120, () -> selectAbility(choiceIndex), i);
            table.add(abilityButtons[i]).size(250, 100).pad(10);
        }


        table.row();
        Texture arrowTexture = AssetLoader.get().getTexture("entities/icons/coin.png");
        selectorArrow = new Image(arrowTexture);
        selectorArrow.setSize(32, 32);
        stage.addActor(table);
        stage.addActor(selectorArrow);
        stage.act(0);
        updateArrowPosition();

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.A:
                    case Input.Keys.LEFT:
                        selectedIndex = (selectedIndex + 2) % offeredAbilities.size();
                        updateArrowPosition();
                        return true;
                    case Input.Keys.D:
                    case Input.Keys.RIGHT:
                        selectedIndex = (selectedIndex + 1) % offeredAbilities.size();
                        updateArrowPosition();
                        return true;
                    case Input.Keys.SPACE:
                    case Input.Keys.ENTER:
                        selectAbility(selectedIndex);
                        return true;
                    case Input.Keys.NUM_1:
                        if (offeredAbilities.size() > 0) selectAbility(0);
                        return true;
                    case Input.Keys.NUM_2:
                        if (offeredAbilities.size() > 1) selectAbility(1);
                        return true;
                    case Input.Keys.NUM_3:
                        if (offeredAbilities.size() > 2) selectAbility(2);
                        return true;
                }
                return false;
            }
        });
    }


    private void updateArrowPosition() {
        for (int i = 0; i < abilityImages.length; i++) {
            Texture texture = AssetLoader.get().getTexture(
                i == selectedIndex
                    ? "entities/icons/ability_icon_selected.png"
                    : "entities/icons/ability_icon.png"
            );
            abilityImages[i].setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
        }

        Stack selected = abilityButtons[selectedIndex];
        selectorArrow.setPosition(
            selected.getX() + selected.getWidth() / 2f - selectorArrow.getWidth() / 2f,
            selected.getY() - selectorArrow.getHeight() - 10
        );
        updateDescription();
    }

    private void updateDescription() {
        if (offeredAbilities == null || selectedIndex >= offeredAbilities.size()) {
            description.setText("No description.");
            return;
        }

        AbilityDescription desc = offeredAbilities.get(selectedIndex);
        description.setText(desc.getDescription() != null ? desc.getDescription() : "No description.");
    }


    private void selectAbility(int index) {
        if (index < offeredAbilities.size()) {
            AbilityDescription desc = offeredAbilities.get(index);
            game.addOrUpgradeAbility(desc);
        }
        isClosed = true;
        MusicHandler.resume();
        Gdx.input.setInputProcessor(game.getGameUI().getStage());
    }


    private void createEffect() {
        chestEffect = new ParticleEffect();
        chestEffect.load(
            Gdx.files.internal("entities/particles/chestOpen.p"),
            Gdx.files.internal("entities/particles")
        );
        chestEffect.setPosition(chestImage.getX(), chestImage.getY());

        chestEffect.start();
    }

    public void update(float delta) {
        if (isClosed) return;

        if (!effectStarted && chestImage.getX() > 0) {
            chestEffect.setPosition(
                chestImage.getX() + chestImage.getWidth() / 2f,
                chestImage.getY() + chestImage.getHeight() / 2f
            );
            chestEffect.start();
            effectStarted = true;
        }

        if (effectStarted) {
            chestEffect.setPosition(
                chestImage.getX() + chestImage.getWidth() / 2f,
                chestImage.getY() + chestImage.getHeight() / 2f
            );
            chestEffect.update(delta);
        }
        stage.act(delta);
    }


    public void draw() {
        if (isClosed) return;

        spriteBatch.setProjectionMatrix(stage.getCamera().combined);

        spriteBatch.begin();
        spriteBatch.setColor(0, 0, 0, 0.4f);
        spriteBatch.draw(
            AssetLoader.get().getTexture("entities/icons/blank.png"),
            0, 0,
            stage.getViewport().getWorldWidth(),
            stage.getViewport().getWorldHeight()
        );
        spriteBatch.setColor(1, 1, 1, 1);
        spriteBatch.end();

        stage.draw();

        spriteBatch.begin();
        chestEffect.draw(spriteBatch);
        spriteBatch.end();
    }


    public boolean isClosed() {
        return isClosed;
    }

    public void dispose() {
        crabRave.stop();
        chestEffect.dispose();
        spriteBatch.dispose();
        stage.dispose();
    }
}
