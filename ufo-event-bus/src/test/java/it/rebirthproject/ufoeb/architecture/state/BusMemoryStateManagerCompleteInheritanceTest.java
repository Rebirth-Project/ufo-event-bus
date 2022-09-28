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
package it.rebirthproject.ufoeb.architecture.state;

import it.rebirthproject.ufoeb.architecture.messages.commands.PostEventMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.RegisterMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.ShutdownStateManagerMessage;
import it.rebirthproject.ufoeb.architecture.messages.interfaces.Message;
import it.rebirthproject.ufoeb.architecture.state.dto.complextest.events.*;
import it.rebirthproject.ufoeb.architecture.state.dto.complextest.objectstoregister.ListenerToRegisterOnAllEvents;
import it.rebirthproject.ufoeb.architecture.state.dto.complextest.objectstoregister.ListenerToRegisterOnBlackPen;
import it.rebirthproject.ufoeb.architecture.state.dto.complextest.objectstoregister.ListenerToRegisterOnSquaredBlackPen;
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
import java.util.concurrent.CountDownLatch;

public class BusMemoryStateManagerCompleteInheritanceTest extends BaseTest {

    private static FakeMessageEmitter fakeMessageEmitter;
    private static FakePoolExecutor fakePoolExecutor;
    private static BusMemoryStateManager busMemoryStateManager;
    private static MemoryState memoryState;

    @BeforeEach
    public void beforeEach() {
        countDownLatch = new CountDownLatch(1);
        fakeMessageEmitter = new FakeMessageEmitter(messageQueue, countDownLatch);
        fakePoolExecutor = new FakePoolExecutor();

        memoryState = new MemoryState(!SAFE_REGISTRATIONS_NEEDED, FactoryInheritancePolicy.createInheritancePolicy(InheritancePolicyType.COMPLETE_EVENT_INHERITANCE), VERBOSE_LOGGING);
        busMemoryStateManager = new BusMemoryStateManager(messageQueue, fakePoolExecutor, countDownLatch, memoryState, listenerMethodFinder, THROW_NO_REGISTRATIONS_WARNING);
        executorService.submit(busMemoryStateManager);
    }

