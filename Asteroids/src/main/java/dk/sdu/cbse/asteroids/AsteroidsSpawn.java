package dk.sdu.cbse.asteroids;

import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessingService;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class AsteroidsSpawn implements IEntityProcessingService {

    private final Random random = new Random();

    private float spawnRate = 10f;
    private float spawnInterval = 5f;
    private float minSpawnInterval = 1f;

    private final List<Float> respawnTimers = new ArrayList<>();

    @Override
    public void process(GameData gameData, World world) {
        spawnInterval -= gameData.getDelta();

        if (spawnInterval <= 0) {
            spawnInterval = minSpawnInterval + random.nextFloat() * spawnRate;
            world.addEntity(new Asteroids(random.nextInt(3 - 1 + 1) + 1));
        }

        for (int i = respawnTimers.size() - 1; i >= 0; i--) {
            float timeLeft = respawnTimers.get(i) - gameData.getDelta();
            respawnTimers.set(i, timeLeft);

            if (timeLeft <= 0) {
                respawnTimers.remove(i);
                spawnAsteroid(gameData, world);
            }
        }
    }

    public void queueRespawn() {
        respawnTimers.add(1.0f);
    }

    public void spawnAsteroid(GameData gameData, World world) {
        Asteroids asteroid = new Asteroids(random.nextInt(3 - 1 + 1) + 1);
        asteroid.setX(random.nextDouble() * gameData.getDisplayWidth());
        asteroid.setY(random.nextDouble() * gameData.getDisplayHeight());
        asteroid.setRotation(random.nextDouble() * 360);
        asteroid.setColor(Color.GRAY);
        asteroid.setPolygonCoordinates(AsteroidsPlugin.generateRandomPolygon(asteroid.getRadius()));
        world.addEntity(asteroid);
    }

}