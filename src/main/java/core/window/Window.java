package core.window;

import core.exception.InvalidSceneException;
import core.listeners.KeyListener;
import core.listeners.MouseListener;
import core.scene.Scene;
import core.util.Time;
import game.scenes.LevelEditorScene;
import game.scenes.LevelScene;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
    private static Window instance;
    private long windowHandle;
    private int windowWidth, windowHeight;
    private boolean isVsync;
    private final String windowTitle;

    public static float r, g, b;

    private static Scene currentScene = null;

    public Window() {
        this.isVsync = true;
        this.windowWidth = 1920;
        this.windowHeight = 1080;
        this.windowTitle = "Something went bad";
    }

    public Window(int windowWidth, int windowHeight, String windowTitle, boolean isVsync) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.windowTitle = windowTitle;
        this.isVsync = isVsync;

        r = 0;
        g = 0;
        b = 0;
    }

    public static Window get() {
        if (instance == null) {
            instance = new Window();
        }

        return instance;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                currentScene.start();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                currentScene.start();
                break;
            default:
                throw new InvalidSceneException();
        }
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        windowHandle = glfwCreateWindow(windowWidth, windowHeight, windowTitle, NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        setAllCallbacks();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(windowHandle, pWidth, pHeight);

            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            if (vidMode != null) {
                glfwSetWindowPos(windowHandle, (vidMode.width()) - pWidth.get(0) / 2, (vidMode.height()) - pHeight.get(0) / 2);
            }
        }

        glfwMakeContextCurrent(windowHandle);

        if (isVsync) {
            glfwSwapInterval(1);
        }


        glfwShowWindow(windowHandle);

        // IMPORTANT: Without this a lot of functions won't work
        GL.createCapabilities();

        Window.changeScene(0);
    }

    public void loop() {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1;


        while (!glfwWindowShouldClose(windowHandle)) {
            glClearColor(r,g,b,1f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (dt >= 0) {
                currentScene.update(dt);
            }

            glfwSwapBuffers(windowHandle);

            glfwPollEvents();

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    private void setAllCallbacks() {
        glfwSetKeyCallback(windowHandle, (windowHandle, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(windowHandle, true);
            }
        });

        glfwSetFramebufferSizeCallback(windowHandle, (windowHandle, width, height) -> {
           this.windowWidth = width;
           this.windowHeight = height;
        });

        glfwSetCursorPosCallback(windowHandle, MouseListener::mousePosCallback);
        glfwSetScrollCallback(windowHandle, MouseListener::mouseScrollCallback);
        glfwSetMouseButtonCallback(windowHandle, MouseListener::mouseButtonCallback);
        glfwSetKeyCallback(windowHandle, KeyListener::keyCallback);
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }
}
