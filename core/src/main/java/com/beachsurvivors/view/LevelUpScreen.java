package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.controller.Controller;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.abilities.Boomerang;

public class LevelUpScreen implements Screen {
    private Controller controller;
    private Stage stage;
    private GameScreen game;
    private Skin skin;
    private Player player;

    public LevelUpScreen(GameScreen game, Player player, Controller controller) {
        this.game = game;
        this.player = player;
        this.controller = controller;

        stage = new Stage(new FitViewport(game.getScreenWidth(), game.getScreenHeight()));
        Gdx.input.setInputProcessor(stage);

        skin = AssetLoader.get().getSkin("level_up_screen/uiskin.json");

        buildUI();
    }

    private void buildUI() {
        Table table = new Table();
//        table.setDebug(true);

        table.setBackground(skin.getDrawable("textfield"));

        Label title = new Label("You've reached level "+ player.getLevel() + "!", skin);
        TextButton upgrade1 = new TextButton("+1 BOOMERANG", skin);
        TextButton upgrade2 = new TextButton("25% INCREASED MOVEMENT SPEED", skin);
        TextButton upgrade3 = new TextButton("+10% CRIT CHANCE", skin);
        TextButton skipButton = new TextButton("Skip", skin);

        title.setFontScale(2f);
        table.add(title).padBottom(40).colspan(3).center();
        table.row();
        table.add(upgrade1).pad(10);
        table.add(upgrade2).pad(10);
        table.add(upgrade3).pad(10);
        table.row();
        table.add(skipButton).padTop(40).colspan(3).center();

        table.pack();
        table.setPosition(
            (stage.getWidth() - table.getWidth()) / 2f,
            (stage.getHeight() - table.getHeight()) / 2f
        );

        stage.addActor(table);

        // Button logic
        upgrade1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                applyUpgrade();
                resumeGame();
            }
        });

        upgrade2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                player.increaseSpeed((int)((player.getSpeed()/100)*25));
                resumeGame();
            }
        });

        upgrade3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                player.increaseCritChance(player.getCriticalHitChance()+0.1f);
                resumeGame();
            }
        });

        skipButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resumeGame();
            }
        });
    }

    private void applyUpgrade() {
        controller.getAbilities().add(new Boomerang());
    }

    private void resumeGame() {
        game.resume();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
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
