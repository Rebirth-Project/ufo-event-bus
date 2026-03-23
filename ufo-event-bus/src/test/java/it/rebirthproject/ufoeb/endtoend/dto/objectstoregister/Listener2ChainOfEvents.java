/*
 * Copyright (C) 2021/2026 Matteo Veroni Rebirth project
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
package it.rebirthproject.ufoeb.endtoend.dto.objectstoregister;

import it.rebirthproject.ufoeb.endtoend.dto.events.Event2;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Listener2ChainOfEvents {
    private final CountDownLatch countDownLatch;
    private final List<Object> events;

    public Listener2ChainOfEvents(CountDownLatch countDownLatch, List<Object> events) {
        this.countDownLatch = countDownLatch;
        this.events = events;
    }

    @Listen
    public void onEvent(Event2 event2) throws EventBusException {
        events.add(event2);
        countDownLatch.countDown();
    }
}
