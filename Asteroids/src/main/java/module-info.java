module Asteroids {
    exports dk.sdu.cbse.asteroids;

    requires dk.sdu.cbse.common;
    requires java.datatransfer;
    requires java.desktop;
    requires javafx.graphics;

    provides dk.sdu.cbse.common.services.IGamePluginService with dk.sdu.cbse.asteroids.AsteroidsPlugin;
    provides dk.sdu.cbse.common.services.IEntityProcessingService with dk.sdu.cbse.asteroids.AsteroidsProcessor;
}