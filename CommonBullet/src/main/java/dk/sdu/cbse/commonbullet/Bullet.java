package dk.sdu.cbse.commonbullet;

import dk.sdu.cbse.common.data.Entity;

public class Bullet extends Entity {

    private String ownerId;
    private double speed = 3.0;

    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public String getOwnerId() { return ownerId; }

    public void setSpeed(double speed) { this.speed = speed; }
    public double getSpeed() { return speed; }
}
