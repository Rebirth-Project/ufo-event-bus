/*
 * Copyright (C) 2021/2024 Andrea Paternesi Rebirth project
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
package it.rebirthproject.ufoeb.benchmarks.baseclasses.posters;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
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
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import java.util.Random;

public class EventPoster implements Runnable {

    private static final int MAX_VALUE = 10;
    private final EventBus eventBus;
    private final int numberOfPosts;
    private static final Random random = new Random();

    public EventPoster(EventBus eventBus, int numberOfPosts) {
        this.eventBus = eventBus;
        this.numberOfPosts = numberOfPosts;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < numberOfPosts; i++) {
                int randomNum = random.nextInt(MAX_VALUE) + 1;
                switch (randomNum) {
                    case 1:
                        eventBus.post(new EventToListen1());
                        break;
                    case 2:
                        eventBus.post(new EventToListen2());
                        break;
                    case 3:
                        eventBus.post(new EventToListen3());
                        break;
                    case 4:
                        eventBus.post(new EventToListen4());
                        break;
                    case 5:
                        eventBus.post(new EventToListen5());
                        break;
                    case 6:
                        eventBus.post(new EventToListen6());
                        break;
                    case 7:
                        eventBus.post(new EventToListen7());
                        break;
                    case 8:
                        eventBus.post(new EventToListen8());
                        break;
                    case 9:
                        eventBus.post(new EventToListen9());
                        break;
                    case 10:
                        eventBus.post(new EventToListen10());
                        break;
                }
            }
        } catch (EventBusException ex) {
        }
    }
}
