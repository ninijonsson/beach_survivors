package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.beachsurvivors.AssetLoader;

public class DeathScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private GameScreen gameScreen;
    private Table rightTable;
    private Table leftTable;

//    private TextButton retryButton;
//    private TextButton exitButton;
//    private TextButton mainMenuButton;

    private ImageButton retryButton;
    private ImageButton exitButton;
    private ImageButton mainMenuButton;

    private Label textLabel;
    private Label enemiesKilledText;
    private int enemiesKilled;
    private Label timeSurvivedText;
    private float timeSurvived;
    private Label totalDamageDone;
    private double totalDamage;
    private String timeStamp;


    public DeathScreen(GameScreen gameScreen, int enemiesKilled, double totalDamage, float timeSurvived) {
        this.gameScreen = gameScreen;
        this.enemiesKilled = enemiesKilled;
        this.totalDamage = totalDamage;
        this.timeSurvived = timeSurvived;

        stage = new Stage(new FitViewport(gameScreen.getScreenWidth(), gameScreen.getScreenHeight()));
        Gdx.input.setInputProcessor(stage);

        skin = AssetLoader.get().manager.get("game_over_screen/deathscreen_skin.json");

        int minutes = (int)(timeSurvived / 60f);
        int seconds = (int)(timeSurvived % 60f);
        timeStamp = String.format("[%02d:%02d] ", minutes, seconds);

        createActors();

    }

    public void createActors() {
        rightTable = new Table();

        Texture backgroundTexture = AssetLoader.get().manager.get("assets/game_over_screen/you died screen.png");
        Image background = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));

        Stack stack = new Stack();
        stack.setSize(1200,972);
        stack.setPosition(gameScreen.getScreenWidth()/2f-stack.getWidth()/2,
            gameScreen.getScreenHeight()/2f-stack.getHeight()/2 );
        stack.add(background);
        stack.add(rightTable);
        stage.addActor(stack);

        leftTable = new Table();

        float fontscale = 1.2f;
        enemiesKilledText = new Label("Enemies killed: " + enemiesKilled , skin);
        enemiesKilledText.setFontScale(fontscale);

        timeSurvivedText = new Label("Time survived: " + timeStamp, skin);
        timeSurvivedText.setFontScale(fontscale);
        totalDamageDone = new Label("Total damage done: " + totalDamage, skin);
        totalDamageDone.setFontScale(fontscale);

        skin.getFont("Silkscreen-Regular").getData().setScale(fontscale);

        retryButton = new ImageButton(skin, "retry");
        exitButton = new ImageButton(skin, "exit");
        mainMenuButton = new ImageButton(skin, "menu");

        //Grow() för att bilden ska fylla ut knapparna
        retryButton.getImageCell().grow();
        mainMenuButton.getImageCell().grow();
        exitButton.getImageCell().grow();

        addActorsRightTable();
        addActorsLeftTable();
        addListeners();
    }

    private void addActorsRightTable() {
        int width = 333;
        int height = 83;

        rightTable.add(textLabel).padBottom(30);
        rightTable.row();
        rightTable.add(retryButton).width(width).height(height).padBottom(20);
        rightTable.row();
        rightTable.add(mainMenuButton).width(width).height(height).padBottom(20);
        rightTable.row();
        rightTable.add(exitButton).width(width).height(height);

        rightTable.setPosition(gameScreen.getScreenWidth()*0.65f, gameScreen.getScreenHeight()/2.5f);
        stage.addActor(rightTable);

    }

    private void addActorsLeftTable() {
        leftTable.add(enemiesKilledText).padBottom(10).left();
        leftTable.row();
        leftTable.add(timeSurvivedText).padBottom(10).left();
        leftTable.row();
        leftTable.add(totalDamageDone).left();

        leftTable.setPosition(gameScreen.getScreenWidth()-1150, gameScreen.getScreenHeight()/2f);
        stage.addActor(leftTable);
    }

    public void addListeners() {

        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("Main menu");
                gameScreen.getMain().goToMainMenu();
            }
        });

        retryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("Retry");
                gameScreen.getMain().restart();
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("Exit");
                Gdx.app.exit();
            }
        });

    }


    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
