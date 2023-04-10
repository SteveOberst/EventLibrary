package net.sxlver.eventlibrary.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public abstract class AEvent<THIS extends AEvent<THIS>> {
    protected IEventHandler<THIS> eventHandler;

    /**
     * Used internally to set the {@code event handler} for the current scope.
     *
     * @param handler the current handler
     */
    @ApiStatus.Internal
    public void injectHandler(final @Nullable IEventHandler<THIS> handler) {
        this.eventHandler = handler;
    }

    /**
     * Returns the {@code IEventHandler} representing the current execution scope.
     *
     * @return The Handler representing the current execution context
     */
    public IEventHandler<THIS> getCurrentHandler() {
        return eventHandler;
    }
}
