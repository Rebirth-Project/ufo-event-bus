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
package it.rebirthproject.ufoeb.services;

import it.rebirthproject.ufoeb.architecture.state.MemoryState;
import it.rebirthproject.ufoeb.dto.BusEventKey;
import it.rebirthproject.ufoeb.dto.enums.EventPriority;
import it.rebirthproject.ufoeb.dto.events.TestEvent1;
import it.rebirthproject.ufoeb.dto.events.TestEventWithParentClass;
import it.rebirthproject.ufoeb.dto.objectstoregister.listenermethodfinder.ClassWithValidListenerMethodAnnotation;
import it.rebirthproject.ufoeb.dto.objectstoregister.services.*;
import it.rebirthproject.ufoeb.dto.objectstoregister.sonpackage.TestClassSonOnDeepPackageToRegister;
import it.rebirthproject.ufoeb.eventinheritancepolicy.FactoryInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.base.InheritancePolicyType;
import it.rebirthproject.ufoeb.eventinheritancepolicy.policies.NoEventInheritancePolicy;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import it.rebirthproject.ufoeb.testutils.BaseTest;
import it.rebirthproject.ufoeb.testutils.validators.ExpectedRegistration;
import it.rebirthproject.ufoeb.testutils.validators.NonBlockingExpectedRegistration;
import it.rebirthproject.ufoeb.testutils.verifiers.ListVerifier;
import it.rebirthproject.ufoeb.testutils.verifiers.UnorderedListVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class ListenerMethodFinderTest extends BaseTest {

    public static final boolean THROW_NO_LISTENER_ANNOTATION_EXCEPTION = true;

    private static MemoryState memoryState;

    @BeforeEach
    public void beforeEach() {
        memoryState = new MemoryState(!SAFE_REGISTRATIONS_NEEDED, FactoryInheritancePolicy.createInheritancePolicy(InheritancePolicyType.COMPLETE_EVENT_INHERITANCE), VERBOSE_LOGGING);
        unorderedRegistrationListVerifier = new UnorderedListVerifier<>();
        registrationListVerifier = new ListVerifier<>();
    }

    @Test
    public void finder_doesnt_throw_no_listener_annotation_exception_when_parameter_off() {
        listenerMethodFinder = new ListenerMethodFinder(LISTENER_SUPERCLASS_INHERITANCE, THROW_NOT_VALID_METHOD_EXCEPTION, !THROW_NO_LISTENER_ANNOTATION_EXCEPTION, USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION, EMPTY_INHERITANCE_FRONTIER_PATH);
        assertDoesNotThrow(() -> {
            listenerMethodFinder.findListenerMethods(new Object(), memoryState);
            assertTrue(memoryState.isEventsRegistrationsEmpty());
        });
    }

    @Test
    public void finder_throws_no_listener_annotation_exception_when_needed() {
        listenerMethodFinder = new ListenerMethodFinder(LISTENER_SUPERCLASS_INHERITANCE, THROW_NOT_VALID_METHOD_EXCEPTION, THROW_NO_LISTENER_ANNOTATION_EXCEPTION, USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION, EMPTY_INHERITANCE_FRONTIER_PATH);
        Object listenerWithoutListenerAnnotationMethod = new Object();
        EventBusException exception = assertThrows(EventBusException.class,
                () -> listenerMethodFinder.findListenerMethods(listenerWithoutListenerAnnotationMethod, memoryState)
        );
        String message = exception.getMessage();
        assertEquals("Listener " + listenerWithoutListenerAnnotationMethod.getClass().getName() + " and its super classes have no public methods with the @Listen annotation.", message);
    }

    @Test
    public void finder_doesnt_throw_no_listener_annotation_exception_when_not_needed() {
        ClassWithValidListenerMethodAnnotation objWithValidListenerObjectMethodAnnotation = new ClassWithValidListenerMethodAnnotation();
        listenerMethodFinder = new ListenerMethodFinder(LISTENER_SUPERCLASS_INHERITANCE, THROW_NOT_VALID_METHOD_EXCEPTION, THROW_NO_LISTENER_ANNOTATION_EXCEPTION, USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION, EMPTY_INHERITANCE_FRONTIER_PATH);
        assertDoesNotThrow(() -> {
            listenerMethodFinder.findListenerMethods(objWithValidListenerObjectMethodAnnotation, memoryState);
            assertEquals(1, memoryState.getEventEventsRegistrationsSize());
        });
    }

    @Test
    public void finder_does_not_throw_not_valid_method_exception_when_not_required() {
        listenerMethodFinder = new ListenerMethodFinder(LISTENER_SUPERCLASS_INHERITANCE, !THROW_NOT_VALID_METHOD_EXCEPTION, !THROW_NO_LISTENER_ANNOTATION_EXCEPTION, USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION, EMPTY_INHERITANCE_FRONTIER_PATH);
        assertDoesNotThrow(() -> {
            listenerMethodFinder.findListenerMethods(new TestClassToRegisterWithMethodWithTooManyParameters(), memoryState);
            assertTrue(memoryState.isEventsRegistrationsEmpty());
        });
    }

    @Test
    public void finder_throws_not_valid_method_exception_on_method_with_too_many_parameters_when_required() {
        listenerMethodFinder = new ListenerMethodFinder(LISTENER_SUPERCLASS_INHERITANCE, THROW_NOT_VALID_METHOD_EXCEPTION, !THROW_NO_LISTENER_ANNOTATION_EXCEPTION, USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION, EMPTY_INHERITANCE_FRONTIER_PATH);
        TestClassToRegisterWithMethodWithTooManyParameters listenerObjectToRegister = new TestClassToRegisterWithMethodWithTooManyParameters();
        EventBusException exception = assertThrows(EventBusException.class, ()
                -> listenerMethodFinder.findListenerMethods(listenerObjectToRegister, memoryState)
        );
        String message = exception.getMessage();
        assertEquals(listenerObjectToRegister.getClass().getName() + ".methodToRegister (@Listen) annotated method must have exactly 1 parameter but has 2.", message);
    }

    @Test
    public void finder_throws_not_valid_method_exception_on_private_method_when_required() {
        listenerMethodFinder = new ListenerMethodFinder(LISTENER_SUPERCLASS_INHERITANCE, THROW_NOT_VALID_METHOD_EXCEPTION, !THROW_NO_LISTENER_ANNOTATION_EXCEPTION, USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION, EMPTY_INHERITANCE_FRONTIER_PATH);
        TestClassToRegisterWithPrivateMethod listenerObjectToRegister = new TestClassToRegisterWithPrivateMethod();
        EventBusException exception = assertThrows(EventBusException.class, ()
                -> listenerMethodFinder.findListenerMethods(listenerObjectToRegister, memoryState)
        );
        String message = exception.getMessage();
        assertEquals(listenerObjectToRegister.getClass().getName() + ".methodToRegister (@Listen) annotated method is not valid : must be public, non-static, and non-abstract.", message);
    }

    @Test
    public void finder_throws_not_valid_method_exception_on_static_method_when_required() {
        listenerMethodFinder = new ListenerMethodFinder(LISTENER_SUPERCLASS_INHERITANCE, THROW_NOT_VALID_METHOD_EXCEPTION, !THROW_NO_LISTENER_ANNOTATION_EXCEPTION, USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION, EMPTY_INHERITANCE_FRONTIER_PATH);
        TestClassToRegisterWithStaticMethod listenerObjectToRegister = new TestClassToRegisterWithStaticMethod();
        EventBusException exception = assertThrows(EventBusException.class, ()
                -> listenerMethodFinder.findListenerMethods(listenerObjectToRegister, memoryState)
        );
        String message = exception.getMessage();
        assertEquals(listenerObjectToRegister.getClass().getName() + ".methodToRegister (@Listen) annotated method is not valid : must be public, non-static, and non-abstract.", message);
    }

    @Test
    public void finder_throws_not_valid_method_exception_on_protected_method_when_required() {
        listenerMethodFinder = new ListenerMethodFinder(LISTENER_SUPERCLASS_INHERITANCE, THROW_NOT_VALID_METHOD_EXCEPTION, !THROW_NO_LISTENER_ANNOTATION_EXCEPTION, USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION, EMPTY_INHERITANCE_FRONTIER_PATH);
        TestClassToRegisterWithProtectedMethod listenerObjectToRegister = new TestClassToRegisterWithProtectedMethod();
        EventBusException exception = assertThrows(EventBusException.class, ()
                -> listenerMethodFinder.findListenerMethods(listenerObjectToRegister, memoryState)
        );
        String message = exception.getMessage();
        assertEquals(listenerObjectToRegister.getClass().getName() + ".methodToRegister (@Listen) annotated method is not valid : must be public, non-static, and non-abstract.", message);
    }

    @Test
    public void finder_does_not_search_for_events_inheritance_when_not_required() throws Exception {
        memoryState = new MemoryState(!SAFE_REGISTRATIONS_NEEDED, new NoEventInheritancePolicy(), true);
        listenerMethodFinder = new ListenerMethodFinder(LISTENER_SUPERCLASS_INHERITANCE, THROW_NOT_VALID_METHOD_EXCEPTION, THROW_NO_LISTENER_ANNOTATION_EXCEPTION, USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION, EMPTY_INHERITANCE_FRONTIER_PATH);
        TestClassToRegisterWithCorrectMethod listenerObjectToRegister = new TestClassToRegisterWithCorrectMethod();
        listenerMethodFinder.findListenerMethods(listenerObjectToRegister, memoryState);

        assertEquals(1, memoryState.getEventEventsRegistrationsSize(), "Finder mistakes in finding the correct registrations.");

        registrationListVerifier.assertAsExpected(memoryState.getRegistrations(new BusEventKey(TestEvent1.class)),
                Arrays.asList(new ExpectedRegistration(listenerObjectToRegister, EventPriority.NONE, "methodToRegister", TestEvent1.class))
        );
    }

    @Test
    public void finder_searches_for_events_with_inheritance_when_required() throws Exception {
        listenerMethodFinder = new ListenerMethodFinder(LISTENER_SUPERCLASS_INHERITANCE, THROW_NOT_VALID_METHOD_EXCEPTION, THROW_NO_LISTENER_ANNOTATION_EXCEPTION, USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION, EMPTY_INHERITANCE_FRONTIER_PATH);
        TestClassToRegisterWithEventInheritance listenerObjectToRegister = new TestClassToRegisterWithEventInheritance();
        listenerMethodFinder.findListenerMethods(listenerObjectToRegister, memoryState);

        assertEquals(1, memoryState.getEventEventsRegistrationsSize(), "Finder mistakes in finding the correct registrations.");

        registrationListVerifier.assertAsExpected(memoryState.getRegistrations(new BusEventKey(TestEventWithParentClass.class)),
                Arrays.asList(new ExpectedRegistration(listenerObjectToRegister, EventPriority.NONE, "methodToRegister", TestEventWithParentClass.class))
        );
    }

    @Test
    public void finder_searches_for_events_in_all_classes_hierarchy() throws Exception {
        listenerMethodFinder = new ListenerMethodFinder(LISTENER_SUPERCLASS_INHERITANCE, THROW_NOT_VALID_METHOD_EXCEPTION, THROW_NO_LISTENER_ANNOTATION_EXCEPTION, USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION, EMPTY_INHERITANCE_FRONTIER_PATH);
        TestClassSonToRegister listenerObjectToRegister = new TestClassSonToRegister();

        listenerMethodFinder.findListenerMethods(listenerObjectToRegister, memoryState);

        assertEquals(1, memoryState.getEventEventsRegistrationsSize(), "Finder mistakes in finding the correct registrations.");

        unorderedRegistrationListVerifier.assertAsExpected(memoryState.getRegistrations(new BusEventKey(TestEvent1.class)),
                Arrays.asList(
                        new NonBlockingExpectedRegistration(listenerObjectToRegister, EventPriority.NONE, "methodToRegisterSon", TestEvent1.class),
                        new NonBlockingExpectedRegistration(listenerObjectToRegister, EventPriority.NONE, "methodToRegisterFather", TestEvent1.class))
        );
    }

    @Test
    public void finder_finds_all_methods_listening_to_the_same_event() throws Exception {
        listenerMethodFinder = new ListenerMethodFinder(LISTENER_SUPERCLASS_INHERITANCE, THROW_NOT_VALID_METHOD_EXCEPTION, THROW_NO_LISTENER_ANNOTATION_EXCEPTION, USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION, EMPTY_INHERITANCE_FRONTIER_PATH);
        TestClassManyMethodsSameEventToRegister listenerObjectToRegister = new TestClassManyMethodsSameEventToRegister();

        listenerMethodFinder.findListenerMethods(listenerObjectToRegister, memoryState);

        assertEquals(1, memoryState.getEventEventsRegistrationsSize(), "Finder mistakes in finding the correct registrations.");

        unorderedRegistrationListVerifier.assertAsExpected(memoryState.getRegistrations(new BusEventKey(TestEvent1.class)),
                Arrays.asList(
                        new NonBlockingExpectedRegistration(listenerObjectToRegister, EventPriority.NONE, "methodToRegister1", TestEvent1.class),
                        new NonBlockingExpectedRegistration(listenerObjectToRegister, EventPriority.NONE, "methodToRegister2", TestEvent1.class),
                        new NonBlockingExpectedRegistration(listenerObjectToRegister, EventPriority.NONE, "methodToRegister3", TestEvent1.class),
                        new NonBlockingExpectedRegistration(listenerObjectToRegister, EventPriority.NONE, "methodToRegister4", TestEvent1.class),
                        new NonBlockingExpectedRegistration(listenerObjectToRegister, EventPriority.NONE, "methodToRegister5", TestEvent1.class))
        );
    }

    @Test
    public void finder_stop_reflection_when_package_is_different_from_inheritance_frontier_path() throws Exception {
        listenerMethodFinder = new ListenerMethodFinder(LISTENER_SUPERCLASS_INHERITANCE, !THROW_NOT_VALID_METHOD_EXCEPTION, !THROW_NO_LISTENER_ANNOTATION_EXCEPTION, USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION, "it.rebirthproject.ufoeb.dto.objectstoregister.sonpackage");
        TestClassSonOnDeepPackageToRegister listenerObjectToRegister = new TestClassSonOnDeepPackageToRegister();
        listenerMethodFinder.findListenerMethods(listenerObjectToRegister, memoryState);

        assertEquals(1, memoryState.getEventEventsRegistrationsSize(), "Finder mistakes in finding the correct registrations.");

        registrationListVerifier.assertAsExpected(memoryState.getRegistrations(new BusEventKey(TestEvent1.class)),
                Arrays.asList(new ExpectedRegistration(listenerObjectToRegister, EventPriority.NONE, "methodToRegisterSonDeepPackage", TestEvent1.class))
        );
    }

    @Test
    public void finder_doesnt_stop_reflection_if_inheritance_frontier_path_not_set() throws Exception {
        listenerMethodFinder = new ListenerMethodFinder(LISTENER_SUPERCLASS_INHERITANCE, !THROW_NOT_VALID_METHOD_EXCEPTION, !THROW_NO_LISTENER_ANNOTATION_EXCEPTION, USE_LAMBDAFACTORY_INSTEAD_OF_STANDARD_REFLECTION, EMPTY_INHERITANCE_FRONTIER_PATH);
        TestClassSonOnDeepPackageToRegister listenerObjectToRegister = new TestClassSonOnDeepPackageToRegister();
        listenerMethodFinder.findListenerMethods(listenerObjectToRegister, memoryState);

        assertEquals(1, memoryState.getEventEventsRegistrationsSize(), "Finder mistakes in finding the correct registrations.");

        unorderedRegistrationListVerifier.assertAsExpected(memoryState.getRegistrations(new BusEventKey(TestEvent1.class)),
                Arrays.asList(
                        new NonBlockingExpectedRegistration(listenerObjectToRegister, EventPriority.NONE, "methodToRegisterSonDeepPackage", TestEvent1.class),
                        new NonBlockingExpectedRegistration(listenerObjectToRegister, EventPriority.NONE, "methodToRegisterFather", TestEvent1.class))
        );
    }

}
