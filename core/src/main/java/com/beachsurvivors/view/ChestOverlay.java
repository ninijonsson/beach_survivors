package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.model.Player;

public class ChestOverlay {
    private GameScreen game;
    private Player player;

    private Stage stage;
    private SpriteBatch spriteBatch;
    private ParticleEffect chestEffect;
    private boolean isClosed = false;
    private Image chestImage;

    public ChestOverlay(GameScreen game) {
        this.game = game;
        this.player = game.getPlayer();

        this.spriteBatch = new SpriteBatch();
        this.stage = new Stage(new FitViewport(game.getScreenWidth(), game.getScreenHeight()));
        Gdx.input.setInputProcessor(stage);

        createTable();
        createEffect();
    }

    private void createTable() {
        Table table = new Table();
        table.setFillParent(true);
        //table.setDebug(true);
        BitmapFont font = new BitmapFont();
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);
        Label title = new Label("You found a chest!", labelStyle);
        title.setFontScale(4f);
        table.add(title).padBottom(400).colspan(3).center();
        table.row();

        Texture chestTexture = AssetLoader.get().getTexture("entities/icons/chest_open.png");
        Image chestImage = new Image(new TextureRegionDrawable(new TextureRegion(chestTexture)));
        chestImage.setScale(10);
        table.add(chestImage).padBottom(40);
        table.row();
        this.chestImage = chestImage; // 👈 Spara referensen i ett fält


        for (int i = 0; i < 3; i++) {
            Texture icon = new Texture(Gdx.files.internal("entities/icons/ability_icon.png"));
            TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(icon));
            ImageButton button = new ImageButton(drawable);
            button.getImage().setSize(50, 50);

            final int index = i;
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.getGameUI().updateInfoTable("Du valde ability: " + index);
                    isClosed = true;
                    Gdx.input.setInputProcessor(game.getGameUI().getStage()); // Återställ input till spelet
                }
            });

            table.add(button).size(60).pad(10);
        }

        stage.addActor(table);
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.NUM_1:
                        selectAbility(0);
                        return true;
                    case Input.Keys.NUM_2:
                        selectAbility(1);
                        return true;
                    case Input.Keys.NUM_3:
                        selectAbility(2);
                        return true;
                }
                return false;
            }
        });
    }

    private void selectAbility(int index) {
        game.getGameUI().updateInfoTable("Du valde ability: " + index);
        isClosed = true;
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
        if (!isClosed) {
            chestEffect.setPosition(chestImage.getX(), chestImage.getY());
            chestEffect.update(delta);

            stage.act(delta);

        }
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

        stage.draw(); // först rita UI
        spriteBatch.begin();
        chestEffect.draw(spriteBatch); // sen partikeleffekten ovanpå
        spriteBatch.end();
    }



    public boolean isClosed() {
        return isClosed;
    }

    public void dispose() {
        chestEffect.dispose();
        spriteBatch.dispose();
        stage.dispose();
    }
}
