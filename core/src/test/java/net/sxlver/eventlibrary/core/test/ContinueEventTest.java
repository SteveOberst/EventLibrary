package net.sxlver.eventlibrary.core.test;

import net.sxlver.eventlibrary.api.AEvent;
import net.sxlver.eventlibrary.api.annotation.EventPriority;
import net.sxlver.eventlibrary.api.annotation.Prioritized;
import net.sxlver.eventlibrary.api.result.IListenerExecutionResult;
import net.sxlver.eventlibrary.common.annotation.EventSubscriber;
import net.sxlver.eventlibrary.core.EventLibrary;
import net.sxlver.eventlibrary.core.result.ListenerExecutionResult;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

public class ContinueEventTest {

    boolean called;

    @Test
    public void test() {
        EventLibrary.registerListener(new Listener());
        EventLibrary.dispatchEvent(new DummyEvent());
        MatcherAssert.assertThat("Last handler wasn't called even though event cancellation was revoked.", called);
    }

    class Listener {

        @EventSubscriber
        public IListenerExecutionResult<DummyEvent> onEvent_1(final DummyEvent event) {
            return ListenerExecutionResult.cancel(event);
        }

        @EventSubscriber(ignoreCancelled = true)
        @Prioritized(priority = EventPriority.HIGH)
        public IListenerExecutionResult<DummyEvent> onEvent_2(final DummyEvent event) {
            return ListenerExecutionResult.continueEvent(event);
        }

        @EventSubscriber
        @Prioritized(priority = EventPriority.HIGH, weight = 2)
        public IListenerExecutionResult<DummyEvent> onEvent_3(final DummyEvent event) {
            called = true;
            return ListenerExecutionResult.success(event);
        }
    }

    static class DummyEvent extends AEvent<DummyEvent> {}
}
