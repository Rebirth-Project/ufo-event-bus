/*
 * Copyright (C) 2021 Andrea Paternesi Rebirth project
 * Modifications copyright (C) 2021 Matteo Veroni Rebirth project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.rebirthproject.ufoeb.architecture.eventbus;

import it.rebirthproject.ufoeb.dto.events.eventinterface.Event;
import it.rebirthproject.ufoeb.dto.objectstoregister.eventbus.FirstListenerRunnable;
import it.rebirthproject.ufoeb.dto.objectstoregister.eventbus.PosterListenerRunnable;
import it.rebirthproject.ufoeb.dto.objectstoregister.eventbus.RegisteringListenerRunnable;
import it.rebirthproject.ufoeb.dto.objectstoregister.eventbus.SecondListenerRunnable;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import it.rebirthproject.ufoeb.testutils.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.Future;
import static org.junit.jupiter.api.Assertions.*;

public class MultithreadedEventBusTest extends BaseTest {

    private static final long SLEEP_TIME = 1000;
    private static final Logger logger = LoggerFactory.getLogger(MultithreadedEventBusTest.class);
    private EventBus bus;

    @BeforeEach
    public void beforeEach() throws EventBusException {
        bus = new EventBusBuilder()
                .setNumberOfWorkers(1)
                .setThrowNoRegistrationsWarning()
                .setThrowNoListenerAnnotationException()
                .setThrowNotValidMethodException()
                .setCompleteEventInheritance()
                .setInheritancePackageFrontierPath(TEST_FRONTIER_PATH)
                .build();
    }

    @AfterEach
    public void afterEach() {
        bus.shutdownBus();
    }

    @Test
    public void registering_on_bus_works() throws Exception {
        FirstListenerRunnable firstListenerRunnable = new FirstListenerRunnable(SLEEP_TIME);

        bus.register(firstListenerRunnable);
        Future<Boolean> registered = bus.isRegistered(firstListenerRunnable);
        Boolean isRegistered = registered.get();

        assertTrue(isRegistered, "FirstListenerRunnable was not registered.");
    }


    @Test
    public void one_thread_registers_on_bus_and_listen_to_events() throws Exception {
        FirstListenerRunnable firstListenerRunnable = new FirstListenerRunnable(SLEEP_TIME);

        bus.register(firstListenerRunnable);

        Thread t = new Thread(firstListenerRunnable);
        t.start();

        bus.post(event1);

        t.join();

        Event receivedEvent = firstListenerRunnable.getReceivedEvent();
        assertNotNull(receivedEvent, "Event did not arrive to FirstListenerRunnable.");
        assertEquals(event1, receivedEvent, "Event was not the expected one: expected TestEvent1.");
    }

    @Test
    public void two_threads_register_on_bus_and_listen_to_events() throws Exception {
        FirstListenerRunnable firstListenerRunnable = new FirstListenerRunnable(SLEEP_TIME);
        SecondListenerRunnable secondListenerRunnable = new SecondListenerRunnable(SLEEP_TIME);

        bus.register(firstListenerRunnable);
        bus.register(secondListenerRunnable);

        Thread t1 = new Thread(firstListenerRunnable);
        Thread t2 = new Thread(secondListenerRunnable);

        t1.start();
        t2.start();

        bus.post(event1);
        bus.post(event2);

        t1.join();
        t2.join();

        Event receivedEvent1 = firstListenerRunnable.getReceivedEvent();
        Event receivedEvent2 = secondListenerRunnable.getReceivedEvent();

        assertNotNull(firstListenerRunnable.getReceivedEvent(), "Event SimpleExampleEvent did not arrive to FirstListenerRunnable.");
        assertNotNull(secondListenerRunnable.getReceivedEvent(), "Event SimpleExampleEvent2 did not arrive to SecondListenerRunnable.");
        assertEquals(event1, receivedEvent1, "Event was not the expected one: expected TestEvent1.");
        assertEquals(event2, receivedEvent2, "Event was not the expected one: expected TestEvent2.");
    }

    @Test
    public void two_threads_listen_from_bus_but_one_of_them_is_a_poster() throws Exception {
        PosterListenerRunnable posterListenerRunnable = new PosterListenerRunnable(bus,event2);
        SecondListenerRunnable secondListenerRunnable = new SecondListenerRunnable(SLEEP_TIME);

        bus.register(posterListenerRunnable);
        bus.register(secondListenerRunnable);

        Thread t1 = new Thread(posterListenerRunnable);
        Thread t2 = new Thread(secondListenerRunnable);

        t1.start();
        t2.start();

        bus.post(event1);

        t1.join();
        t2.join();

        Event receivedEvent1 = posterListenerRunnable.getReceivedEvent();
        Event receivedEvent2 = secondListenerRunnable.getReceivedEvent();

        assertNotNull(posterListenerRunnable.getReceivedEvent(), "Event TestEvent1 did not arrive to PosterListenerRunnable.");
        assertNotNull(secondListenerRunnable.getReceivedEvent(), "Event TestEvent2 did not arrive to SecondListenerRunnable.");
        assertEquals(event1, receivedEvent1, "Event was not the expected one: expected TestEvent1.");
        assertEquals(event2, receivedEvent2, "Event was not the expected one: expected TestEvent2.");
    }

    @Test
    public void a_sticky_event_is_posted_before_any_registrations_and_no_exception_is_thrown() {
        assertDoesNotThrow(() -> {
            bus.postSticky(event2);
        });
    }
    
    
    @Test
    public void two_threads_listen_from_bus_but_one_of_them_registers_later_listening_for_sticky_event() throws Exception {
        SecondListenerRunnable secondListenerRunnable = new SecondListenerRunnable(SLEEP_TIME);
        RegisteringListenerRunnable registeringListenerRunnable = new RegisteringListenerRunnable(bus,secondListenerRunnable);

        bus.register(registeringListenerRunnable);

        Thread t1 = new Thread(registeringListenerRunnable);
        Thread t2 = new Thread(secondListenerRunnable);

        t1.start();
        t2.start();

        bus.postSticky(event2);
        bus.post(event1);

        t1.join();
        t2.join();

        Event receivedEvent1 = registeringListenerRunnable.getReceivedEvent();
        Event receivedEvent2 = secondListenerRunnable.getReceivedEvent();

        assertNotNull(registeringListenerRunnable.getReceivedEvent(), "Event TestEvent1 did not arrive to RegisteringListenerRunnable.");
        assertNotNull(secondListenerRunnable.getReceivedEvent(), "Event TestEvent2 did not arrive to SecondListenerRunnable.");
        assertEquals(event1, receivedEvent1, "Event was not the expected one: expected TestEvent1.");
        assertEquals(event2, receivedEvent2, "Event was not the expected one: expected TestEvent2.");
    }
}
