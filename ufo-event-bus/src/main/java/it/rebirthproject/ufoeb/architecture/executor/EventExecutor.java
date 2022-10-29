/*
 * Copyright (C) 2021/2022 Andrea Paternesi Rebirth project
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
package it.rebirthproject.ufoeb.architecture.executor;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBusBuilder;
import it.rebirthproject.ufoeb.dto.registrations.Registration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An EventExecutor is a worker used by the bus to notify listeners. It's
 * possible to use just one EventExecutor or more, depending on the bus
 * configuration specified using the {@link EventBusBuilder}
 *
 * @see EventBusBuilder#setNumberOfWorkers(Integer)
 * @see EventBusBuilder#setNumberOfWorkersAsAvailableProcessors()
 */
public class EventExecutor implements Runnable {

    /**
     * The logger used by this class
     */
    private static final Logger logger = LoggerFactory.getLogger(EventExecutor.class);
    private final List<Registration> registrationList;
    private final Object eventToPost;

    /**
     * The constructor used to build an EventExecutor
     *
     * @param registrationList List of registrations that get the event
     * @param eventToPost The posted event to send to the registrations
     */
    public EventExecutor(List<Registration> registrationList, Object eventToPost) {
        this.registrationList = registrationList;
        this.eventToPost = eventToPost;
    }

    /**
     * This is the main method of the {@link EventExecutor}. It just iterate to
     * process the event with every registrations.
     */
    @Override
    public void run() {
        logger.debug("Execute event message");
        for (Registration registration : registrationList) {
            try {
                registration.process(eventToPost);
            } catch (Throwable ex) {
                logger.error("Message " + eventToPost.getClass().getCanonicalName() + " cannot be delivered to Object " + registration.getListener().getClass().getCanonicalName() + ".", ex);
            }
        }
        logger.debug("All messages are been delivered");
    }

    /**
     * Get the event to post to the various registrations
     *
     * @return The event to post to the registrations
     */
    public Object getEventToPost() {
        return eventToPost;
    }

    /**
     * Get the registrations list
     *
     * @return The registrations list
     */
    public List<Registration> getRegistrationList() {
        return registrationList;
    }
}
