# UFO Event bus ![Ufo Eventbus Icon](documentation/UfoEventBus.png)
UFO Event bus (Ultra Fast Object-oriented Event bus) is a powerful, lightweight and scalable publish/subscribe event 
system written in Java. 

* is paraller and scalable
* is completely asynchronous
* is very tiny (~50k jar)
* performs well with Android >= 8.0 and all versions of java >= 8
* used with java > 9 is compiled as module increasing encapsulation
* it has no dependencies but SLF4J library for enabling logging 
* can simplify the communication between components since it decouples event posters and listeners
* is fast in almost every situations and loads and can be configured
* has advanced features like listener priorities, events inheritance, listeners inheritance, inbound event order
* is entirely documented
* the code is clean, testable, compact and very easy to understand and mantain
* is covered with unit test in basically every part of logical code

![Build Status](https://github.com/Rebirth-Project/ufo-event-bus/actions/workflows/build.yml/badge.svg?raw=true)
<br/>

## Add Ufo Eventbus in your project

##### ```Gradle```

```
dependencies {
    implementation "it.rebirthproject:ufoeventbus:1.0.0"
}
```
##### ```Maven```

```
<dependency>
    <groupId>it.rebirthproject</groupId>
    <artifactId>ufoeventbus</artifactId>
    <version>1.0.0</version>
</dependency>
```
<br/>
##Core Objects

- EventBus: The API of the event bus system
- EventBusBuilder: The builder class to create new event bus instances
- GlobalEventBus: A global singleton event bus instance

## Architecture Overview

![Ufo Eventbus Architectureschema](documentation/UfoEventBusArchitectureFinalWhiteBackground.png?raw=true)


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

## Credits and License
Copyright (C) 2021/2022 [Andrea Paternesi](https://github.com/patton73) 
 
Copyright (C) 2021/2022 [Matteo Veroni](https://github.com/mavek87)  

[Rebirth Project](https://www.rebirth-project.it)

Ufo Eventbus binaries and source code can be used according to the [Apache License, Version 2.0](LICENSE.md).
