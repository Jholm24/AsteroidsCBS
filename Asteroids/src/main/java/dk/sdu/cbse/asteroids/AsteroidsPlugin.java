package dk.sdu.cbse.asteroids;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IGamePluginService;
import javafx.scene.paint.Color;
import java.util.Random;

public class AsteroidsPlugin implements IGamePluginService {

    private static final Random RANDOM = new Random();

    @Override
    public void start(final GameData gameData, final World world) {
        int initialAsteroidsSpawn = 10;
        for (int i = 0; i < initialAsteroidsSpawn; i++) {
            Asteroids asteroid = new Asteroids(RANDOM.nextInt(3 - 1 + 1) + 1);
            asteroid.setX(RANDOM.nextDouble() * gameData.getDisplayWidth());
            asteroid.setY(RANDOM.nextDouble() * gameData.getDisplayHeight());
            asteroid.setRotation(RANDOM.nextDouble() * 360);
            asteroid.setColor(Color.GRAY);
            asteroid.setPolygonCoordinates(generateRandomPolygon(asteroid.getRadius()));
            world.addEntity(asteroid);
        }
    }

    @Override
    public void stop(final GameData gameData, final World world) {
        for (Entity e : world.getEntities(Entity.class)) {
            world.removeEntity(e);
        }
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