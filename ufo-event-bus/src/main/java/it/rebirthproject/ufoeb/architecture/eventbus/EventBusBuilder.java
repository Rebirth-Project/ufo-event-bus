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

import it.rebirthproject.ufoeb.architecture.state.BusMemoryStateManager;
import it.rebirthproject.ufoeb.dto.registrations.Registration;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import it.rebirthproject.ufoeb.eventinheritancepolicy.FactoryInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.base.InheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.base.InheritancePolicyType;
import it.rebirthproject.ufoeb.eventinheritancepolicy.policies.ClassEventInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.policies.CompleteEventInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.policies.InterfaceEventInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.policies.NoEventInheritancePolicy;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import it.rebirthproject.ufoeb.services.ListenerMethodFinder;
import static it.rebirthproject.ufoeb.eventinheritancepolicy.base.InheritancePolicyType.INTERFACE_EVENT_INHERITANCE;

/**
 * A builder which can be used to create an {@link EventBus}
 */
public final class EventBusBuilder {

    /**
     * The length of the infrastructure internal queues used by the
     * {@link EventBusInfrastructure}. Basically the length is important for the
     * commandQueryMessageQueue of the {@link BusMemoryStateManager} to avoid
     * the block of the {@link BusMemoryStateManager} and should be calculated
     * correctly depending on the pressure of the application that uses the bus
     * (posts speed, number of posting threads, and so on). The default number
     * of workers is 100.
     */
    private Integer queueLength = 100;

    /**
     * The number of internal workers used by the {@link EventBus} behind the
     * scenes to process posted events and notify listeners. The default number
     * of workers is 1.
     */
    private Integer numberOfWorkers = 1;
    /**
     * This parameter ensures that workers gets an unmodifiable
     * {@link Registration}'s list. For best performance leave this parameter to
     * false (default) and avoid to register/unregister listeners at runtime
     * time. Otherwise, set it to true.
     */
    private boolean safeRegistrationsListNeeded = false;
    /**
     * This parameter should be used when you want to use inheritance over a
     * listener and all its superclasses. Enabling it will let the bus look for
     * all listeners' methods considering also all their superclasses methods.
     * The default value is to look for only the listener's class methods.
     */
    private boolean listenerSuperclassInheritance = false;
    /**
     * This parameter should be used when you want to use spped up listener's
     * methods call. Instead the standard reflection getMethod() workers will
     * use the method handler created by the lambdafactory getMethodHandler().
     * Very useful when listener's methods are not time consuming. Anyway beware
     * that MethodHandles.lookup() does not work with modules and with java 9>
     * when a listener class is located in a different module than the
     * eventbus's one. So basically this will work always with java 8. And with
     * java 9> when you do not use modules, for example in an application. If
     * you want to create a library with java 9> that uses the ufoeventbus then
     * you must use the default method. For more informations see 
     * https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html
     */
    private boolean useLambdaFactoryInsteadOfStandardReflection = false;
    /**
     * If a registering listener does not have any {@link Listen} annotated
     * method or, in case of event inheritance enabled, also its super classes
     * or interfaces does not have any {@link Listen} annotated method, then an
     * {@link EventBusException} is thrown.
     */
    private boolean throwNoListenerAnnotationException = true;
    /**
     * Sets the bus to log warnings when no registration is found for a specific
     * event. If not set, no warning will be printed to the log. This is useful
     * to debug application.
     *
     * <p>
     * Example:
     * <pre>
     * Event E is posted to the bus but no Listener is registered to listen to it.
     * </pre>
     * </p>
     */
    private boolean throwNoRegistrationsWarning = true;
    /**
     * if set then an {@link EventBusException} is thrown when an invalid
     * {@link Listen} annotated method is found in a Listener.
     *
     * <p>
     * The method must be defined using the following rules:
     * <ol>
     * <li>The method must be public and not static</li>
     * <li>The method must have only one parameter that represents the listened
     * event</li>
     * </ol>
     * </p>
     */
    private boolean throwNotValidMethodException = true;
    /**
     * The events {@link InheritancePolicy} used by the eventbus. The default
     * value for the inheritance policy is {@link NoEventInheritancePolicy}
     *
     * @see InheritancePolicy
     * @see InheritancePolicyType
     * @see NoEventInheritancePolicy
     */
    private InheritancePolicy eventInheritancePolicy = FactoryInheritancePolicy.createInheritancePolicy(InheritancePolicyType.NO_EVENT_INHERITANCE);
    /**
     * The #inheritancePackageFrontierPath is used to stop the iteration over
     * classes while using event inheritance. If a class belongs to the set
     * package then the iteration stops. This parameter must be used only when
     * event inheritance policy is enabled otherwise it is useless even if set.
     *
     * <p>
     * Example:
     * <pre>
     * Class A extends class B and is defined under x.y.z java package
     * Class B is defined under x.y java package
     * inheritancePackageFrontierPath is defined to x.y,z<br>
     * Then the iteration will stop when it reaches class A without iterating on its parent class.
     * </pre>
     * </p>
     *
     * @see #setEventSuperclassInheritance()
     * @see #setEventInterfaceInheritance()
     * @see #setCompleteEventInheritance()
     */
    private String inheritancePackageFrontierPath = "";
    /**
     * Boolean attribute used to enable or disable verbose logging
     */
    private boolean verboseLogging = false;

