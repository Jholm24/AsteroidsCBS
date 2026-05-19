package dk.sdu.cbse.enemy;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.entities.enemy.IEnemy;
import javafx.scene.paint.Color;

public class Enemy extends Entity implements IEnemy {

    private long lastShootTime = System.currentTimeMillis();
    private long lastDirectionChangeTime = 0;
    private double rotationRate = 0;

    public Enemy() {
        setHealth(10);
        setRadius(10);
        setColor(Color.RED);
        setImmuneDurationMs(1000);
    }

    public long getLastShootTime() { return lastShootTime; }
    public void setLastShootTime(long time) { lastShootTime = time; }

    public long getLastDirectionChangeTime() { return lastDirectionChangeTime; }
    public void setLastDirectionChangeTime(long time) { lastDirectionChangeTime = time; }

    public double getRotationRate() { return rotationRate; }
    public void setRotationRate(double rate) { rotationRate = rate; }
}
