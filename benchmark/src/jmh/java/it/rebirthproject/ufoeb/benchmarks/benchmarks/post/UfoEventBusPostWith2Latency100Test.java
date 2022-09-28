/*
 * Copyright (C) 2021-2022 Andrea Paternesi Rebirth project
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
package it.rebirthproject.ufoeb.benchmarks.benchmarks.post;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBusBuilder;
import it.rebirthproject.ufoeb.benchmarks.baseclasses.listeners.post.ListenerToTenEventsWith10000Latency;
import it.rebirthproject.ufoeb.benchmarks.baseclasses.listeners.post.ListenerToTenEventsWith100Latency;
import it.rebirthproject.ufoeb.benchmarks.baseclasses.posters.EventPoster;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

public class UfoEventBusPostWith2Latency100Test {


    private static final int NUMBER_OF_LISTENERS = 5;
    private static final int NUMBER_OF_TOTAL_POST = 500000;
    private static final int NUMBER_OF_TOTAL_POSTERS = 5;

    @State(Scope.Thread)
    public static class BenchmarkState {

        private EventBus ufoEventBus;
        private CountDownLatch countDownLatch;

        private ListenerToTenEventsWith100Latency listener1ToTenEvent;
        private ListenerToTenEventsWith100Latency listener2ToTenEvent;
        private ListenerToTenEventsWith100Latency listener3ToTenEvent;
        private ListenerToTenEventsWith100Latency listener4ToTenEvent;
        private ListenerToTenEventsWith100Latency listener5ToTenEvent;


        private EventPoster poster1;
        private EventPoster poster2;
        private EventPoster poster3;
        private EventPoster poster4;
        private EventPoster poster5;

        @Setup(Level.Iteration)
        public void setupTest() throws EventBusException {

            ufoEventBus = new EventBusBuilder().setQueuesLength(1000).setNumberOfWorkers(3).build();
            countDownLatch = new CountDownLatch(NUMBER_OF_TOTAL_POST * NUMBER_OF_LISTENERS);

            listener1ToTenEvent = new ListenerToTenEventsWith100Latency(countDownLatch);
            listener1ToTenEvent.setListenerNumber(1);
            listener2ToTenEvent = new ListenerToTenEventsWith100Latency(countDownLatch);
            listener2ToTenEvent.setListenerNumber(2);
            listener3ToTenEvent = new ListenerToTenEventsWith100Latency(countDownLatch);
            listener3ToTenEvent.setListenerNumber(3);
            listener4ToTenEvent = new ListenerToTenEventsWith100Latency(countDownLatch);
            listener4ToTenEvent.setListenerNumber(4);
            listener5ToTenEvent = new ListenerToTenEventsWith100Latency(countDownLatch);
            listener5ToTenEvent.setListenerNumber(5);

            poster1 = new EventPoster(ufoEventBus, NUMBER_OF_TOTAL_POST / NUMBER_OF_TOTAL_POSTERS);
            poster2 = new EventPoster(ufoEventBus, NUMBER_OF_TOTAL_POST / NUMBER_OF_TOTAL_POSTERS);
            poster3 = new EventPoster(ufoEventBus, NUMBER_OF_TOTAL_POST / NUMBER_OF_TOTAL_POSTERS);
            poster4 = new EventPoster(ufoEventBus, NUMBER_OF_TOTAL_POST / NUMBER_OF_TOTAL_POSTERS);
            poster5 = new EventPoster(ufoEventBus, NUMBER_OF_TOTAL_POST / NUMBER_OF_TOTAL_POSTERS);

            ufoEventBus.register(listener1ToTenEvent);
            ufoEventBus.register(listener2ToTenEvent);
            ufoEventBus.register(listener3ToTenEvent);
            ufoEventBus.register(listener4ToTenEvent);
            ufoEventBus.register(listener5ToTenEvent);
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            ufoEventBus.shutdownBus();
        }
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchmarkPost(BenchmarkState state) throws Exception {
        Thread t1 = new Thread(state.poster1);
        Thread t2 = new Thread(state.poster2);
        Thread t3 = new Thread(state.poster3);
        Thread t4 = new Thread(state.poster4);
        Thread t5 = new Thread(state.poster5);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        state.countDownLatch.await();

        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();        
    }
}
