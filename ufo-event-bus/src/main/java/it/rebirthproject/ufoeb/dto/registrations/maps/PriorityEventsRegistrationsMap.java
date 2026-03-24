/*
 * Copyright (C) 2021/2026 Matteo Veroni Rebirth project
 * Modifications copyright (C) 2021/2026 Andrea Paternesi Rebirth project
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
package it.rebirthproject.ufoeb.dto.registrations.maps;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.architecture.state.MemoryState;
import it.rebirthproject.ufoeb.dto.BusEventKey;
import it.rebirthproject.ufoeb.dto.registrations.Registration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The map used to add a {@link Registration} using its priority. The priority
 * is relative to a precise event and higher priority listeners will be served
 * before. So, listeners that listen for a precise event using priority will be
 * ordered inside the registration list.
 *
 * <pre>example:
 *
 *  Listener A listen for event E1 with priority 5
 *  Listener B listen for event E1 with priority 1
 *  Listener C listen for event E2 with priority 0
 *
 *  if event E1 is posted to the bus then execution will be
 *
 *  - First to be served is A with event E1 (since 5 is greater than 1)
 *  - Second to be server is B with event E1
 *  - Listener C does not receive anything because it is listening to E2
 *
 * </pre>
 *
 * @see EventBus
 * @see MemoryState
 * @see Registration
 */
public class PriorityEventsRegistrationsMap {

    private static final Registration[] EMPTY_REGISTRATIONS = new Registration[0];

    /**
     * The map used to store the {@link Registration}s of the bus.
     */
    private final Map<BusEventKey, Registration[]> registrations = new HashMap<>();

    /**
     * Getter method to obtain the registrations snapshot for a {@link BusEventKey}.
     *
     * @param eventKey A key for events used by the {@link EventBus}
     * @return The registrations snapshot for a {@link BusEventKey}
     */
    public Registration[] get(BusEventKey eventKey) {
        Registration[] snapshot = registrations.get(eventKey);
        return snapshot == null ? EMPTY_REGISTRATIONS : snapshot;
    }

    /**
     * Check if the registrations map is empty or not.
     *
     * @return True if the map is empty, false otherwise
     */
    public boolean isEmpty() {
        return registrations.isEmpty();
    }

    /**
     * Check the size of the registrations map.
     *
     * @return The size of the registrations map
     */
    public int size() {
        return registrations.size();
    }

    /**
     * Check if the registrations map contains a {@link BusEventKey}.
     *
     * @param eventKey The event key to check
     * @return True if the map contains the key, false otherwise
     */
    public boolean containsKey(BusEventKey eventKey) {
        return registrations.containsKey(eventKey);
    }

    /**
     * Removes the entry associated with the specified {@link BusEventKey}.
     *
     * @param eventKey The key to remove from the map
     */
    public void remove(BusEventKey eventKey) {
        registrations.remove(eventKey);
    }

    /**
     * Gets all the {@link BusEventKey}s contained in the registrations map.
     *
     * @return all the keys contained in the registrations map
     */
    public Set<BusEventKey> keySet() {
        return registrations.keySet();
    }

    /**
     * Clears all registrations.
     */
    public void clear() {
        registrations.clear();
    }

    /**
     * Removes all registrations associated to a specific listener for the given event key.
     *
     * @param eventKey The event key to update
     * @param listenerToUnregister The listener to remove from registrations
     */
    public void removeRegistrationsForListener(BusEventKey eventKey, Object listenerToUnregister) {
        Registration[] currentRegistrations = registrations.get(eventKey);
        if (currentRegistrations == null || currentRegistrations.length == 0) {
            return;
        }

        int survivingRegistrations = 0;
        for (Registration registration : currentRegistrations) {
            if (!listenerToUnregister.equals(registration.getListener())) {
                survivingRegistrations++;
            }
        }

        if (survivingRegistrations == currentRegistrations.length) {
            return;
        }

        if (survivingRegistrations == 0) {
            registrations.remove(eventKey);
            return;
        }

        Registration[] filteredRegistrations = new Registration[survivingRegistrations];
        int index = 0;
        for (Registration registration : currentRegistrations) {
            if (!listenerToUnregister.equals(registration.getListener())) {
                filteredRegistrations[index] = registration;
                index++;
            }
        }
        registrations.put(eventKey, filteredRegistrations);
    }

     /**
     * The method that adds a new {@link Registration} to the {@link Registration}s map by priority.
     *
     * @param eventKey     The {@link BusEventKey} used to add a new {@link Registration}.
     * @param registration The {@link Registration} to add.
     */
    public void addRegistration(BusEventKey eventKey, Registration registration) {
        Registration[] currentRegistrations = registrations.get(eventKey);
        if (currentRegistrations == null) {
            registrations.put(eventKey, new Registration[]{registration});
            return;
        }

        int insertIndex = calculateInsertIndex(currentRegistrations, registration.getPriority());
        Registration[] updatedRegistrations = new Registration[currentRegistrations.length + 1];

        System.arraycopy(currentRegistrations, 0, updatedRegistrations, 0, insertIndex);
        updatedRegistrations[insertIndex] = registration;
        System.arraycopy(currentRegistrations, insertIndex, updatedRegistrations, insertIndex + 1, currentRegistrations.length - insertIndex);

        registrations.put(eventKey, updatedRegistrations);
    }

     /**
     * Private helper method that inserts by priority a {@link Registration} in the {@link Registration}s list.
     */
    private int calculateInsertIndex(Registration[] currentRegistrations, int priority) {
        int insertIndex = 0;
        while (insertIndex < currentRegistrations.length && priority <= currentRegistrations[insertIndex].getPriority()) {
            insertIndex++;
        }
        return insertIndex;
    }
}
