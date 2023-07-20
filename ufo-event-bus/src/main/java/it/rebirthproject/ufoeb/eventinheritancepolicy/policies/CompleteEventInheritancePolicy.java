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
package it.rebirthproject.ufoeb.eventinheritancepolicy.policies;

import it.rebirthproject.ufoeb.dto.registrations.maps.interfaces.EventsRegistrationsMap;
import it.rebirthproject.ufoeb.eventinheritancepolicy.base.AbstractEventInheritancePolicy;
import it.rebirthproject.ufoeb.services.ClassProcessableService;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This implementation of the {@link InheritancePolicy} interface searches for
 * all superclasses and interfaces given the event class. With this policy the
 * resulting eventSuperClassesAndInterfacesCache set will contain the given
 * class, all its extended superclasses and all its implemented interfaces
 * (recursively).
 */
public class CompleteEventInheritancePolicy extends AbstractEventInheritancePolicy {  
    /**
     *
     * @param classProcessableService The service that checks if you are extending or implementing a forbidden type
     */
    public CompleteEventInheritancePolicy(ClassProcessableService classProcessableService) {
        this.classProcessableService = classProcessableService;
    }

    @Override
    public Set<Class<?>> getAllEventInheritanceObjects(Object eventObjectToPost, EventsRegistrationsMap eventsRegistrations, Map<Class<?>, Set<Class<?>>> eventSuperClassesAndInterfacesCache) {
        Class<? extends Object> eventCalssToPost = eventObjectToPost.getClass();
        Set<Class<?>> eventSuperClassesAndInterfaces = eventSuperClassesAndInterfacesCache.get(eventCalssToPost);
        if (eventSuperClassesAndInterfaces == null) {
            eventSuperClassesAndInterfaces = serializeEventStructure(eventCalssToPost, eventsRegistrations);
            eventSuperClassesAndInterfacesCache.put(eventCalssToPost, eventSuperClassesAndInterfaces);
        }
        return eventSuperClassesAndInterfaces;
    }

    /**
     * This method will find all superclasses and interfaces given the event
     * class. The resulting set will contain the given class, all its extended
     * superclasses and all its implemented interfaces (recursively).
     *
     * @param eventClass The class to serialize (we want to find all
     * superclasses).
     * @param eventsRegistrations The event registration map.
     * @return The complete set of classes.
     */
    private Set<Class<?>> serializeEventStructure(Class<?> eventClass, EventsRegistrationsMap eventsRegistrations) {
        Set<Class<?>> eventClassesAndInterfaces = new LinkedHashSet<>();
        Class<?> clazz = eventClass;
        // clazz != null is needed to avoid NullPointerExceptions. e.g. If class is an interface then clazz.getSuperclass() returns null
        while (clazz != null && clazz != Object.class) {
            addClassOrInterfaceToSerializationIfNecessary(eventClassesAndInterfaces, eventsRegistrations, clazz);
            serializeInterfaces(eventClassesAndInterfaces, eventsRegistrations, clazz);
            clazz = clazz.getSuperclass();
        }
        return eventClassesAndInterfaces;
    }
}
