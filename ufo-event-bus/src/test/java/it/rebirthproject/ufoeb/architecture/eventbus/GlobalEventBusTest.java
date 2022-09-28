/*
 * Copyright (C) 2021 Matteo Veroni Rebirth project
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

import it.rebirthproject.ufoeb.exceptions.EventBusException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalEventBusTest {

    private static final Logger logger = LoggerFactory.getLogger(GlobalEventBusTest.class);

    @BeforeEach
    public void beforeEach() {
        GlobalEventBus.clearGlobalInstance();
    }

    @Test
    public void calling_getInstance_method_before_setup_throws_exception() {
        Assertions.assertThrows(EventBusException.class, () -> {
            EventBus instance = GlobalEventBus.getInstance();
        }, "Error, calling getInstance before setup doesn't throw exception as expected");
    }

    @Test
    public void calling_setup_method_more_than_once_throws_exception() {
        EventBusBuilder busBuilder1 = new EventBusBuilder().setNumberOfWorkers(1);
        EventBusBuilder busBuilder2 = new EventBusBuilder().setNumberOfWorkers(2);

        Assertions.assertThrows(EventBusException.class, () -> {
            GlobalEventBus.setup(busBuilder1);
            GlobalEventBus.setup(busBuilder2);
        }, "Error, calling setup more than once doesn't throw exception as expected");
    }

    public void everytime_getInstance_is_called_the_same_singleton_instance_is_returned() throws InterruptedException, EventBusException {
        GlobalEventBus.setup(new EventBusBuilder().setNumberOfWorkers(1));

        int numberOfThreads = 100;
        CountDownLatch threadsCountDownLatch = new CountDownLatch(numberOfThreads);
        Map<EventBus, EventBus> busInstances = new ConcurrentHashMap<>();

        for (int i = 0; i < numberOfThreads; i++) {
            Thread thread = new Thread(new TaskGetEventBusInstance(busInstances, threadsCountDownLatch));
            thread.start();
        }

        threadsCountDownLatch.await();

        assertEquals(1, busInstances.size(), "Error, GlobalEventBus must be a singleton but getInstance returned different instances");
    }

    private class TaskGetEventBusInstance implements Runnable {

        private final Map<EventBus, EventBus> busInstances;
        private final CountDownLatch countDownLatch;
        private final EventBus instance;

        public TaskGetEventBusInstance(Map<EventBus, EventBus> busInstances, CountDownLatch countDownLatch) throws EventBusException {
            instance = GlobalEventBus.getInstance();
            this.busInstances = busInstances;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {            
            busInstances.put(instance, instance);
            countDownLatch.countDown();
        }
    }
}
