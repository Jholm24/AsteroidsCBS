package dk.sdu.cbse.commonasteroid;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;

public interface AsteroidSPI {
    Entity createAsteroid(Entity e, GameData gameData);
    void splitAsteroids(Asteroid asteroid, World world);
}
