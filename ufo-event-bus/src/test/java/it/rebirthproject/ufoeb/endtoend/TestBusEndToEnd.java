package it.rebirthproject.ufoeb.endtoend;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBusBuilder;
import it.rebirthproject.ufoeb.endtoend.dto.events.Event1;
import it.rebirthproject.ufoeb.endtoend.dto.objectstoregister.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBusEndToEnd {

    private static final Logger logger = LoggerFactory.getLogger(TestBusEndToEnd.class);
    private static EventBus bus;

    @BeforeEach
    public void init()  throws Exception {
        bus = new EventBusBuilder().setNumberOfWorkers(1).setThrowNoRegistrationsWarning().build();
    }
    
    @Test
    public void test_natural_order_of_event_execution_based_on_registration() throws Exception {        
        CountDownLatch listenersCountDownLatch = new CountDownLatch(2);
        List<Class> receivers = new ArrayList<>();
        Event1 event1 = new Event1();
        Listener1OfEvent1 listener1 = new Listener1OfEvent1(listenersCountDownLatch, receivers);
        Listener2OfEvent1 listener2 = new Listener2OfEvent1(listenersCountDownLatch, receivers);

        bus.register(listener2);
        bus.register(listener1);

        bus.post(event1);

        listenersCountDownLatch.await();

        assertEquals(Listener2OfEvent1.class, receivers.get(0), "Error, first listener is different from expectations");
        assertEquals(Listener1OfEvent1.class, receivers.get(1), "Error, second listener is different from expectations");
    }

    @Test
    public void test_order_of_event_execution_by_priority() throws Exception {        
        CountDownLatch listenersCountDownLatch = new CountDownLatch(2);
        Event1 event1 = new Event1();
        List<Class> receivers = new ArrayList<>();
        ListenerOfEvent1WithHighPriority listenerHighPriority = new ListenerOfEvent1WithHighPriority(listenersCountDownLatch, receivers);
        ListenerOfEvent1WithLowPriority listenerLowPriority = new ListenerOfEvent1WithLowPriority(listenersCountDownLatch, receivers);

        bus.register(listenerLowPriority);
        bus.register(listenerHighPriority);

        bus.post(event1);

        listenersCountDownLatch.await();

        assertEquals(ListenerOfEvent1WithHighPriority.class, receivers.get(0), "Error, first listener is different from expectations");
        assertEquals(ListenerOfEvent1WithLowPriority.class, receivers.get(1), "Error, second listener is different from expectations");
    }

    @Test
    public void test_chain_of_events() throws Exception {       
        CountDownLatch listenersCountDownLatch = new CountDownLatch(2);
        List<Object> receivedEvents = new ArrayList<>();
        Event1 event1 = new Event1();
        Listener1ChainOfEvents listener1ChainOfEvents = new Listener1ChainOfEvents(listenersCountDownLatch, bus, receivedEvents);
        Listener2ChainOfEvents listener2ChainOfEvents = new Listener2ChainOfEvents(listenersCountDownLatch, receivedEvents);

        bus.register(listener1ChainOfEvents);
        bus.register(listener2ChainOfEvents);

        bus.post(event1);

        listenersCountDownLatch.await();

        assertEquals(event1, receivedEvents.get(0), "Error, first event is different from expectations");
        assertEquals(listener1ChainOfEvents.getEvent2(), receivedEvents.get(1), "Error, second event is different from expectations");
    }
}
