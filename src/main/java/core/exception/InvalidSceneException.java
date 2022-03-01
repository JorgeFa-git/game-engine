package core.exception;

import core.util.LogHandler;

public class InvalidSceneException extends RuntimeException {
    private static final long serialVersionUID = 6L;

    public InvalidSceneException() {
        logError();
        super.printStackTrace();
        System.exit(-1);
    }

    private void logError() {
        LogHandler.error("Invalid scene selected");
    }
}
