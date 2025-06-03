package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.model.abilities.Ability;
import com.beachsurvivors.model.abilities.Shield;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.groundItems.PowerUp;

import java.util.HashMap;
import java.util.Map;

public class GameUI {
    private final FitViewport viewport;
    private final GameScreen game;
    private final Stage stage;
    private ProgressBar progressBar;
    private ProgressBar healthBar;
    private ProgressBar shieldBar;
    private Label percentageLabel;
    private Label nextLevel;
    private Label currentLevel;
    private Table abilityTable;
    private Array<String> infoLog;
    private Array<Label> infoLabels;
    private BitmapFont levelFont;


    private ProgressBar bossHealthBar;
    private Label bossHealthLabel;
    private Table bossHealthTable;

    private Table progressBarTable;
    private Table healthTable;
    private Table xpTable;

    private Table statsTable;
    private Label healthPoints;
    private Label damage;
    private Label critChance;
    private Label critDamage;
    private Label cooldownReduction;
    private Label movementSpeed;
    private Label hpRegen;
    private Label areaRadius;
    private Label lifeSteal;

    private Array<Image> equippedAbilitiesIcons;
    private Map<String, Label> abilityCountLabels = new HashMap<>();
    private Map<String, Integer> abilityCounts = new HashMap<>();

    private Stack abilityBarStack;
    private Table icons;
    private final int ICON_SIZE = 64;

    private Array<PowerUp> currentPlayerBuffs;
    private Table buffs;
    private Table buffIcons;
    private Stack buffStack;


    private Label timerLabel;
    private float gameTime = 0f;

    public GameUI(FitViewport viewport, GameScreen game) {

        this.viewport = viewport;
        this.game = game;
        stage = new Stage(viewport);
        equippedAbilitiesIcons = new Array<>();
        currentPlayerBuffs = new Array<>();
        icons = new Table();

        addAbilityIcon("entities/abilities/bullet.png");
        createTables();
    }

    /**
     * Creates all UI-components and places them on the stage (screen).
     */
    private void createTables() {
        createAbilityTable();
        createExpTable();
        createProgressBar();
        createPlayerHealthBar();
        createTimerLabel();
        createInfoTable();
        createPlayerStats();
        createBuffBar();
        createFpsLabel();
        createBossHealthBar();

        addActors();
    }

    private void addActors(){
        stage.addActor(healthTable);

        stage.addActor(timerLabel);
        stage.addActor(abilityBarStack);
        stage.addActor(xpTable);
        stage.addActor(buffStack);
        stage.addActor(progressBar);
        stage.addActor(currentLevel);
        stage.addActor(nextLevel);
    }

    private void createBossHealthBar() {
        Skin bossSkin = new Skin(Gdx.files.internal("skin_composer/healthbutton.json"));

        bossHealthBar = new ProgressBar(0, 100, 1f, false, bossSkin);
        bossHealthBar.setValue(100);
        bossHealthBar.setAnimateDuration(0.25f);
        bossHealthBar.setSize(600, 30);

        BitmapFont bossFont = new BitmapFont();
        bossFont.getData().setScale(2f);
        Label.LabelStyle bossLabelStyle = new Label.LabelStyle(bossFont, Color.RED);
        bossHealthLabel = new Label("BOSS HP", bossLabelStyle);
        bossHealthLabel.setAlignment(Align.center);

        bossHealthTable = new Table();
        bossHealthTable.setFillParent(false);
        bossHealthTable.setSize(650, 60);
        bossHealthTable.top().center();
        bossHealthTable.setPosition(viewport.getWorldWidth() / 2f - 325, viewport.getWorldHeight() - 150);

        bossHealthTable.add(bossHealthLabel).padBottom(10).row();
        bossHealthTable.add(bossHealthBar).width(600).height(30);

        stage.addActor(bossHealthTable);
    }

    private void createAbilityTable() {
        abilityBarStack = new Stack();
        abilityTable = new Table();

        Texture abilityBar = AssetLoader.get().getTexture("entities/ui/ability_bar.png");

        Image abilityBackground = new Image(abilityBar);
        abilityTable.add(abilityBackground);
        abilityTable.bottom();
        abilityTable.center();
        abilityTable.pack();

        abilityTable.setPosition(((viewport.getWorldWidth()/2 - abilityTable.getWidth() / 2)), 0);

        createAbilityIconsTable();
        updateAbilityBar();
    }

    private void createAbilityIconsTable() {
        icons.setSize(600,90);
        icons.align(Align.bottomLeft);
        icons.bottom();
        icons.setPosition(abilityTable.getX(), abilityTable.getY());

        int bottomPad = 5;
        int rightPad = 5;
        icons.add(equippedAbilitiesIcons.get(0)).padLeft(25).padBottom(bottomPad).padRight(rightPad);


        abilityBarStack.add(abilityTable);
        abilityBarStack.add(icons);
        abilityBarStack.setSize(600,90);

        abilityBarStack.setPosition(((viewport.getWorldWidth()/2) - abilityTable.getWidth()/2), 0);
    }

