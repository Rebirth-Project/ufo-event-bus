/*
 * Copyright (C) 2021/2025 Andrea Paternesi Rebirth project
 * Modifications copyright (C) 2021/2025 Matteo Veroni Rebirth project
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
package it.rebirthproject.ufoeb.architecture.state;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.dto.BusEventKey;
import it.rebirthproject.ufoeb.dto.EventMethodKey;
import it.rebirthproject.ufoeb.dto.registrations.Registration;
import it.rebirthproject.ufoeb.dto.registrations.maps.PriorityEventsRegistrationsMap;
import it.rebirthproject.ufoeb.dto.registrations.maps.UnorderedEventsRegistrationsMap;
import it.rebirthproject.ufoeb.dto.registrations.maps.interfaces.EventsRegistrationsMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import it.rebirthproject.ufoeb.eventinheritancepolicy.base.EventInheritancePolicy;

/**
 * The memory state of the {@link EventBus}. This class contains data structures
 * used by the bus to store events and registered classes (event listeners)
 *
 * @see BusMemoryStateManager
 */
public class MemoryState {

    /**
     * The logger used by this class
     */
    private static final Logger logger = LoggerFactory.getLogger(MemoryState.class);
    /**
     * Data structure used to map each listener's registration metadata stored
     * into the memory state
     */
    private final EventsRegistrationsMap eventsRegistrations = new PriorityEventsRegistrationsMap();
    /**
     * This map data structure contains all the registered listeners and their
     * corresponding listened events keys A listener can listen to different
     * events (one or more).
     */
    private final Map<Object, Set<EventMethodKey>> listenerToEventsMap = new HashMap<>();
    /**
     * Map data structure containing all the metadata related to sticky events
     */
    private final Map<BusEventKey, Object> stickyEventsMap = new HashMap<>();
    /**
     * A map used to cache superclasses and interfaces related to an event class
     */
    private final Map<Class<?>, Set<Class<?>>> eventSuperClassesAndInterfacesCache = new HashMap<>();
    /**
     * This attribute ensures that workers gets an unmodifiable
     * {@link Registration}'s list. For best performance this attribute should
     * be set to false (default) and avoid to register/unregister listeners at
     * runtime time. Otherwise, set it to true.
     */
    private final boolean safeRegistrationsListNeeded;
    /**
     * Partial registrations on sticky events for each Listener (used only in
     * listener registration)
     */
    private final EventsRegistrationsMap foundListenerStickyEventsRegistrations = new UnorderedEventsRegistrationsMap();
    /**
     * The chosen system inheritancePolicy is stored here.
     *
     * @see EventInheritancePolicy
     */
    private final EventInheritancePolicy inheritancePolicy;
    /**
     * Attribute set to true if verbose logging is enabled or to false otherwise
     */
    private final boolean verboseLogging;

    /**
     * The constructor used to build the memory state
     *
     * @param safeRegistrationsListNeeded The parameter to select whether the
     * safeRegistrationsListNeeded
     * @param inheritancePolicy The chosen system inheritancePolicy to use
     * @param verboseLogging If set to true verbose logging will be enabled
     */
    public MemoryState(boolean safeRegistrationsListNeeded, EventInheritancePolicy inheritancePolicy, boolean verboseLogging) {
        this.safeRegistrationsListNeeded = safeRegistrationsListNeeded;
        this.inheritancePolicy = inheritancePolicy;
        this.verboseLogging = verboseLogging;
    }

    /**
     * Get the list of listeners registrations for a particular event
     *
     * @param busEventKey The {@link BusEventKey} is used to retrieve all
     * related listeners registrations
     * @return The list of listeners registrations for the specified event
     */
    public List<Registration> getRegistrations(BusEventKey busEventKey) {
        List<Registration> registrationsList = eventsRegistrations.get(busEventKey);
        return safeRegistrationsListNeeded ? Collections.unmodifiableList(registrationsList) : registrationsList;
    }

