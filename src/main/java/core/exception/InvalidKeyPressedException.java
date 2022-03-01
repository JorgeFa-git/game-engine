package core.exception;

import core.util.LogHandler;

public class InvalidKeyPressedException extends RuntimeException {
    private static final long serialVersionUID = 5L;

    public InvalidKeyPressedException() {
        logError();
        super.printStackTrace();
    }

    private void logError() {
        LogHandler.error("The pressed key is not a valid");
    }
}
