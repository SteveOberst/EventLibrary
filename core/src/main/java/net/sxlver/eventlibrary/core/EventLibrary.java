package net.sxlver.eventlibrary.core;

import com.google.common.collect.Maps;
import lombok.NonNull;
import net.sxlver.eventlibrary.api.IEventHandler;
import net.sxlver.eventlibrary.api.annotation.EventPriority;
import net.sxlver.eventlibrary.api.AEvent;
import net.sxlver.eventlibrary.api.result.IEventResult;
import net.sxlver.eventlibrary.api.result.IListenerExecutionResult;
import net.sxlver.eventlibrary.common.Reflect;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class EventLibrary {

    /**
     * Maps a {@link HandlerList} to each {@code Event} keeping track of all registered handlers
     */
    private static final Map<Class<? extends AEvent<?>>, HandlerList<?>> HANDLER_LIST_MAP = Maps.newConcurrentMap();

    /**
     * Creates handlers for each method annotated with {@code EventSubscriber} and registers them so that
     * they will receive events.
     *
     * @param listener Any object that contains methods annotated with {@link net.sxlver.eventlibrary.common.annotation.EventSubscriber}
     * @param <T>      The type of event the handler will receive.
     */
    public static <T extends AEvent<T>> void registerListener(final Object listener) {
        final Collection<Method> subscriber = Reflect.getSubscriber(listener);
        for (final Method method : subscriber) {
            final HandlerList.Handler<T> handler = (HandlerList.Handler<T>) HandlerList.makeHandler(listener, method);
            final HandlerList<T> handlerList = getOrCreateHandlerList(handler.getEvent());
            handlerList.registerHandler(handler);
        }
    }

    /**
     * Wraps an anonymous event handler to {@link net.sxlver.eventlibrary.core.HandlerList.AnonymousHandler} and registers it.
     *
     * @param handler handler that will be wrapped and registered
     *
     * @return an {@link net.sxlver.eventlibrary.core.HandlerList.AnonymousHandler} wrapping the handler passed in
     *
     * @param <T> The type of event the listener will receive
     * @see #registerListener(Function, Class, EventPriority, int, boolean)
     */

    public static <T extends AEvent<T>> IEventHandler<T> registerListener(final @NonNull Function<T, IListenerExecutionResult<T>> handler,
                                                                          final @NonNull Class<T> event) {
        return registerListener(handler, event, EventPriority.DEFAULT);
    }

    /**
     * Wraps an anonymous event handler to {@link net.sxlver.eventlibrary.core.HandlerList.AnonymousHandler} and registers it.
     *
     * @param handler  handler that will be wrapped and registered
     * @param priority the handlers listening priority
     *
     * @return an {@link net.sxlver.eventlibrary.core.HandlerList.AnonymousHandler} wrapping the handler passed in
     *
     * @param <T> The type of event the listener will receive
     * @see #registerListener(Function, Class, EventPriority, int, boolean)
     */
    public static <T extends AEvent<T>> IEventHandler<T> registerListener(final @NonNull Function<T, IListenerExecutionResult<T>> handler,
                                                                          final @NonNull Class<T> event, final @NonNull EventPriority priority) {
        return registerListener(handler, event, priority, 1);
    }

    /**
     * Wraps an anonymous event handler to {@link net.sxlver.eventlibrary.core.HandlerList.AnonymousHandler} and registers it.
     *
     * @param handler  handler that will be wrapped and registered, not null
     * @param priority the handlers listening priority, not null
     * @param weight   the handlers listening weight
     *
     * @return an {@link net.sxlver.eventlibrary.core.HandlerList.AnonymousHandler} wrapping the handler passed in
     *
     * @param <T> The type of event the listener will receive
     * @see #registerListener(Function, Class, EventPriority, int, boolean)
     */
    public static <T extends AEvent<T>> IEventHandler<T> registerListener(final @NonNull Function<T, IListenerExecutionResult<T>> handler,
                                                                          final @NonNull Class<T> event, final @NonNull EventPriority priority,
                                                                          final int weight) {
        return registerListener(handler, event, priority, weight, false);
    }

    /**
     * Wraps an anonymous event handler to {@link net.sxlver.eventlibrary.core.HandlerList.AnonymousHandler} and registers it.
     * <p>
     * The passed in anonymous class will be wrapped into an {@link net.sxlver.eventlibrary.core.HandlerList.AnonymousHandler}
     * and then registered in the cache. The returned value can be used to keep track of the handler and unregister
     * it later on.
     *
     * @param handler         handler that will be wrapped and registered, not null
     * @param priority        the handlers listening priority, not null
     * @param weight          the handlers listening weight
     * @param ignoreCancelled whether the handler will receive events that have been marked as cancelled
     *                        by other handlers
     *
     * @return an {@link net.sxlver.eventlibrary.core.HandlerList.AnonymousHandler} wrapping the handler passed in
     *
     * @param <T> The type of event the listener will receive
     */
    public static <T extends AEvent<T>> IEventHandler<T> registerListener(final @NonNull Function<T, IListenerExecutionResult<T>> handler,
                                                                          final @NonNull Class<T> event, final @NonNull EventPriority priority,
                                                                          final int weight, final boolean ignoreCancelled) {
        final IEventHandler<T> eventHandler = HandlerList.makeAnonymousHandler(handler, event, priority, weight, ignoreCancelled);
        final HandlerList<T> handlerList = getOrCreateHandlerList(event);
        handlerList.registerHandler(eventHandler);
        return eventHandler;
    }

    /**
     * Dispatches the event handlers in an asynchronous context.
     * <p>
     * Returns a {@link CompletableFuture} object which can be used to process the result.
     *
     * @param event event to be called
     * @return      An instance of the {@code CompletableFuture}
     */
    public static <T extends AEvent<T>> CompletableFuture<IEventResult<T>> dispatchEventAsync(final @NonNull T event) {
        return CompletableFuture.supplyAsync(() -> dispatchEvent(event));
    }

    /**
     * Dispatches an {@code event} and invokes all registered handlers.
     *
     * @param event event to be fired, not null
     * @return      an instance of {@link IEventResult} containing information about how handlers responded to the event.
     */
    public static <T extends AEvent<T>> IEventResult<T> dispatchEvent(final @NonNull T event) {
        final Class<T> type = (Class<T>) event.getClass();
        final HandlerList<T> handlerList = getOrCreateHandlerList(type);
        return handlerList.callHandlers(event);
    }

    /**
     * Unregisters all handlers within the instance so that they will no longer receive any events.
     *
     * @param listener the instance containing the handlers to be unregistered, not null
     */
    public static <T extends AEvent<T>> void unregisterListener(final @NonNull Object listener) {
        final Collection<Method> subscriber = Reflect.getSubscriber(listener);
        for (final Method method : subscriber) {
            final Class<T> event = (Class<T>) Reflect.getSubscriberTarget(method);
            final HandlerList<T> handlerList = getOrCreateHandlerList(event);
            final IEventHandler<T> handler = handlerList.getHandler(method);
            if(handler == null) continue;
            handlerList.unregisterHandler(handler);
        }
    }

    /**
     * Unregisters a single handler.
     * <p>
     * This method can be used to unregister anonymous handlers created through {@link #registerListener(Function, Class, EventPriority, int, boolean)}
     * 
     * @param handler the handler to be unregistered, not null
     * @see #registerListener(Function, Class, EventPriority, int, boolean) 
     */
    public static <T extends AEvent<T>> void unregisterHandler(final @NonNull IEventHandler<T> handler) {
        final HandlerList<T> handlerList = getOrCreateHandlerList(handler.getEvent());
        handlerList.unregisterHandler(handler);
    }

    public static <T extends AEvent<T>> HandlerList<T> getOrCreateHandlerList(final @NonNull Class<T> cls) {
        return (HandlerList<T>) HANDLER_LIST_MAP.computeIfAbsent(cls, HandlerList::new);
    }
}
