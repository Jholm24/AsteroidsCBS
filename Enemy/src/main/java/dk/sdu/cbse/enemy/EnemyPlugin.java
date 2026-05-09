package dk.sdu.cbse.enemy;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IGamePluginService;

public class EnemyPlugin implements IGamePluginService {

    @Override
    public void start(GameData gameData, World world) {
        world.addEntity(EnemyControl.createEnemy(gameData));
    }

    @Override
    public void stop(GameData gameData, World world) {
        for (Entity e : world.getEntities(Enemy.class)) {
            world.removeEntity(e);
        }
    }
}