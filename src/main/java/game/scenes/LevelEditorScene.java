package game.scenes;

import core.*;
import core.camera.Camera;
import core.components.SpriteRenderer;
import core.components.Spritesheet;
import core.objects.GameObject;
import core.scene.Scene;
import core.util.AssetPool;
import imgui.ImGui;
import org.joml.Vector2f;

public class LevelEditorScene extends Scene {

    private GameObject obj;
    private Spritesheet spritesheet;

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f(-250, 0));

        if (levelLoaded) {
            return;
        }

        spritesheet = AssetPool.getSpritesheet("src/main/resources/assets/images/spritesheet.png");

        loadGameObjects();
    }

    @Override
    public void update(float dt) {
        this.gameObjects.forEach(go -> go.update(dt));
        this.renderer.render();
    }

    @Override
    public void imgui() {
        ImGui.begin("Test Window");
        ImGui.text("Some random test");
        ImGui.end();
    }

    private void loadResources() {
        AssetPool.getShader("src/main/resources/shaders/default.glsl");

        AssetPool.addSpritesheet("src/main/resources/assets/images/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("src/main/resources/assets/images/spritesheet.png"), 16, 16, 26, 0));
    }

    private void loadGameObjects() {
        obj = new GameObject("Object 1", new Transform(new Vector2f(100 + 10, 100), new Vector2f(64, 64)), 0);
        obj.addComponent(new SpriteRenderer().setSprite(spritesheet.getSprite(0)));
        addGameObjectToScene(obj);
        this.activeGameObject = obj;
    }
}