    /**
     * Check if the {@link #eventsRegistrations} map contains the specified
     * {@link BusEventKey}
     *
     * @param busEventKey The {@link BusEventKey} to check for
     * @return True if the {@link #eventsRegistrations} map contains the
     * specified {@link BusEventKey} or false otherwise
     */
    public boolean registrationMapContainsKey(BusEventKey busEventKey) {
        return eventsRegistrations.containsKey(busEventKey);
    }

    /**
     * Check if the {@link #eventsRegistrations} map is empty
     *
     * @return True if the {@link #eventsRegistrations} is empty or false
     * otherwise
     */
    public boolean isEventsRegistrationsEmpty() {
        return eventsRegistrations.isEmpty();
    }

    /**
     * Get the {@link #eventsRegistrations} map size
     *
     * @return The size of the {@link #eventsRegistrations}
     */
    public int getEventEventsRegistrationsSize() {
        return eventsRegistrations.size();
    }

    /**
     * Method used to register a listener in the memory state
     *
     * @param eventKey The {@link BusEventKey} of the event to register
     * @param registration The {@link Registration} containing all the metadata
     * of the listener to register
     */
    public void registerListener(final BusEventKey eventKey, final Registration registration) {
        Object listener = registration.getListener();

        if (isNewListener(listener)) {
            foundListenerStickyEventsRegistrations.clear();
        }

        Set<EventMethodKey> eventKeys = listenerToEventsMap.getOrDefault(listener, new HashSet<>());
        EventMethodKey eventMethodKey = new EventMethodKey(eventKey.getEventClass(),registration.getMethod());        
        if (!eventKeys.contains(eventMethodKey)) {                        
            eventKeys.add(eventMethodKey);
            listenerToEventsMap.put(listener, eventKeys);
            eventsRegistrations.addRegistration(eventKey, registration);

            if (isStickyEventRegistered(eventKey)) {
                foundListenerStickyEventsRegistrations.addRegistration(eventKey, registration);
            }
            logger.debug("Registered new event {}", eventKey.getEventClass());
            logger.debug("Registrations sticky size: {}", foundListenerStickyEventsRegistrations.containsKey(eventKey) ? foundListenerStickyEventsRegistrations.get(eventKey).size() : 0);
            if (verboseLogging) {
                printState();
            }
        }
    }

    /**
     * Gets the found lister sticky events registrations
     *
     * @return The found listener sticky events registrations
     */
    public EventsRegistrationsMap getFoundListenerStickyEventsRegistrations() {
        return foundListenerStickyEventsRegistrations;
    }

    /**
     * Check if a sticky event with a specified {@link BusEventKey} is
     * registered in the {@link #stickyEventsMap}
     *
     * @param eventKey The {@link BusEventKey} to check
     * @return True if the sticky event with the specified {@link BusEventKey}
     * is already registered in the {@link #stickyEventsMap} or false otherwise
     */
    public boolean isStickyEventRegistered(BusEventKey eventKey) {
        return stickyEventsMap.containsKey(eventKey);
    }

    /**
     * Get a saved sticky event into the {@link #stickyEventsMap} if present
     * specifying its {@link BusEventKey}
     *
     * @param eventKey The {@link BusEventKey} of the sticky event
     * @return The sticky event with the specified {@link BusEventKey}
     */
    public Object getStickyEvent(BusEventKey eventKey) {
        return stickyEventsMap.get(eventKey);
    }

    /**
     * Register a sticky event in the memory state
     *
     * @param eventKey The {@link BusEventKey} of the sticky event to register
     * @param event The sticky event to register
     */
    public void registerStickyEvent(BusEventKey eventKey, Object event) {
        stickyEventsMap.put(eventKey, event);
    }

    /**
     * Unregister a sticky event from the memory state
     *
     * @param eventKey The {@link BusEventKey} of the sticky event to register
     */
    public void unregisterStickyEvent(BusEventKey eventKey) {
        stickyEventsMap.remove(eventKey);
    }

