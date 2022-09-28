# UFO EventBus
UFO EventBus (Ultra Fast Object-Oriented Event Bus) is a powerful, lightweight and scalable publish/subscribe event 
system written in Java.


## Core Objects

- EventBus: The API of the event bus system
- EventBusBuilder: The builder class to create new event bus instances
- GlobalEventBus: A global singleton event bus instance

## Architecture Overview

![Ufo Eventbus Architectureschema](documentation/UfoEventBusArchitectureFinalWhiteBackground.png?raw=true"Title")

## How to use it in your project

#### ```Gradle```

```
repositories {
    maven { url '...' }
}

dependencies {
    implementation ''
}
```
#### ```Maven```

```
<repositories>
    <repository>
    	<id></id>
	<url></url>
    </repository>
</repositories>

<dependency>
    <groupId></groupId>
    <artifactId></artifactId>
    <version></version>
</dependency>
```

## Basic usage

### 1) a

xxxxxxxxxxxx

```java
X x = new X(y);
```

### 2) b

xxxxxxxxxxxx

```java
X.a(y);
```

### 3) c

xxxxxxxxxxxx

```java
```

## Examples

#### abc

```java
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    public static void main(String[] args) {
    }
}
```

## Advanced usages

.......

## Credits

#### Authors:
- Andrea Paternesi (https://github.com/patton73)
- Matteo Veroni (https://github.com/mavek87)

## License
[MIT](https://github.com/xxx/xxx/blob/master/LICENSE)