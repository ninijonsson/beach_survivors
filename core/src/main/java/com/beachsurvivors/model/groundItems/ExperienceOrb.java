package com.beachsurvivors.model.groundItems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.beachsurvivors.AssetLoader;
import com.beachsurvivors.utilities.ParticleEffectPoolManager;
import com.beachsurvivors.model.Player;

public class ExperienceOrb extends GroundItem implements PickUpAble {
    private int experience;
    private float width = 32;
    private float height = 32;
    // used for xpOrb acceleration
    private float speed = 1;
    private Sound pickUpSound;

    private float lifetime = 0f;
    private float maxLifetime = 30f;


    private boolean attracted = false;
    private boolean bounced = false;
    private float verticalVelocity = 0f;
    private float horizontalJitter = 0f;
    private float gravity = -1500f;

    ParticleEffectPool.PooledEffect xpEffect;

    public ExperienceOrb(float x, float y, int experience, ParticleEffectPoolManager poolManager) {
        super(AssetLoader.get().getTexture("entities/abilities/xp_orb.png"), x,y, null);
        this.experience=experience;
        setParticleEffect(poolManager.obtain("entities/particles/xp_orb.p"));
        getSprite().setSize(width, height);
        horizontalJitter = MathUtils.random(-100f, 100f); // side shake
        this.pickUpSound = AssetLoader.get().getSound("sounds/item_pickup.wav");
    }

    public void updateExperienceOrbMovement(Player player) {
        float delta = Gdx.graphics.getDeltaTime();
        Vector2 playerPos = new Vector2(player.getPosition());
        Vector2 orbPos = new Vector2(getSprite().getX(), getSprite().getY());
        Vector2 vector = moveTowardsPlayer(playerPos, orbPos);

        if (!attracted) {
            attracted = true;

            // Trigger upward bounce
            verticalVelocity = 450f;
            bounced = true;
        }

        // Apply vertical bounce if active
        if (bounced) {
            verticalVelocity += gravity * delta;
            getSprite().translateY(verticalVelocity * delta);
            getSprite().translateX(horizontalJitter * delta);

            // stop bouncing when landing
            if (verticalVelocity < -450f) {
                bounced = false;
            }
        }

        if (!bounced) {
            // Only apply Y tracking when bounce is done
            getSprite().translateY(vector.y * (player.getVaccumStrength() * speed) * delta);
        getSprite().translateX(vector.x * (player.getVaccumStrength() * speed) * delta);
            speed += 0.5;
        }
        getHitbox().set(getSprite().getX(), getSprite().getY(), getWidth(), getHeight());

    }

    public boolean hasExpired() {
        return lifetime >= maxLifetime;
    }

    // Custom overlap-check method because libgdx does not have one for checking Circle-Rectangle overlaps.
    // Intended for checking if xpOrb is in players radius.
    public boolean overlaps(Circle circle) {

        // Find the closest X coordinate on the rectangle to the circle's center
        // Clamp the circle's X to the rectangle's horizontal bounds
        float closestX = Math.max(getHitbox().getX(), Math.min(circle.x, getHitbox().getX() + getHitbox().getWidth()));

        // Find the closest Y coordinate on the rectangle to the circle's center
        // Clamp the circle's Y to the rectangle's vertical bounds
        float closestY = Math.max(getHitbox().getY(), Math.min(circle.y, getHitbox().getY() + getHitbox().getHeight()));

        float dx = circle.x - closestX;
        float dy = circle.y - closestY;

        // Calculate the squared distance from the circle center to the rectangle
        // Compare it with the square of the circle's radius
        // If it's less, the circle and rectangle are overlapping
        return (dx * dx + dy * dy) < (circle.radius * circle.radius);
    }


    public Vector2 moveTowardsPlayer(Vector2 playerPosition, Vector2 orbPosition) {
        Vector2 direction = new Vector2(playerPosition).sub(orbPosition);
        if (direction.len2() > 0.01f) direction.nor();
        return direction;
    }

    @Override
    public void onPickup(Player player) {
        player.gainExp(experience);
        pickUpSound.setVolume(pickUpSound.play(), 0.2f);
    }

    @Override
    public void dispose() {
        super.dispose();
        xpEffect.free();
        xpEffect=null;
    }

    public boolean isAttracted() {
        return attracted;
    }

    public int getExp() {
        return experience;
    }
}
