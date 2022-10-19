# UFO Eventbus Documentation

## Features

UFO Event bus (Ultra Fast Object-oriented Event bus) is a powerful asynchronous , lightweight and scalable publish/subscribe event
system written in Java. 

It has a lot of interesting features:

* Asynchronous parallel non-blocking (it depends on  worker's number) event delivery
* [Listener's event priority](#listeners-event-priority)
* Global sticky events
* Events' delivery based on event's classes  inheritance if configured
* Events' delivery based on listener's classes inheritance if configured
* Asynchronous queries based on Java's completable futures

###Internal Architecture Overview

![Ufo Eventbus Architectureschema](UfoEventBusArchitectureFinalWhiteBackground.png?raw=true)

UFO Eventbus' Architecture is designed to achieve asynchronous  non-blocking event delivery in the fastest way.
Basically there are four architectural blocks:

* The bus interface (the only block exposed to the programmer)
* The principal Message queue used basically to order events' execution
* The Memory manager that manages the bus memory state
* The execution pool where workers deliver events to the listeners (default value is to have 1 worker)

The bus internally uses messages. Those messages are divided in command and queries, a concept very similar to the CQRS standard architecture. A command tells the bus to do something, for example a post of an event, or a post af a sticky event. A query asks the bus to return some information. The actual difference between a command and a query is that the command does not return any value while the query returns a completable future (pheraps this will be changed in future).
In the standard configuration bus guarantees event execution order since it uses only a worker to send events. This is a blocking configuration and must be used when computation after event delivery is fast. Otherwise you should configure the bus to have a major number of workers (parallel execution), that unfortunately cannot guarantee the finishing order for the events' computations.
So basically: 

* if you want scalability you can guarantee events' order execution but cannot guarantee computations' finishing order. 
* if you want strict order execution you must set the number of workers to 1 (default). This will garantee computations' order, but would limit scalability when computations are very time consuming (filesystem, db access, network).

<ins>Please keep in mind that workers are threads and have an overhead</ins>. So for very fast computation environments (in memory computation) is not needed to change the workers' number or the queue lenght. A worker and the standard queue's lenght will garanteee the fastest performances. 
However you can change those values passing the right parameters to the builder.

The bus keeps inside the memory state the current status of listeners' registration. A listener will register to the bus on particular events. So can happen that many listeners register to the bus for the same event. Therefore when an event is posted to the bus a registration list is created and sent to the workers. A worker 
take the list and iterate over registrations to execute them all. Order of registration is guaranteed. So if a listener registers before another events will be delivered accordingly. 
Bus permits the programmer using registration/deregistration of listeners during post execution. But you should then be aware of the fact that internal status can be screwed up.
If you want to keep internal status safe and continue to guarantee events' order then you must use the correct configuration in the bus builder (**safeRegistrationsListNeeded**); this will use copies of the registrations' list instead of the shared reference state but would be a bit slower.
If your configuration for registered/unregistered listeners does not change at runtime then use the default paramentes.

Another important feature of the bus is that it uses reflection to execute listeners' registered methods. Hence an annotation is used to register a listener's method and, by default, reflection's ```method.invoke()``` is used. Is also provided a faster way to execute events by setting the correct parameter (**useLambdaFactoryInsteadOfStandardReflection**). This will use a LambdaFactory to create methods handlers (a lot faster than standard reflection). This method can be used always but in combination with modules as explained [here](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html), so this is not the default behaviour of the bus. 

### Eventbus' builder and configuration's options

To build the eventbus you must use the provided builder. It is a standard builder with fluent syntax. It returns a completely configured bus.
There are various parameters you can use to configure the bus:

* Performance parameters
* Java Class inheritance parameters
* Safeness' parametrs
* Logging, debugging and Exceptions' parameters

####Performance parameters:

* **queueLength** This varies the inbound message queue length, default value is set to 100. This should be changed only when you need more workers to scale in performaces and when operating in heavy load environments. From tests this lenght should not be bigger than 1000, because if there are not free workers the bus will block anyway. Is does not make sense in filling a huge queue using a lot of memory, while you do not have free workers.
* **numberOfWorkers** This varies the number of workers available in the pool. Default value is set to 1. Modify this value only when you operate in a heavy duty environment. The scalability curve tells that you should add workers only when event's execution is time consuming (just see the performance tests). In multi-threaded processor's you can use up to 20 workers, but workload should be high and blocking. Usually not more that 2-3 workers are needed. A worker thread brings overead into bus performance so the workload must be higher. Use this parameter in combination with <ins>queueLength</ins>.
* **useLambdaFactoryInsteadOfStandardReflection** This parameter is used to force the bus using an internal LambaFactory to speed up the execution of events. This will use Method handlers instead of standard method reflection invocation. It is a lot faster but have some drawbacks as stated in Java documentation. Be sure of what you are doing or leave the default value.

####Inheritance parameters:

* **listenerSuperclassInheritance** Use this parameter if you want the bus keep track of listeners classes inheritance. This value by default is switched off. So the bus will not iterate through parent classes to search for listening methods, but will find only the listener main class' methods. If you want to iterate through the parent classes just use this parameter. Please <ins>note</ins> that using this feature is dangerous because by default reflection will stop only in certain cases (when package does not contain java or android classes). Somethimes this does not work. So to be sure to avoid problems using also the next parameter.

* **inheritancePackageFrontierPath** Use this parameter to tell the bus where to stop when iterating between paret classes. This should be always used to speedup methods search and to avoid strange exceptions at runtime.
Usually what you want is to look for classes in your project's package. So we reccomend to always use this parameter in the following way:
```java 
  builder.setInheritancePackageFrontierPath("com.mycompany.mypackage"); 
```
* **eventInheritancePolicy** Use this parameter to achieve a powerful bus feature. This will  tell the bus to keep track also of events class inheritance. There are four main policies. The default one is to not use events' inheritance. Otherwise you can iterate over classes, over interfaces or over both. 
```java 
1) Default : no event inheritance 
2) builder.setEventSuperclassInheritance(); // iterate only over classes
3) builder.setEventInterfaceInheritance();  // iterate only over interfaces
4) builder.setCompleteEventInheritance(); // iterate over classes and interfaces

//As a simple example 
//You have Event E1 that extends class C1 
//You have Event E2 that extends class C2 that implements interface I
//and then define a listener L that listens for class E1, class C2 and interface I in three different methods 
//The policy will tell the bus which methods to call when an event arrives.

//lets say that you post event E2, according to policy we can have different behaviours on listener L
//1) no method will be called
//2) the method that listens to C2 will be called
//3) the method that listens to I will be called
//4) the methods that listen to I and C2 will be called 

//lets say that you post event E1, according to policy we can have different behaviours on listener L
//1) method that listens to E1 will be called
//2) method that listens to E1 will be called
//3) no method will be called
//4) method that listens to E1 will be called
```
####Safeness parameters:

* **safeRegistrationsListNeeded** this forces the bus to make a copy of registrations' lists instead of passing to the workers the references. Useful when you want to play with registration/deregistration of listeners at runtime.

####Logging, debugging and Exceptions' parameters

* **throwNoListenerAnnotationException** If a registering listener does not have any annotated method or, in case of event inheritance enabled, also its super classes or interfaces does not have any annotated method, then a non blocking Exception is thrown.
By default bus handles this case slently. Use this to debug application.

* **throwNoRegistrationsWarning** A non blocking Exception is thrown when event E is posted to the bus but no listener is registered to listen to it. By default bus handles this case slently. Use this to debug application.

* **throwNotValidMethodException** if set then a non blocking Exception is thrown when an invalid  annotated method is found in a Listener. The method must be public and not static and must have only one parameter that represents the listened event. By default bus handles this case slently. Use this to debug application.

* **verboseLogging** This opstion is used to debug eventbus memory state. It will print out actual state.

####Builder usage example
```java
EventBus ufoEventBus = new EventBusBuilder()
	.setListenerSuperclassInheritance()
	.setEventSuperclassInheritance()
	.setThrowNoRegistrationsWarning()
	.setThrowNoListenerAnnotationException()
	.setThrowNotValidMethodException()
	.setInheritancePackageFrontierPath("it.rebirthproject.myexampleproject")
	.build();
```

###Eventbus reference, dependency injection and singleton

We reccommend to instantiate the eventbus using the builder and any dependency injection system. You can even simply create the instance and pass it by constructor to the desired objects. This is up to you and your decision only. The advice is always to Keep It Simple Stupid (KISS principle). If you can avoid over structures you do not go wrong. For those who want instead having a singleton at any cost we provided a completely separated special class that implements it in a safe way. 

```java
//1) Standard way to create it
//build the eventbus builder and set all needed paramenters
EventBusBuilder eventbusBuilder = new EventBusBuilder();
//call the GlobalEventBus setup method
GlobalEventBus.setup(eventbusBuilder);
//get the bus singleton instance after created it
EventBus singletonInstanceBus = GlobalEventBus.getInstance();

//2) quicker way to create it
//use the convenience method to create quickly and safely the singleton instance
EventBus singletonInstanceBus = GlobalEventBus.setupAndGetInstance(eventbusBuilder);
//get the bus singleton instance after created it
EventBus singletonInstanceBus = GlobalEventBus.getInstance();

```

Separating methods to create the instance and to obtain it is a safe way to avoid singleton anti-pattern problems. However be careful in using it, there could be many situations where singleton usage will lead to problems.
<ins>Dependency Injection usage is always to be preferred</ins>.
 
###Listener's event priority

Event priority is another powerful feature of the bus. The priority is declared as an attribute of the ```Listen``` annotation.
The priority is basically managed inside the bus as a global declaration for the listeners and the related event. So you can order methods' execution inside a listener (methods listening the same event) , or more you can order the event execution between different listeners. You cannot order methods listening for different events, since it depends on the application's runtime flow (call a post before another for example).

```java
//Let's have a listener that listens the same event in two different methods, and you want to guarantee
//that the first method is called before the second one, you then need to set the priority annotation. 
//Higher priority means calling the related method first. Note that priority applies only on the same event.
public class ExampleListener {
    @Listen(priority = 1)
    public void method1(Event event) throws InterruptedException {       
    }
    
     @Listen(priority = 0)
    public void method2(Event event) throws InterruptedException {       
    }    
}

//This applies also to different listeners
//In this case Method1 of Listener1 will be called before Method2 of listener 2
public class Listener1 {
    @Listen(priority = 1)
    public void method1(Event event) throws InterruptedException {       
    }
}

public class Listener2 {    
    @Listen(priority = 0)
    public void method2(Event event) throws InterruptedException {       
    }    
}
```

 





