module Bullet {
    requires dk.sdu.cbse.common;
    requires java.desktop;
    requires Asteroids;
    requires javafx.graphics;

    provides dk.sdu.cbse.common.services.IEntityProcessingService with dk.sdu.cbse.bullet.BulletProccessor;
    provides dk.sdu.cbse.common.services.IPostEntityProcessingService with dk.sdu.cbse.bullet.BulletProccessor;

    exports dk.sdu.cbse.bullet;
}