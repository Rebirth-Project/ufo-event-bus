/*
 * Copyright (C) 2021/2024 Andrea Paternesi Rebirth project
 * Copyright (C) 2021/2024 Matteo Veroni Rebirth project
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

package it.rebirthproject.plainjavaexample.eventemitters;

import it.rebirthproject.plainjavaexample.events.EventMessage;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

/**
 * Post a new EventMessage on the eventbus each 2 seconds. This is iterated 10 times before to stop
 */
public class EventEmitterRunnable implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(EventEmitterRunnable.class);
    private final EventBus eventBus;

    public EventEmitterRunnable(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(1500);
                eventBus.post(new EventMessage((i + 1), UUID.randomUUID().toString()));
            } catch (Exception ex) {
                log.error("Error", ex);
            }
        }
    }
}
