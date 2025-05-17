package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.Upgrade;
import com.beachsurvivors.model.abilities.UpgradeType;

import java.util.Random;

public class LevelUpScreen implements Screen {

    private Stage stage;
    private GameScreen game;
    private Skin skin;
    private Player player;
    private Random random;

    private TextButton upgradeButton1;
    private TextButton upgradeButton2;
    private TextButton upgradeButton3;
    private ImageButton skipButton;

    private Upgrade upgrade1;
    private Upgrade upgrade2;
    private Upgrade upgrade3;

    private Sound levelUpSound;

    public LevelUpScreen(GameScreen game, Player player) {
        this.game = game;
        this.player = player;
        random = new Random();

        stage = new Stage(new FitViewport(game.getScreenWidth(), game.getScreenHeight()));
        Gdx.input.setInputProcessor(stage);

        skin = AssetLoader.get().getSkin("game_over_screen/deathscreen_skin.json");
        this.levelUpSound = AssetLoader.get().getSound("sounds/level_up.mp3");
        //levelUpSound.setVolume(levelUpSound.play(),0.1f);

        buildUI();
    }

    private void buildUI() {
        Table table = new Table();

        Label title = new Label("You've reached level "+ player.getLevel() + "!", skin, "pixel2");
        Label choose = new Label("Select an upgrade!", skin, "pixel2");
        upgradeButton1 = new TextButton("", skin, "levelup2");
        upgradeButton2 = new TextButton("", skin, "levelup2");
        upgradeButton3 = new TextButton("", skin, "levelup2");
        skipButton = new ImageButton(skin, "skip");

        generateUpgrades();
        updateLabel();

        title.setFontScale(2f);
        table.add(title).padBottom(20).colspan(3).center().row();
        table.add(choose).padBottom(30).colspan(3).center().row();
        table.add(upgradeButton1).pad(10).width(500).height(300);
        table.add(upgradeButton2).pad(10).width(500).height(300);
        table.add(upgradeButton3).pad(10).width(500).height(300);
        table.row();
        table.add(skipButton).padTop(40).colspan(3).center().padBottom(20);

        table.pack();
        table.setPosition(
            (stage.getWidth() - table.getWidth()) / 2f,
            (stage.getHeight() - table.getHeight()) / 2f
        );

        stage.addActor(table);
        addListeners();
    }

    private void addListeners() {
        // Button logic
        upgradeButton1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                applyUpgrade(upgrade1.getType());
                game.printLog("Upgrade chosen: " + upgrade1.getType());
                resumeGame();
            }
        });

        upgradeButton2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                applyUpgrade(upgrade2.getType());
                game.printLog("Upgrade chosen: " + upgrade2.getType());
                resumeGame();
            }
        });

        upgradeButton3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                applyUpgrade(upgrade3.getType());
                game.printLog("Upgrade chosen: " + upgrade3.getType());
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

    private void resumeGame() {
        game.resume();
    }

    private void updateLabel() {
        upgradeButton1.setText(upgrade1.getDescription());
        upgradeButton2.setText(upgrade2.getDescription());
        upgradeButton3.setText(upgrade3.getDescription());
    }

    private void applyUpgrade(UpgradeType type) {
        switch (type) {
            case Health:
                player.increaseMaximumHealthPoints(20);
                break;
            case Speed:
                player.increaseSpeed(100);
                break;
            case Damage:
                player.increaseDamage(10);
                break;
            case CriticalHitChance:
                player.increaseCritChance(0.05f);
                break;
            case CriticalHitDamage:
                player.increaseCritDamage(0.5f);
                break;
            case CooldownReduction:
                player.increaseCooldownReduction(10);

                break;
        }
    }

    private Upgrade getRandomUpgrade() {
        Array<Upgrade> upgrades = new Array<>();

        upgrades.add(new Upgrade(UpgradeType.Health, "Gain +20 to \n maximum health"));
        upgrades.add(new Upgrade(UpgradeType.Speed,"Gain +100 \n movement speed"));
        upgrades.add(new Upgrade(UpgradeType.Damage, "Increases your \n damage by 10"));
        upgrades.add(new Upgrade(UpgradeType.CriticalHitChance, "Increases your \n critical  hit \n chance by 5%"));
        upgrades.add(new Upgrade(UpgradeType.CriticalHitDamage, "Increases your \n critical hit \n damage by 50%"));
        upgrades.add(new Upgrade(UpgradeType.CooldownReduction, "Increases your \n cooldown  reduction \n by 10%"));

        return upgrades.get(random.nextInt(upgrades.size));
    }

    private void generateUpgrades() {
        upgrade1 = getRandomUpgrade();

        do {
            upgrade2 = getRandomUpgrade();
        } while (upgrade2.getType() == upgrade1.getType());

        do {
            upgrade3 = getRandomUpgrade();
        } while (upgrade3.getType() == upgrade1.getType() || upgrade3.getType() == upgrade2.getType());

    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        keyBinds();
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

    public void keyBinds() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            applyUpgrade(upgrade1.getType());
            game.printLog("Upgrade chosen: " + upgrade1.getType());
            resumeGame();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            applyUpgrade(upgrade2.getType());
            game.printLog("Upgrade chosen: " + upgrade2.getType());
            resumeGame();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            applyUpgrade(upgrade3.getType());
            game.printLog("Upgrade chosen: " + upgrade3.getType());
            resumeGame();
        }
    }
}