    /**
     * Remove all the sticky events from the memory state
     */
    public void removeAllStickyEvents() {
        stickyEventsMap.clear();
    }

    /**
     * Returns a boolean which is true if the passed listener is currently
     * registered in the memory state, or false otherwise.
     *
     * @param listener The listener to check
     * @return A boolean true if the passed listener is registered in the memory
     * state, or false otherwise
     */
    public boolean isListenerRegistered(Object listener) {
        return listenerToEventsMap.containsKey(listener);
    }

    /**
     * Method used to unregister a listener from the memory state
     *
     * @param listenerToUnregister The listener to unregister from memory state
     */
    public void unregisterListener(Object listenerToUnregister) {
        Set<EventMethodKey> eventsListenedByListener = listenerToEventsMap.get(listenerToUnregister);
        if (eventsListenedByListener != null) {
            for (EventMethodKey eventMethodKey : eventsListenedByListener) {
                eventsRegistrations.get(new BusEventKey(eventMethodKey.getEventClass())).removeIf(registration -> {
                    Object listener = registration.getListener();
                    logger.debug("Priority={} registeredObject={}", registration.getPriority(), listener.getClass().getName());
                    return listenerToUnregister.equals(listener);
                });
            }

            listenerToEventsMap.remove(listenerToUnregister);
            if (verboseLogging) {
                printState();
            }
        }
    }

    /**
     * Gets all the superclasses and interfaces for the event to post regarding
     * the current {@link EventInheritancePolicy}
     *
     * @param eventObjectToPost The event to post
     * @return all the superclasses and interfaces for the event to post
     * regarding the current {@link EventInheritancePolicy}
     * @see EventInheritancePolicy
     */
    public Set<Class<?>> getEventSuperClassesAndInterfaces(Object eventObjectToPost) {
        return inheritancePolicy.getAllEventInheritanceObjects(eventObjectToPost, eventsRegistrations, eventSuperClassesAndInterfacesCache);
    }

    /**
     * Print all the useful data in the {@link MemoryState}
     */
    public void printState() {
        printEventsRegistrations();
        printListenerToEventsMap();
    }

    /**
     * Checks if the specified listener is already registered in the
     * {@link #listenerToEventsMap}
     *
     * @param listener The listener to check
     * @return True if the specified listener is not present and false otherwise
     */
    private boolean isNewListener(Object listener) {
        return !listenerToEventsMap.containsKey(listener);
    }

    /**
     * Print all the data in the {@link #eventsRegistrations}
     */
    private void printEventsRegistrations() {
        logger.info("--- Events registrations ---");
        logger.info("Number of registered events: " + eventsRegistrations.size());
        for (BusEventKey eventKey : eventsRegistrations.keySet()) {
            logger.info("Event:" + eventKey.getClass().getName());
            List<Registration> registrationsList = getRegistrations(eventKey);
            logger.info("Number of registrations per event: " + registrationsList.size());
            for (Registration registration : registrationsList) {
                logger.info("Class:" + registration.getListener().getClass().getName());               
                logger.info("Registered Method:" + registration.getMethod().getName());
                logger.info("Priority:" + registration.getPriority());
            }
            logger.info("\n");
        }
    }

    /**
     * Print all the data in the {@link #listenerToEventsMap}
     */
    private void printListenerToEventsMap() {
        logger.info("--- Events listeners ---");
        logger.info("Number of registered listeners: " + listenerToEventsMap.size());
        for (Object object : listenerToEventsMap.keySet()) {
            logger.info("Object:" + object.getClass().getName());
            Set<EventMethodKey> eventSet = listenerToEventsMap.get(object);
            for (EventMethodKey busEvent : eventSet) {
                logger.info("Event:" + busEvent.getEventClass().getName());                 
                logger.info("Registered Method:" + busEvent.getMethod().getName());
            }
            logger.info("\n");
        }
    }
}
