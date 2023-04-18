# EventLibrary
A bloat-free library for dispatching and receiving events.

# Dependencies

### Gradle
```groovy
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/SteveOberst/EventLibrary")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv().get("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv().get("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation group: "net.sxlver.eventlibrary", name: "core", version: "<latest>"
}
```
![latest](https://img.shields.io/badge/latest-0.1.1--stable-blue)
# Features

**EventLibrary** contains the following features:

- dispatch events (synchronously and asynchronously)
- register (anonymous) listeners for events
- provide your custom implementations to process events however you want

## Initialization
No need for initialization!
The Framework is mostly based off of static access, so no need to store any references.

## Event Dispatching
```java
final MyEvent event = new MyEvent();
final IEventResult<MyEvent> result = EventLibrary.dispatchEvent(event);
```

## Event Subscribers
```java
public class MyListener {
    @EventSubscriber
    public IListenerExecutionResult<MyEvent> onEvent(final MyEvent event) {
        //...
        return ListenerExecutionResult.success(event);
    }
}

public class Main {
    public static void main(String[] args) {
        EventLibrary.registerListener(new MyListener());
    }
}
```

## Anonymous Handlers
```java
final IEventHandler<MyEvent> handler = EventLibrary.registerListener(event -> {
    //...
    return ListenerExecutionResult.success(event);
, MyEvent.class})
```

## Examples and docs
To get started with the framework make sure to checkout the [Examples](./examples) and [Docs](./Docs)

## License
Released under the [MIT License](./LICENSE).
