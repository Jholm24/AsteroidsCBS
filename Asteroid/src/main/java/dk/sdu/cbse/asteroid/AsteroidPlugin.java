package dk.sdu.cbse.asteroid;

import dk.sdu.cbse.commonasteroid.Asteroid;
import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IGamePluginService;

public class AsteroidPlugin implements IGamePluginService {

    @Override
    public void start(GameData gameData, World world) {
        // Initialize multiple asteroids floating in random directions
        AsteroidControlSystem asteroidControlSystem = new AsteroidControlSystem();

        // Create 5 asteroids with random positions and directions
        System.out.println("AsteroidPlugin: Creating asteroids...");
        for (int i = 0; i < 10; i++) {
            Entity asteroid = asteroidControlSystem.createAsteroid(null, gameData);
            world.addEntity(asteroid);
            System.out.println("AsteroidPlugin: Created asteroid " + (i + 1) + " at (" + asteroid.getX() + ", " + asteroid.getY() + ")");
        }
        System.out.println("AsteroidPlugin: Total asteroids in world: " + world.getEntities().size());
    }

    @Override
    public void stop(GameData gameData, World world) {
        // Remove all asteroids when the plugin stops
        for (Entity e : world.getEntities()) {
            if (e.getClass() == Asteroid.class) {
                world.removeEntity(e);
            }
        }
    }
}
