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
package it.rebirthproject.ufoeb.architecture.eventbus;

import it.rebirthproject.ufoeb.architecture.eventbus.dto.SlowListenerForShutdownTest;
import it.rebirthproject.ufoeb.dto.events.TestEvent1;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventBusShutdownBehaviorTest {

    @Test
    public void should_ProcessAllPostedEvents_When_ShutdownBusIsCalled() throws Exception {
        final int postedEvents = 40;
        EventBus eventBus = new EventBusBuilder()
                .setNumberOfWorkers(1)
                .setQueuesLength(128)
                .build();

        SlowListenerForShutdownTest listener = new SlowListenerForShutdownTest(postedEvents);
        eventBus.register(listener);

        for (int i = 0; i < postedEvents; i++) {
            eventBus.post(new TestEvent1());
        }

        eventBus.shutdownBus();

        boolean allEventsProcessed = listener.awaitAllEvents(1, TimeUnit.SECONDS);
        Assertions.assertTrue(allEventsProcessed);
        Assertions.assertEquals(postedEvents, listener.getReceivedEventsCounter());
    }
}
