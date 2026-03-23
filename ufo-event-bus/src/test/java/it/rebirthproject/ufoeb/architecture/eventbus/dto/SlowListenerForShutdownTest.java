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
package it.rebirthproject.ufoeb.architecture.eventbus.dto;

import it.rebirthproject.ufoeb.dto.events.TestEvent1;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SlowListenerForShutdownTest {

    private static final int SLEEP_MILLIS = 30;
    private final CountDownLatch countDownLatch;
    private final AtomicInteger receivedEventsCounter = new AtomicInteger(0);

    public SlowListenerForShutdownTest(int expectedEvents) {
        this.countDownLatch = new CountDownLatch(expectedEvents);
    }

    @Listen
    public void onEvent(TestEvent1 event) {
        try {
            Thread.sleep(SLEEP_MILLIS);
            receivedEventsCounter.incrementAndGet();
            countDownLatch.countDown();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean awaitAllEvents(long timeout, TimeUnit unit) throws InterruptedException {
        return countDownLatch.await(timeout, unit);
    }

    public int getReceivedEventsCounter() {
        return receivedEventsCounter.get();
    }
}
