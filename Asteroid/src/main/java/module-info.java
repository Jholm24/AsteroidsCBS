module Asteroid {

    requires CommonAsteroid;
    requires dk.sdu.cbse.common;
    requires javafx.graphics;

    provides dk.sdu.cbse.common.services.IGamePluginService with dk.sdu.cbse.asteroid.AsteroidPlugin;
    provides dk.sdu.cbse.commonasteroid.AsteroidSPI with dk.sdu.cbse.asteroid.AsteroidControlSystem;
    provides dk.sdu.cbse.common.services.IEntityProcessingService with dk.sdu.cbse.asteroid.AsteroidControlSystem;
}

