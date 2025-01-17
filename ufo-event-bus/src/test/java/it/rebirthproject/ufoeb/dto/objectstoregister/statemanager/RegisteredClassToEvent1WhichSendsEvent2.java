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
package it.rebirthproject.ufoeb.dto.objectstoregister.statemanager;

import it.rebirthproject.ufoeb.dto.events.TestEvent1;
import it.rebirthproject.ufoeb.dto.events.TestEvent2;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisteredClassToEvent1WhichSendsEvent2 {

    private static final Logger logger = LoggerFactory.getLogger(RegisteredClassToEvent1WhichSendsEvent2.class);
    private final EventBus eventBus;
    private final TestEvent2 event2;

    public RegisteredClassToEvent1WhichSendsEvent2(EventBus eventBus, TestEvent2 event2) throws InterruptedException {
        this.eventBus = eventBus;       
        this.event2 = event2;
    }

    @Listen
    public void onEvent1(TestEvent1 testEvent1) throws EventBusException {
        String classSimpleName = getClass().getSimpleName();
        logger.info("{} is receiving this message: {}", classSimpleName, testEvent1.getMessage());

        eventBus.post(event2);
        logger.info("{} sent : {}", classSimpleName, event2.getMessage());
    }
}
