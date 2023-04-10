# Creating Custom Events
### To create a custom event you simply need to extend the AEvent class in your code.
```java
public class UserLoginEvent extends AEvent<UserLoginEvent> {
    
    private final User user;
    
    public UserLoginEvent(final User user) {
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
}

class User {
    private final String username;
    private final UUID uuid;
    //...
}
```

And that's it really. You can then dispatch the event by invoking``EventLibrary.dispatchEvent(new UserLoginEvent(user));``. 
