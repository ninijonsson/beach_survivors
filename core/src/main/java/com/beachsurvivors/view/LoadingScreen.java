package com.beachsurvivors.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.beachsurvivors.AssetLoader;

public class LoadingScreen implements Screen {
    private final Main game;
    private final boolean goToGameAfterLoad;
    private SpriteBatch batch;
    private BitmapFont font;

    public LoadingScreen(Main game, boolean goToGameAfterLoad) {
        this.game = game;
        this.goToGameAfterLoad = goToGameAfterLoad;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        AssetLoader.get(); // Registrera resurser om inte redan
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (AssetLoader.get().manager.update()) {
            if (goToGameAfterLoad) {
                game.startGame();
            } else {
                game.setScreen(new MainMenuScreen(game));
            }
        }
        else {
            float progress = AssetLoader.get().manager.getProgress();
            batch.begin();
            font.draw(batch, "Laddar: " + (int)(progress * 100) + "%", 100, 150);
            batch.end();
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        batch.dispose();
        font.dispose();
    }
}

