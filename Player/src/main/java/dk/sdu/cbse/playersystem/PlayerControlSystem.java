package dk.sdu.cbse.playersystem;

import dk.sdu.cbse.bullet.Bullet;
import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.GameKeys;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessingService;


public class PlayerControlSystem implements IEntityProcessingService {
    @Override
    public void process(GameData gameData, World world) {
        for (Entity e : world.getEntities(Player.class)) {
            if (gameData.getKeys().isDown(GameKeys.A)) {
                e.setRotation(e.getRotation() - 2);

            }
            if (gameData.getKeys().isDown(GameKeys.D)) {
                e.setRotation(e.getRotation() + 2);

            }
            if (gameData.getKeys().isDown(GameKeys.W)) {
                double changeX = Math.cos(Math.toRadians(e.getRotation()));
                double changeY = Math.sin(Math.toRadians(e.getRotation()));
                e.setX(e.getX() + changeX);
                e.setY(e.getY() + changeY);
            }
            if (gameData.getKeys().isPressed(GameKeys.SPACE)) {
                Bullet bullet = new Bullet();
                bullet.setX(e.getX());
                bullet.setY(e.getY());
                bullet.setRotation(e.getRotation());
                bullet.setPolygonCoordinates(-2, -2, 2, -2, 2, 2, -2, 2);
                world.addEntity(bullet);
            }

            if (e.getX() < 0) {
                e.setX(1);
            }

            if (e.getX() > gameData.getDisplayWidth()) {
                e.setX(gameData.getDisplayWidth() - 1);
            }

            if (e.getY() < 0) {
                e.setY(1);
            }

            if (e.getY() > gameData.getDisplayHeight()) {
                e.setY(gameData.getDisplayHeight() - 1);
            }
        }
    }
}
