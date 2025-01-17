/*
 * Copyright (C) 2021/2025 Andrea Paternesi Rebirth project
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
import it.rebirthproject.ufoeb.architecture.state.mock.FakeMessageEmitter;
import it.rebirthproject.ufoeb.architecture.state.mock.FakePoolExecutor;
import it.rebirthproject.ufoeb.architecture.state.dto.inheritancetest.events.EventExtendingJavaLibClass;
import it.rebirthproject.ufoeb.architecture.state.dto.inheritancetest.events.EventImplementingJavaLibInterface;
import it.rebirthproject.ufoeb.architecture.state.dto.inheritancetest.objectstoregister.ListenerForForbiddenEvent1;
import it.rebirthproject.ufoeb.architecture.state.dto.inheritancetest.objectstoregister.ListenerForForbiddenEvent2;
import it.rebirthproject.ufoeb.eventinheritancepolicy.FactoryInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.base.AbstractEventInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.base.InheritancePolicyType;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import it.rebirthproject.ufoeb.testutils.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BusMemoryStateManagerEventFrontierPathCheckTest extends BaseTest {

    private static FakeMessageEmitter fakeMessageEmitter;
    private static FakePoolExecutor fakePoolExecutor;
    private static BusMemoryStateManager busMemoryStateManager;
    private static MemoryState memoryState;

    @BeforeEach
    public void beforeEach() {
        fakeMessageEmitter = new FakeMessageEmitter(messageQueue);
        fakePoolExecutor = new FakePoolExecutor();

        memoryState = new MemoryState(!SAFE_REGISTRATIONS_NEEDED, FactoryInheritancePolicy.createInheritancePolicy(InheritancePolicyType.COMPLETE_EVENT_INHERITANCE, classProcessableService), VERBOSE_LOGGING);
        busMemoryStateManager = new BusMemoryStateManager(messageQueue, fakePoolExecutor, memoryState, listenerMethodFinder, THROW_NO_REGISTRATIONS_WARNING);
    }

    @Test
    public void finder_stop_reflection_if_event_listened_class_hierachy_contains_forbidden_class_type() throws Exception {
        EventExtendingJavaLibClass eventExtendingJavaLibClass = new EventExtendingJavaLibClass();
        ListenerForForbiddenEvent1 listener = new ListenerForForbiddenEvent1();

        fakeMessageEmitter
                .sendMessage(new RegisterMessage(listener))
                .sendMessage(new PostEventMessage(eventExtendingJavaLibClass));

        EventBusException exception = Assertions.assertThrows(EventBusException.class, () -> {
            busMemoryStateManager.run();
        });
        Assertions.assertTrue(exception.getMessage().equals(AbstractEventInheritancePolicy.INHERITANCE_ERROR), "");
    }

    @Test
    public void finder_stop_reflection_if_event_listened_class_hierachy_contains_forbidden_interface_type() throws Exception {
        EventImplementingJavaLibInterface eventImplementingJavaLibInterface = new EventImplementingJavaLibInterface();
        ListenerForForbiddenEvent2 listener = new ListenerForForbiddenEvent2();

        fakeMessageEmitter
                .sendMessage(new RegisterMessage(listener))
                .sendMessage(new PostEventMessage(eventImplementingJavaLibInterface));

        EventBusException exception = Assertions.assertThrows(EventBusException.class, () -> {
            busMemoryStateManager.run();
        });
        Assertions.assertTrue(exception.getMessage().equals(AbstractEventInheritancePolicy.INHERITANCE_ERROR), "");
    }

    @Test
    public void finder_stop_reflection_if_event_listened_class_hierachy_does_not_belong_to_the_correct_frontier_path() throws Exception {
        //We override the Processable service setting the frontier path
        //we set a different frontier path and we use an event that does not belong to that frontier path
        classProcessableService.setInheritancePackageFrontierPath("it.rebirthproject.ufoeb.architecture.state.dto.inheritancetest.objectstoregister");
      
        EventExtendingJavaLibClass eventExtendingJavaLibClass = new EventExtendingJavaLibClass();
        ListenerForForbiddenEvent1 listener = new ListenerForForbiddenEvent1();

        fakeMessageEmitter
                .sendMessage(new RegisterMessage(listener))
                .sendMessage(new PostEventMessage(eventExtendingJavaLibClass));

        EventBusException exception = Assertions.assertThrows(EventBusException.class, () -> {
            busMemoryStateManager.run();
        });
        Assertions.assertTrue(exception.getMessage().equals(AbstractEventInheritancePolicy.INHERITANCE_ERROR), "");
    }
}
