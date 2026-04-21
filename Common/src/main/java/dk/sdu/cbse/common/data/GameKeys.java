package dk.sdu.cbse.common.data;

public class GameKeys {

    private static boolean[] keys;
    private static boolean[] pkeys;

    private static final int NUM_KEYS = 4;
    public static final int W = 0;
    public static final int A = 1;
    public static final int D = 2;
    public static final int SPACE = 3;

    public GameKeys() {
        keys = new boolean[NUM_KEYS];
        pkeys = new boolean[NUM_KEYS];
    }

    public void update() {
        System.arraycopy(keys, 0, pkeys, 0, NUM_KEYS);
    }

    public void setKey(int k, boolean b) {
        keys[k] = b;
    }

    public boolean isDown(int k) {
        return keys[k];
    }

    public boolean isPressed(int k) {
        return keys[k] && !pkeys[k];
    }

}
