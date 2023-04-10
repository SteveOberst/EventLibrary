package net.sxlver.eventlibrary.common;

import lombok.NonNull;
import net.sxlver.eventlibrary.common.annotation.EventSubscriber;
import net.sxlver.eventlibrary.common.exception.EventException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class Reflect {

    @NonNull
    public static <T extends Annotation> Optional<T> getMethodAnnotation(final Method method, final Class<T> cls) {
        if(!method.isAnnotationPresent(cls))
            return Optional.empty();

        return Optional.of(method.getAnnotation(cls));
    }

    public static Object invoke(final Object inst, final Method method, final Object... params) {
        try {
            method.setAccessible(true);
            return method.invoke(inst, params);
        } catch (IllegalAccessException e) {
            final String message = "Illegal access of method '" + method + "' " + "on object " + inst + ".";;
            throw new EventException(message, e);
        } catch (InvocationTargetException e) {
            final String message = "Method " + method.getName() + " of class " + inst.getClass().getSimpleName() + " has thrown an exception.";
            throw new EventException(message, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Class<?> getSubscriberTarget(final Method method) {
        Validator.checkValidSubscriber(method);
        return method.getParameterTypes()[0];
    }

    public static Collection<Method> getSubscriber(final Object listener) {
        return Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(EventSubscriber.class))
                .collect(Collectors.toList());
    }
}
