package dk.sdu.cbse.common.data;

public class GameData {

    private int displayWidth = 1000;
    private int displayHeight = 900;
    private final GameKeys keys = new GameKeys();
    private float delta;

    public GameKeys getKeys() {
        return keys;
    }

    public void setDisplayWidth(int width) {
        this.displayWidth = width;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayHeight(int height) {
        this.displayHeight = height;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public float getDelta() {
        return delta;
    }

}