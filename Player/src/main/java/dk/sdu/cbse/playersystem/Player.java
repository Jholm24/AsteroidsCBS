package dk.sdu.cbse.playersystem;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.entities.player.IPlayer;

import javafx.scene.paint.Color;

public class Player extends Entity implements IPlayer {

    public Player() {
        this.setColor(Color.BLUE);
        this.setHealth(50);
        this.setImmuneDurationMs(3000);
    }

}
