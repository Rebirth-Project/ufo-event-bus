# UFO Event bus ![Ufo Eventbus Icon](documentation/UfoEventBus.png)
UFO Event bus (Ultra Fast Object-oriented Event bus) is a powerful, lightweight and scalable publish/subscribe event 
system written in Java. 

* is parallel and scalable
* is completely asynchronous
* is very tiny (~50k jar)
* performs well with Android and Java
* used with java > 9 is compiled as module increasing encapsulation
* it has no dependencies but SLF4J library for enabling logging 
* can simplify the communication between components since it decouples event posters and listeners
* is fast in almost every situations and loads and can be configured
* has advanced features like listener priorities, events inheritance, listeners inheritance, inbound event order
* is entirely documented
* the code is clean, testable, compact and very easy to understand and mantain
* is covered with unit test in basically every part of logical code

![Build Status](https://github.com/Rebirth-Project/ufo-event-bus/actions/workflows/build.yml/badge.svg?raw=true)

## Goals
  * Provide a simple to use library to allow messaging within the app's objects
  * Make a completely asyncrhonous message passing system
  * Make a fast and reliable message passing system 
  * Make a library with no architectural dependencies (code must depend only on standard java libraries)
  * Make the code as cleaner and testable as possible

## Requirements
- Minumum Java version: 8
- Minimum Android version: 8 minSdkVersion 26

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

##Quick Start

```java
//Create an event as a simple java class with necessary fields
public class Event { /* Add fields if needed */ }

//Create a listener for the event Event using the provided annotation
import it.rebirthproject.ufoeb.eventannotation.Listen;

public class ListenerForEvent {
    @Listen
    public void onEvent(EventToListen1 event) {
      	//Do something useful here
    }
}

//create the Eventbus using the builder (the default values apply for almost every situation)
//but read how to configure advanced bus features when needed
EventBus ufoEventBus = new EventBusBuilder().build();

//register the listener on the bus
ufoEventBus.register(ListenerForEventInstance);

//post messages on the bus
ufoEventBus.post(new Event());
```
<br/>

## Internal Architecture Overview and detailed documentation
You can read detailed documentation here [UfO Eventbus documentation](documentation/Documentation.md).
<br/>

## Credits and License
Copyright (C) 2021/2022 [Andrea Paternesi](https://github.com/patton73) 
 
Copyright (C) 2021/2022 [Matteo Veroni](https://github.com/mavek87)  

[Rebirth Project](https://www.rebirth-project.it)

Ufo Eventbus binaries and source code can be used according to the [Apache License, Version 2.0](LICENSE.md).
