package core.util;

import core.components.Spritesheet;
import core.renderer.Shader;
import core.renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaderPool = new HashMap<>();
    private static Map<String, Texture> texturePool = new HashMap<>();
    private static Map<String, Spritesheet> spriteSheetPool = new HashMap<>();

    public static Shader getShader(String resourceName) {
        File file = new File(resourceName);
        if (AssetPool.shaderPool.containsKey(file.getAbsolutePath())) {
            return shaderPool.get(file.getAbsolutePath());
        }

        Shader shader = new Shader(resourceName);
        shader.compileAndLinkShaders();
        AssetPool.shaderPool.put(file.getAbsolutePath(), shader);
        return shader;
    }

    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        if (AssetPool.texturePool.containsKey(file.getAbsolutePath())) {
            return texturePool.get(file.getAbsolutePath());
        }

        Texture texture = new Texture().init(resourceName);
        AssetPool.texturePool.put(file.getAbsolutePath(), texture);
        return texture;
    }

    public static void addSpritesheet(String resourceName, Spritesheet spritesheet) {
        File file = new File(resourceName);
        if (!AssetPool.spriteSheetPool.containsKey(file.getAbsolutePath())) {
            AssetPool.spriteSheetPool.put(file.getAbsolutePath(), spritesheet);
        }
    }

    public static Spritesheet getSpritesheet(String resourceName) {
        File file = new File(resourceName);

        assert AssetPool.spriteSheetPool.containsKey(file.getAbsolutePath()) : "Spritesheet not found";

        return AssetPool.spriteSheetPool.getOrDefault(file.getAbsolutePath(), null);
    }
}
