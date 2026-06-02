package dk.sdu.cbse.collision;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.entities.enemy.IEnemy;
import dk.sdu.cbse.common.entities.player.IPlayer;
import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.common.services.IPostEntityProcessingService;
import dk.sdu.cbse.commonasteroid.Asteroid;
import dk.sdu.cbse.commonasteroid.AsteroidSPI;
import dk.sdu.cbse.commonbullet.Bullet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ServiceLoader;

public class Collision implements IPostEntityProcessingService {

    private final Random random = new Random();

    @Override
    public void process(GameData gameData, World world) {
        List<Entity> entitiesToRemove = new ArrayList<>();

        for (Entity e1 : world.getEntities()) {
            for (Entity e2 : world.getEntities()) {
                if (e1 != e2 && isColliding(e1, e2)) {
                    handleCollision(e1, e2, entitiesToRemove, gameData, world);
                }
            }
        }

        // Remove entities that were destroyed
        for (Entity entity : entitiesToRemove) {
            world.removeEntity(entity);
        }
    }

    private void handleCollision(Entity e1, Entity e2, List<Entity> entitiesToRemove, GameData gameData, World world) {
        // Player hits Asteroid
        if (e1 instanceof IPlayer && e2 instanceof Asteroid) {
            if (!e1.isImmune() && !e2.isImmune()) {
                handlePlayerAsteroidCollision(e1, (Asteroid) e2, gameData);
            }
        } else if (e1 instanceof Asteroid && e2 instanceof IPlayer) {
            if (!e1.isImmune() && !e2.isImmune()) {
                handlePlayerAsteroidCollision(e2, (Asteroid) e1, gameData);
            }
        }

        // Player hits Enemy
        else if (e1 instanceof IPlayer && e2 instanceof IEnemy) {
            if (!e1.isImmune() && !e2.isImmune()) {
                handlePlayerEnemyCollision(e1, gameData);
            }
        } else if (e1 instanceof IEnemy && e2 instanceof IPlayer) {
            if (!e1.isImmune() && !e2.isImmune()) {
                handlePlayerEnemyCollision(e2, gameData);
            }
        }

        // Bullet hits Asteroid - only player bullets destroy asteroids; enemy bullets are just removed
        else if (e1 instanceof Bullet && e2 instanceof Asteroid) {
            if (!e2.isImmune()) {
                handleBulletAsteroidCollision((Bullet) e1, (Asteroid) e2, entitiesToRemove, gameData, world);
            }
        } else if (e1 instanceof Asteroid && e2 instanceof Bullet) {
            if (!e1.isImmune()) {
                handleBulletAsteroidCollision((Bullet) e2, (Asteroid) e1, entitiesToRemove, gameData, world);
            }
        }

        // Bullet hits Enemy - only player bullets damage enemies
        else if (e1 instanceof Bullet && e2 instanceof IEnemy) {
            handleBulletEnemyCollision((Bullet) e1, e2, entitiesToRemove);
        } else if (e1 instanceof IEnemy && e2 instanceof Bullet) {
            handleBulletEnemyCollision((Bullet) e2, e1, entitiesToRemove);
        }

        // Bullet hits Player - only enemy bullets damage the player
        else if (e1 instanceof Bullet && e2 instanceof IPlayer) {
            if (!e2.isImmune()) {
                handleBulletPlayerCollision((Bullet) e1, e2, entitiesToRemove, gameData);
            }
        } else if (e1 instanceof IPlayer && e2 instanceof Bullet) {
            if (!e1.isImmune()) {
                handleBulletPlayerCollision((Bullet) e2, e1, entitiesToRemove, gameData);
            }
        }

        // Enemy hits Asteroid - enemy redirects, asteroid unaffected
        else if (e1 instanceof IEnemy && e2 instanceof Asteroid) {
            if (!e1.isImmune()) {
                e1.setRotation(e1.getRotation() + 180 + (random.nextDouble() - 0.5) * 90);
                e1.setLastCollisionTime(System.currentTimeMillis());
            }
        } else if (e1 instanceof Asteroid && e2 instanceof IEnemy) {
            if (!e2.isImmune()) {
                e2.setRotation(e2.getRotation() + 180 + (random.nextDouble() - 0.5) * 90);
                e2.setLastCollisionTime(System.currentTimeMillis());
            }
        }

        // Asteroid collides with Asteroid
        else if (e1 instanceof Asteroid && e2 instanceof Asteroid) {
            if (!e1.isImmune() && !e2.isImmune()) {
                handleAsteroidAsteroidCollision((Asteroid) e1, (Asteroid) e2);
            }
        }
    }

