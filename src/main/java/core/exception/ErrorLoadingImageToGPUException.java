package core.exception;

import core.util.LogHandler;

public class ErrorLoadingImageToGPUException extends RuntimeException {
    private static final long serialVersionUID = 7L;

    public ErrorLoadingImageToGPUException() {
        logError();
        super.printStackTrace();
        System.exit(-1);
    }

    private void logError() {
        LogHandler.error("Could not load image to GPU");
    }
}
