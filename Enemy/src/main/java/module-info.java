module Enemy {
    requires dk.sdu.cbse.common;
    requires CommonBullet;
    requires javafx.graphics;

    uses dk.sdu.cbse.commonbullet.BulletSPI;

    provides dk.sdu.cbse.common.services.IEntityProcessingService with dk.sdu.cbse.enemy.EnemyControl;
    provides dk.sdu.cbse.common.services.IGamePluginService with dk.sdu.cbse.enemy.EnemyPlugin;
}