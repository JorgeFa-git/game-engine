package core.components;

import core.objects.GameObject;

public abstract class Component {
    public transient GameObject gameObject = null;

    public void start() {

    }

    public void update(float dt) {

    };

    public void imgui() {

    }
}
