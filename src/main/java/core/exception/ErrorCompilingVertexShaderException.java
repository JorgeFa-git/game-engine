package core.exception;

import core.util.LogHandler;

public class ErrorCompilingVertexShaderException extends RuntimeException {
    private static final long serialVersionUID = 2L;

    public ErrorCompilingVertexShaderException(String infoLog) {
        logError("Vertex Shader " + infoLog);
        super.printStackTrace();
        System.exit(-1);
    }

    private void logError(String infoLog) {
        LogHandler.error(infoLog);
    }
}
