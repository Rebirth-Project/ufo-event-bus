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
package it.rebirthproject.ufoeb.architecture.eventbus;

import it.rebirthproject.ufoeb.exceptions.EventBusException;

/**
 * Class which exposes an API to create and use a global eventbus with a singleton instance
 */
public final class GlobalEventBus {

    /**
     * A unique instance of {@link GlobalEventBus}. This is a singleton {@link EventBus} instance which can be used
     * across each class of an application.
     */
    private static volatile EventBus globalInstance = null;

    /**
     * Private constructor
     */
    private GlobalEventBus(){}

    /**
     * Set up the global eventbus singleton instance. Call it just once.
     *
     * @param eventBusBuilder The builder that will be used to construct the eventbus singleton instance
     * @throws EventBusException If an error occurs while trying to set up a new {@link GlobalEventBus} instance through an {@link EventBusBuilder}
     */
    public static synchronized void setup(EventBusBuilder eventBusBuilder) throws EventBusException {
        if (GlobalEventBus.globalInstance != null) {
            throw new EventBusException("Global instance already exists. It may be only set once before it's used the first time to ensure consistent behavior.");
        }
        globalInstance = eventBusBuilder.build();
    }

    /**
     * Get the global eventbus singleton instance. Use this method only after the setupInstance method is being called.
     *
     * @return The instance of the new setup singleton eventbus
     * @throws EventBusException If an error occurs while trying to obtain a new instance
     */
    public static EventBus getInstance() throws EventBusException {
        if (globalInstance == null) {
            throw new EventBusException("You must use the setup method to create the EventBus before calling getInstance().");
        }
        return globalInstance;
    }

    /**
     * Setup the global eventbus singleton instance and returns it. Call it just once.
     *
     * @param eventBusBuilder The builder that will be used to construct the eventbus singleton instance
     * @return The instance of the new setup singleton eventbus
     * @throws EventBusException If an error occurs during {@link #setup(EventBusBuilder)} or {@link #getInstance()}
     * @see #setup(EventBusBuilder)
     * @see #getInstance()
     */
    public static synchronized EventBus setupAndGetInstance(EventBusBuilder eventBusBuilder) throws EventBusException {
        setup(eventBusBuilder);
        return getInstance();
    }

    /**
     * Utility method just for test purposes
     */
    static synchronized void clearGlobalInstance() {
        globalInstance = null;
    }
}
