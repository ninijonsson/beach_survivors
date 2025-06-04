package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.beachsurvivors.utilities.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.view.DamageText;

public class WaterWave extends Ability {

    private final Texture crosshairTexture;
    private ParticleEffect effect;
    private Vector2 position;
    private Vector2 direction;
    private Vector2 startPosition;
    private float maxDistance = 600f;
    private float speed = 750f;
    private float scaleFactor = 1f;
    private float waveAmplitude = 10f;
    private float waveFrequency = 10f;
    private float totalDistanceTraveled = 0f;
    private TextureRegion crosshairRegion;
    private Vector2 crosshairPosition;
    private float crosshairDistance = 180f;
    private final ObjectMap<Enemy, Float> damageCooldowns = new ObjectMap<>();
    float angleDegrees = 0; // eller player.getLastDirection().angleDeg()
    private Rectangle hitBox = new Rectangle();
    private boolean isFinished = false;




    public WaterWave(String name, double damage, float cooldown, int width, int height,
                     Vector2 startPosition, ParticleEffectPoolManager poolManager) {
        super(name, "entities/particles/bullet.png", AbilityType.ATTACK, damage, cooldown, width, height);
        this.position = new Vector2(startPosition);
        this.startPosition = new Vector2(startPosition);
        this.effect = poolManager.obtain("entities/particles/blueFlame.p", position.x, position.y);
        effect.scaleEffect(1.0f);
        crosshairTexture = AssetLoader.get().manager.get("entities/aim.png", Texture.class);
        crosshairRegion = new TextureRegion(crosshairTexture);
    }

    public void setDirection(Vector2 dir) {
        this.direction = new Vector2(dir).nor();
        if (crosshairPosition == null) {
            crosshairPosition = new Vector2();
        }
        crosshairPosition.set(position).add(direction.cpy().scl(crosshairDistance));
    }

    @Override
    public Rectangle getHitBox() {
        if (hitBox == null) hitBox = new Rectangle();
        hitBox.set(position.x - getWidth()/2f, position.y - getHeight()/2f, getWidth(), getHeight());
        return hitBox;
    }


    public void draw(SpriteBatch batch) {
        if (effect != null) {
            effect.draw(batch);
        }
        if (crosshairTexture != null && crosshairPosition != null) {
            batch.draw(
                crosshairRegion,
                crosshairPosition.x - 46, crosshairPosition.y - 17,
                46, 17,
                92, 34,
                1, 1,
                angleDegrees
            );

        }
    }

    public boolean isComplete() {
        return isFinished && (effect == null || effect.isComplete());
    }

    @Override
    public void updatePosition(float delta, Vector2 position) {

    }



    @Override
    public void update(float delta, Player player, Array<Enemy> enemies, Array<Ability> abilities) {
        if (effect == null || direction == null) return;
        float dx = direction.x * speed * delta;
        float dy = direction.y * speed * delta;

        totalDistanceTraveled += Math.sqrt(dx * dx + dy * dy);

        float offset = (float)Math.sin(totalDistanceTraveled / waveFrequency) * waveAmplitude;
        Vector2 normal = new Vector2(-direction.y, direction.x);

        for (Enemy e : damageCooldowns.keys()) {
            float time = damageCooldowns.get(e);
            damageCooldowns.put(e, time - delta);
        }

        position.add(dx, dy);
        position.add(normal.scl(offset * delta));
        effect.setPosition(position.x, position.y);
        effect.update(delta);

        float distance = position.dst(startPosition);
        scaleFactor = scaleFactor + (distance / maxDistance) * 0.5f;

        if (distance >= maxDistance) {
            effect.allowCompletion();
            isFinished = true; // 🔥 markera den som färdig
        }

        // ✅ Uppdatera crosshairPosition baserat på spelarens senaste direction
        Vector2 dir = player.getLastDirection();
        if (!dir.isZero()) {
            crosshairPosition.set(player.getPosition()).add(dir.cpy().scl(crosshairDistance));
        } else {
            crosshairPosition.set(player.getPosition()); // fallback mitt på spelaren
        }

        if (!dir.isZero()) {
            angleDegrees = dir.angleDeg()-90;
        }
    }

    @Override
    public void use(float delta, Player player, Array<Enemy> enemies, Array<Ability> abilities, Array<DamageText> damageTexts, Array<Projectile> playerProjectiles) {
    }

    public void upgrade(){
        maxDistance = maxDistance+100;
    }

    @Override
    public void dispose() {
        if (effect != null) {
            effect.dispose();
        }
    }
}
