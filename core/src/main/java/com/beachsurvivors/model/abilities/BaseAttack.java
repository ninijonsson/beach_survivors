package com.beachsurvivors.model.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.beachsurvivors.utilities.AssetLoader;
import com.beachsurvivors.model.Player;
import com.beachsurvivors.model.enemies.Enemy;
import com.beachsurvivors.utilities.TargetingHelper;

public class BaseAttack extends Ability {
    private Vector2 position;
    private Vector2 direction;
    private Sound fireSound;
    private float projectileSpeed;

    //Constructor för default baseattack (spelarens)
    public BaseAttack() {
        super("bullet", "entities/abilities/bullet.png", AbilityType.ATTACK, 1.0, 0.4f, 64, 64);
        this.position = new Vector2();
        this.direction = new Vector2();
        this.projectileSpeed = 600f;

        this.fireSound= AssetLoader.get().getSound("entities/abilities/water_gun_fire.wav");
        fireSound.setVolume(fireSound.play(), 0.5f);
    }

    //Constructor för custom base attack (för Navy Seals t.ex.)
    public BaseAttack(String texturePath, int damage, float projectileSpeed) {
        super("bullet", texturePath, AbilityType.ATTACK, damage, 1, 32, 32);
        this.projectileSpeed = projectileSpeed;
        this.position = new Vector2();
        this.direction = new Vector2();

    }

    public Vector2 getDirection() { return direction; }

    public void setDirection(Vector2 direction) {
        this.direction.set(direction);
    }

    @Override
    public void use(float delta, Player player, Array<Enemy> enemies, Array<Ability> abilities) {

        Enemy target = TargetingHelper.getNearestEnemy(player, enemies);

        if (target != null) {
            Vector2 direction = new Vector2(
                target.getSprite().getX() - player.getPosition().x,
                target.getSprite().getY() - player.getPosition().y
            ).nor();

            BaseAttack bullet = new BaseAttack();
            bullet.setDirection(direction);
            bullet.setPosition(player.getPosition().cpy());
            abilities.add(bullet);
            }

    }

    @Override
    public void updatePosition(float delta, Vector2 vector) {

        Vector2 movement = direction.cpy().scl(projectileSpeed * delta);
        this.position.add(movement);

        getSprite().setPosition(position.x, position.y);
        getHitBox().setPosition(position.x, position.y);

        getSprite().setOriginCenter();
        getSprite().setRotation(direction.angleDeg() + 90);
    }

    public void setPosition(Vector2 startPosition) {
        this.position = startPosition;
        getSprite().setPosition(position.x, position.y);
        getHitBox().setPosition(position.x, position.y);
    }

    public Vector2 getPosition() {
        return position;
    }


    public float getProjectileSpeed() {
        return projectileSpeed;
    }
}
