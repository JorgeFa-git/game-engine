package core.exception;

import core.util.LogHandler;

public class ErrorCompilingFragmentShaderException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ErrorCompilingFragmentShaderException(String infoLog) {
        logError("Fragment Shader " + infoLog);
        super.printStackTrace();
        System.exit(-1);
    }

    private void logError(String infoLog) {
        LogHandler.error(infoLog);
    }
}
