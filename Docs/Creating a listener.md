# Creating a listener

All you need to do in order to create a listener is annotating a method with ``@EventSubscriber``
and registering the object so that it can start receiving events.

```java
public class MyListener {
    @EventSubscriber
    public IListenerExecutionResult<MyEvent> onEvent(final MyEvent event) {
        if(!event.foo() && !event.bar()) {
            return ListenerExecutionResult.cancel(event);
        }
        
        if(event.fooBar() == null) {
            // something unexpected happened, throw an error
            final String message = "fooBar may not be null!";
            return ListenerExecutionResult.cancelWithError(event, new EventException(
                            new NullPointerException(message)
                    )
            );
        }
        
        return ListenerExecutionResult.success(event);
    }
}
```

### Registering the listener
To register the listener we can use ``EventLibrary.registerListener(Object)``.
```java
public class EntryPoint {
    public void onStart() {
        EventLibrary.registerListener(new MyListener());
    }
}
```
That's it! All event handlers in ``MyListener.class`` will now receive events when they're being dispatched!
