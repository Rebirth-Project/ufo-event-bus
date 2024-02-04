/*
 * Copyright (C) 2021/2024 Andrea Paternesi Rebirth project
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
package it.rebirthproject.ufoeb.dto.objectstoregister.eventbus;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.dto.events.TestEvent1;
import it.rebirthproject.ufoeb.dto.events.TestEvent2;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PosterListenerRunnable extends AbstractRunnable {

    private static final Logger logger = LoggerFactory.getLogger(PosterListenerRunnable.class);
    private final EventBus eventbus;
    private final TestEvent2 event2;

    public PosterListenerRunnable(EventBus eventBus, TestEvent2 event2) {
        super(0);
        this.eventbus = eventBus;
        this.event2 = event2;
    }

    @Listen()
    public void listen(TestEvent1 event) {
        logger.info("I received an event of type {}!", event.getClass().getName());
        receivedEvent = event;

        try {
            eventbus.post(event2);
        } catch (EventBusException ex) {
        }
    }
}
