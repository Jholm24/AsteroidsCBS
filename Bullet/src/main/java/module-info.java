module Bullet {

    requires CommonBullet;
    requires dk.sdu.cbse.common;
    requires javafx.graphics;

    provides dk.sdu.cbse.common.services.IGamePluginService with dk.sdu.cbse.bullet.BulletPlugin;
    provides dk.sdu.cbse.commonbullet.BulletSPI with dk.sdu.cbse.bullet.BulletControlSystem;
    provides dk.sdu.cbse.common.services.IEntityProcessingService with dk.sdu.cbse.bullet.BulletControlSystem;
}