    @Test
    public void complex_test_with_event_inheritance1() throws Exception {
        BlackTriangularPen blackTriangularPenEvent = new BlackTriangularPen();
        ListenerToRegisterOnBlackPen listener = new ListenerToRegisterOnBlackPen();

        fakeMessageEmitter
                .sendMessage(new RegisterMessage(listener))
                .sendMessage(new PostEventMessage(blackTriangularPenEvent))
                .sendMessage(new ShutdownStateManagerMessage())
                .awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(blackTriangularPenEvent))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(new ExpectedRegistration(listener, EventPriority.NONE, "method", BlackPen.class))
        );
    }

    @Test
    public void complex_test_with_event_inheritance2() throws Exception {
        BlackPen blackPenEvent = new BlackPen();
        ListenerToRegisterOnAllEvents listener = new ListenerToRegisterOnAllEvents();

        fakeMessageEmitter
                .sendMessage(new RegisterMessage(listener))
                .sendMessage(new PostEventMessage(blackPenEvent))
                .sendMessage(new ShutdownStateManagerMessage())
                .awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(blackPenEvent),
                        new ExpectedMessage(blackPenEvent))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(new ExpectedRegistration(listener, EventPriority.NONE, "method4", BlackPen.class))
        );

        FakeMessage message2 = (FakeMessage) returnMessageList.get(1);
        List<Registration> registrationsList2 = message2.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList2,
                Arrays.asList(new ExpectedRegistration(listener, EventPriority.NONE, "method1", BlackColorInterface.class))
        );
    }

    @Test
    public void complex_test_with_event_inheritance3() throws Exception {
        BlackTriangularPen blackTriangularPenEvent = new BlackTriangularPen();
        ListenerToRegisterOnAllEvents listener = new ListenerToRegisterOnAllEvents();

        fakeMessageEmitter
                .sendMessage(new RegisterMessage(listener))
                .sendMessage(new PostEventMessage(blackTriangularPenEvent))
                .sendMessage(new ShutdownStateManagerMessage())
                .awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(blackTriangularPenEvent),
                        new ExpectedMessage(blackTriangularPenEvent),
                        new ExpectedMessage(blackTriangularPenEvent),
                        new ExpectedMessage(blackTriangularPenEvent),
                        new ExpectedMessage(blackTriangularPenEvent))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(new ExpectedRegistration(listener, EventPriority.NONE, "method5", BlackTriangularPen.class))
        );

        FakeMessage message2 = (FakeMessage) returnMessageList.get(1);
        List<Registration> registrationsList2 = message2.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList2,
                Arrays.asList(new ExpectedRegistration(listener, EventPriority.NONE, "method3", TriangleInterface.class))
        );

        FakeMessage message3 = (FakeMessage) returnMessageList.get(2);
        List<Registration> registrationsList3 = message3.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList3,
                Arrays.asList(new ExpectedRegistration(listener, EventPriority.NONE, "method2", PoligonInterface.class))
        );

        FakeMessage message4 = (FakeMessage) returnMessageList.get(3);
        List<Registration> registrationsList4 = message4.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList4,
                Arrays.asList(new ExpectedRegistration(listener, EventPriority.NONE, "method4", BlackPen.class))
        );

        FakeMessage message5 = (FakeMessage) returnMessageList.get(4);
        List<Registration> registrationsList5 = message5.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList5,
                Arrays.asList(new ExpectedRegistration(listener, EventPriority.NONE, "method1", BlackColorInterface.class))
        );
    }

    @Test
    public void complex_test_with_event_inheritance4() throws Exception {
        BlackSquaredPen blackSquaredPen = new BlackSquaredPen();
        ListenerToRegisterOnSquaredBlackPen listenerSquared = new ListenerToRegisterOnSquaredBlackPen();
        ListenerToRegisterOnAllEvents listenerAll = new ListenerToRegisterOnAllEvents();

        fakeMessageEmitter
                .sendMessage(new RegisterMessage(listenerSquared))
                .sendMessage(new RegisterMessage(listenerAll))
                .sendMessage(new PostEventMessage(blackSquaredPen))
                .sendMessage(new ShutdownStateManagerMessage())
                .awaitUntilExecutorFinishToWorkAndDie();

        List<Message> returnMessageList = fakePoolExecutor.getReceivedMessageList();
        messageListVerifier.assertAsExpected(returnMessageList,
                Arrays.asList(new ExpectedMessage(blackSquaredPen),
                        new ExpectedMessage(blackSquaredPen),
                        new ExpectedMessage(blackSquaredPen))
        );

        FakeMessage message1 = (FakeMessage) returnMessageList.get(0);
        List<Registration> registrationsList1 = message1.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList1,
                Arrays.asList(new ExpectedRegistration(listenerSquared, EventPriority.NONE, "method2", BlackSquaredPen.class))
        );

        FakeMessage message2 = (FakeMessage) returnMessageList.get(1);
        List<Registration> registrationsList2 = message2.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList2,
                Arrays.asList(
                        new ExpectedRegistration(listenerSquared, EventPriority.NONE, "method1", BlackPen.class),
                        new ExpectedRegistration(listenerAll, EventPriority.NONE, "method4", BlackPen.class))
        );

        FakeMessage message3 = (FakeMessage) returnMessageList.get(2);
        List<Registration> registrationsList3 = message3.getRegistrationsList();
        registrationListVerifier.assertAsExpected(
                registrationsList3,
                Arrays.asList(new ExpectedRegistration(listenerAll, EventPriority.NONE, "method1", BlackColorInterface.class))
        );
    }
}
