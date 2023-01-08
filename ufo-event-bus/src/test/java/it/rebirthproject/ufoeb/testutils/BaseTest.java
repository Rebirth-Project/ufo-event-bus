/*
 * Copyright (C) 2021/2023 Andrea Paternesi Rebirth project
 * Modifications copyright (C) 2021/2023 Matteo Veroni Rebirth project
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
package it.rebirthproject.ufoeb.testutils;

import it.rebirthproject.ufoeb.architecture.messages.interfaces.Message;
import it.rebirthproject.ufoeb.dto.events.*;
import it.rebirthproject.ufoeb.dto.events.eventinterface.Event;
import it.rebirthproject.ufoeb.dto.events.eventinterface.EventInterfaceExtendingInterfaces;
import it.rebirthproject.ufoeb.dto.objectstoregister.statemanager.*;
import it.rebirthproject.ufoeb.dto.objectstoregister.statemanager.sticky.RegisteredClass1ToStickyEvent1;
import it.rebirthproject.ufoeb.dto.registrations.Registration;
import it.rebirthproject.ufoeb.services.ListenerMethodFinder;
import it.rebirthproject.ufoeb.testutils.validators.ExpectedMessage;
import it.rebirthproject.ufoeb.testutils.validators.ExpectedRegistration;
import it.rebirthproject.ufoeb.testutils.validators.NonBlockingExpectedRegistration;
import it.rebirthproject.ufoeb.testutils.verifiers.ListVerifier;
import it.rebirthproject.ufoeb.testutils.verifiers.UnorderedListVerifier;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;

public abstract class BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    public static final String TEST_FRONTIER_PATH = "it.rebirthproject.ufoeb";

    public static final boolean USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION = false;
    public static final boolean LISTENER_SUPERCLASS_INHERITANCE = true;
    public static final boolean THROW_NOT_VALID_METHOD_EXCEPTION = true;
    public static final boolean THROW_NO_LISTENERS_EXCEPTION = true;
    public static final boolean SAFE_REGISTRATIONS_NEEDED = true;
    public static final boolean THROW_NO_REGISTRATIONS_WARNING = true;
    public static final boolean EVENT_SUPERCLASS_INHERITANCE = true;
    public static final boolean VERBOSE_LOGGING = true;
    public static final String EMPTY_INHERITANCE_FRONTIER_PATH = "";

    private static final int QUEUE_LENGHT = 10;

    protected static BlockingQueue<Message> messageQueue;
    protected static BlockingQueue<Message> eventWorkerQueue;
    protected static ListenerMethodFinder listenerMethodFinder;
    protected static ExecutorService executorService;
    protected static ListVerifier<Message, ExpectedMessage> messageListVerifier;
    protected static ListVerifier<Registration, ExpectedRegistration> registrationListVerifier;
    protected static UnorderedListVerifier<Registration, NonBlockingExpectedRegistration> unorderedRegistrationListVerifier;

    protected static RegisteredClass1ToEventInterface registeredObject1ToEventInterface = new RegisteredClass1ToEventInterface();
    protected static RegisteredClass1ToEvent1 registeredObject1ToEvent1 = new RegisteredClass1ToEvent1();
    protected static RegisteredClass2ToEvent1 registeredObject2ToEvent1 = new RegisteredClass2ToEvent1();
    protected static RegisteredClass1ToEvent2 registeredObject1ToEvent2 = new RegisteredClass1ToEvent2();
    protected static RegisteredClassToTwoEvents registeredObjectToTwoEvents = new RegisteredClassToTwoEvents();
    protected static RegisteredClassToEvent1WithLowPriority registeredObjectToEvent1WithLowPriority = new RegisteredClassToEvent1WithLowPriority();
    protected static RegisteredClassToEvent1WithMediumPriority registeredObjectToEvent1WithMediumPriority = new RegisteredClassToEvent1WithMediumPriority();
    protected static RegisteredClassToEvent1WithHighPriority registeredObjectToEvent1WithHighPriority = new RegisteredClassToEvent1WithHighPriority();
    protected static RegisteredClassToEvent2WithLowPriority registeredObjectToEvent2WithLowPriority = new RegisteredClassToEvent2WithLowPriority();
    protected static RegisteredClassToEvent2WithMediumPriority registeredObjectToEvent2WithMediumPriority = new RegisteredClassToEvent2WithMediumPriority();
    protected static RegisteredClassToEvent2WithHighPriority registeredObjectToEvent2WithHighPriority = new RegisteredClassToEvent2WithHighPriority();
    protected static RegisteredClass1ToStickyEvent1 registeredObject1ToStickyEvent1 = new RegisteredClass1ToStickyEvent1();
    protected static RegisteredClassToEventInterfaceExtendingInterfaces registeredObjectToEventInterfaceExtendingInterfaces = new RegisteredClassToEventInterfaceExtendingInterfaces();
    protected static RegisteredClassToEventParent registeredObjectToEventParent = new RegisteredClassToEventParent();
    protected static RegisteredClassToEventImplementingInterfaceWithParentClass registeredClassToEventImplementingInterfaceWithParentClass = new RegisteredClassToEventImplementingInterfaceWithParentClass();

    protected static Event event1BehindInterface1 = new TestEvent1();
    protected static EventInterfaceExtendingInterfaces eventBehindInterfaceExtendingInterfaces = new ConcreteEventInterfaceExtendingInterfaces();
    protected static ConcreteEventImplementingInterfaceWithParentClass eventImplementingInterfaceWithParentClass = new ConcreteEventImplementingInterfaceWithParentClass();
    protected static TestEvent1 event1 = new TestEvent1();
    protected static TestEvent1 secondEvent1 = new TestEvent1();
    protected static TestEvent2 event2 = new TestEvent2();
    protected static TestEvent2 secondEvent2 = new TestEvent2();
    protected static TestEventParent testEventParent = new TestEventParent();

    @BeforeAll
    public static void beforeAll() {
        logger.info("Java version: {}", System.getProperty("java.version"));

        messageQueue = new ArrayBlockingQueue<>(QUEUE_LENGHT);
        eventWorkerQueue = new LinkedBlockingQueue<>(QUEUE_LENGHT);
        listenerMethodFinder = new ListenerMethodFinder(LISTENER_SUPERCLASS_INHERITANCE, THROW_NOT_VALID_METHOD_EXCEPTION, THROW_NO_LISTENERS_EXCEPTION, USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION, EMPTY_INHERITANCE_FRONTIER_PATH);
        messageListVerifier = new ListVerifier<>();
        registrationListVerifier = new ListVerifier<>();
    }

    public void awaitUntilExecutorFinishToWorkAndDie() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);
    }
}
