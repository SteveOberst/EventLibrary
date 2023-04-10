# Handler priorities explained
We can annotate handlers with ``@Prioritize(EventPriority, int)`` in order to assign it a
custom priority and weight. Listeners will be called in order from lowest to highest priority
and weight. The weight is being used as second parameter to sort handlers with the same ``EventPriority``.

```java
public class MyListener {
    
    @EventSubscriber
    @Prioritize(priority = EventPriority.HIGHEST)
    public IListenerExecutionResult<MyEvent> onEvent(final MyEvent event) {
        // this handler will be called first
        // do stuff on MyEvent...
        
        // this handler decides the event should be cancelled
        return ListenerExecutionResult.cancel(event);
    }

    @EventSubscriber(ignoreCancelled = true)
    @Prioritize(priority = EventPriority.HIGHEST, weight = Short.MAX_VALUE)
    public IListenerExecutionResult<MyEvent> onEventHighPriority(final MyEvent event) {
        // now this handler will be called
        // do stuff on MyEvent...
        
        // we decide the event should not be marked as cancelled, since this handler
        // has a higher weight than the other one it will be called later, and we can 
        // therefore "overpower" the other handler.
        return ListenerExecutionResults.continueEvent(event);
    }
}
```

Handlers will have a default priority of ``priority = EventPriority.DEFAULT & weight = 1``. You should
refer from modifying the event on the ``HIGHEST`` priority as other handlers might not be able to intercept
the event if required.