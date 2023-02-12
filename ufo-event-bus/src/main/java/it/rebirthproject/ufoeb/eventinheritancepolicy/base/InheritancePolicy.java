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
package it.rebirthproject.ufoeb.eventinheritancepolicy.base;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.dto.BusEventKey;
import it.rebirthproject.ufoeb.dto.registrations.Registration;
import it.rebirthproject.ufoeb.dto.registrations.maps.interfaces.EventsRegistrationsMap;
import it.rebirthproject.ufoeb.eventinheritancepolicy.policies.ClassEventInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.policies.CompleteEventInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.policies.InterfaceEventInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.policies.NoEventInheritancePolicy;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * The interface that defines an inheritance policy to apply on the event class. An inheritance policy is an internal
 * algorithm of the {@link EventBus} which change is behaviour relating to the management of events notifications.
 * For example in the {@link ClassEventInheritancePolicy} when a new event is posted in the bus, it is notified to all
 * the listeners which listen to the specific event, and also to all the listeners which listen to events which extends
 * the first event. Take a look to each policy to a better understanding of the details.
 *
 * @see ClassEventInheritancePolicy
 * @see CompleteEventInheritancePolicy
 * @see InterfaceEventInheritancePolicy
 * @see NoEventInheritancePolicy
 */
public interface InheritancePolicy {

    /**
     * The interface method that returns a set of classes (the implementation
     * uses {@link LinkedHashSet} to preserve order of insertion), based on the
     * chosen inheritance policy.
     *
     * @param eventObjectToPost                   The event object to post.
     * @param eventsRegistrations                 The complete map of events'
     *                                            {@link Registration}s.
     * @param eventSuperClassesAndInterfacesCache The cache used to save event
     *                                            class/superclasses/interfaces serialization.
     * @return a set of classes based on the chosen inheritance policy.
     */
    public Set<Class<?>> getAllEventInheritanceObjects(Object eventObjectToPost, EventsRegistrationsMap eventsRegistrations, Map<Class<?>, Set<Class<?>>> eventSuperClassesAndInterfacesCache);

    /**
     * Method that finds all interfaces given the starting class to serialize
     * and adds them to the given parameter set (eventClassesAndInterfaces).
     *
     * @param eventClassesAndInterfaces The set to populate.
     * @param eventsRegistrations       The event registration map.
     * @param clazz                     The class to serialize (we want to find all interfaces)
     */
    default void serializeInterfaces(Set<Class<?>> eventClassesAndInterfaces, EventsRegistrationsMap eventsRegistrations, Class<?> clazz) {
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            addClassOrInterfaceToSerializationIfNecessary(eventClassesAndInterfaces, eventsRegistrations, interfaceClass);
            serializeInterfaces(eventClassesAndInterfaces, eventsRegistrations, interfaceClass);
        }
    }

    /**
     * Method that adds a class to the (eventClassesAndInterfaces) set if the
     * class is not already contained in the (eventsRegistrations) map.
     *
     * @param eventClassesAndInterfaces The set to populate.
     * @param eventsRegistrations       The event registration map.
     * @param clazz                     The class to add to the set.
     */
    default void addClassOrInterfaceToSerializationIfNecessary(Set<Class<?>> eventClassesAndInterfaces, EventsRegistrationsMap eventsRegistrations, Class<?> clazz) {
        if (eventsRegistrations.containsKey(new BusEventKey(clazz))) {
            eventClassesAndInterfaces.add(clazz);
        }
    }
}