    /**
     * Sets the {@link #safeRegistrationsListNeeded} attribute to true
     *
     * @return The {@link EventBusBuilder} instance configured with the
     * {@link #safeRegistrationsListNeeded} set to true
     * @see #safeRegistrationsListNeeded
     */
    public EventBusBuilder setSafeRegistrationsListNeeded() {
        this.safeRegistrationsListNeeded = true;
        return this;
    }

    /**
     * If a registering listener does not have any {@link Listen} annotated
     * method or, in case of event inheritance enabled, also its super classes
     * or interfaces does not have any {@link Listen} annotated method, then an
     * {@link EventBusException} is thrown.
     *
     * @return The {@link EventBusBuilder} instance configured to throw a no
     * listener found {@link EventBusException}.
     */
    public EventBusBuilder setThrowNoListenerAnnotationException() {
        this.throwNoListenerAnnotationException = true;
        return this;
    }

    /**
     * if set then an {@link EventBusException} is thrown when an invalid
     * {@link Listen} annotated method is found in a Listener.
     *
     * <p>
     * The method must be defined using the following rules:
     * <ol>
     * <li>The method must be public and not static</li>
     * <li>The method must have only one parameter that represents the listened
     * event</li>
     * </ol>
     * </p>
     *
     * @return The {@link EventBusBuilder} instance configured to throw a not
     * valid method {@link EventBusException}.
     */
    public EventBusBuilder setThrowNotValidMethodException() {
        this.throwNotValidMethodException = true;
        return this;
    }

    /**
     * Sets the bus to log warnings when no registration is found for a specific
     * event. If not set, no warning will be printed to the log. This is useful
     * to debug application.
     *
     * <p>
     * Example:
     * <pre>
     * Event E is posted to the bus but no Listener is registered to listen to it.
     * </pre>
     * </p>
     *
     * @return The {@link EventBusBuilder} instance configured to log no
     * registration found warnings.
     */
    public EventBusBuilder setThrowNoRegistrationsWarning() {
        this.throwNoRegistrationsWarning = true;
        return this;
    }

    /**
     * if set this parameter enables event inheritance over listeners classes
     * (and superclasses). Otherwise, the default is to not use listener
     * inheritance at all.
     *
     * <p>
     * Example:
     * <pre>
     * Class Cat extends class Animal and both listen to event E, but only class Cat is registered on the bus.
     * if class event inheritance is set then event E is registered inside the bus with both Cat and Animal classes.
     * When a post arrives carrying event E, then both classes will receive the event even if only class Cat is registered on the bus.
     * Otherwise only Cat class will receive the event.
     * </pre>
     * </p>
     *
     * @return The {@link EventBusBuilder} instance configured to iterate over
     * listener classes and super classes.
     */
    public EventBusBuilder setListenerSuperclassInheritance() {
        this.listenerSuperclassInheritance = true;
        return this;
    }

    /**
     * This set the usage of Lambdafactory instead of standard java reflection
     *
     * @see EventBusBuilder#useLambdaFactoryInsteadOfStandardReflection
     * 
     * @return The {@link EventBusBuilder} instance configured to throw a no
     * listener found {@link EventBusException}.
     */
    public EventBusBuilder setUseLambdaFactoryInsteadOfStandardReflection() {
        this.useLambdaFactoryInsteadOfStandardReflection = true;
        return this;
    }

    /**
     * Sets the {@link #eventInheritancePolicy} to
     * {@link ClassEventInheritancePolicy}
     *
     * @return The {@link EventBusBuilder} configured with the
     * {@link ClassEventInheritancePolicy}
     * @see InheritancePolicy
     * @see InheritancePolicyType
     * @see ClassEventInheritancePolicy
     */
    public EventBusBuilder setEventSuperclassInheritance() {
        this.eventInheritancePolicy = FactoryInheritancePolicy.createInheritancePolicy(InheritancePolicyType.CLASS_EVENT_INHERITANCE);
        return this;
    }

