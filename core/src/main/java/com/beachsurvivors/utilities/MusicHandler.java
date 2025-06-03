package com.beachsurvivors.utilities;

import com.badlogic.gdx.audio.Music;

public class MusicHandler {

    private static Music currentMusic;
    private static float currentVolume = 0.1f;

    public static void play(String path, boolean loop) {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose(); // eller spara för återanvändning om du vill
        }
        currentMusic = AssetLoader.get().manager.get(path, Music.class);
        currentMusic.setLooping(loop);
        currentMusic.setVolume(currentVolume);
        currentMusic.play();
    }

    public static void setVolume(float volume) {
        currentVolume = volume;
        if (currentMusic != null) {
            currentMusic.setVolume(volume);
        }
    }

    public static void pause() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    public static void resume() {
        if (currentMusic != null) {
            currentMusic.play();
        }
    }

    public static void stop() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public static boolean isPlaying() {
        return currentMusic != null && currentMusic.isPlaying();
    }
}
