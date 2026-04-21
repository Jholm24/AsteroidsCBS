module dk.sdu.cbse.core {
    requires javafx.controls;
    requires javafx.graphics;
    requires spring.context;
    requires dk.sdu.cbse.common;
    requires java.desktop;

    exports dk.sdu.cbse.main;
    opens dk.sdu.cbse.main to spring.core, spring.beans, spring.context;

    uses dk.sdu.cbse.common.services.IGamePluginService;
    uses dk.sdu.cbse.common.services.IEntityProcessingService;
    uses dk.sdu.cbse.common.services.IPostEntityProcessingService;
}