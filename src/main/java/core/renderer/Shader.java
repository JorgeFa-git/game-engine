package core.renderer;

import core.exception.ErrorCompilingFragmentShaderException;
import core.exception.ErrorCompilingVertexShaderException;
import core.exception.ErrorLinkingShaderProgramException;
import core.util.ShaderReader;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramId;

    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    private boolean isUsing;

    public Shader(String filepath) {
        this.filepath = filepath;
        ShaderReader shaderReader = new ShaderReader(filepath);
        vertexSource = shaderReader.getVertexSource();
        fragmentSource = shaderReader.getFragmentSource();
    }

    public void compileAndLinkShaders() {
        int vertexId, fragmentId;

        // -----------------------------------------------------------------------------------------
        // Compile and link shaders
        // Load and compile vertex shader
        // -----------------------------------------------------------------------------------------
        vertexId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexId, vertexSource);
        glCompileShader(vertexId);

        // Check for error
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int length = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            throw new ErrorCompilingVertexShaderException(glGetShaderInfoLog(vertexId, length));
        }

        // -----------------------------------------------------------------------------------------
        // Load and compile fragment shader
        // -----------------------------------------------------------------------------------------
        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentId, fragmentSource);
        glCompileShader(fragmentId);

        // Check for error
        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int length = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            throw new ErrorCompilingFragmentShaderException(glGetShaderInfoLog(fragmentId, length));
        }

        // -----------------------------------------------------------------------------------------
        // Link shaders and check for errors
        // -----------------------------------------------------------------------------------------
        shaderProgramId = glCreateProgram();
        glAttachShader(shaderProgramId, vertexId);
        glAttachShader(shaderProgramId, fragmentId);
        glLinkProgram(shaderProgramId);

        // Check for linking errors
        success = glGetProgrami(shaderProgramId, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int length = glGetProgrami(shaderProgramId, GL_INFO_LOG_LENGTH);
            throw new ErrorLinkingShaderProgramException(glGetProgramInfoLog(shaderProgramId, length));
        }
    }

    public void use() {
        // Bind shader program
        if (!isUsing) {
            glUseProgram(shaderProgramId);
            isUsing = true;
        }
    }

    public void detach() {
        // Detach shader program
        glUseProgram(0);
        isUsing = false;
    }

    public void uploadMat4F(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        glUniform1i(varLocation, val);
    }

    public void uploadIntArray(String varName, int[] array) {
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        glUniform1iv(varLocation, array);
    }


    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        glUniform1i(varLocation, slot);
    }
}