    private void handlePlayerEnemyCollision(Entity player, GameData gameData) {
        player.setDamage(10f);
        player.setLastCollisionTime(System.currentTimeMillis());
        System.out.println("Player hit by enemy! Health: " + player.getHealth());
        if (player.getHealth() <= 0) {
            gameData.setGameOver(true);
        }
    }

    private void handleBulletEnemyCollision(Bullet bullet, Entity enemy, List<Entity> entitiesToRemove) {
        if (enemy.getID().equals(bullet.getOwnerId())) return;
        entitiesToRemove.add(bullet);
        enemy.setDamage(10f);
        enemy.setLastCollisionTime(System.currentTimeMillis());
        System.out.println("Enemy hit by bullet! Health: " + enemy.getHealth());
        if (enemy.getHealth() <= 0) {
            entitiesToRemove.add(enemy);
            System.out.println("Enemy destroyed!");
        }
    }

    private void handleBulletPlayerCollision(Bullet bullet, Entity player, List<Entity> entitiesToRemove, GameData gameData) {
        if (player.getID().equals(bullet.getOwnerId())) return;
        entitiesToRemove.add(bullet);
        player.setDamage(5f);
        player.setLastCollisionTime(System.currentTimeMillis());
        System.out.println("Player hit by enemy bullet! Health: " + player.getHealth());
        if (player.getHealth() <= 0) {
            gameData.setGameOver(true);
        }
    }

    private void handlePlayerAsteroidCollision(Entity player, Asteroid asteroid, GameData gameData) {
        player.setDamage(10f);
        player.setLastCollisionTime(System.currentTimeMillis());
        asteroid.setLastCollisionTime(System.currentTimeMillis());
        System.out.println("Player hit by asteroid! Health: " + player.getHealth());
        if (player.getHealth() <= 0) {
            gameData.setGameOver(true);
        }
    }

    private void handleBulletAsteroidCollision(Bullet bullet, Asteroid asteroid, List<Entity> entitiesToRemove, GameData gameData, World world) {
        entitiesToRemove.add(bullet);
        // Enemy bullets are removed but do not destroy asteroids
        if (bullet.getOwnerId() == null || world.getEntities().stream()
                .filter(e -> e instanceof IEnemy)
                .anyMatch(e -> e.getID().equals(bullet.getOwnerId()))) {
            return;
        }
        ServiceLoader.load(AsteroidSPI.class).findFirst().ifPresent(spi -> spi.splitAsteroids(asteroid, world));
        entitiesToRemove.add(asteroid);
        asteroid.setLastCollisionTime(System.currentTimeMillis());
        gameData.incrementDestroyedAsteroids();
        gameData.addPendingAsteroidSpawn(System.currentTimeMillis() + 500);
    }

    private void handleAsteroidAsteroidCollision(Asteroid asteroid1, Asteroid asteroid2) {
        // Change direction for both asteroids by reversing and adding random angle
        double rotationChange1 = 180 + (random.nextDouble() - 0.5) * 90;
        double rotationChange2 = 180 + (random.nextDouble() - 0.5) * 90;


        asteroid1.setRotation(asteroid1.getRotation() + rotationChange1);
        asteroid2.setRotation(asteroid2.getRotation() + rotationChange2);

        asteroid1.setLastCollisionTime(System.currentTimeMillis());
        asteroid2.setLastCollisionTime(System.currentTimeMillis());
    }

    boolean isColliding(Entity e1, Entity e2) {
        // Calculate distance between two entities
        double dx = e1.getX() - e2.getX();
        double dy = e1.getY() - e2.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Check if distance is less than sum of radius (collision detection)
        float minDistance = e1.getRadius() + e2.getRadius();
        return distance < minDistance;
    }
}


