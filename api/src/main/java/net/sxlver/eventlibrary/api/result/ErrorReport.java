package net.sxlver.eventlibrary.api.result;

import net.sxlver.eventlibrary.api.AEvent;
import net.sxlver.eventlibrary.common.exception.EventException;
import net.sxlver.eventlibrary.common.exception.HandlerInvocationException;

import java.io.PrintStream;

public class ErrorReport<A extends Throwable, B extends AEvent<B>> {

    public static ErrorReport<Throwable, ? extends AEvent> EMPTY = new ErrorReport<>(new Throwable());

    private final A error;
    private final String cause;
    private StackTraceElement[] stacktrace;

    public ErrorReport(final A error) {
        this.error = error;
        this.cause = error.getMessage();
        this.stacktrace = error.getStackTrace();
    }

    public A getError() {
        return error;
    }

    public String getCause() {
        return cause;
    }

    public StackTraceElement[] getStackTrace() {
        return stacktrace;
    }

    public void printStackTrace() {
        new HandlerInvocationException(error).printStackTrace();
    }
}
