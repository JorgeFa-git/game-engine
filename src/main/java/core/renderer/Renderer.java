package core.renderer;

import core.components.SpriteRenderer;
import core.objects.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> renderBatchList;

    public Renderer() {
        this.renderBatchList = new ArrayList<>();
    }

    public void add(GameObject gameObject) {
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        if (spriteRenderer != null) {
            add(spriteRenderer);
        }
    }

    private void add(SpriteRenderer spriteRenderer) {
        boolean added = false;

        for (RenderBatch batch : renderBatchList) {
            if (batch.hasRoom() && batch.getzIndex() == spriteRenderer.gameObject.getzIndex()) {
                Texture texture = spriteRenderer.getTexture();
                if (texture == null || (batch.hasTexture(texture) || batch.hasTextureRoom())) {
                    batch.addSprite(spriteRenderer);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, spriteRenderer.gameObject.getzIndex());
            newBatch.start();
            renderBatchList.add(newBatch);
            newBatch.addSprite(spriteRenderer);
            Collections.sort(renderBatchList);
        }
    }

    public void render() {
        renderBatchList.forEach(RenderBatch::render);
    }
}
