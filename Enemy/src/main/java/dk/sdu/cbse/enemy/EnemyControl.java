package dk.sdu.cbse.enemy;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.entities.player.IPlayer;
import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.commonbullet.Bullet;
import dk.sdu.cbse.commonbullet.BulletSPI;

import java.util.Collection;
import java.util.Random;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;

public class EnemyControl implements IEntityProcessingService {

    private static final Random RANDOM = new Random();
    private static final double SPEED = 0.4;
    private static final long SPAWN_INTERVAL_MS = 10000;
    private static final int MAX_ENEMIES = 2;

    private long lastSpawnTime = System.currentTimeMillis();

    @Override
    public void process(GameData gameData, World world) {
        long now = System.currentTimeMillis();
        if (now - lastSpawnTime >= SPAWN_INTERVAL_MS
                && world.getEntities(Enemy.class).size() < MAX_ENEMIES) {
            world.addEntity(createEnemy(gameData));
            lastSpawnTime = now;
        }

        for (Entity entity : world.getEntities(Enemy.class)) {
            Enemy enemy = (Enemy) entity;

            // Every 1.5 seconds pick a new committed turn direction
            if (now - enemy.getLastDirectionChangeTime() >= 1500) {
                int choice = RANDOM.nextInt(3); // 0 = left, 1 = right, 2 = straight
                enemy.setRotationRate(choice == 0 ? -1.5 : choice == 1 ? 1.5 : 0);
                enemy.setLastDirectionChangeTime(now);
            }

            enemy.setRotation(enemy.getRotation() + enemy.getRotationRate());

            double changeX = SPEED * Math.cos(Math.toRadians(enemy.getRotation()));
            double changeY = SPEED * Math.sin(Math.toRadians(enemy.getRotation()));
            enemy.setX(enemy.getX() + changeX);
            enemy.setY(enemy.getY() + changeY);

            if (enemy.getX() < 0) { enemy.setX(1); enemy.setRotation(RANDOM.nextDouble() * 360); }
            if (enemy.getX() > gameData.getDisplayWidth()) { enemy.setX(gameData.getDisplayWidth() - 1); enemy.setRotation(RANDOM.nextDouble() * 360); }
            if (enemy.getY() < 0) { enemy.setY(1); enemy.setRotation(RANDOM.nextDouble() * 360); }
            if (enemy.getY() > gameData.getDisplayHeight()) { enemy.setY(gameData.getDisplayHeight() - 1); enemy.setRotation(RANDOM.nextDouble() * 360); }

            if (RANDOM.nextInt(80) == 0) {
                shootAtPlayer(enemy, world, gameData);
            }
        }
    }

    private void shootAtPlayer(Entity enemy, World world, GameData gameData) {
        Enemy e = (Enemy) enemy;
        if (System.currentTimeMillis() - e.getLastShootTime() < 2000) return;

        Entity player = world.getEntities().stream()
                .filter(p -> p instanceof IPlayer)
                .findFirst()
                .orElse(null);
        if (player == null) return;
        e.setLastShootTime(System.currentTimeMillis());

        double dx = player.getX() - enemy.getX();
        double dy = player.getY() - enemy.getY();
        double angleToPlayer = Math.toDegrees(Math.atan2(dy, dx));

        double originalRotation = enemy.getRotation();
        enemy.setRotation(angleToPlayer);
        getBulletSPIs().stream().findFirst().ifPresent(spi -> {
            Entity bullet = spi.createBullet(enemy, gameData);
            if (bullet instanceof Bullet) ((Bullet) bullet).setSpeed(1.5);
            world.addEntity(bullet);
        });
        enemy.setRotation(originalRotation);
    }

    static Entity createEnemy(GameData gameData) {
        Enemy enemy = new Enemy();
        enemy.setPolygonCoordinates(24, -15, 15, -15, 0, -21, -15, -21, -24, -15, -24, -9, -15, -9, 0, -3, 15, -3, 24, -9);
        enemy.setX(RANDOM.nextDouble() * gameData.getDisplayWidth());
        enemy.setY(RANDOM.nextDouble() * gameData.getDisplayHeight());
        enemy.setRotation(RANDOM.nextDouble() * 360);
        System.out.println("Enemy spawned at (" + (int) enemy.getX() + ", " + (int) enemy.getY() + ")");
        return enemy;
    }

    private Collection<? extends BulletSPI> getBulletSPIs() {
        return ServiceLoader.load(BulletSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}