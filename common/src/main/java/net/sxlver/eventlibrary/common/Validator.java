package net.sxlver.eventlibrary.common;

import lombok.SneakyThrows;
import net.sxlver.eventlibrary.common.annotation.EventSubscriber;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.function.Function;

public class Validator {
    public static void checkEventSubscriber(final AnnotatedElement element) {
        if(!element.isAnnotationPresent(EventSubscriber.class)) {
            final String message = "Annotation " + EventSubscriber.class + " not present on " + element;
            throw new ValidationException(message);
        }
    }

    @SneakyThrows
    public static void checkValidSubscriber(final Method method) {
        if(!method.isAnnotationPresent(EventSubscriber.class) || method.getParameterCount() != 1
                || !Class.forName("net.sxlver.eventlibrary.api.AEvent").isAssignableFrom(method.getParameterTypes()[0])) {
            final String message = method.getName() + " in class " + method.getDeclaringClass() +
                    " is not a valid event subscriber. An event subscriber may only take the event as parameter " +
                    "and should be annotated with " + EventSubscriber.class;
            throw new ValidationException(message);
        }
    }

    public static <T> T checkNotNull(final T val, final Function<Throwable, RuntimeException> exception) {
        if(val == null)
            throw exception.apply(new NullPointerException("Value cannot be null."));

        return val;
    }

    private static final class ValidationException extends RuntimeException {
        public ValidationException(final String msg) {
            super(msg);
        }
    }
}