    public void addAbilityIcon(String imagePath) {
        if (!abilityCounts.containsKey(imagePath)) {
            Texture texture = AssetLoader.get().getTexture(imagePath);
            Image icon = new Image(texture);
            icon.setSize(ICON_SIZE, ICON_SIZE);

            Stack stack = new Stack();
            stack.add(icon);

            Label label = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
            label.setFontScale(1.4f);
            label.setAlignment(Align.bottomLeft);
            label.setVisible(false);

            stack.add(label);
            icons.add(stack).padLeft(25).padBottom(5).padRight(5).size(ICON_SIZE);

            abilityCountLabels.put(imagePath, label);
            abilityCounts.put(imagePath, 1);
            equippedAbilitiesIcons.add(icon);
        } else {
            int count = abilityCounts.get(imagePath) + 1;
            abilityCounts.put(imagePath, count);

            Label label = abilityCountLabels.get(imagePath);
            if (count > 1) {
                label.setText(String.valueOf(count));
                label.setVisible(true);
            }
        }
    }


    public void updateBossHealth(float current, float max) {
        bossHealthBar.setRange(0, max);
        bossHealthBar.setValue(current);
    }

    public void setBossBarVisible(boolean visible) {
        bossHealthTable.setVisible(visible);
    }

    private void updateAbilityBar() {
        int bottomPad = 5;
        int rightPad = 5;
        icons.clear();
        icons.add(equippedAbilitiesIcons.get(0)).padLeft(25).padBottom(bottomPad).padRight(rightPad).size(ICON_SIZE);
        for (int i = 1; i < equippedAbilitiesIcons.size; i++) {
            icons.add(equippedAbilitiesIcons.get(i)).padBottom(bottomPad).padRight(rightPad).size(ICON_SIZE);
        }
    }

    private void createBuffBar() {
        buffStack = new Stack();
        buffStack.setSize(200, 90);
        buffs = new Table();
        buffs.setSize(200,90);
        buffIcons = new Table();
        buffIcons.setSize(200,90);
        buffIcons.align(Align.bottomLeft);

        buffStack.add(buffs);
        buffStack.add(buffIcons);
        buffStack.setPosition(((viewport.getWorldWidth()/2) - abilityTable.getWidth()/2), 85);

    }

    public void addBuff(PowerUp buff) {
        currentPlayerBuffs.add(buff);
        updateBuffIcons();
    }

    public void removeBuff(PowerUp buff) {
        currentPlayerBuffs.removeValue(buff, true);
        updateBuffIcons();
    }

    public void updateBuffIcons() {
        int bottomPad = 10;
        int rightPad = 5;
        buffIcons.clear();

        Skin skin = AssetLoader.get().getSkin("game_over_screen/deathscreen_skin.json");

        for (int i = 0; i < currentPlayerBuffs.size; i++) {
            PowerUp buff = currentPlayerBuffs.get(i);

            Image icon = buff.getIcon();
            float remainingTime = Math.max(0, buff.getRemainingDuration());
            Label timerLabel = new Label(String.format("%.1f", remainingTime), skin);
            timerLabel.setFontScale(1.3f);
            timerLabel.setAlignment(Align.center);

            Stack iconStack = new Stack();
            iconStack.add(icon);
            iconStack.add(timerLabel);

            Cell<Stack> cell = buffIcons.add(iconStack).padBottom(bottomPad).padRight(rightPad).size(ICON_SIZE);
            if (i == 0) cell.padLeft(25);

        }
    }

    private void createExpTable() {
        this.xpTable = new Table();
        Texture xpTexture = new Texture(Gdx.files.internal("entities/abilities/exp_bar.png"));
        Image xpBar = new Image(xpTexture);
        xpBar.setScale(1.5f);
        xpBar.setSize(400,70);
        xpTable.add(xpBar);
        xpTable.pack();
        xpTable.setPosition(
            ((viewport.getWorldWidth() - xpTable.getWidth()*1.5f) / 2), viewport.getWorldHeight()-xpTable.getHeight()*1.5f
        );
    }


