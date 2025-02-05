/*
 * Copyright (C) 2021/2025 Matteo Veroni Rebirth project
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
import it.rebirthproject.ufoeb.dto.enums.EventPriority;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisteredClassToEvent1WithLowPriority {

    private static final Logger logger = LoggerFactory.getLogger(RegisteredClassToEvent1WithLowPriority.class);

    @Listen(priority = EventPriority.LOW_VALUE)
    public void onEvent1(TestEvent1 testEvent1) throws InterruptedException {
        logger.info("{} is receiving this message: {}", getClass().getSimpleName(), testEvent1.getMessage());
    }
}
