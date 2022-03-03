package core.scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.camera.Camera;
import core.components.Component;
import core.components.ComponentSerializer;
import core.objects.GameObject;
import core.objects.GameObjectSerializer;
import core.renderer.Renderer;
import imgui.ImGui;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected GameObject activeGameObject = null;
    protected boolean levelLoaded = false;

    public Scene() {}

    public void init() {

    }

    public void start() {
        gameObjects.forEach(go -> {
            go.start();
            this.renderer.add(go);
        });
        this.isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }
    }

    public abstract void update(float dt);

    public Camera getCamera() {
        return camera;
    }

    public void sceneImgui() {
        if (activeGameObject != null) {
            ImGui.begin("Inspector");
            activeGameObject.imgui();
            ImGui.end();
        }

        imgui();
    }

    public void imgui() {

    }

    public void saveExit() {
        // TODO: Write my own serializer
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(GameObject.class, new GameObjectSerializer())
                .registerTypeAdapter(Component.class, new ComponentSerializer())
                .create();

        try {
            FileWriter writer = new FileWriter("level.txt");
            writer.write(gson.toJson(this.gameObjects));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        // TODO: Write my own serializer
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(GameObject.class, new GameObjectSerializer())
                .registerTypeAdapter(Component.class, new ComponentSerializer())
                .create();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inFile.equals("")) {
            GameObject[] objects = gson.fromJson(inFile, GameObject[].class);

            for (GameObject object : objects) {
                addGameObjectToScene(object);
            }

            this.levelLoaded = true;
        }
    }
}
