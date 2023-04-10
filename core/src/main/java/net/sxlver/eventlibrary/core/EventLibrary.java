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
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class EventLibrary {

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
            ensureHandlerListPresent(handler.getEvent());
            final HandlerList<T> handlerList = getHandlerList(handler.getEvent());
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
     * @param handler  handler that will be wrapped and registered
     * @param priority the handlers listening priority
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
     * @param handler         handler that will be wrapped and registered
     * @param priority        the handlers listening priority
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
        ensureHandlerListPresent(event);
        final IEventHandler<T> eventHandler = HandlerList.makeAnonymousHandler(handler, event, priority, weight, ignoreCancelled);
        final HandlerList<T> handlerList = getHandlerList(event);
        handlerList.registerHandler(eventHandler);
        return eventHandler;
    }

    public static <T extends AEvent<T>> void unregisterListener(final Object listener) {
        final Collection<Method> subscriber = Reflect.getSubscriber(listener);
        for (final Method method : subscriber) {
            final HandlerList.Handler<T> handler = (HandlerList.Handler<T>) HandlerList.makeHandler(listener, method);
            ensureHandlerListPresent(handler.getEvent());
            final HandlerList<T> handlerList = getHandlerList(handler.getEvent());
            handlerList.unregisterHandler(handler);
        }
    }

    public static <T extends AEvent<T>> void unregisterHandler(final IEventHandler<T> handler) {
        ensureHandlerListPresent(handler.getEvent());
        final HandlerList<T> handlerList = getHandlerList(handler.getEvent());
        handlerList.unregisterHandler(handler);
    }

    public static <T extends AEvent<T>> IEventResult<T> dispatchEvent(final @NonNull T event) {
        ensureHandlerListPresent(event.getClass());
        final Class<T> type = (Class<T>) event.getClass();
        final HandlerList<T> handlerList = getHandlerList(type);
        return handlerList.callHandlers(event);
    }

    public static <T extends AEvent<T>> HandlerList<T> getHandlerList(final Class<T> cls) {
        return (HandlerList<T>) HANDLER_LIST_MAP.get(cls);
    }

    private static <T extends AEvent<T>> void ensureHandlerListPresent(final Class<T> event) {
        HANDLER_LIST_MAP.computeIfAbsent(event, HandlerList::new);
    }
}
