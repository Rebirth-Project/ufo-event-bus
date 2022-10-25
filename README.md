# UFO Eventbus ![Ufo Eventbus Icon](documentation/UfoEventBus.png)
UFO Event bus (Ultra Fast Object-oriented Event bus) is a powerful asynchronous, lightweight and scalable publish/subscribe event 
system written in Java.

It was inspired by [Greenrobot eventbus](https://github.com/greenrobot/EventBus), but basically coded from scratch.

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
  * Make a completely asynchronous message passing system
  * Make a fast and reliable message passing system 
  * Make a library with no architectural dependencies (code must depend only on standard Java libraries)
  * Make the code as cleaner and testable as possible

## Requirements
- Minumum Java version: 8
- Minimum Android version: 8.0 minSdkVersion 26

## Add Ufo Eventbus in your project

##### Gradle:

```
dependencies {
    implementation "it.rebirthproject:ufoeventbus:1.0.0"
}
```
##### Maven:

```
<dependency>
    <groupId>it.rebirthproject</groupId>
    <artifactId>ufoeventbus</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

##### First step - Create an event:
``` java
// Create an event as a simple java class with necessary fields
public class Event { // Add fields if needed }
```

##### Second step - Create an event listener:
``` java
import it.rebirthproject.ufoeb.eventannotation.Listen;

// Create a listener for the event using the '@Listen' annotation
public class ListenerForEvent {
    
    @Listen
    public void someMethod(Event event) {
      	// Do something useful here.. Maybe using some data taken from the event...
    }
}
```

##### Third step - Create the bus, register the listeners and send the events:

``` java
// Instantiate the listener
ListenerForEvent listener = new ListenerForEvent();

// Create the Eventbus using the builder
// The default values apply for almost every situation, but
// read how to configure advanced bus features when needed
EventBus ufoEventBus = new EventBusBuilder().build();

// Register the listener on the bus
ufoEventBus.register(listener); // The 'register' method throws an EventBusException

// Post messages on the bus. This will call the listener method!
ufoEventBus.post(new Event()); // The 'post' method throws an EventBusException
```

## Internal Architecture Overview and detailed documentation
You can read detailed documentation [here](documentation/Documentation.md).

You can acces the javadoc documentation [here](https://www.rebirth-project.it/ufoeventbus/documentation/index.html).

## Roadmap
Right now the next big thing for the bus will be:

* event runtime filters (block at runtime the delivery of an event using a filter)
* rework the exception system if needed

## Contributors
Interested persons and contributors can just use the standard github tools to interact with the project.
 
For communications you can use this email [rebirthproject2021@gmail.com](mailto:rebirthproject2021@gmail.com)

## Credits and License
Copyright (C) 2021/2022 [Andrea Paternesi](https://github.com/patton73)
 
Copyright (C) 2021/2022 [Matteo Veroni](https://github.com/mavek87)  

Current website under creation [Rebirth Project](https://www.rebirth-project.it)

Ufo Eventbus binaries and source code can be used according to the [Apache License, Version 2.0](LICENSE.md).