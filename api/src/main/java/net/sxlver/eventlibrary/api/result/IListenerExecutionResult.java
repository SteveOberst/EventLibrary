package net.sxlver.eventlibrary.api.result;

import net.sxlver.eventlibrary.api.IEventHandler;
import net.sxlver.eventlibrary.api.AEvent;
import net.sxlver.eventlibrary.common.exception.EventException;

public interface IListenerExecutionResult<T extends AEvent<T>> {
    boolean ok();

    boolean error();

    boolean shouldCancel();

    boolean shouldContinue();

    IEventHandler<T> getHandler();

    <A extends EventException> ErrorReport<A, T> getError();
}
