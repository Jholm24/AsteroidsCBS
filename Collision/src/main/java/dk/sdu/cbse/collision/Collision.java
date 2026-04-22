package dk.sdu.cbse.collision;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.entities.player.IPlayer;
import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.commonasteroid.Asteroid;
import dk.sdu.cbse.commonbullet.Bullet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Collision implements IEntityProcessingService {

    private final Random random = new Random();

    @Override
    public void process(GameData gameData, World world) {
        List<Entity> entitiesToRemove = new ArrayList<>();

        for (Entity e1 : world.getEntities()) {
            for (Entity e2 : world.getEntities()) {
                if (e1 != e2 && isColliding(e1, e2)) {
                    handleCollision(e1, e2, entitiesToRemove);
                }
            }
        }

        // Remove entities that were destroyed
        for (Entity entity : entitiesToRemove) {
            world.removeEntity(entity);
        }
    }

    private void handleCollision(Entity e1, Entity e2, List<Entity> entitiesToRemove) {
        // Player hits Asteroid - Player takes damage
        if (e1 instanceof IPlayer && e2 instanceof Asteroid) {
            // Check if either entity is immune
            if (!e1.isImmune() && !e2.isImmune()) {
                handlePlayerAsteroidCollision(e1, (Asteroid) e2);
            }
        } else if (e1 instanceof Asteroid && e2 instanceof IPlayer) {
            // Check if either entity is immune
            if (!e1.isImmune() && !e2.isImmune()) {
                handlePlayerAsteroidCollision(e2, (Asteroid) e1);
            }
        }

        // Bullet hits Asteroid - Asteroid destroyed
        else if (e1 instanceof Bullet && e2 instanceof Asteroid) {
            // Check if asteroid is immune
            if (!e2.isImmune()) {
                handleBulletAsteroidCollision((Bullet) e1, (Asteroid) e2, entitiesToRemove);
            }
        } else if (e1 instanceof Asteroid && e2 instanceof Bullet) {
            // Check if asteroid is immune
            if (!e1.isImmune()) {
                handleBulletAsteroidCollision((Bullet) e2, (Asteroid) e1, entitiesToRemove);
            }
        }

        // Asteroid collides with Asteroid - Both change direction randomly
        else if (e1 instanceof Asteroid && e2 instanceof Asteroid) {
            // Check if either asteroid is immune
            if (!e1.isImmune() && !e2.isImmune()) {
                handleAsteroidAsteroidCollision((Asteroid) e1, (Asteroid) e2);
            }
        }
    }

    private void handlePlayerAsteroidCollision(Entity player, Asteroid asteroid) {
        // Player takes damage (e.g., 10 health points)
        float damage = 10f;
        player.setDamage(damage);
        player.setLastCollisionTime(System.currentTimeMillis());
        asteroid.setLastCollisionTime(System.currentTimeMillis());
        if (player instanceof IPlayer) {
            //spillet slutter hvis health == 0

        }
        System.out.println("Player hit by asteroid! Health: " + player.getHealth() + " (Immune for 3 seconds)");
    }

    private void handleBulletAsteroidCollision(Bullet bullet, Asteroid asteroid, List<Entity> entitiesToRemove) {
        // Mark both bullet and asteroid for removal
        entitiesToRemove.add(bullet);
        entitiesToRemove.add(asteroid);
        asteroid.setLastCollisionTime(System.currentTimeMillis());
        System.out.println("Asteroid destroyed by bullet!");
    }

    private void handleAsteroidAsteroidCollision(Asteroid asteroid1, Asteroid asteroid2) {
        // Change direction for both asteroids by reversing and adding random angle
        double rotationChange1 = 180 + (random.nextDouble() - 0.5) * 90;
        double rotationChange2 = 180 + (random.nextDouble() - 0.5) * 90;

        // Apply rotation changes
        asteroid1.setRotation(asteroid1.getRotation() + rotationChange1);
        asteroid2.setRotation(asteroid2.getRotation() + rotationChange2);


        // Set immunity time for both asteroids
        asteroid1.setLastCollisionTime(System.currentTimeMillis());
        asteroid2.setLastCollisionTime(System.currentTimeMillis());

        System.out.println("Asteroids collided! Changing direction... (Both immune for 3 seconds)");
    }

    private boolean isColliding(Entity e1, Entity e2) {
        // Calculate distance between two entities
        double dx = e1.getX() - e2.getX();
        double dy = e1.getY() - e2.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Check if distance is less than sum of radii (collision detection)
        float minDistance = e1.getRadius() + e2.getRadius();
        return distance < minDistance;
    }
}


