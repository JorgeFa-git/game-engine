package game.scenes;

import core.*;
import core.camera.Camera;
import core.components.SpriteRenderer;
import core.components.Spritesheet;
import core.listeners.KeyListener;
import core.objects.GameObject;
import core.scene.Scene;
import core.util.AssetPool;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorScene extends Scene {

    private GameObject obj;
    private Spritesheet spritesheet;

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        loadResources();

        spritesheet = AssetPool.getSpritesheet("src/main/resources/assets/spritesheet.png");

        loadGameObjects();

        this.camera = new Camera(new Vector2f(-250, 0));

    }

    Transform lastTransform;

    @Override
    public void update(float dt) {
        int moveSpeed = 55;

        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            moveSpeed = 100;
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
            lastTransform = obj.transform;
            obj.transform.position.x -= moveSpeed * dt;
            moveAnimation(dt);
        } else if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
            lastTransform = obj.transform;
            obj.transform.position.x += moveSpeed * dt;
            moveAnimation(dt);
        } else {
            obj.getComponent(SpriteRenderer.class).setSprite(spritesheet.getSprite(0));
        }

        this.gameObjects.forEach(go -> go.update(dt));
        this.renderer.render();
    }

    private void loadResources() {
        AssetPool.getShader("src/main/resources/shaders/default.glsl");

        AssetPool.addSpritesheet("src/main/resources/assets/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("src/main/resources/assets/spritesheet.png"), 16, 16, 26, 0));
    }

    private void loadGameObjects() {
        obj = new GameObject("Object 1", new Transform(new Vector2f(100 + 10, 100), new Vector2f(64, 64)));
        obj.addComponent(new SpriteRenderer(spritesheet.getSprite(0)));
        addGameObjectToScene(obj);
    }

    int spriteIndex = 0;
    float spriteFlipTime = 0.1f;
    float spriteFlipTimeLeft = 0;

    private void moveAnimation(float dt) {
        spriteFlipTimeLeft -= dt;
        if (spriteFlipTimeLeft <= 0) {
            spriteFlipTimeLeft = spriteFlipTime;
            spriteIndex++;

            if (spriteIndex > 3) {
                spriteIndex = 1;
            }

            obj.getComponent(SpriteRenderer.class).setSprite(spritesheet.getSprite(spriteIndex));
        }
    }
}
