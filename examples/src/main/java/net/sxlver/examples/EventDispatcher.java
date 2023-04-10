package net.sxlver.examples;

import net.sxlver.eventlibrary.api.AEvent;
import net.sxlver.eventlibrary.core.EventLibrary;

public class EventDispatcher {

    public <T extends AEvent<T>> void dispatchEvent(final T event) {
        // Events can be dispatched via EventLibrary#dispatchEvent.
        // This will invoke every event handler listening to this event.
        EventLibrary.dispatchEvent(event);
    }
}
