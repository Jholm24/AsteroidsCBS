package dk.sdu.cbse.common.data;

import java.util.ArrayDeque;
import java.util.Queue;

public class GameData {

    private int displayWidth = 1000;
    private int displayHeight = 900;
    private final GameKeys keys = new GameKeys();
    private float delta;
    private final Queue<Long> pendingAsteroidSpawns = new ArrayDeque<>();

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

    private boolean gameOver = false;

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    private int destroyedAsteroids = 0;

    public int getDestroyedAsteroids() {
        return destroyedAsteroids;
    }

    public void incrementDestroyedAsteroids() {
        destroyedAsteroids++;
    }

    public void resetDestroyedAsteroids() {
        destroyedAsteroids = 0;
    }

    public void addPendingAsteroidSpawn(long spawnAtMs) {
        pendingAsteroidSpawns.add(spawnAtMs);
    }

    public Queue<Long> getPendingAsteroidSpawns() {
        return pendingAsteroidSpawns;
    }

    public void clearPendingAsteroidSpawns() {
        pendingAsteroidSpawns.clear();
    }

}