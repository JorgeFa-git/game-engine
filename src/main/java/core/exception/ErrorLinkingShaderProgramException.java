package core.exception;

import core.util.LogHandler;

public class ErrorLinkingShaderProgramException extends RuntimeException {
    private static final long serialVersionUID = 3L;

    public ErrorLinkingShaderProgramException(String infoLog) {
        logError("Linking error " + infoLog);
        super.printStackTrace();
        System.exit(-1);
    }

    private void logError(String infoLog) {
        LogHandler.error(infoLog);
    }
}
