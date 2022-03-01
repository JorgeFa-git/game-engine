package core.renderer;

import core.window.Window;
import core.components.SpriteRenderer;
import core.util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {
    /*
        Vertex
        =============
            Pos                    Color                      tex coords       tex id
        float, float,       float, float, float, float,      float, float       float
    */
    private final int VERTICES_QUANTITY = 4;

    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 9;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private Shader shader;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;

    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};
    private List<Texture> textureList;
    private float[] vertices;
    private int vaoId, vboId;
    private int maxBatchSize;

    boolean rebufferData = false;

    public RenderBatch(int maxBatchSize) {
        shader = AssetPool.getShader("src/main/resources/shaders/default.glsl");
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        vertices = new float[maxBatchSize * VERTICES_QUANTITY * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        this.textureList = new ArrayList<>();
    }

    public void start() {
        // -----------------------------------------------------------------------------------------
        // Generate VAO, VBO, EBO and send to GPU
        // -----------------------------------------------------------------------------------------
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Allocate space for vertices
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, generateIndices(), GL_STATIC_DRAW);

        // Enable the buffer attribute pointers

        enableVertexAttribPointers(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        enableVertexAttribPointers(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        enableVertexAttribPointers(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        enableVertexAttribPointers(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
    }

    public void addSprite(SpriteRenderer sprite) {
        // Get index and add the renderObject
        int index = this.numSprites;
        this.sprites[index] = sprite;
        this.numSprites++;

        if (sprite.getTexture() != null) {
            if (!textureList.contains(sprite.getTexture())) {
                textureList.add(sprite.getTexture());
            }
        }

        // Add the properties to local vertices array
        loadVertexProperties(index);

        if (numSprites >= this.maxBatchSize) this.hasRoom = false;
    }

    public void render() {

        checkForDirtySprites();

        rebufferDataIfNeeded();

        // Use shader
        shader.use();

        uploadUniforms();

        textureList.forEach(texture -> {
            glActiveTexture(GL_TEXTURE0 + textureList.indexOf(texture) + 1);
            texture.bind();
        });

        drawnElements();

        textureList.forEach(Texture::unbind);

        shader.detach();
    }

    private void checkForDirtySprites() {
        for (int i = 0; i < numSprites; i++) {
            SpriteRenderer spr = sprites[i];
            if (spr.isDirty()) {
                loadVertexProperties(i);
                spr.setClean();
                rebufferData = true;
            }
        }
    }

    private void rebufferDataIfNeeded() {
        if (rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }
    }

    private void uploadUniforms() {
        shader.uploadMat4F("uProj", Window.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4F("uView", Window.getCurrentScene().getCamera().getViewMatrix());
        shader.uploadIntArray("uTextures", texSlots);
    }

    private void drawnElements() {
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    private void enableVertexAttribPointers(int index, int size, int type, boolean normalizedData, int stride, long offset) {
        glVertexAttribPointer(index, size, type, normalizedData, stride, offset);
        glEnableVertexAttribArray(index);
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer spriteRenderer = this.sprites[index];

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = spriteRenderer.getColor();
        Vector2f[] texCoords = spriteRenderer.getTexCoords();

        int texId = 0;

        if (spriteRenderer.getTexture() != null) {
            texId = textureList.indexOf(spriteRenderer.getTexture()) + 1;
        }

        /* Add vertices with the appropriate properties

                 (3)    (0)
                    x  x
                    x  x
                 (2)    (1)

         */

        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 1:
                    yAdd = 0f;
                    break;
                case 2:
                    xAdd = 0f;
                    break;
                case 3:
                    yAdd = 1f;
                    break;
                default:
                    break;
            }

            // Load position, first to elements of the array
            vertices[offset] = spriteRenderer.gameObject.transform.position.x + (xAdd * spriteRenderer.gameObject.transform.scale.x);
            vertices[offset + 1] = spriteRenderer.gameObject.transform.position.y + (yAdd * spriteRenderer.gameObject.transform.scale.y);

            // Load color
            vertices[offset + 2] = color.x; // R
            vertices[offset + 3] = color.y; // G
            vertices[offset + 4] = color.z; // B
            vertices[offset + 5] = color.w; // A

            // Load texure coords
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;

            // Load texture id
            vertices[offset + 8] = texId;

            offset += VERTEX_SIZE;

        }
    }

    // IMPORTANT: Generate indices
    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset; // + 0
        elements[offsetArrayIndex + 3] = offset; // + 0
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }

    public boolean hasTextureRoom() {
        return this.textureList.size() < 8;
    }

    public boolean hasTexture(Texture texture) {
        return this.textureList.contains(texture);
    }
}
