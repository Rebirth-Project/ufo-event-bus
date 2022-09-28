/*
 * Copyright (C) 2021 Andrea Paternesi Rebirth project
 * Copyright (C) 2021 Matteo Veroni Rebirth project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.* See the License for the specific language governing permissions and* limitations under the License.
 */

package it.rebirthproject.ufoeb.architecture.eventbus;

import it.rebirthproject.ufoeb.exceptions.EventBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * Eventbus decorator. Utilizzabile in caso non si abbia il bisogno di gestire le eccezioni restituite.
 * Cosi mi sa che non va bene. C'è da trovare un modo.
 */
public class NoExposedExceptionUfoEventbus implements EventBus {

    /**
     * The logger used by this class
     */
    private static final Logger logger = LoggerFactory.getLogger(UfoEventBus.class);

    private final EventBus eventBus;

    public NoExposedExceptionUfoEventbus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void register(Object listenerToRegister) {
        try {
            eventBus.register(listenerToRegister);
        } catch (EventBusException exception) {
            logger.error("Eventbus exception " + exception.getMessage());
        }
    }

    @Override
    public void unregister(Object listenerToUnregister) {
        try {
            eventBus.unregister(listenerToUnregister);
        } catch (EventBusException exception) {
            logger.error("Eventbus exception " + exception.getMessage());
        }
    }

    @Override
    public void post(Object event) {
        try {
            eventBus.post(event);
        } catch (EventBusException exception) {
            logger.error("Eventbus exception " + exception.getMessage());
        }
    }

    @Override
    public void postSticky(Object event) {
        try {
            eventBus.postSticky(event);
        } catch (EventBusException exception) {
            logger.error("Eventbus exception " + exception.getMessage());
        }
    }

    @Override
    public void removeSticky(Object event) {
        try {
            eventBus.removeSticky(event);
        } catch (EventBusException exception) {
            logger.error("Eventbus exception " + exception.getMessage());
        }
    }

    @Override
    public void removeSticky(Class<?> eventClass) {
        try {
            eventBus.removeSticky(eventClass);
        } catch (EventBusException exception) {
            logger.error("Eventbus exception " + exception.getMessage());
        }
    }

    @Override
    public void removeAllSticky() {
        try {
            eventBus.removeAllSticky();
        } catch (EventBusException exception) {
            logger.error("Eventbus exception " + exception.getMessage());
        }
    }

    @Override
    public void printBusState() throws EventBusException {
        try {
            eventBus.printBusState();
        } catch (EventBusException exception) {
            logger.error("Eventbus exception " + exception.getMessage());
        }
    }
    
    @Override
    public void shutdownBus() {
        eventBus.shutdownBus();
    }

    @Override
    public Future<Boolean> isRegistered(Object possibleRegisteredListener) throws EventBusException {
        return eventBus.isRegistered(possibleRegisteredListener);
    }    
}
