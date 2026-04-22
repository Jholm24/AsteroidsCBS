package dk.sdu.cbse.commonasteroid;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;

public interface AsteroidSPI {
    Entity createAsteroid(Entity e, GameData gameData);
}
