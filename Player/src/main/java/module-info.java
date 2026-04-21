module Player {
    requires dk.sdu.cbse.common;
    requires java.datatransfer;
    requires java.desktop;
    requires javafx.graphics;
    requires CommonBullet;

    uses dk.sdu.cbse.commonbullet.BulletSPI;

    provides dk.sdu.cbse.common.services.IGamePluginService with dk.sdu.cbse.playersystem.PlayerPlugin;
    provides dk.sdu.cbse.common.services.IEntityProcessingService with dk.sdu.cbse.playersystem.PlayerControlSystem;
}