    private void createInfoTable() {
        infoLog = new Array<>();
        infoLabels = new Array<>();

        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.5f);

        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 0, 0, 0.5f);
        bgPixmap.fill();
        Texture bgTexture = new Texture(bgPixmap);
        Drawable bgDrawable = new Image(bgTexture).getDrawable();

        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        style.background = bgDrawable;

        float startY = 150;
        float spacing = 25;

        for (int i = 0; i < 5; i++) {
            Label label = new Label("", style);
            label.setPosition(40, startY - i * spacing);
            infoLabels.add(label);
            stage.addActor(label);
        }

        updateInfoTable("Welcome to Beach Survivors, try not to die");
    }

    public void updateInfoTable(String logMessage) {
        if (infoLog.size == 5) {
            infoLog.removeIndex(0);
        }

        int minutes = (int)(gameTime / 60f);
        int seconds = (int)(gameTime % 60f);
        String timestamp = String.format("[%02d:%02d] ", minutes, seconds);

        infoLog.add(timestamp + logMessage);

        for (int i = 0; i < infoLabels.size; i++) {
            if (i < infoLog.size) {
                infoLabels.get(i).setText(infoLog.get(i));
            } else {
                infoLabels.get(i).setText("");
            }
        }
    }

    public void updateInfoTable_color(String logMessage, Color color) {
        if (infoLog.size == 5) {
            infoLog.removeIndex(0);
        }

        int minutes = (int)(gameTime / 60f);
        int seconds = (int)(gameTime % 60f);
        String timestamp = String.format("[%02d:%02d] ", minutes, seconds);

        infoLog.add(timestamp + logMessage);

        for (int i = 0; i < infoLabels.size; i++) {
            if (i < infoLog.size) {
                infoLabels.get(i).setText(infoLog.get(i));
                if (i == infoLog.size - 1) {
                    // Color only the latest message
                    infoLabels.get(i).setColor(color);
                } else {
                    // Reset older messages to default color (e.g., white)
                    infoLabels.get(i).setColor(Color.WHITE);
                }
            } else {
                infoLabels.get(i).setText("");
            }
        }
    }



    private void createTimerLabel() {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2);

        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 0, 0, 0.5f);
        bgPixmap.fill();
        Texture bgTexture = new Texture(bgPixmap);
        Drawable bgDrawable = new Image(bgTexture).getDrawable();

        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        style.background = bgDrawable;

        timerLabel = new Label("00:00", style);
        timerLabel.setPosition(30, viewport.getWorldHeight() - 50);
        stage.addActor(timerLabel);
    }


    private void createPlayerHealthBar() {
        Skin healthSkin = new Skin(Gdx.files.internal("skin_composer/healthbutton.json"));

        // Skapa shield-baren
        shieldBar = new ProgressBar(0, 100, 0.5f, false, healthSkin, "shield");
        shieldBar.setSize(100, 50);

        // Skapa HP-baren
        healthBar = new ProgressBar(0, 100, 0.5f, false, healthSkin);
        healthBar.setSize(100, 50);

        percentageLabel = new Label("100%", healthSkin);
        percentageLabel.setColor(Color.BLACK);

        // Lägg båda i en gemensam table
        healthTable = new Table();

        healthTable.align(Align.center);

        // Lägg till shieldBar överst


        // Lägg till healthBar och HP-text
        healthTable.add(shieldBar).width(100).height(10).row();
        healthTable.add(healthBar).width(100).height(10);

        healthTable.add(percentageLabel).padLeft(10);

        // Placera tabellen på skärmen
        healthTable.setPosition(
            viewport.getWorldWidth() / 2 - healthTable.getWidth() / 2,
            viewport.getWorldHeight() / 2 - healthTable.getHeight() / 2 + 100
        );
    }


    public Stage getStage() {
        return stage;
    }

    public void setProgressBarValue(float value) {
        progressBar.setValue(value);
    }

    public void setHealthBarValue(Player player) {
        float currentHp = player.getCurrentHealthPoints();
        float maxHp = player.getMaxHealthPoints();
        healthBar.setRange(0, maxHp);
        healthBar.setValue(currentHp);
        percentageLabel.setText((int)currentHp + " / " + (int)maxHp);

        Shield shield = null;
        for (Ability ability : game.getAbilities()) {
            if (ability instanceof Shield) {
                shield = (Shield) ability;
                break;
            }
        }

        if (shield != null) {
            float currentShield = shield.getCurrentShieldStrength();
            float maxShield = shield.getInitialShieldStrength();
            shieldBar.setRange(0, maxShield);
            shieldBar.setValue(currentShield);
        } else {
            shieldBar.setValue(0);
        }
    }




    public void createProgressBar() {
        Skin skin = new Skin(Gdx.files.internal("skin_composer/testbuttons.json"));

        progressBar = new ProgressBar(0, 100, 0.5f, false, skin);
        progressBar.setValue(0);
        progressBar.setSize(550, 70);

        levelFont = new BitmapFont(Gdx.files.internal("fonts/level.fnt"));
        levelFont.setColor(Color.WHITE);
        Label.LabelStyle labelStyle = new Label.LabelStyle(levelFont, Color.WHITE);


        currentLevel = new Label("Level: 1", labelStyle);
        nextLevel = new Label("2", labelStyle);


        float centerX = viewport.getWorldWidth() / 2f;
        float topY = xpTable.getY() - 2;

        currentLevel.setPosition(centerX - 450, topY + 20);   // vänster sida
        progressBar.setPosition(centerX - 272, topY);         // mitten
        nextLevel.setPosition(centerX + 330, topY + 20);       // höger sida
    }


    private void createPlayerStats() {
        float fontScale = 1.2f;
        Skin skin = AssetLoader.get().manager.get("game_over_screen/deathscreen_skin.json");
        statsTable = new Table();
        statsTable.setSize(600,400);

        healthPoints = new Label("HealthPoints" , skin, "stats");
        statsTable.add(healthPoints).left().row();
        healthPoints.setFontScale(fontScale);

        hpRegen = new Label("HP Regen" , skin, "stats");
        statsTable.add(hpRegen).left().row();
        hpRegen.setFontScale(fontScale);

        damage = new Label("Base Damage" , skin, "stats");
        statsTable.add(damage).left().row();
        damage.setFontScale(fontScale);

        critChance = new Label("Critical Chance" ,skin, "stats");
        statsTable.add(critChance).left().row();
        critChance.setFontScale(fontScale);

        critDamage = new Label("Critical Damage" , skin, "stats");
        statsTable.add(critDamage).left().row();
        critDamage.setFontScale(fontScale);

        cooldownReduction = new Label("Cooldown Reduction" , skin, "stats");
        statsTable.add(cooldownReduction).left().row();
        cooldownReduction.setFontScale(fontScale);

        movementSpeed = new Label("Movement Speed" , skin, "stats");
        statsTable.add(movementSpeed).left().row();
        movementSpeed.setFontScale(fontScale);

        areaRadius = new Label("Area" , skin, "stats");
        statsTable.add(areaRadius).left().row();
        areaRadius.setFontScale(fontScale);

        lifeSteal = new Label("Life Steal" , skin, "stats");
        statsTable.add(lifeSteal).left().row();
        lifeSteal.setFontScale(fontScale);

        statsTable.setPosition(-80, game.getScreenHeight()/2f);
        stage.addActor(statsTable);
        statsTable.setVisible(false);

    }


    public void updateStats(Player player) {
        healthPoints.setText("Health Points " + String.format("%.1f",player.getCurrentHealthPoints()) + "/" + String.format("%.0f", player.getMaxHealthPoints()));
        hpRegen.setText("HP / Second: " + player.getHpRegenPerSecond());
        damage.setText("Base Damage: " + player.getDamage());
        critChance.setText("CritHit Chance: " + String.format("%.0f", player.getCriticalHitChance()*100) + "%");
        critDamage.setText("CritHit Damage: " + String.format("%.0f", player.getCriticalHitDamage()*100) + "%");
        cooldownReduction.setText("Cooldown Time: " + String.format("%.1f", player.getCooldownTime()*100) + "%");
        movementSpeed.setText("Movement Speed: " + player.getSpeed());
        areaRadius.setText("Area: " + player.getAreaRange());
        lifeSteal.setText("Life Steal: " + player.getLifesteal());
    }

    public void showStatsTable() {
        statsTable.setVisible(!statsTable.isVisible());
    }

    Label fps;

    private void createFpsLabel() {
        Skin skin = AssetLoader.get().manager.get("game_over_screen/deathscreen_skin.json");
        String fpsText = "FPS: " + Gdx.graphics.getFramesPerSecond();
        fps = new Label(fpsText, skin, "stats");
        fps.setPosition(1800,1000);
        stage.addActor(fps);
    }

    public void updateFpsLabel(String fpsText) {
        fps.setText(fpsText);
    }


    private void updateLevelLabels() {
        if(game.getPlayer()!=null){
        currentLevel.setText("Level: " + game.getPlayer().getLevelSystem().getCurrentLevel());
        nextLevel.setText(game.getPlayer().getLevelSystem().getCurrentLevel() + 1);}
    }

    public void update(float deltaTime) {
        gameTime += deltaTime;

        int minutes = (int) (gameTime / 60f);
        int seconds = (int) (gameTime % 60f);

        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        updateLevelLabels();

        stage.act(deltaTime);
    }

    public void updateXpBar() {
        if (game.getPlayer() == null) return;

        int currentExp = game.getPlayer().getLevelSystem().getCurrentExp();
        int expToNext = game.getPlayer().getLevelSystem().getExpToNextLevel();

        progressBar.setRange(0, expToNext);
        progressBar.setValue(currentExp);

        currentLevel.setText("Level: " + game.getPlayer().getLevel());
        nextLevel.setText(String.valueOf(game.getPlayer().getLevel() + 1));
    }


    public void draw() {
        stage.draw();
    }

    public float getGameTimeSeconds() {
        return gameTime;
    }
}
