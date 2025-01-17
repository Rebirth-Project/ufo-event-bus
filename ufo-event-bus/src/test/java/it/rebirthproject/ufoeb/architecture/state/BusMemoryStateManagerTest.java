/*
 * Copyright (C) 2021/2025 Andrea Paternesi Rebirth project
 * Modifications copyright (C) 2021/2025 Matteo Veroni Rebirth project
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
package it.rebirthproject.ufoeb.architecture.state;

import it.rebirthproject.ufoeb.architecture.messages.commands.PostEventMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.PostStickyEventMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.RegisterMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.ShutdownStateManagerMessage;
import it.rebirthproject.ufoeb.architecture.messages.interfaces.Message;
import it.rebirthproject.ufoeb.architecture.state.mock.FakeMessage;
import it.rebirthproject.ufoeb.architecture.state.mock.FakeMessageEmitter;
import it.rebirthproject.ufoeb.architecture.state.mock.FakePoolExecutor;
import it.rebirthproject.ufoeb.dto.BusEventKey;
import it.rebirthproject.ufoeb.dto.enums.EventPriority;
import it.rebirthproject.ufoeb.dto.events.TestEvent1;
import it.rebirthproject.ufoeb.dto.events.TestEvent2;
import it.rebirthproject.ufoeb.dto.events.TestEventParent;
import it.rebirthproject.ufoeb.dto.events.eventinterface.Event;
import it.rebirthproject.ufoeb.dto.events.eventinterface.EventInterfaceExtendingInterfaces;
import it.rebirthproject.ufoeb.dto.registrations.Registration;
import it.rebirthproject.ufoeb.eventinheritancepolicy.FactoryInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.base.InheritancePolicyType;
import it.rebirthproject.ufoeb.testutils.BaseTest;
import it.rebirthproject.ufoeb.testutils.validators.ExpectedMessage;
import it.rebirthproject.ufoeb.testutils.validators.ExpectedRegistration;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BusMemoryStateManagerTest extends BaseTest {

    private static FakeMessageEmitter fakeMessageEmitter;
    private static FakePoolExecutor fakePoolExecutor;
    private static BusMemoryStateManager busMemoryStateManager;
    private static MemoryState memoryState;

    @BeforeEach
    public void beforeEach() {
        executorService = Executors.newSingleThreadExecutor();
        fakeMessageEmitter = new FakeMessageEmitter(messageQueue);
        fakePoolExecutor = new FakePoolExecutor();

        memoryState = new MemoryState(!SAFE_REGISTRATIONS_NEEDED, FactoryInheritancePolicy.createInheritancePolicy(InheritancePolicyType.COMPLETE_EVENT_INHERITANCE, classProcessableService), VERBOSE_LOGGING);
        busMemoryStateManager = new BusMemoryStateManager(messageQueue, fakePoolExecutor, memoryState, listenerMethodFinder, THROW_NO_REGISTRATIONS_WARNING);
        executorService.submit(busMemoryStateManager);
    }

    @Test
    public void registering_twice_an_object_does_not_modify_memory_state() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObject1ToEvent1))
                .sendMessage(new RegisterMessage(registeredObject1ToEvent1))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Registration> registrations = memoryState.getRegistrations(new BusEventKey(TestEvent1.class));
        assertEquals(1, registrations.size(), "The number of registrations was different from expectations.");
    }

    @Test
    public void one_event_to_send_to_one_registered_object() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObject1ToEvent1))
                .sendMessage(new PostEventMessage(event1))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event1))
        );

        FakeMessage message = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList = message.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList,
                Arrays.asList(new ExpectedRegistration(registeredObject1ToEvent1, EventPriority.NONE, "onEvent1", TestEvent1.class))
        );
    }

    @Test
    public void one_event_to_send_to_two_registered_objects() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObject1ToEvent1))
                .sendMessage(new RegisterMessage(registeredObject2ToEvent1))
                .sendMessage(new PostEventMessage(event1))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event1))
        );

        FakeMessage message = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList = message.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList,
                Arrays.asList(
                        new ExpectedRegistration(registeredObject1ToEvent1, EventPriority.NONE, "onEvent1", TestEvent1.class),
                        new ExpectedRegistration(registeredObject2ToEvent1, EventPriority.NONE, "onEvent1", TestEvent1.class))
        );
    }

    @Test
    public void two_different_events_to_send_to_same_object() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObjectToTwoEvents))
                .sendMessage(new PostEventMessage(event1))
                .sendMessage(new PostEventMessage(event2))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event1),
                        new ExpectedMessage(event2))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(new ExpectedRegistration(registeredObjectToTwoEvents, EventPriority.NONE, "onEvent1", TestEvent1.class))
        );

        FakeMessage message2 = (FakeMessage) returnMessageList.get(1);
        List<Registration> registrationsList2 = message2.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList2,
                Arrays.asList(new ExpectedRegistration(registeredObjectToTwoEvents, EventPriority.NONE, "onEvent2", TestEvent2.class))
        );
    }

    @Test
    public void two_equal_events_to_send_to_same_object() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObject1ToEvent1))
                .sendMessage(new PostEventMessage(event1))
                .sendMessage(new PostEventMessage(event1))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event1),
                        new ExpectedMessage(event1))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(new ExpectedRegistration(registeredObject1ToEvent1, EventPriority.NONE, "onEvent1", TestEvent1.class))
        );

        FakeMessage message2 = (FakeMessage) returnMessageList.get(1);
        List<Registration> registrationsList2 = message2.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList2,
                Arrays.asList(new ExpectedRegistration(registeredObject1ToEvent1, EventPriority.NONE, "onEvent1", TestEvent1.class))
        );
    }

    @Test
    public void two_different_events_to_send_to_two_different_objects() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObject1ToEvent1))
                .sendMessage(new RegisterMessage(registeredObject1ToEvent2))
                .sendMessage(new PostEventMessage(event1))
                .sendMessage(new PostEventMessage(event2))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event1),
                        new ExpectedMessage(event2))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(new ExpectedRegistration(registeredObject1ToEvent1, EventPriority.NONE, "onEvent1", TestEvent1.class))
        );

        FakeMessage message2 = (FakeMessage) returnMessageList.get(1);
        List<Registration> registrationsList2 = message2.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList2,
                Arrays.asList(new ExpectedRegistration(registeredObject1ToEvent2, EventPriority.NONE, "onEvent2", TestEvent2.class))
        );
    }

    @Test
    public void two_equal_events_to_send_to_two_objects() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObject1ToEvent1))
                .sendMessage(new RegisterMessage(registeredObject2ToEvent1))
                .sendMessage(new PostEventMessage(event1))
                .sendMessage(new PostEventMessage(event1))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event1),
                        new ExpectedMessage(event1))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(
                        new ExpectedRegistration(registeredObject1ToEvent1, EventPriority.NONE, "onEvent1", TestEvent1.class),
                        new ExpectedRegistration(registeredObject2ToEvent1, EventPriority.NONE, "onEvent1", TestEvent1.class))
        );

        FakeMessage message2 = (FakeMessage) returnMessageList.get(1);
        List<Registration> registrationsList2 = message2.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList2,
                Arrays.asList(
                        new ExpectedRegistration(registeredObject1ToEvent1, EventPriority.NONE, "onEvent1", TestEvent1.class),
                        new ExpectedRegistration(registeredObject2ToEvent1, EventPriority.NONE, "onEvent1", TestEvent1.class))
        );
    }

    @Test
    public void four_different_events_to_same_object_maintains_order_when_received() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObjectToTwoEvents))
                .sendMessage(new PostEventMessage(event2))
                .sendMessage(new PostEventMessage(event1))
                .sendMessage(new PostEventMessage(secondEvent1))
                .sendMessage(new PostEventMessage(secondEvent2))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event2),
                        new ExpectedMessage(event1),
                        new ExpectedMessage(secondEvent1),
                        new ExpectedMessage(secondEvent2))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(new ExpectedRegistration(registeredObjectToTwoEvents, EventPriority.NONE, "onEvent2", TestEvent2.class))
        );

        FakeMessage message2 = (FakeMessage) returnMessageList.get(1);
        List<Registration> registrationsList2 = message2.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList2,
                Arrays.asList(new ExpectedRegistration(registeredObjectToTwoEvents, EventPriority.NONE, "onEvent1", TestEvent1.class))
        );

        FakeMessage message3 = (FakeMessage) returnMessageList.get(2);
        List<Registration> registrationsList3 = message3.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList3,
                Arrays.asList(new ExpectedRegistration(registeredObjectToTwoEvents, EventPriority.NONE, "onEvent1", TestEvent1.class))
        );

        FakeMessage message4 = (FakeMessage) returnMessageList.get(3);
        List<Registration> registrationsList4 = message4.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList4,
                Arrays.asList(new ExpectedRegistration(registeredObjectToTwoEvents, EventPriority.NONE, "onEvent2", TestEvent2.class))
        );
    }

    @Test
    public void test_priority_order_for_one_event() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObjectToEvent1WithMediumPriority))
                .sendMessage(new RegisterMessage(registeredObjectToEvent1WithLowPriority))
                .sendMessage(new RegisterMessage(registeredObjectToEvent1WithHighPriority))
                .sendMessage(new PostEventMessage(event1))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event1))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(
                        new ExpectedRegistration(registeredObjectToEvent1WithHighPriority, EventPriority.HIGH, "onEvent1", TestEvent1.class),
                        new ExpectedRegistration(registeredObjectToEvent1WithMediumPriority, EventPriority.MEDIUM, "onEvent1", TestEvent1.class),
                        new ExpectedRegistration(registeredObjectToEvent1WithLowPriority, EventPriority.LOW, "onEvent1", TestEvent1.class))
        );
    }

    @Test
    public void test_priority_order_for_two_events() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObjectToEvent1WithMediumPriority))
                .sendMessage(new RegisterMessage(registeredObjectToEvent2WithLowPriority))
                .sendMessage(new RegisterMessage(registeredObjectToEvent1WithLowPriority))
                .sendMessage(new RegisterMessage(registeredObjectToEvent1WithHighPriority))
                .sendMessage(new RegisterMessage(registeredObjectToEvent2WithMediumPriority))
                .sendMessage(new RegisterMessage(registeredObjectToEvent2WithHighPriority))
                .sendMessage(new PostEventMessage(event2))
                .sendMessage(new PostEventMessage(event1))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event2),
                        new ExpectedMessage(event1))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(
                        new ExpectedRegistration(registeredObjectToEvent2WithHighPriority, EventPriority.HIGH, "onEvent2", TestEvent2.class),
                        new ExpectedRegistration(registeredObjectToEvent2WithMediumPriority, EventPriority.MEDIUM, "onEvent2", TestEvent2.class),
                        new ExpectedRegistration(registeredObjectToEvent2WithLowPriority, EventPriority.LOW, "onEvent2", TestEvent2.class))
        );

        FakeMessage message2 = (FakeMessage) returnMessageList.get(1);
        List<Registration> registrationsList2 = message2.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList2,
                Arrays.asList(
                        new ExpectedRegistration(registeredObjectToEvent1WithHighPriority, EventPriority.HIGH, "onEvent1", TestEvent1.class),
                        new ExpectedRegistration(registeredObjectToEvent1WithMediumPriority, EventPriority.MEDIUM, "onEvent1", TestEvent1.class),
                        new ExpectedRegistration(registeredObjectToEvent1WithLowPriority, EventPriority.LOW, "onEvent1", TestEvent1.class))
        );
    }

    @Test
    public void events_posted_before_registrations_are_delivered_only_if_they_are_sticky_events() throws Exception {
        fakeMessageEmitter
                .sendMessage(new PostStickyEventMessage(event1))
                .sendMessage(new PostEventMessage(event2))
                .delay(500)
                .sendMessage(new RegisterMessage(registeredObject1ToStickyEvent1))
                .sendMessage(new RegisterMessage(registeredObject1ToEvent2))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event1))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(new ExpectedRegistration(registeredObject1ToStickyEvent1, EventPriority.NONE, "onEvent1", TestEvent1.class))
        );
    }

    @Test
    public void sending_events_using_interfaces() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObject1ToEventInterface))
                .sendMessage(new PostEventMessage(event1BehindInterface1))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event1BehindInterface1))
        );

        FakeMessage message = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(new ExpectedRegistration(registeredObject1ToEventInterface, EventPriority.NONE, "onEvent", Event.class))
        );
    }

    @Test
    public void sending_event_using_interface_which_extends_interfaces() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObjectToEventInterfaceExtendingInterfaces))
                .sendMessage(new PostEventMessage(eventBehindInterfaceExtendingInterfaces))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(eventBehindInterfaceExtendingInterfaces))
        );

        FakeMessage message = (FakeMessage) returnMessageList.get(0);

        List<Registration> registrationsList = message.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList,
                Arrays.asList(new ExpectedRegistration(registeredObjectToEventInterfaceExtendingInterfaces, EventPriority.NONE, "onEvent", EventInterfaceExtendingInterfaces.class))
        );
    }

    @Test
    public void send_event_which_implements_interface_both_to_listener_of_concrete_event_and_to_listener_of_interface() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObject1ToEventInterface))
                .sendMessage(new RegisterMessage(registeredObject1ToEvent2))
                .sendMessage(new PostEventMessage(event2))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event2),
                        new ExpectedMessage(event2))
        );

        //The order depends on the implementation of the event2 and on the fact that eventInheritance is activated        
        //since event2 is of class TestEvent2 and implements interface Event, and event2 is posted to the bus
        //bus will call first "TestEvent2" and then "Event" listening methods.
        FakeMessage message2 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList2 = message2.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList2,
                Arrays.asList(new ExpectedRegistration(registeredObject1ToEvent2, EventPriority.NONE, "onEvent2", TestEvent2.class))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(1);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(
                        new ExpectedRegistration(registeredObject1ToEventInterface, EventPriority.NONE, "onEvent", Event.class))
        );
    }

    @Test
    public void registering_two_listeners_that_listen_to_different_event_interfaces_only_one_gets_the_posted_event() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObjectToEventInterfaceExtendingInterfaces))
                .sendMessage(new RegisterMessage(registeredObject1ToEventInterface))
                .sendMessage(new PostEventMessage(event1BehindInterface1))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event1BehindInterface1))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(new ExpectedRegistration(registeredObject1ToEventInterface, EventPriority.NONE, "onEvent", Event.class))
        );

        //registeredObjectToEventInterfaceExtendingInterfaces must not get the posted event because
        //EventInterfaceExtendingInterfaces.class.isAssignableFrom(event1BehindInterface1.getClass()) is false
    }

    @Test
    public void registering_two_listeners_that_listen_to_different_event_interfaces_boths_get_the_posted_event() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObjectToEventInterfaceExtendingInterfaces))
                .sendMessage(new RegisterMessage(registeredObject1ToEventInterface))
                .sendMessage(new PostEventMessage(eventBehindInterfaceExtendingInterfaces))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(eventBehindInterfaceExtendingInterfaces),
                        new ExpectedMessage(eventBehindInterfaceExtendingInterfaces))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(new ExpectedRegistration(registeredObjectToEventInterfaceExtendingInterfaces, EventPriority.NONE, "onEvent", EventInterfaceExtendingInterfaces.class))
        );

        FakeMessage message2 = (FakeMessage) returnMessageList.get(1);
        List<Registration> registrationsList2 = message2.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList2,
                Arrays.asList(new ExpectedRegistration(registeredObject1ToEventInterface, EventPriority.NONE, "onEvent", Event.class))
        );

        //registeredObject1ToEventInterface gets the posted event because
        //Event.class.isAssignableFrom(eventBehindInterfaceExtendingInterfaces.getClass()) is true        
    }

    @Test
    public void listener_that_listen_to_derived_event_does_not_get_the_event() throws Exception {
        fakeMessageEmitter
                .sendMessage(new RegisterMessage(registeredObjectToEventParent))
                .sendMessage(new RegisterMessage(registeredObject1ToEventInterface))
                .sendMessage(new RegisterMessage(registeredClassToEventImplementingInterfaceWithParentClass))
                .sendMessage(new PostEventMessage(testEventParent))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(testEventParent),
                        new ExpectedMessage(testEventParent))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(new ExpectedRegistration(registeredObjectToEventParent, EventPriority.NONE, "onEvent", TestEventParent.class))
        );

        FakeMessage message2 = (FakeMessage) returnMessageList.get(1);
        List<Registration> registrationsList2 = message2.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList2,
                Arrays.asList(new ExpectedRegistration(registeredObject1ToEventInterface, EventPriority.NONE, "onEvent", Event.class))
        );
    }
}
