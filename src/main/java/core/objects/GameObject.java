package core.objects;

import core.Transform;
import core.components.Component;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private String name;
    private List<Component> components = new ArrayList<>();
    public Transform transform;
    private int zIndex;

    public GameObject(String name) {
        this.name = name;
        this.transform = new Transform();
        this.zIndex = 0;
    }

    public GameObject(String name, Transform transform, int zIndex) {
        this.name = name;
        this.transform = transform;
        this.zIndex = zIndex;
    }

    public void addComponent(Component c) {
        this.components.add(c);
        c.gameObject = this;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for(Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public void update(float dt) {
        components.forEach(component -> {
            component.update(dt);
        });
    }

    public void start() {
        components.forEach(Component::start);
    }

    public int getzIndex() {
        return zIndex;
    }

    public void imgui() {
        for (Component c : components) {
            c.imgui();
        }
    }
}
