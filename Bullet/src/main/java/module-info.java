import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.common.services.IGamePluginService;
import dk.sdu.cbse.commonbullet.BulletSPI;

module Bullet {

    requires CommonBullet;
    requires dk.sdu.cbse.common;

    provides IGamePluginService with dk.sdu.cbse.bullet.BulletPlugin;
    provides BulletSPI with dk.sdu.cbse.bullet.BulletControlSystem;
    provides IEntityProcessingService with dk.sdu.cbse.bullet.BulletControlSystem;
}