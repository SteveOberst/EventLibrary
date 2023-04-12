package net.sxlver.eventlibrary.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NonNull;
import net.sxlver.eventlibrary.api.IEventHandler;
import net.sxlver.eventlibrary.api.result.IEventResult;
import net.sxlver.eventlibrary.api.result.IListenerExecutionResult;
import net.sxlver.eventlibrary.api.AEvent;
import net.sxlver.eventlibrary.api.annotation.EventPriority;
import net.sxlver.eventlibrary.api.annotation.Prioritized;
import net.sxlver.eventlibrary.common.Reflect;
import net.sxlver.eventlibrary.common.Validator;
import net.sxlver.eventlibrary.common.annotation.EventSubscriber;
import net.sxlver.eventlibrary.common.exception.HandlerInstantiationException;
import net.sxlver.eventlibrary.common.exception.HandlerInvocationException;
import net.sxlver.eventlibrary.core.result.EventResult;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class HandlerList<T extends AEvent<T>> {

    private final Comparator<IEventHandler<T>> handlerWeightCmp = Comparator.<IEventHandler<T>>comparingInt(IEventHandler::getPriorityOrdinal)
            .thenComparingInt(IEventHandler::getWeight);

    private final Class<? extends AEvent<?>> cls;
    private final Map<HandlerKeyHash<T>, IEventHandler<T>> handlers;

    public HandlerList(Class<? extends AEvent<?>> cls) {
        this.cls = cls;
        this.handlers = Maps.newConcurrentMap();
    }

    void registerHandler(final IEventHandler<T> handler) {
        final HandlerKeyHash<T> handlerHash = new HandlerKeyHash<>(handler);
        if (handlers.get(handlerHash) != null)
            return;


        handlers.put(handlerHash, handler);
    }

    void unregisterHandler(final IEventHandler<T> handler) {
        final HandlerKeyHash<T> handlerHash = new HandlerKeyHash<>(handler);
        handlers.remove(handlerHash);
    }

    public IEventResult<T> callHandlers(final T event) {
        final List<IEventHandler<T>> handlersSorted = handlers.values().stream()
                .sorted(handlerWeightCmp)
                .collect(Collectors.toList());

        boolean cancelled = false;
        final List<IListenerExecutionResult<T>> executionResults = Lists.newArrayList();

        for (final IEventHandler<T> handler : handlersSorted) {
            if(cancelled && !handler.ignoreCancelled()) continue;
            event.injectHandler(handler);
            final IListenerExecutionResult<T> result = handler.onEvent(event);

            if(result.shouldCancel()) cancelled = true;
            else if(result.shouldContinue()) cancelled = false;

            if(result.error()) {
                result.getError().printStackTrace();
            }

            event.injectHandler(null);
            executionResults.add(result);
        }

        return makeResult(event, executionResults);
    }

    private IEventResult<T> makeResult(final T event, final Collection<IListenerExecutionResult<T>> executionResults) {
        return new EventResult<>(event, executionResults);
    }

    IEventHandler<T> getHandler(final Method method) {
        return handlers.values().stream().filter(handler -> handler.hashCode() == method.hashCode()).findFirst().orElse(null);
    }

    static <T extends AEvent<T>> IEventHandler<T> makeHandler(final Object instance, final Method method) {
        final Optional<Prioritized> annotationOp = Reflect.getMethodAnnotation(method, Prioritized.class);
        final EventSubscriber subscriberAnnotation = Reflect.getMethodAnnotation(method, EventSubscriber.class).orElseThrow(
                () -> new HandlerInstantiationException("Missing " + EventSubscriber.class + " annotation on " + method.getName() + " in class " + method.getDeclaringClass())
        );

        if (annotationOp.isPresent()) {
            final Prioritized annotation = annotationOp.get();
            return new Handler<>(instance, (Class<T>)Reflect.getSubscriberTarget(method), method, annotation.priority(), annotation.weight(), subscriberAnnotation.ignoreCancelled());
        }
        return new Handler<>(instance, (Class<T>)Reflect.getSubscriberTarget(method), method, EventPriority.DEFAULT, 1, subscriberAnnotation.ignoreCancelled());
    }

    static <T extends AEvent<T>> IEventHandler<T> makeAnonymousHandler(final @NonNull Function<T, IListenerExecutionResult<T>> handler,
                                                                       final @NonNull Class<T> event, final @NonNull EventPriority priority,
                                                                       final int weight, final boolean ignoreCancelled) {
        return new AnonymousHandler<>(handler, event, priority, weight, ignoreCancelled);
    }

    @Data
    public static class Handler<T extends AEvent<T>> implements IEventHandler<T> {
        private final Object inst;
        private final Class<T> event;
        private final Method method;
        private final EventPriority priority;
        private final int weight;
        private boolean ignoreCancelled;

        public Handler(final @NonNull Object inst, final @NonNull Class<T> event, final @NonNull Method method,
                       final @NonNull EventPriority priority, final int weight, final boolean ignoreCancelled) {
            this.inst = inst;
            this.event = event;
            this.method = method;
            this.priority = priority;
            this.weight = weight;
            this.ignoreCancelled = ignoreCancelled;
        }

        private IListenerExecutionResult<T> invoke(final T event) {
            return Validator.checkNotNull((IListenerExecutionResult<T>) Reflect.invoke(inst, method, event), HandlerInvocationException::new);
        }

        @Override
        public Class<T> getEvent() {
            return event;
        }

        @Override
        public boolean ignoreCancelled() {
            return ignoreCancelled;
        }

        @Override
        public int getPriorityOrdinal() {
            return priority.ordinal();
        }

        @Override
        public IListenerExecutionResult<T> onEvent(final T event) {
            return invoke(event);
        }

        @Override
        public int hashCode() {
            return method.hashCode();
        }
    }

    public static class AnonymousHandler<T extends AEvent<T>> implements IEventHandler<T> {

        private final Function<T, IListenerExecutionResult<T>> handler;
        private final EventPriority priority;
        private final int weight;
        private final boolean ignoreCancelled;
        private final Class<T> event;

        public AnonymousHandler(final @NonNull Function<T, IListenerExecutionResult<T>> handler, final @NonNull Class<T> event,
                                final @NonNull EventPriority priority, final int weight, final boolean ignoreCancelled) {
            this.handler = handler;
            this.event = event;
            this.priority = priority;
            this.weight = weight;
            this.ignoreCancelled = ignoreCancelled;
        }

        @Override
        public IListenerExecutionResult<T> onEvent(final T event) {
            return handler.apply(event);
        }

        @Override
        public Class<T> getEvent() {
            return event;
        }

        @Override
        public EventPriority getPriority() {
            return priority;
        }

        @Override
        public int getPriorityOrdinal() {
            return priority.ordinal();
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public boolean ignoreCancelled() {
            return ignoreCancelled;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    private static class HandlerKeyHash<T extends AEvent<T>> {
        private static int SALT = ThreadLocalRandom.current().nextInt(99);
        private IEventHandler<T> handler;
        private final int hash;

        public HandlerKeyHash(final IEventHandler<T> handler) {
            this.handler = handler;
            this.hash = handler.hashCode() + SALT;
        }

        public static int getHash(final Method method) {
            return method.hashCode() + SALT;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Number)
                return hash == ((Number)obj).intValue();
            else if (!(obj instanceof HandlerList.HandlerKeyHash))
                return false;

            return obj.hashCode() == hash;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
