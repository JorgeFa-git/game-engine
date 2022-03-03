package core.objects;

import com.google.gson.*;
import core.Transform;
import core.components.Component;
import core.components.SpriteRenderer;

import java.lang.reflect.Type;

public class GameObjectSerializer implements JsonDeserializer<GameObject> {
    @Override
    public GameObject deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");
        Transform transform = jsonDeserializationContext.deserialize(jsonObject.get("transform"), Transform.class);
        int zindex = jsonDeserializationContext.deserialize(jsonObject.get("zIndex"), int.class);

        GameObject go = new GameObject(name, transform, zindex);

        for (JsonElement e : components) {
            Component c = jsonDeserializationContext.deserialize(e, Component.class);
            go.addComponent(c);
        }
        System.out.println(go.getComponent(SpriteRenderer.class).getColor());
        return go;
    }
}
