package net.sxlver.examples;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.sxlver.eventlibrary.api.AEvent;

import java.util.UUID;

/**
 * Consider this a user login event
 *
 *
 */
public class CustomEvent extends AEvent<CustomEvent> {

    private final User user;
    private final LoginState state;
    private final long timeOfLogin;

    /*
    pass all information the event should hold to the constructor
     */
    public CustomEvent(final User user, final LoginState state, final long timeOfLogin) {
        this.user = user;
        this.state = state;
        this.timeOfLogin = timeOfLogin;
    }

    /*
    make the data held by this class accessible for handlers
     */
    public User getUser() {
        return user;
    }

    public LoginState getState() {
        return state;
    }

    public long getTimeOfLogin() {
        return timeOfLogin;
    }

    @Data
    @AllArgsConstructor
    class User {
        String username;
        UUID uuid;
    }

    enum LoginState {
        SUCCESS,
        ERR_WRONG_PASS,
        ERR_CONNECTION_THROTTLED,
        ERR_INTERNAL_ERROR
    }
}
