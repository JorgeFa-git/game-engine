package core.exception;

import core.util.LogHandler;

public class ErrorNumberOfChannelsInvalidException extends RuntimeException {
    private static final long serialVersionUID = 4L;

    public ErrorNumberOfChannelsInvalidException(int numberOfChannels) {
        logError("Error: (Texture) Unknown number of channels '" + numberOfChannels + "'");
        super.printStackTrace();
        System.exit(-1);
    }

    private void logError(String infoLog) {
        LogHandler.error(infoLog);
    }
}
