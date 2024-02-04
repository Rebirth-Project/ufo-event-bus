/*
 * Copyright (C) 2021/2024 Matteo Veroni Rebirth project
 * Modifications copyright (C) 2021/2024 Andrea Paternesi Rebirth project
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
package it.rebirthproject.ufoeb.dto.registrations.maps.interfaces;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.architecture.state.MemoryState;
import it.rebirthproject.ufoeb.dto.BusEventKey;
import it.rebirthproject.ufoeb.dto.registrations.Registration;
import java.util.*;

/**
 * The base abstract class that implements the {@link Registration}s maps used
 * by the {@link EventBus}' {@link MemoryState}.
 *
 * @see EventBus
 * @see MemoryState
 * @see Registration
 */
public abstract class EventsRegistrationsMap {

    /**
     * The map used to store the {@link Registration}s of the bus
     */
    protected final Map<BusEventKey, List<Registration>> registrations = new HashMap<>();

    /**
     * Getter method to obtain the list of {@link Registration}s for a {@link BusEventKey}
     *
     * @param eventKey A key for events used by the {@link EventBus}
     * @return The list of {@link Registration}s for a {@link BusEventKey}
     * @see BusEventKey
     */
    public List<Registration> get(BusEventKey eventKey) {
        return registrations.get(eventKey);
    }

    /**
     * Check if the {@link #registrations} map is empty or not
     *
     * @return A boolean value, which is true if the map 'registrations' is empty or false otherwise
     */
    public boolean isEmpty() {
        return registrations.isEmpty();
    }

    /**
     * Check the size of the {@link #registrations} map
     *
     * @return The size of the {@link #registrations} map
     */
    public int size() {
        return registrations.size();
    }

    /**
     * Check if the {@link #registrations} map contains a {@link BusEventKey}
     *
     * @param eventKey The passed {@link BusEventKey} to check if it's already defined into the {@link #registrations} map
     * @return A boolean value equals to 'true' if the passed {@link BusEventKey} is defined into the {@link #registrations} map, or false otherwise
     */
    public boolean containsKey(BusEventKey eventKey) {
        return registrations.containsKey(eventKey);
    }

    /**
     * Obtains all the {@link BusEventKey}s contained in the {@link #registrations} map as a java set of keys
     *
     * @return all the {@link BusEventKey}s contained in the {@link #registrations} map as a java set of keys
     */
    public Set<BusEventKey> keySet() {
        return registrations.keySet();
    }

    /**
     * Clear all the {@link BusEventKey}s contained in the {@link #registrations} map
     */
    public void clear() {
        registrations.clear();
    }

    /**
     * The abstract method to add a new {@link Registration} to the {@link Registration}s map.
     *
     * @param eventKey     The {@link BusEventKey} used to add a new {@link Registration}.
     * @param registration The {@link Registration} to add.
     */
    public abstract void addRegistration(BusEventKey eventKey, Registration registration);
}
