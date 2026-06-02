module Collision {
    exports dk.sdu.cbse.collision;

    requires dk.sdu.cbse.common;
    requires java.datatransfer;
    requires java.desktop;
    requires javafx.graphics;

    // Import the concrete entity types
    requires CommonAsteroid;
    requires CommonBullet;

    uses dk.sdu.cbse.commonasteroid.AsteroidSPI;

    provides dk.sdu.cbse.common.services.IPostEntityProcessingService with dk.sdu.cbse.collision.Collision;
}

