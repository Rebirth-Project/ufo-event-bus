/*
 * Copyright (C) 2021/2022-2022 Andrea Paternesi Rebirth project
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

public class UfoEventBusPostWith5Latency10000Test {

    private static final int NUMBER_OF_LISTENERS = 5;
    private static final int NUMBER_OF_TOTAL_POST = 500000;
    private static final int NUMBER_OF_TOTAL_POSTERS = 5;
    private static final int MESSAGES_PER_POSTER = NUMBER_OF_TOTAL_POST / NUMBER_OF_TOTAL_POSTERS;


    @State(Scope.Thread)
    public static class BenchmarkState {

        private EventBus ufoEventBus;
        private CountDownLatch countDownLatch;


        private ListenerToTenEventsWith10000Latency listener1ToTenEvent;
        private ListenerToTenEventsWith10000Latency listener2ToTenEvent;
        private ListenerToTenEventsWith10000Latency listener3ToTenEvent;
        private ListenerToTenEventsWith10000Latency listener4ToTenEvent;
        private ListenerToTenEventsWith10000Latency listener5ToTenEvent;

        private EventPoster poster1;
        private EventPoster poster2;
        private EventPoster poster3;
        private EventPoster poster4;
        private EventPoster poster5;

        @Setup(Level.Iteration)
        public void setupTest() throws EventBusException {

            ufoEventBus = new EventBusBuilder().setQueuesLength(1000).setNumberOfWorkers(20).build();

            countDownLatch = new CountDownLatch(NUMBER_OF_TOTAL_POST * NUMBER_OF_LISTENERS);


            listener1ToTenEvent = new ListenerToTenEventsWith10000Latency(countDownLatch);
            listener1ToTenEvent.setListenerNumber(1);
            listener2ToTenEvent = new ListenerToTenEventsWith10000Latency(countDownLatch);
            listener2ToTenEvent.setListenerNumber(2);
            listener3ToTenEvent = new ListenerToTenEventsWith10000Latency(countDownLatch);
            listener3ToTenEvent.setListenerNumber(3);
            listener4ToTenEvent = new ListenerToTenEventsWith10000Latency(countDownLatch);
            listener4ToTenEvent.setListenerNumber(4);
            listener5ToTenEvent = new ListenerToTenEventsWith10000Latency(countDownLatch);
            listener5ToTenEvent.setListenerNumber(5);            

            poster1 = new EventPoster(ufoEventBus, MESSAGES_PER_POSTER);
            poster2 = new EventPoster(ufoEventBus, MESSAGES_PER_POSTER);
            poster3 = new EventPoster(ufoEventBus, MESSAGES_PER_POSTER);
            poster4 = new EventPoster(ufoEventBus, MESSAGES_PER_POSTER);
            poster5 = new EventPoster(ufoEventBus, MESSAGES_PER_POSTER);

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