    /**
     * Sets the {@link #eventInheritancePolicy} to
     * {@link InterfaceEventInheritancePolicy}
     *
     * @return The {@link EventBusBuilder} configured with the
     * {@link InterfaceEventInheritancePolicy}
     * @see InheritancePolicy
     * @see InheritancePolicyType
     * @see InterfaceEventInheritancePolicy
     */
    public EventBusBuilder setEventInterfaceInheritance() {
        this.eventInheritancePolicy = FactoryInheritancePolicy.createInheritancePolicy(INTERFACE_EVENT_INHERITANCE);
        return this;
    }

    /**
     * Sets the {@link #eventInheritancePolicy} to
     * {@link CompleteEventInheritancePolicy}
     *
     * @return The {@link EventBusBuilder} configured with the
     * {@link CompleteEventInheritancePolicy}
     * @see InheritancePolicy
     * @see InheritancePolicyType
     * @see CompleteEventInheritancePolicy
     */
    public EventBusBuilder setCompleteEventInheritance() {
        this.eventInheritancePolicy = FactoryInheritancePolicy.createInheritancePolicy(InheritancePolicyType.COMPLETE_EVENT_INHERITANCE);
        return this;
    }

    /**
     * Sets the length of internal queues used by the the
     * {@link EventBusInfrastructure}.
     *
     * @param queueLength The length of the infrastructure queues
     * @return The {@link EventBusBuilder} instance configured with the updated
     * number of workers
     */
    public EventBusBuilder setQueuesLength(final Integer queueLength) {
        this.queueLength = queueLength;
        return this;
    }

    /**
     * Sets the number of internal workers used by the eventbus behind the
     * scenes to process posted events and notify listeners
     *
     * @param numberOfWorkers The number of workers used by the eventbus
     * @return The {@link EventBusBuilder} instance configured with the updated
     * number of workers
     */
    public EventBusBuilder setNumberOfWorkers(final Integer numberOfWorkers) {
        if (numberOfWorkers != null) {
            this.numberOfWorkers = numberOfWorkers;
        }
        return this;
    }

    /**
     * Sets the number of internal workers used by the eventbus equal to the
     * available processors of current device.
     *
     * @return The {@link EventBusBuilder} instance configured with the updated
     * number of workers
     * @see #setNumberOfWorkers(Integer)
     */
    public EventBusBuilder setNumberOfWorkersAsAvailableProcessors() {
        this.numberOfWorkers = Runtime.getRuntime().availableProcessors();
        return this;
    }

    /**
     * Sets the package used to stop the iteration over classes while using
     * event inheritance. If a class belongs to the set package then the
     * iteration stops. This parameter must be used only when event inheritance
     * policy is enabled otherwise it is useless even if set.
     *
     * <p>
     * Example:
     * <pre>
     * Class A extends class B and is defined under x.y.z java package
     * Class B is defined under x.y java package
     * inheritancePackageFrontierPath is defined to x.y,z<br>
     * Then the iteration will stop when it reaches class A without iterating on its parent class.
     * </pre>
     * </p>
     *
     * @param inheritancePackageFrontierPath The package frontier path
     * @return The {@link EventBusBuilder} instance configured with the updated
     * frontier path
     * @see #setEventSuperclassInheritance()
     * @see #setEventInterfaceInheritance()
     * @see #setCompleteEventInheritance()
     */
    public EventBusBuilder setInheritancePackageFrontierPath(final String inheritancePackageFrontierPath) {
        this.inheritancePackageFrontierPath = inheritancePackageFrontierPath;
        return this;
    }

    /**
     * If this setter is set, the verbose logging will be enabled
     *
     * @return The {@link EventBusBuilder} instance configured with verbose
     * logging
     */
    public EventBusBuilder setVerboseLogging() {
        this.verboseLogging = true;
        return this;
    }

    /**
     * Build an {@link EventBus} configured by {@link EventBusBuilder}'s
     * properties eventually set or with pre-configured default values
     *
     * @return an {@link EventBus} instance
     * @throws EventBusException If some error occurs while building a new
     * eventbus
     */
    public EventBus build() throws EventBusException {
        try {
            final EventBusInfrastructure eventBusInfrastructure = new EventBusInfrastructure(
                    new ListenerMethodFinder(listenerSuperclassInheritance, throwNotValidMethodException, throwNoListenerAnnotationException, useLambdaFactoryInsteadOfStandardReflection, inheritancePackageFrontierPath),
                    eventInheritancePolicy,
                    queueLength,
                    numberOfWorkers,
                    safeRegistrationsListNeeded,
                    throwNoRegistrationsWarning,
                    verboseLogging
            );
            eventBusInfrastructure.startup();
            return new UfoEventBus(eventBusInfrastructure);
        } catch (Exception ex) {
            throw new EventBusException("Error building the eventbus", ex);
        }
    }
}
