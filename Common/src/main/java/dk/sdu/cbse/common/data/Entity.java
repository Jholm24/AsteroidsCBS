package dk.sdu.cbse.common.data;

import javafx.scene.paint.Color;
import java.io.Serializable;
import java.util.UUID;

public class Entity implements Serializable {

    private final UUID ID = UUID.randomUUID();

    private double[] polygonCoordinates;
    private double x;
    private double y;
    private double rotation;
    private float radius;
    private float health;
    private float damage;
    private Color color;
    private long lastCollisionTime = 0;
    private long immuneDurationMs = 3000;


    public String getID() {
        return ID.toString();
    }


    public void setPolygonCoordinates(double... coordinates ) {
        this.polygonCoordinates = coordinates;
    }

    public double[] getPolygonCoordinates() {
        return polygonCoordinates;
    }


    public void setX(double x) {
        this.x =x;
    }

    public double getX() {
        return x;
    }


    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return this.radius;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    //Health and damage
    public void setHealth(float health) {
        this.health = health;
    }
    public float getHealth() {
        return this.health;
    }

    public void setDamage(float damage) {
        this.health -= damage;
    }
    public float getDamage() {
        return this.damage;
    }
    
    // Immunity frames
    public void setLastCollisionTime(long time) {
        this.lastCollisionTime = time;
    }

    public long getLastCollisionTime() {
        return this.lastCollisionTime;
    }

    public void setImmuneDurationMs(long ms) {
        this.immuneDurationMs = ms;
    }

    public boolean isImmune() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastCollisionTime) < immuneDurationMs;
    }

}
