/*
 * Copyright (C) 2021/2022 Andrea Paternesi Rebirth project
 * Modifications copyright (C) 2021/2022 Matteo Veroni Rebirth project
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
import it.rebirthproject.ufoeb.architecture.messages.commands.RegisterMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.ShutdownStateManagerMessage;
import it.rebirthproject.ufoeb.architecture.messages.interfaces.Message;
import it.rebirthproject.ufoeb.architecture.state.dto.inheritancetest.events.*;
import it.rebirthproject.ufoeb.architecture.state.dto.inheritancetest.objectstoregister.Listener;
import it.rebirthproject.ufoeb.architecture.state.mock.FakeMessage;
import it.rebirthproject.ufoeb.architecture.state.mock.FakeMessageEmitter;
import it.rebirthproject.ufoeb.architecture.state.mock.FakePoolExecutor;
import it.rebirthproject.ufoeb.dto.enums.EventPriority;
import it.rebirthproject.ufoeb.dto.registrations.Registration;
import it.rebirthproject.ufoeb.eventinheritancepolicy.FactoryInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.base.InheritancePolicyType;
import it.rebirthproject.ufoeb.testutils.BaseTest;
import it.rebirthproject.ufoeb.testutils.validators.ExpectedMessage;
import it.rebirthproject.ufoeb.testutils.validators.ExpectedRegistration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class BusMemoryStateManagerInheritanceOverInterfaceTest extends BaseTest {

    private static FakeMessageEmitter fakeMessageEmitter;
    private static FakePoolExecutor fakePoolExecutor;
    private static BusMemoryStateManager busMemoryStateManager;
    private static MemoryState memoryState;

    private Listener listener;

    @BeforeEach
    public void beforeEach() {
        executorService = Executors.newSingleThreadExecutor();
        fakeMessageEmitter = new FakeMessageEmitter(messageQueue);
        fakePoolExecutor = new FakePoolExecutor();

        memoryState = new MemoryState(!SAFE_REGISTRATIONS_NEEDED, FactoryInheritancePolicy.createInheritancePolicy(InheritancePolicyType.INTERFACE_EVENT_INHERITANCE), VERBOSE_LOGGING);
        busMemoryStateManager = new BusMemoryStateManager(messageQueue, fakePoolExecutor, memoryState, listenerMethodFinder, THROW_NO_REGISTRATIONS_WARNING);
        executorService.submit(busMemoryStateManager);
        listener = new Listener();
    }

    @Test
    public void send_event_which_implements_interface() throws Exception {
        Event1 event1 = new Event1();

        fakeMessageEmitter
                .sendMessage(new RegisterMessage(listener))
                .sendMessage(new PostEventMessage(event1))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event1),
                        new ExpectedMessage(event1))
        );

        FakeMessage message = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList = message.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList,
                Arrays.asList(new ExpectedRegistration(listener, EventPriority.NONE, "method2", Event1.class))
        );

        FakeMessage message2 = (FakeMessage) returnMessageList.get(1);
        List<Registration> registrationsList2 = message2.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList2,
                Arrays.asList(new ExpectedRegistration(listener, EventPriority.NONE, "method1", EventInterface1.class))
        );
    }

    @Test
    public void send_event_which_implements_interface_but_doesnt_send_to_superclass() throws Exception {
        Event2 event2 = new Event2();

        fakeMessageEmitter
                .sendMessage(new RegisterMessage(listener))
                .sendMessage(new PostEventMessage(event2))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event2),
                        new ExpectedMessage(event2))
        );

        FakeMessage message = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList = message.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList,
                Arrays.asList(new ExpectedRegistration(listener, EventPriority.NONE, "method3", Event2.class))
        );

        FakeMessage message2 = (FakeMessage) returnMessageList.get(1);
        List<Registration> registrationsList2 = message2.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList2,
                Arrays.asList(new ExpectedRegistration(listener, EventPriority.NONE, "method1", EventInterface1.class))
        );
    }

    @Test
    public void doesnt_send_to_superclasses() throws Exception {
        Event3 event3 = new Event3();

        fakeMessageEmitter
                .sendMessage(new RegisterMessage(listener))
                .sendMessage(new PostEventMessage(event3))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event3))
        );

        FakeMessage message = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList = message.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList,
                Arrays.asList(new ExpectedRegistration(listener, EventPriority.NONE, "method4", Event3.class))
        );
    }

    @Test
    public void test_for_interfaces_cascading_on_superclasses_when_listener_doesnt_listen_to_the_class() throws Exception {
        Event5 event5 = new Event5();

        fakeMessageEmitter
                .sendMessage(new RegisterMessage(listener))
                .sendMessage(new PostEventMessage(event5))
                .sendMessage(new ShutdownStateManagerMessage());

        awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(event5))
        );

        FakeMessage message = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList = message.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList,
                Arrays.asList(new ExpectedRegistration(listener, EventPriority.NONE, "method1", EventInterface1.class))
        );
    }
}
