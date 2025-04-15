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

/**
 * This class handles all the logic surrounding the playable map.
 */
public class Map {
    private TiledMap map;
    private List<Polygon> mapBoundary;
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
        this.mapBoundary = new ArrayList<>();
        getMapLayer();
        collisionLayer = map.getLayers().get("collision");
        getStartPos();
    }

    /**
     * This method is used to extract the different layers from the TiledMap. These layers are used to get the positions
     * of different objects where player collision with the environment should occur.
     * The layer named "collision" is the boundary which keeps the player on the island in the map.
     * The layer named "collisionObjects" is used to get the positions of the collidable objects on the map (trees, rocks).
     */
    private void getMapLayer() {
        MapLayer layer = map.getLayers().get("collision");
        if (layer == null) return;

        for (MapObject object : layer.getObjects()) {
            if (object instanceof PolygonMapObject) {
                Polygon polygon = ((PolygonMapObject) object).getPolygon();
                mapBoundary.add(scalePolygon(polygon));
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

    /**
     * Retrieves the startposition for the TiledMap using the layer startPos.
     */
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

    /**
     * This is used to scale the positions appropriately to the game's scale. Since we are working with different scales
     * in the game and the map this has to be done to create correctly sized collision objects.
     * @param polygon This is the starting polygon.
     * @return is the converted polygon that has been adjusted to the games scale.
     */
    private Polygon scalePolygon(Polygon polygon) {
        float[] vertices = polygon.getVertices();
        float[] scaledVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; i++) {
            scaledVertices[i] = vertices[i] * gameScale;
        }

        Polygon scaledPolygon = new Polygon(scaledVertices);
        scaledPolygon.setPosition(polygon.getX() * gameScale, polygon.getY() * gameScale);
        return scaledPolygon;
    }

    /**
     * This method is used by the movementKeys of the Player class to check if the next move is a valid one.
     * In this case a valid move is if
     * @param x x-position
     * @param y y- position
     * @return boolean indicating that the move is okay.
     */
    public boolean isInsidePolygon(float x, float y) {
        for (Polygon polygon : mapBoundary) {
            if (polygon.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidMove(Polygon test) {
        for (Polygon collisionPolygon : mapBoundary) {
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

    public List<Polygon> getMapBoundary() {
        return mapBoundary;
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
