/*
 * Copyright (C) 2021/2024 Andrea Paternesi Rebirth project
 * Modifications copyright (C) 2021/2024 Matteo Veroni Rebirth project
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

import it.rebirthproject.ufoeb.architecture.messages.commands.PostEventMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.RemoveStickyEventMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.RegisterMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.PostStickyEventMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.PrintStateMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.UnregisterListenerMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.RemoveAllStickyEventsMessage;
import it.rebirthproject.ufoeb.architecture.messages.query.IsListenerRegisteredMessage;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * The concrete implementation of the {@link EventBus} interface
 */
final class UfoEventBus implements EventBus {

    /**
     * The logger used by this class
     */
    private static final Logger logger = LoggerFactory.getLogger(UfoEventBus.class);

    /**
     * The {@link EventBusInfrastructure} is a class that manage the internal
     * components of the {@link EventBus}
     */
    private final EventBusInfrastructure eventBusInfrastructure;

    /**
     * The constructor used to build the {@link UfoEventBus}
     *
     * @param eventBusInfrastructure The {@link EventBusInfrastructure}
     */
    UfoEventBus(EventBusInfrastructure eventBusInfrastructure) {
        this.eventBusInfrastructure = eventBusInfrastructure;
        printEventbusInformation();
    }

    @Override
    public void register(Object listenerToRegister) throws EventBusException {
        try {
            Objects.requireNonNull(listenerToRegister);
            eventBusInfrastructure.sendMessage(new RegisterMessage(listenerToRegister));
        } catch (NullPointerException ex) {
            throw new EventBusException("The listener to register is null");
        }
    }

    @Override
    public void unregister(Object listenerToUnregister) throws EventBusException {
        try {
            Objects.requireNonNull(listenerToUnregister);
            eventBusInfrastructure.sendMessage(new UnregisterListenerMessage(listenerToUnregister));
        } catch (NullPointerException ex) {
            throw new EventBusException("The listener to unregister is null");
        }
    }

    @Override
    public void post(Object event) throws EventBusException {
        try {
            Objects.requireNonNull(event);
            eventBusInfrastructure.sendMessage(new PostEventMessage(event));
        } catch (NullPointerException ex) {
            throw new EventBusException("The event to post is null");
        }
    }

    @Override
    public void postSticky(Object event) throws EventBusException {
        try {
            Objects.requireNonNull(event);
            eventBusInfrastructure.sendMessage(new PostStickyEventMessage(event));
        } catch (NullPointerException ex) {
            throw new EventBusException("The event to post is null");
        }
    }

    @Override
    public void removeSticky(Class<?> eventClass) throws EventBusException {
        try {
            Objects.requireNonNull(eventClass);
            eventBusInfrastructure.sendMessage(new RemoveStickyEventMessage(eventClass));
        } catch (NullPointerException ex) {
            throw new EventBusException("The sticky event class to remove is null");
        }
    }

    @Override
    public void removeSticky(Object event) throws EventBusException {
        try {
            Objects.requireNonNull(event);
            eventBusInfrastructure.sendMessage(new RemoveStickyEventMessage(event.getClass()));
        } catch (NullPointerException ex) {
            throw new EventBusException("The sticky event to remove is null");
        }
    }

    @Override
    public void removeAllSticky() throws EventBusException {
        eventBusInfrastructure.sendMessage(new RemoveAllStickyEventsMessage());
    }

    @Override
    public Future<Boolean> isRegistered(Object possibleRegisteredListener) throws EventBusException {
        try {
            Objects.requireNonNull(possibleRegisteredListener);
            IsListenerRegisteredMessage isObjectRegisteredMessage = new IsListenerRegisteredMessage(possibleRegisteredListener);
            eventBusInfrastructure.sendMessage(isObjectRegisteredMessage);
            return isObjectRegisteredMessage.getResponse();
        } catch (NullPointerException ex) {
            throw new EventBusException("The listener to check is null");
        }
    }

    @Override
    public void printBusState() throws EventBusException {
        eventBusInfrastructure.sendMessage(new PrintStateMessage());
    }

    @Override
    public void shutdownBus() {
        eventBusInfrastructure.shutdown();
    }

    /**
     * Prints basic information about the used Java VM and the UfoEventBus
     * version
     */
    private void printEventbusInformation() {
        logger.info("Java version: {}", System.getProperty("java.version"));
        Package packageInfo = UfoEventBus.class.getPackage();
        if (packageInfo != null) {
            logger.info("{} {} {}", packageInfo.getImplementationTitle(), packageInfo.getImplementationVendor(), packageInfo.getImplementationVersion());
        }
    }
}
