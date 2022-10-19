/*
 * Copyright (C) 2021/2022 Matteo Veroni Rebirth project
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
import it.rebirthproject.ufoeb.testutils.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UfoEventBusTest extends BaseTest {

    private EventBus eventBus;

    @BeforeEach
    public void beforeEach() throws EventBusException {
        eventBus = new EventBusBuilder()
                .setNumberOfWorkers(1)
                .setThrowNoRegistrationsWarning()
                .setThrowNoListenerAnnotationException()
                .setThrowNotValidMethodException()
                .setInheritancePackageFrontierPath(TEST_FRONTIER_PATH)
                .build();
    }

    @AfterEach
    public void afterEach() {
        eventBus.shutdownBus();
    }

    @Test
    public void registering_null_object_throws_exception() {
        Assertions.assertThrows(EventBusException.class, () -> eventBus.register(null));
    }

    @Test
    public void unregistering_null_object_throws_exception() {
        Assertions.assertThrows(EventBusException.class, () -> eventBus.unregister(null));
    }

    @Test
    public void trying_to_post_null_object_throws_exception() {
        Assertions.assertThrows(EventBusException.class, () -> eventBus.post(null));
    }

    @Test
    public void trying_to_post_sticky_null_object_throws_exception() {
        Assertions.assertThrows(EventBusException.class, () -> eventBus.postSticky(null));
    }

    @Test
    public void trying_to_remove_sticky_null_object_throws_exception() {
        Object nullObject = null;
        Assertions.assertThrows(EventBusException.class, () -> eventBus.removeSticky(nullObject));
    }

    @Test
    public void trying_to_remove_sticky_null_class_throws_exception() {
        Class nullClass = null;
        Assertions.assertThrows(EventBusException.class, () -> eventBus.removeSticky(nullClass));
    }
}