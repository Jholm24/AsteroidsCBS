import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.common.services.IGamePluginService;
import dk.sdu.cbse.playersystem.PlayerControlSystem;
import dk.sdu.cbse.playersystem.PlayerPlugin;

module Player {
    requires dk.sdu.cbse.common;
    requires Bullet;
    requires java.datatransfer;
    requires java.desktop;
    requires javafx.graphics;
    provides IGamePluginService with PlayerPlugin;
    provides IEntityProcessingService with PlayerControlSystem;

}