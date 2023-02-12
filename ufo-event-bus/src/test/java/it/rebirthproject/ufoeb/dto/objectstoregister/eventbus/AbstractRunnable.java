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
package it.rebirthproject.ufoeb.dto.objectstoregister.eventbus;

import it.rebirthproject.ufoeb.dto.events.eventinterface.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRunnable.class);

    protected final long sleepTime;
    protected Event receivedEvent;

    public AbstractRunnable(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        try {
            logger.info("I am going to sleep.");
            while (receivedEvent == null) {
                Thread.sleep(sleepTime);
            }
            logger.info("Good Morning! I am the {}.", this.getClass().getSimpleName());
        } catch (InterruptedException ex) {
            logger.error("Error", ex);
        }
    }

    public Event getReceivedEvent() {
        return receivedEvent;
    }
}
