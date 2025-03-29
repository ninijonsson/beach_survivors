package model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class Shark extends Enemy {

    private Texture sharkImage;
    private Rectangle sharkHitbox;
    private Sprite sharkSprite;
    private int x;
    private int y;
    private double hitpoints;
    private boolean alive;

    public Shark () {
        sharkImage = new Texture("entities/Shark.png");
        sharkSprite = new Sprite(sharkImage);
        sharkSprite.setSize(100,100);

        Random random = new Random();
        x = random.nextInt(0, (int) (1920-sharkSprite.getWidth()));
        y = random.nextInt(0, (int) (1080-sharkSprite.getHeight()));

        sharkHitbox = new Rectangle(x,y, 100,100);
        sharkSprite.setPosition(sharkSprite.getX()+x,sharkSprite.getY()+y);
        sharkSprite.setPosition(x,y);
        this.hitpoints=20;
        this.alive=true;

    }
    public void hit(double damageTaken){
        hitpoints=hitpoints-damageTaken;
        if(hitpoints<=0){
            alive=false;
        }
    }

    public boolean isDead(){
        return !alive;
    }
    @Override
    public void move() {

    }

    @Override
    public void attack() {

    }

    @Override
    public void spawnEnemy() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public Sprite getSprite() {
        return sharkSprite;
    }


    public Rectangle getHitBox() {
        return sharkHitbox;
    }
}
