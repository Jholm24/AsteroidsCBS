package dk.sdu.cbse.asteroid;

import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.commonasteroid.Asteroid;
import dk.sdu.cbse.commonasteroid.AsteroidSPI;
import javafx.scene.paint.Color;

import java.util.Random;

public class AsteroidControlSystem implements IEntityProcessingService, AsteroidSPI {


    private static final Random RANDOM = new Random();

    @Override

    public void process(GameData gameData, World world) {
        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            // Get the asteroid's current velocity from rotation (direction angle)
            double changeX = Math.cos(Math.toRadians(asteroid.getRotation()));
            double changeY = Math.sin(Math.toRadians(asteroid.getRotation()));

            // Update position with velocity
            asteroid.setX(asteroid.getX() + changeX * 0.5);
            asteroid.setY(asteroid.getY() + changeY * 0.5);

            // Wrap around screen edges for seamless scrolling
            if (asteroid.getX() > gameData.getDisplayWidth()) {
                asteroid.setX(0);
            } else if (asteroid.getX() < 0) {
                asteroid.setX(gameData.getDisplayWidth());
            }

            if (asteroid.getY() > gameData.getDisplayHeight()) {
                asteroid.setY(0);
            } else if (asteroid.getY() < 0) {
                asteroid.setY(gameData.getDisplayHeight());
            }
        }
    }

    @Override
    public Entity createAsteroid(Entity e, GameData gameData) {
        Entity asteroid = new Asteroid();

        // Asteroid size
        asteroid.setRadius(15);

        // Set polygon shape (diamond-like shape)
        asteroid.setPolygonCoordinates(generateRandomPolygon(asteroid.getRadius()));

        // Random position on screen
        asteroid.setX(Math.random() * gameData.getDisplayWidth());
        asteroid.setY(Math.random() * gameData.getDisplayHeight());

        // Random direction (rotation determines movement direction)
        asteroid.setRotation(Math.random() * 360);

        // Set health for collision damage
        asteroid.setHealth(50);

        // Set appearance
        asteroid.setColor(Color.WHITE);

        return asteroid;
    }

    public static double[] generateRandomPolygon(final float baseRadius) {
        int points = 12;
        double[] polygon = new double[points * 2];
        double angleStep = (Math.PI * 2) / points;
        double radiusVariation = baseRadius * 0.15;
        for (int i = 0; i < points; i++) {
            double angle = i * angleStep;
            double radius = baseRadius + (RANDOM.nextDouble() * 2 - 1) * radiusVariation;

            polygon[i * 2] = Math.cos(angle) * radius;
            polygon[i * 2 + 1] = Math.sin(angle) * radius;
        }

        return polygon;
    }
}
