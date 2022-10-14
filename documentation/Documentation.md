# UFO Eventbus Documentation

## Features

UFO Event bus (Ultra Fast Object-oriented Event bus) is a powerful asynchronous , lightweight and scalable publish/subscribe event
system written in Java. 

It has a lot of interesting features:

* Asynchronous parallel non-blocking (it depends on  worker's number) event delivery
* Listener's internal event priority
* Global sticky events
* Events delivery based on event's classes  inheritance if configured
* Events delivery based on listener's classes inheritance if configured
* Asynchronous queries based on Java's completable futures

### Internal Architecture Overview

![Ufo Eventbus Architectureschema](UfoEventBusArchitectureFinalWhiteBackground.png?raw=true)

UFO Eventbus' Architecture is designed to achieve asynchronous  non-blocking event delivery in the fastest way.
Basically there are four architectural blocks:

* The bus interface (the only exposed block to the programmer)
* The principal Message queue used basically to order events' execution
* The Memory manager that manages the bus memory state
* The execution pool where workers deliver events to the listeners (default value is to have 1 worker)

The bus internally uses messages. Those messages are divided in command and queries, a concept very similar to the CQRS standard architecture. A command tells the bus to do something, for example a post of an event, or a post af a sticky event. A query asks the bus to return some information. The actual difference between a command and a query is that the command does not return any value while the query returns a completable future (pheraps this will be changed in future).
In the standard configuration bus guarantees event execution order since it uses only a worker to send events. This is a blocking configuration and must be used when computation after event delivery is fast. Otherwise you should configure the bus to have a major number of workers (parallel execution), that unfortunately cannot guarantee the finishing order for the events' computations.
So basically: 

* if you want scalability you can guarantee events' order execution but cannot guarantee computations' finishing order. 
* if you want strict order execution you must set the number of workers to 1 (default). This will garantee computations' order, but would limit scalability when computations are very time consuming. (filesystem, db access, network).

<u>Please keep in mind that workers are threads and have an overhead</u>. So for very fast computation environments (in memory computation) is not needed to change the workers' number or the queue lenght. A worker and the standard queue's lenght will garanteee the fastest performances. 
However you can change those values passing the right parameters to the builder.

The bus keep inside the memory state the current status of listeners' registration. A listener will register to the bus on particular events. So can happen that many listeners register to the bus for the same event. So when an event is posted to the bus a registration list is created and sent to the workers. A worker 
take the list and iterate over registrations to execute them all. Order of registration is guaranteed. So if a listener registers before another events will be delivered accordingly. 
Bus permits the programmer using registration/deregistration of listeners during post execution. But you should then be aware of the fact that internal status can be ruined.
If you want to keep internal status safe and continue to guarantee events' order then you must use the correct configuration in the bus builder (**safeRegistrationsListNeeded**); this will use copies of the registrations' list instead of the shared reference state but would be a bit slower.
If your configuration for registered/unregistered listeners does not change at runtime then use the default paramentes.

Another important feature of the bus is that it uses reflection to execute listeners' registered methods. So an annotation is used to register a listener's method and by default
reflections' <u>method.invoke()</u> is used. Another faster way can be used instead of standard reflection by setting the correct parameter (**useLambdaFactoryInsteadOfStandardReflection**). This will use a LambdaFactory to create methods handlers (a lot faster that standard reflection). This method can be used always but in combination with modules as explained [here](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html), so this is not the default behaviour of the bus. 

### Internal Architecture Overview





