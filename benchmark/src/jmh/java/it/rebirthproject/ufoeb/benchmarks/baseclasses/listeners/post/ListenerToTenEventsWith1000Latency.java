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
 */package it.rebirthproject.ufoeb.benchmarks.baseclasses.listeners.post;

import it.rebirthproject.ufoeb.benchmarks.baseclasses.events.EventToListen1;
import it.rebirthproject.ufoeb.benchmarks.baseclasses.events.EventToListen10;
import it.rebirthproject.ufoeb.benchmarks.baseclasses.events.EventToListen2;
import it.rebirthproject.ufoeb.benchmarks.baseclasses.events.EventToListen3;
import it.rebirthproject.ufoeb.benchmarks.baseclasses.events.EventToListen4;
import it.rebirthproject.ufoeb.benchmarks.baseclasses.events.EventToListen5;
import it.rebirthproject.ufoeb.benchmarks.baseclasses.events.EventToListen6;
import it.rebirthproject.ufoeb.benchmarks.baseclasses.events.EventToListen7;
import it.rebirthproject.ufoeb.benchmarks.baseclasses.events.EventToListen8;
import it.rebirthproject.ufoeb.benchmarks.baseclasses.events.EventToListen9;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import java.util.concurrent.CountDownLatch;
import org.openjdk.jmh.infra.Blackhole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListenerToTenEventsWith1000Latency {

    private static final Logger logger = LoggerFactory.getLogger(ListenerToTenEventsWith1000Latency.class);

    private final CountDownLatch countDownLatch;
    private int listenerNumber;

    public ListenerToTenEventsWith1000Latency(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public void setListenerNumber(int listenerNumber) {
        this.listenerNumber = listenerNumber;
    }    
    
    @Listen
    public void onEvent(EventToListen1 event) {
        Blackhole.consumeCPU(1000);
        countDownLatch.countDown();
         //logger.info("Listener " + listenerNumber + " COUNT -------------------------------> " + countDownLatch.getCount());
    }

    @Listen
    public void onEvent2(EventToListen2 event) {
        Blackhole.consumeCPU(1000);
        countDownLatch.countDown();
         //logger.info("Listener " + listenerNumber + " COUNT -------------------------------> " + countDownLatch.getCount());
    }

    @Listen
    public void onEvent3(EventToListen3 event) {
        Blackhole.consumeCPU(1000);
        countDownLatch.countDown();
         //logger.info("Listener " + listenerNumber + " COUNT -------------------------------> " + countDownLatch.getCount());
    }

    @Listen
    public void onEvent4(EventToListen4 event) {
        Blackhole.consumeCPU(1000);
        countDownLatch.countDown();
         //logger.info("Listener " + listenerNumber + " COUNT -------------------------------> " + countDownLatch.getCount());
    }

    @Listen
    public void onEven5(EventToListen5 event) {
        Blackhole.consumeCPU(1000);
        countDownLatch.countDown();
         //logger.info("Listener " + listenerNumber + " COUNT -------------------------------> " + countDownLatch.getCount());
    }

    @Listen
    public void onEvent6(EventToListen6 event) {
        Blackhole.consumeCPU(1000);
        countDownLatch.countDown();
         //logger.info("Listener " + listenerNumber + " COUNT -------------------------------> " + countDownLatch.getCount());
    }

    @Listen
    public void onEvent7(EventToListen7 event) {
        Blackhole.consumeCPU(1000);
        countDownLatch.countDown();
         //logger.info("Listener " + listenerNumber + " COUNT -------------------------------> " + countDownLatch.getCount());
    }

    @Listen
    public void onEvent8(EventToListen8 event) {
        Blackhole.consumeCPU(1000);
        countDownLatch.countDown();
         //logger.info("Listener " + listenerNumber + " COUNT -------------------------------> " + countDownLatch.getCount());
    }

    @Listen
    public void onEvent9(EventToListen9 event) {
        Blackhole.consumeCPU(1000);
        countDownLatch.countDown();
         //logger.info("Listener " + listenerNumber + " COUNT -------------------------------> " + countDownLatch.getCount());
    }

    @Listen
    public void onEvent10(EventToListen10 event) {
        Blackhole.consumeCPU(1000);
        countDownLatch.countDown();
         //logger.info("Listener " + listenerNumber + " COUNT -------------------------------> " + countDownLatch.getCount());
    }
}
