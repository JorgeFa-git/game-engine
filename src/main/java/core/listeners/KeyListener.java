package core.listeners;

import core.exception.InvalidKeyPressedException;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
    private static KeyListener instance;
    private boolean[] keyPressed = new boolean[GLFW_KEY_LAST];

    private KeyListener() {}

    private static KeyListener get() {
        if (instance == null) {
            instance = new KeyListener();
        }

        return instance;
    }

    public static void keyCallback(long windowHandle, int key, int scancode, int action, int mods) {
        if (key < get().keyPressed.length) {
            if (action == GLFW_PRESS) {
                get().keyPressed[key] = true;
            } else if (action == GLFW_RELEASE) {
                get().keyPressed[key] = false;
            }
        }
    }

    public static boolean isKeyPressed(int keyCode) {
        if (keyCode > get().keyPressed.length) {
            throw new InvalidKeyPressedException();
        }

        return get().keyPressed[keyCode];
    }
}
