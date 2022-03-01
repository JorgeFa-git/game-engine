package game.scenes;

import core.scene.Scene;
import core.window.Window;

public class LevelScene extends Scene {
    public LevelScene() {
        System.out.println("Inside level scene");
        Window.r = 1;
        Window.g = 1;
        Window.b = 1;
    }

    @Override
    public void update(float dt) {

    }
}
