package com.beachsurvivors.model.Map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Map {
    private TiledMap map;
    private List<Polygon> collisionBoxes;
    private MapLayer collisionLayer;
    private int width;
    private int height;
    private float gameScale;
    private float startingX;
    private float startingY;
    private List<Rectangle> collisionObjects = new ArrayList<>();

    public Map(TiledMap map) {
        this.map = map;
        this.width = map.getProperties().get("width", Integer.class) *
                map.getProperties().get("tilewidth", Integer.class);
        this.height = map.getProperties().get("height", Integer.class) *
                map.getProperties().get("tileheight", Integer.class);
        this.gameScale = 2f;
        this.collisionBoxes = new ArrayList<>();
        getMapLayer();
        collisionLayer = map.getLayers().get("collision");
        getStartPos();
    }

    private void getMapLayer() {
        MapLayer layer = map.getLayers().get("collision");
        if (layer == null) return;

        for (MapObject object : layer.getObjects()) {
            if (object instanceof PolygonMapObject) {
                Polygon polygon = ((PolygonMapObject) object).getPolygon();
                collisionBoxes.add(scalePolygon(polygon));
            }
        }

        MapLayer objectsLayer = map.getLayers().get("collisionObjects");
        if (objectsLayer == null) return;
        for (MapObject object : objectsLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                rect.set(rect.x * gameScale, rect.y * gameScale, rect.width * gameScale, rect.height * gameScale);
                collisionObjects.add(rect);

            }
        }System.out.println(collisionObjects.size());
    }

    private void getStartPos() {
        MapLayer startLayer = map.getLayers().get("startPos");
        if (startLayer == null) return;
        for (MapObject object : startLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle start = ((RectangleMapObject) object).getRectangle();
                startingX = start.x * gameScale;
                startingY = start.y * gameScale;
                return; // Stoppa loopen efter första träffen
            }
        }
    }

    private Polygon scalePolygon(Polygon polygon) {
        float[] vertices = polygon.getVertices(); // OBS: Inte transformed!
        float[] scaledVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; i++) {
            scaledVertices[i] = vertices[i] * gameScale;
        }

        Polygon scaledPolygon = new Polygon(scaledVertices);
        scaledPolygon.setPosition(polygon.getX() * gameScale, polygon.getY() * gameScale);
        return scaledPolygon;
    }

    public boolean isInsidePolygon(float x, float y) {
        for (Polygon polygon : collisionBoxes) {
            if (polygon.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidMove(Polygon test) {
        for (Polygon collisionPolygon : collisionBoxes) {
            if (!collisionPolygon.contains(test.getX(), test.getY())) {
                return false;
            }
        }
        return true;
    }

    public float getStartingX() {
        return startingX;
    }

    public float getStartingY() {
        return startingY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getGameScale() {
        return gameScale;
    }

    public List<Polygon> getCollisionBoxes() {
        return collisionBoxes;
    }

    public boolean collidesWithObject(Rectangle playerHitBox) {
        for (Rectangle object : collisionObjects) {
            if (playerHitBox.overlaps(object)) {
                return true;
            }
        }
        return false;
    }

}
