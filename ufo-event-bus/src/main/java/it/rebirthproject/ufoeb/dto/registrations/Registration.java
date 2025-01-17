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
package it.rebirthproject.ufoeb.dto.registrations;

import java.lang.reflect.Method;

/**
 * Interface representing a registration.
 */
public abstract class Registration {

    /**
     * The listener of the {@link Registration}
     */
    protected Object listener;
    /**
     * The priority of the {@link Registration}
     */
    protected int priority;

    /**
     * The callback method to invoke when a new notification for the listener
     * needs to be delivered
     */
    protected Method method;

    /**
     * Method to process a registration
     *
     * @param event the event to process
     * @throws java.lang.Exception
     */
    public abstract void process(Object event) throws Exception;

    /**
     * Getter for the listener of the {@link Registration}
     *
     * @return The listener of the {@link Registration}
     */
    public Object getListener() {
        return listener;
    }

    /**
     * Getter for the priority of the {@link Registration}
     *
     * @return The priority of the {@link Registration}
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Getter for method to invoke when a new notification for the listener
     * needs to be delivered
     *
     * @return The method to invoke when a new notification for the listener
     * needs to be delivered
     */
    public Method getMethod() {
        return method;
    }
}
