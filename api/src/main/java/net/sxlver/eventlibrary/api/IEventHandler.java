package net.sxlver.eventlibrary.api;

import net.sxlver.eventlibrary.api.annotation.EventPriority;
import net.sxlver.eventlibrary.api.result.IListenerExecutionResult;

public interface IEventHandler<T extends AEvent<T>> {
    IListenerExecutionResult<T> onEvent(final T event);

    default Class<T> getEvent() { return null; }

    default EventPriority getPriority() {
        return EventPriority.DEFAULT;
    }

    default int getPriorityOrdinal() {
        return getPriority().ordinal();
    }

    default int getWeight() {
        return 1;
    }

    default boolean ignoreCancelled() {
        return false;
    }

    int hashCode();
}
