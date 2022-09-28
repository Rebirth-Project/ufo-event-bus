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
 * The map used to add a {@link Registration} by order of arrival. 
 */
public class UnorderedEventsRegistrationsMap extends EventsRegistrationsMap {

    /**
     * The method that adds a new {@link Registration} to the {@link Registration}s map by order of arrival. 
     *
     * @param eventKey     The {@link BusEventKey} used to add a new {@link Registration}.
     * @param registration The {@link Registration} to add.
     */
    @Override
    public void addRegistration(BusEventKey eventKey, Registration registration) {
        List<Registration> registrationsList = registrations.getOrDefault(eventKey, new ArrayList<>());
        registrationsList.add(registration);
        registrations.put(eventKey, registrationsList);
    }
}
