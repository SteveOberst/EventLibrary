package net.sxlver.eventlibrary.common.exception;

public class HandlerInvocationException extends EventException{
    public HandlerInvocationException(final String message) {
        super(message);
    }

    public HandlerInvocationException(final Throwable throwable) {
        super(throwable.getMessage(), throwable);
    }
}
