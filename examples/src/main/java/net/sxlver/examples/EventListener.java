package net.sxlver.examples;

import net.sxlver.eventlibrary.api.annotation.EventPriority;
import net.sxlver.eventlibrary.api.annotation.Prioritized;
import net.sxlver.eventlibrary.api.result.IListenerExecutionResult;
import net.sxlver.eventlibrary.common.annotation.EventSubscriber;
import net.sxlver.eventlibrary.common.exception.EventException;
import net.sxlver.eventlibrary.core.result.ListenerExecutionResult;

import java.time.Instant;

public class EventListener {

    /*
    this is how a handler that receives events could look
     */
    @EventSubscriber
    // the Prioritized annotation can be used to give the listener a custom priority.
    // Priorities are used to determine which event handler will be invoked first
    @Prioritized(priority = EventPriority.HIGHEST, weight = 100)
    public IListenerExecutionResult<CustomEvent> onUserLogin(final CustomEvent event) {

        if(event.getUser() == null) {
            // something unexpected happened, throw an error but let other handlers receive the event
            return ListenerExecutionResult.fail(event, new EventException("User cannot be null."));
        }

        if(event.getState() == CustomEvent.LoginState.ERR_INTERNAL_ERROR) {
            // critical error encountered. Throw error and cancel event. Only handlers with
            // the ignoreCancelled option set to true will be able to further on process the event
            return ListenerExecutionResult.cancelWithError(event, new EventException("Internal error encountered."));
        }

        // log user login
        System.out.printf("Attempted login from user %s at %s. Status: %s%n", event.getUser().getUsername(), Instant.ofEpochMilli(event.getTimeOfLogin()), event.getState());

        // return success and pass the event on to the next handler
        return ListenerExecutionResult.success(event);
    }
}
