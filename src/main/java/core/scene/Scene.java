package core.scene;

import core.camera.Camera;
import core.objects.GameObject;
import core.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    protected Renderer renderer = new Renderer();

    protected Camera camera;

    private boolean isRunning = false;

    protected List<GameObject> gameObjects = new ArrayList<>();

    public Scene() {}

    public void init() {

    }

    public void start() {
        gameObjects.forEach(go -> {
            go.start();
            this.renderer.add(go);
        });
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
}
