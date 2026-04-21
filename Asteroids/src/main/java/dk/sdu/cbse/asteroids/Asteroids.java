package dk.sdu.cbse.asteroids;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.entities.asteroids.IAsteroids;


public final class Asteroids extends Entity implements IAsteroids {

    private int size;

    public Asteroids(final int asteroidSize) {
        this.size = asteroidSize;
        setRadius(10 * asteroidSize);
        setHealth(50 * asteroidSize);
        setDamage(10);
    }

    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

}
