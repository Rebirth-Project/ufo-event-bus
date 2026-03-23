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

import it.rebirthproject.ufoeb.architecture.eventbus.dto.ListenerForBuilderDefaultsTest;
import it.rebirthproject.ufoeb.dto.events.TestEvent1;
import it.rebirthproject.ufoeb.dto.objectstoregister.services.TestClassToRegisterWithMethodWithTooManyParameters;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventBusBuilderDefaultsTest {

    @Test
    public void should_KeepWorking_When_ListenerWithoutListenMethodsIsRegisteredWithDefaultBuilderConfiguration() throws Exception {
        EventBus eventBus = new EventBusBuilder()
                .setNumberOfWorkers(1)
                .build();

        ListenerForBuilderDefaultsTest validListener = new ListenerForBuilderDefaultsTest();

        eventBus.register(new Object());
        eventBus.register(validListener);
        eventBus.post(new TestEvent1());

        boolean eventDelivered = validListener.awaitEvent(2, TimeUnit.SECONDS);
        eventBus.shutdownBus();

        assertTrue(eventDelivered, "Default EventBusBuilder configuration should not stop the bus when a listener without @Listen methods is registered.");
    }

    @Test
    public void should_KeepWorking_When_ListenerWithInvalidListenMethodIsRegisteredWithDefaultBuilderConfiguration() throws Exception {
        EventBus eventBus = new EventBusBuilder()
                .setNumberOfWorkers(1)
                .build();

        ListenerForBuilderDefaultsTest validListener = new ListenerForBuilderDefaultsTest();

        eventBus.register(new TestClassToRegisterWithMethodWithTooManyParameters());
        eventBus.register(validListener);
        eventBus.post(new TestEvent1());

        boolean eventDelivered = validListener.awaitEvent(2, TimeUnit.SECONDS);
        eventBus.shutdownBus();

        assertTrue(eventDelivered, "Default EventBusBuilder configuration should not stop the bus when a listener has an invalid @Listen method signature.");
    }
}
