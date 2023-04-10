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

public class HandlerWeightTest {
    @Test
    public void test() {
        EventLibrary.registerListener(new Listener());
        EventLibrary.dispatchEvent(new DummyEvent());
    }

    static class Listener {

        private boolean firstCalled;

        @EventSubscriber
        @Prioritized(priority = EventPriority.HIGHEST)
        public IListenerExecutionResult<DummyEvent> onEvent_1(final DummyEvent event) {
            firstCalled = true;
            return ListenerExecutionResult.success(event);
        }

        @EventSubscriber
        @Prioritized(priority = EventPriority.HIGHEST, weight = 2)
        public IListenerExecutionResult<DummyEvent> onEvent_2(final DummyEvent event) {
            MatcherAssert.assertThat("Weighted handler not called in right order.", firstCalled);
            return ListenerExecutionResult.success(event);
        }
    }

    static class DummyEvent extends AEvent<DummyEvent> {}
}
