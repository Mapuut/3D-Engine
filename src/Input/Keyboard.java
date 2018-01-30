package Input;

import org.lwjgl.glfw.GLFWKeyCallback;
import renderEngine.window.Window;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class Keyboard extends GLFWKeyCallback {
    private static final int NUM_OF_KEYS = 65536; //max key id that will be included in game logic.

    private static boolean[] realTimeKeys = new boolean[NUM_OF_KEYS];
    private static boolean[] currentKeys = new boolean[NUM_OF_KEYS];
    private static boolean[] lastUpdateKeys = new boolean[NUM_OF_KEYS];

    // The GLFWKeyCallback class is an abstract method that
    // can't be instantiated by itself and must instead be extended
    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key >= NUM_OF_KEYS) return;
        if (Window.DEBUG) System.out.println("The key number that were pressed: " + key);

        realTimeKeys[key] = (action != GLFW_RELEASE);
    }

    /**Update keys for game logic.*/
    public static void update() {
        for (int i = 0; i < NUM_OF_KEYS; i++) {
            lastUpdateKeys[i] = currentKeys[i];
            currentKeys[i] =  realTimeKeys[i];
        }
    }

    /**Return true if currently key is pressed.*/
    public static boolean isKeyDown(int keycode) {
        return currentKeys[keycode];
    }

    /**Return true if currently key is pressed, but last update it was not.*/
    public static boolean isKeyPressed(int keycode) {
        return currentKeys[keycode] && !lastUpdateKeys[keycode];
    }

    /**Return true if currently key is released, but last update it was not.*/
    public static boolean isKeyReleased(int keycode) {
        return !currentKeys[keycode] && lastUpdateKeys[keycode];
    }

}