/*
 * Copyright (C) 2021 Matteo Veroni Rebirth project
 * Modifications copyright (C) 2021 Andrea Paternesi Rebirth project
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

import it.rebirthproject.ufoeb.dto.registrations.maps.interfaces.EventsRegistrationsMap;
import it.rebirthproject.ufoeb.dto.BusEventKey;
import it.rebirthproject.ufoeb.dto.registrations.Registration;
import java.util.*;

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
 * @see Registration
 */
public class PriorityEventsRegistrationsMap extends EventsRegistrationsMap {

     /**
     * The method that adds a new {@link Registration} to the {@link Registration}s map by priority.
     *
     * @param eventKey     The {@link BusEventKey} used to add a new {@link Registration}.
     * @param registration The {@link Registration} to add.
     */
    @Override
    public void addRegistration(BusEventKey eventKey, Registration registration) {
        List<Registration> registrationsList = registrations.getOrDefault(eventKey, new ArrayList<>());
        addRegistrationToTheListByPriority(registrationsList, registration);
        registrations.put(eventKey, registrationsList);
    }

     /**
     * Private helper method that inserts by priority a {@link Registration} in the {@link Registration}s list.
     */
    private void addRegistrationToTheListByPriority(List<Registration> registrationsList, Registration registration) {
        int size = registrationsList.size();
        for (int i = 0; i <= size; i++) {
            if (i == size || registration.getPriority() > registrationsList.get(i).getPriority()) {
                registrationsList.add(i, registration);
                break;
            }
        }
    }
}
