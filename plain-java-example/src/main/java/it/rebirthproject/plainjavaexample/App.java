/*
 * Copyright (C) 2021/2022 Andrea Paternesi Rebirth project
 * Copyright (C) 2021/2022 Matteo Veroni Rebirth project
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

package it.rebirthproject.plainjavaexample;

import it.rebirthproject.plainjavaexample.eventemitters.EventEmitterRunnable;
import it.rebirthproject.plainjavaexample.listeners.EventListener;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBusBuilder;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        new App().start();
    }

    private void start() {
        EventBus eventBus = null;
        try {
            // Building the event bus
            eventBus = new EventBusBuilder()
                    .setNumberOfWorkers(1)
                    .build();

            // Building the event listener
            EventListener listener = new EventListener();

            // Registering the listener
            eventBus.register(listener);

            // Creating an event emitter
            Thread eventEmitterThread = new Thread(new EventEmitterRunnable(eventBus));
            eventEmitterThread.start();

            // Waiting util the emitter thread stops
            eventEmitterThread.join();
        } catch (EventBusException ex1) {
            log.error("EventBus error", ex1);
        } catch (InterruptedException ex2) {
            log.error("Error", ex2);
        } finally {
            // Eventbus shutdown
            if (eventBus != null) {
                eventBus.shutdownBus();
            }
        }
    }
}
