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
        long now = System.currentTimeMillis();
        while (!gameData.getPendingAsteroidSpawns().isEmpty()
                && gameData.getPendingAsteroidSpawns().peek() <= now) {
            gameData.getPendingAsteroidSpawns().poll();
            world.addEntity(createAsteroid(null, gameData));
        }

        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            // Get the asteroid's current velocity from rotation (direction angle)
            double changeX = Math.cos(Math.toRadians(asteroid.getRotation()));
            double changeY = Math.sin(Math.toRadians(asteroid.getRotation()));

            //Ændre på changeX,Y for at gøre asteroiden langsommere eller hurtigere.
            asteroid.setX(asteroid.getX() + changeX * 0.5);
            asteroid.setY(asteroid.getY() + changeY * 0.5);

            //hvis astroiden rammer siden på skærmen teleportere den til modsatte side
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

        // Spawn on a random edge, facing inward
        int edge = RANDOM.nextInt(4);
        switch (edge) {
            case 0 -> { // top
                asteroid.setX(RANDOM.nextDouble() * gameData.getDisplayWidth());
                asteroid.setY(0);
                asteroid.setRotation(RANDOM.nextDouble() * 180); // 0-180 points downward
            }
            case 1 -> { // right
                asteroid.setX(gameData.getDisplayWidth());
                asteroid.setY(RANDOM.nextDouble() * gameData.getDisplayHeight());
                asteroid.setRotation(90 + RANDOM.nextDouble() * 180); // 90-270 points leftward
            }
            case 2 -> { // bottom
                asteroid.setX(RANDOM.nextDouble() * gameData.getDisplayWidth());
                asteroid.setY(gameData.getDisplayHeight());
                asteroid.setRotation(180 + RANDOM.nextDouble() * 180); // 180-360 points upward
            }
            default -> { // left
                asteroid.setX(0);
                asteroid.setY(RANDOM.nextDouble() * gameData.getDisplayHeight());
                asteroid.setRotation(RANDOM.nextDouble() * 180 - 90); // -90 to 90 points rightward
            }
        }

        // Set health for collision damage
        asteroid.setHealth(50);
        asteroid.setDamage(10);

        // Set appearance
        asteroid.setColor(Color.GREY);

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
