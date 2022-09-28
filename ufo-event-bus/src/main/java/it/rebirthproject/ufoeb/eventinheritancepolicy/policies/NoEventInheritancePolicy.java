/*
 * Copyright (C) 2021 Andrea Paternesi Rebirth project
 * Modifications copyright (C) 2021 Matteo Veroni Rebirth project
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
import it.rebirthproject.ufoeb.eventinheritancepolicy.base.InheritancePolicy;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * The simpler implementation of the {@link InheritancePolicy} interface. With
 * this policy we dont' want to use event inheritance. So only the given class is added to
 * the eventSuperClassesAndInterfacesCache set.
 */
public class NoEventInheritancePolicy implements InheritancePolicy {
    
    @Override
    public Set<Class<?>> getAllEventInheritanceObjects(Object eventObjectToPost, EventsRegistrationsMap eventsRegistrations, Map<Class<?>, Set<Class<?>>> eventSuperClassesAndInterfacesCache) {
        Set<Class<?>> eventInheritanceObjects = new LinkedHashSet<>();
        eventInheritanceObjects.add(eventObjectToPost.getClass());
        return eventInheritanceObjects;
    }
}
