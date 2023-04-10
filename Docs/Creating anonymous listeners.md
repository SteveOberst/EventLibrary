# Creating anonymous listeners

The framework also supports the creation of anonymous listeners. You can register anonymous listeners via 
``EventLibrary.registerListener(Function<A extends AEvent, IListenerExecutionResult<A>>, Class<T extends AEvent>)``.

```java
public class Main {
    public static void main(String[] args) {
        final IEventHandler<MyEvent> handler = EventLibrary.registerListener(event -> {
            System.out.println("Wow, we have received an event!");
            return ListenerExecutionResult.success(event);
        }, MyEvent.class);
    }
}
```

We should keep track of the instance of ``IEventHandler`` returned by the function call incase
we want to unregister the listener later on. We can later unregister the listener using
``EventLibrary.unregisterHandler(IEventHandler<T extends AEvent>);``