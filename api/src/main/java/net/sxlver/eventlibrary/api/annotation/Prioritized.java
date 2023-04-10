package net.sxlver.eventlibrary.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is used to sort listeners by their priority.
 * <p>
 * Listeners will by default be prioritized with {@link EventPriority#DEFAULT} and a weight of {@code 1}.
 * The framework will call listeners in order, sorted by their priority (lowest - highest) and then their
 * given weight (lowest - highest). Meaning a listener wither a priority of LOWEST would be called before
 * one with a priority of DEFAULT, giving default more power over the events result.
 *
 * @author Steve Oberst
 * @see    EventPriority
 * @since  0.1-alpha
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Prioritized {

    EventPriority priority();

    /**
     * A second parameter used to sort listeners by their priority.
     * <p>
     * When two listeners share the same {@link EventPriority}, the weight may be used by either of them
     * to "overpower" the other listener. It is used as a second parameter when sorting the listeners by
     * their priority.
     *
     * @return the listeners weight or the default value of 1
     */
    short weight() default 1;
}
