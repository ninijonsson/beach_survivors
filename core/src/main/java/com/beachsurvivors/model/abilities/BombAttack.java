package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.beachsurvivors.AssetLoader;
//import com.badlogic.gdx.graphics.g2d.ParticleEffect;
//import com.badlogic.gdx.audio.Sound;

public class BombAttack {

    private final Sound fallSound;
    private float stateTime = 0f;
    private Vector2 targetPosition;
    //private Texture ringTexture;
    private Texture bombTexture;
    private Texture shadowTexture;
    private float shadowAlpha = 0f;
    private float shadowScale = 0.5f;

    private Sound explosionSound;
    private ParticleEffect explosionEffect;
    private ParticleEffect radiusEffect;

    private boolean hasExploded = false;
    private boolean bombStartedFalling = false;

    private float bombY;
    private float ringScale = 1.0f;

    public BombAttack(Vector2 targetPosition) {
        this.targetPosition = targetPosition;
        //this.ringTexture = AssetLoader.get().manager.get("entities/abilities/bomb_ring.png", Texture.class);
        this.bombTexture = AssetLoader.get().manager.get("entities/abilities/bomb.png", Texture.class);
        this.shadowTexture = AssetLoader.get().manager.get("entities/abilities/bomb_shadow.png", Texture.class);
        this.explosionSound = AssetLoader.get().manager.get("entities/abilities/bomb_explosion.mp3", Sound.class);
        this.explosionEffect = AssetLoader.get().manager.get("entities/particles/bomb_explosion.p");
        this.fallSound = AssetLoader.get().manager.get("entities/abilities/bomb_fall.wav", Sound.class);
        this.radiusEffect = AssetLoader.get().manager.get("entities/particles/impact_zone.p");
        this.radiusEffect = new ParticleEffect(radiusEffect); // kopia så den kan spelas separat
        this.radiusEffect.setPosition(targetPosition.x, targetPosition.y);
        this.radiusEffect.start();

        this.bombY = targetPosition.y + 1000f;
    }

    public void update(float delta) {
        stateTime += delta;
        radiusEffect.update(delta);


        if (stateTime < 2f) {
            ringScale = 1f;
        } else if (stateTime < 4f) {
            ringScale = Math.max(0.2f, 1f - ((stateTime - 2f) / 2f));
        } else if (!bombStartedFalling) {
            fallSound.setVolume(fallSound.play(), 0.2f);
            bombStartedFalling = true;
        }  else if (stateTime < 8f) {

            float t = MathUtils.clamp((stateTime - 4f)/4f, 0f, 1f);
            float ease = t * t; // accelererar gradvis
            bombY = MathUtils.lerp(targetPosition.y + 1000f, targetPosition.y, ease);
            shadowAlpha = MathUtils.clamp((stateTime - 4f) / 4f, 0f, 1f);
            shadowScale = MathUtils.lerp(0.5f, 1.2f, shadowAlpha);

        } else if (!hasExploded) {
            explosionEffect.setPosition(targetPosition.x, targetPosition.y);
            explosionEffect.start();
            this.fallSound.stop();
            explosionSound.play();

            hasExploded = true;
        }

        explosionEffect.update(delta*0.5f);
    }

    public void draw(SpriteBatch batch) {
        radiusEffect.draw(batch);

        if (stateTime >= 4f && stateTime < 8f) {
            // Rita skuggan först
            batch.setColor(1f, 1f, 1f, shadowAlpha);
            float shadowSize = 96f * shadowScale;
            batch.draw(shadowTexture, targetPosition.x - shadowSize / 2, targetPosition.y - shadowSize / 2, shadowSize, shadowSize);
            batch.setColor(1f, 1f, 1f, 1f); // återställ färgen

            // Rita bomben
            batch.draw(bombTexture, targetPosition.x - 48, bombY, 96, 96);
        }


        explosionEffect.draw(batch);
    }

    public boolean isFinished() {
        return stateTime > 9f && explosionEffect.isComplete();
    }

    public void dispose() {
        explosionEffect = null;
    }
}
