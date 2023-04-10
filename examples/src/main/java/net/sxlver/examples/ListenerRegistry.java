package net.sxlver.examples;

import net.sxlver.eventlibrary.api.IEventHandler;
import net.sxlver.eventlibrary.api.annotation.EventPriority;
import net.sxlver.eventlibrary.core.EventLibrary;
import net.sxlver.eventlibrary.core.result.ListenerExecutionResult;

import java.time.Instant;

public class ListenerRegistry {

    /*
    function in your code the registers all of your event handlers (listeners)=
     */
    void registerListeners() {
        // registering event handlers is as simple as it gets...
        final EventListener listener = new EventListener();
        EventLibrary.registerListener(listener);

        // To unregister event handlers simply call the EventLibrary#unregister function on them
        EventLibrary.unregisterListener(listener);

        // we may also register anonymous handlers
        final IEventHandler<CustomEvent> handler = EventLibrary.registerListener(event -> {
            System.out.printf("[%s] A user has logged in! Status: %s", Instant.ofEpochMilli(event.getTimeOfLogin()), event.getState());
            return ListenerExecutionResult.success(event);
        },  CustomEvent.class, EventPriority.HIGH, Short.MAX_VALUE, false);
    }
}
