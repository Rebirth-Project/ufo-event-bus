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

import it.rebirthproject.ufoeb.dto.BusEventKey;
import it.rebirthproject.ufoeb.dto.registrations.Registration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriorityEventsRegistrationsMapTest {

    private static final BusEventKey EVENT_KEY = new BusEventKey(String.class);

    @Test
    public void should_InsertAtHead_When_NewPriorityIsHighest() {
        PriorityEventsRegistrationsMap map = new PriorityEventsRegistrationsMap();
        map.addRegistration(EVENT_KEY, new TestRegistration("A", 5));

        map.addRegistration(EVENT_KEY, new TestRegistration("B", 10));

        Registration[] registrations = map.get(EVENT_KEY);
        assertEquals("B", ((TestRegistration) registrations[0]).getId());
        assertEquals("A", ((TestRegistration) registrations[1]).getId());
    }

    @Test
    public void should_InsertAtTail_When_NewPriorityIsLowest() {
        PriorityEventsRegistrationsMap map = new PriorityEventsRegistrationsMap();
        map.addRegistration(EVENT_KEY, new TestRegistration("A", 10));

        map.addRegistration(EVENT_KEY, new TestRegistration("B", 1));

        Registration[] registrations = map.get(EVENT_KEY);
        assertEquals("A", ((TestRegistration) registrations[0]).getId());
        assertEquals("B", ((TestRegistration) registrations[1]).getId());
    }

    @Test
    public void should_PreserveRegistrationOrder_When_PrioritiesAreEqual() {
        PriorityEventsRegistrationsMap map = new PriorityEventsRegistrationsMap();
        map.addRegistration(EVENT_KEY, new TestRegistration("A", 5));

        map.addRegistration(EVENT_KEY, new TestRegistration("B", 5));

        Registration[] registrations = map.get(EVENT_KEY);
        assertEquals("A", ((TestRegistration) registrations[0]).getId());
        assertEquals("B", ((TestRegistration) registrations[1]).getId());
    }

    @Test
    public void should_KeepDescendingAndStableOrder_When_MixedPrioritiesAreInserted() {
        PriorityEventsRegistrationsMap map = new PriorityEventsRegistrationsMap();

        map.addRegistration(EVENT_KEY, new TestRegistration("A", 5));
        map.addRegistration(EVENT_KEY, new TestRegistration("B", 1));
        map.addRegistration(EVENT_KEY, new TestRegistration("C", 10));
        map.addRegistration(EVENT_KEY, new TestRegistration("D", 5));
        map.addRegistration(EVENT_KEY, new TestRegistration("E", 10));
        map.addRegistration(EVENT_KEY, new TestRegistration("F", 1));

        Registration[] registrations = map.get(EVENT_KEY);
        assertEquals("C", ((TestRegistration) registrations[0]).getId());
        assertEquals("E", ((TestRegistration) registrations[1]).getId());
        assertEquals("A", ((TestRegistration) registrations[2]).getId());
        assertEquals("D", ((TestRegistration) registrations[3]).getId());
        assertEquals("B", ((TestRegistration) registrations[4]).getId());
        assertEquals("F", ((TestRegistration) registrations[5]).getId());
    }
}
