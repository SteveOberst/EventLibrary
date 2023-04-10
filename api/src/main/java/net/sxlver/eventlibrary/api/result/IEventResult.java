package net.sxlver.eventlibrary.api.result;

import lombok.NonNull;
import net.sxlver.eventlibrary.api.AEvent;

import java.util.LinkedList;
import java.util.Stack;

public interface IEventResult<T extends AEvent<T>> {

    T getEvent();

    boolean cancelled();

    @NonNull
    LinkedList<IListenerExecutionResult<T>> getExecutionStack();

}
