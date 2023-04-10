package net.sxlver.eventlibrary.api.result;

import net.sxlver.eventlibrary.api.AEvent;

public class ErrorReport<A extends Throwable, B extends AEvent> {

    public static ErrorReport<Throwable, AEvent> EMPTY = new ErrorReport<>(new Throwable());

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
}
