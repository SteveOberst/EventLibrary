package net.sxlver.eventlibrary.core.result;

import lombok.NonNull;
import net.sxlver.eventlibrary.api.IEventHandler;
import net.sxlver.eventlibrary.api.AEvent;
import net.sxlver.eventlibrary.api.result.ErrorReport;
import net.sxlver.eventlibrary.api.result.IListenerExecutionResult;
import net.sxlver.eventlibrary.common.exception.EventException;
import org.jetbrains.annotations.Nullable;

public class ListenerExecutionResult<T extends AEvent<T>> implements IListenerExecutionResult<T> {

    private static final int OK       = 0x1;
    private static final int CANCEL   = 0x2;
    private static final int CONTINUE = 0x4;
    private static final int ERROR    = 0x8;

    private final IEventHandler<T> handler;
    private int flags;
    private final ErrorReport<? extends EventException, T> error;

    ListenerExecutionResult(final IEventHandler<T> handler, final int flags) {
        this(handler, flags, null);
    }

    ListenerExecutionResult(final IEventHandler<T> handler, final int flags, final @Nullable ErrorReport<? extends EventException, T> error) {
        this.handler = handler;
        this.flags = flags;
        this.error = error;
    }

    @Override
    public boolean ok() {
        return (flags & OK) != 0;
    }

    @Override
    public boolean error() {
        return (flags & ERROR) != 0;
    }

    @Override
    public boolean shouldCancel() {
        return (flags & CANCEL) != 0;
    }

    @Override
    public boolean shouldContinue() {
        return (flags & CONTINUE) != 0;
    }

    @Override
    public IEventHandler<T> getHandler() {
        return handler;
    }

    @Override
    public @Nullable ErrorReport<? extends EventException, T> getError() {
        return error;
    }

    public static <T extends AEvent<T>> IListenerExecutionResult<T> success(final @NonNull AEvent<T> event) {
        return new ListenerExecutionResult<>(event.getCurrentHandler(), OK);
    }

    public static <T extends AEvent<T>> IListenerExecutionResult<T> fail(final @NonNull AEvent<T> event, final EventException exception) {
        return new ListenerExecutionResult<>(event.getCurrentHandler(), 0, new ErrorReport<>(exception));
    }

    public static <T extends AEvent<T>> IListenerExecutionResult<T> continueEvent(final @NonNull AEvent<T> event) {
        return new ListenerExecutionResult<>(event.getCurrentHandler(), OK | CONTINUE);
    }

    public static <T extends AEvent<T>> IListenerExecutionResult<T> cancel(final @NonNull AEvent<T> event) {
        return new ListenerExecutionResult<>(event.getCurrentHandler(), OK | CANCEL);
    }

    public static <T extends AEvent<T>> IListenerExecutionResult<T> cancelWithError(final @NonNull AEvent<T> event, final EventException exception) {
        return new ListenerExecutionResult<>(event.getCurrentHandler(), ERROR | CANCEL, new ErrorReport<>(exception));
    }
}
