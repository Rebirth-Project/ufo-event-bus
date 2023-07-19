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

import it.rebirthproject.ufoeb.dto.BusEventKey;
import it.rebirthproject.ufoeb.dto.registrations.Registration;
import it.rebirthproject.ufoeb.dto.registrations.maps.interfaces.EventsRegistrationsMap;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import it.rebirthproject.ufoeb.services.ClassProcessableService;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractEventInheritancePolicy implements EventInheritancePolicy {

    /**
     * The service that checks if you are extending or implementing a forbidden
     * type
     */
    protected ClassProcessableService classProcessableService;

    /**
     * The interface method that returns a set of classes (the implementation
     * uses {@link LinkedHashSet} to preserve order of insertion), based on the
     * chosen inheritance policy.
     *
     * @param eventObjectToPost The event object to post.
     * @param eventsRegistrations The complete map of events'
     * {@link Registration}s.
     * @param eventSuperClassesAndInterfacesCache The cache used to save event
     * class/superclasses/interfaces serialization.
     * @return a set of classes based on the chosen inheritance policy.
     */
    @Override
    public abstract Set<Class<?>> getAllEventInheritanceObjects(Object eventObjectToPost, EventsRegistrationsMap eventsRegistrations, Map<Class<?>, Set<Class<?>>> eventSuperClassesAndInterfacesCache);

    /**
     * Method that finds all interfaces given the starting class to serialize
     * and adds them to the given parameter set (eventClassesAndInterfaces).
     *
     * @param eventClassesAndInterfaces The set to populate.
     * @param eventsRegistrations The event registration map.
     * @param clazz The class to serialize (we want to find all interfaces)
     */
    protected void serializeInterfaces(Set<Class<?>> eventClassesAndInterfaces, EventsRegistrationsMap eventsRegistrations, Class<?> clazz) {
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
     * @param eventsRegistrations The event registration map.
     * @param clazz The class to add to the set.
     * 
     * @trows EventBusException
     */
    protected void addClassOrInterfaceToSerializationIfNecessary(Set<Class<?>> eventClassesAndInterfaces, EventsRegistrationsMap eventsRegistrations, Class<?> clazz) {
        if (eventsRegistrations.containsKey(new BusEventKey(clazz))) {
            if (classProcessableService.isClassProcessableByPackage(clazz.getName())) {
                eventClassesAndInterfaces.add(clazz);
            } else {
                throw new EventBusException("You have defined an Event that extends/implements a Java or Android class or does not belong to the eventually defined inheritance frontier path. This is terribly wrong.");
            }
        }
    }
}