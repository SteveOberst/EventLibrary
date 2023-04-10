package net.sxlver.eventlibrary.core.result;

import lombok.NonNull;
import net.sxlver.eventlibrary.api.result.IEventResult;
import net.sxlver.eventlibrary.api.result.IListenerExecutionResult;
import net.sxlver.eventlibrary.api.AEvent;
import net.sxlver.eventlibrary.common.exception.EventException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;

public class EventResult<A extends AEvent<A>> implements IEventResult<A> {

    private final A event;
    private boolean cancelled;
    private LinkedList<IListenerExecutionResult<A>> executionStack = new LinkedList<>();

    public EventResult(final A event, final Collection<IListenerExecutionResult<A>> executionResults) {
        this.event = event;
        processResults(executionResults);
    }

    private void processResults(final Collection<IListenerExecutionResult<A>> executionResults) {
        for (final IListenerExecutionResult<A> executionResult : executionResults) {
            executionStack.add(executionResult);

            if(executionResult.shouldCancel()) cancelled = true;
            else if(executionResult.shouldContinue()) cancelled = false;
        }
    }

    @Override
    public A getEvent() {
        return event;
    }

    @Override
    public boolean cancelled() {
        return false;
    }

    @Override
    public @NonNull LinkedList<IListenerExecutionResult<A>> getExecutionStack() {
        return executionStack;
    }
}
