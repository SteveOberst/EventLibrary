# Dispatching events and working with their results

To dispatch an event we use ``EventLibrary.dispatchEvent(AEvent event)`` which returns an
``IEventResult<T extends AEvent>``. The result will hold information about how handlers modified
the event and will hold a history of each ``IListenerExecutionResult<T extends AEvent>`` returned
by each handler.

```java
public class MyClass {
    public void loginUser(final User user) {
        final IEventResult<UserLoginEvent> result = EventLibrary
                .dispatchEvent(new UserLoginEvent(user));
        
        if(result.cancelled()) {
            // Event has been cancelled by one of the handlers
            System.out.printf("Couldn't login user. Error: %s", result.getEvent().getError());
            return;
        }
        
        // Continue with the logic to log in the user...
    }
}
```
