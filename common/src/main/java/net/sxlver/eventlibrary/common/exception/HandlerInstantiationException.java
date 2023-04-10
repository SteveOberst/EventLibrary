package net.sxlver.eventlibrary.common.exception;

public class HandlerInstantiationException extends EventException {
    public HandlerInstantiationException(String message) {
        super(message);
    }

    public HandlerInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
