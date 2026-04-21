package dk.sdu.cbse.bullet;

import dk.sdu.cbse.asteroids.AsteroidsPlugin;
import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.entities.asteroids.IAsteroids;
import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.common.services.IPostEntityProcessingService;

import java.util.ArrayList;
import java.util.List;

public class BulletProccessor implements IPostEntityProcessingService, IEntityProcessingService {

    @Override
    public void process(GameData gameData, World world) {
        List<Entity> bulletsToRemove = new ArrayList<>();
        List<Entity> asteroidsToRemove = new ArrayList<>();

        for (Entity bullet : world.getEntities(Bullet.class)) {
            double changeX = Math.cos(Math.toRadians(bullet.getRotation()));
            double changeY = Math.sin(Math.toRadians(bullet.getRotation()));

            bullet.setX(bullet.getX() + changeX * ((Bullet) bullet).getSpeed());
            bullet.setY(bullet.getY() + changeY * ((Bullet) bullet).getSpeed());

            if (bullet.getX() < 0 || bullet.getX() > gameData.getDisplayWidth()
                    || bullet.getY() < 0 || bullet.getY() > gameData.getDisplayHeight()) {
                bulletsToRemove.add(bullet);
            }
        }

        for (Entity bullet : world.getEntities(Bullet.class)) {
            for (Entity asteroid : world.getEntities(IAsteroids.class)) {
                double dx = bullet.getX() - asteroid.getX();
                double dy = bullet.getY() - asteroid.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < bullet.getRadius() + asteroid.getRadius()) {
                    bulletsToRemove.add(bullet);
                    asteroidsToRemove.add(asteroid);
                    break;
                }
            }
        }

        for (Entity bullet : bulletsToRemove) {
            world.removeEntity(bullet);
        }

        for (Entity asteroid : asteroidsToRemove) {
            world.removeEntity(asteroid);
            //AsteroidsPlugin.spawnAsteroids(gameData, world);
        }
    }
}