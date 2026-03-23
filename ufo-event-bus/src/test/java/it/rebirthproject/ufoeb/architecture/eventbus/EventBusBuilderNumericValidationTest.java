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

import it.rebirthproject.ufoeb.exceptions.EventBusException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class EventBusBuilderNumericValidationTest {

    @Test
    public void should_ThrowException_When_QueueLengthIsNull() {
        EventBusBuilder builder = new EventBusBuilder();

        Executable executable = new Executable() {
            @Override
            public void execute() {
                builder.setQueuesLength(null);
            }
        };

        Assertions.assertThrows(EventBusException.class, executable);
    }

    @Test
    public void should_ThrowException_When_QueueLengthIsNotPositive() {
        EventBusBuilder builder = new EventBusBuilder();

        Executable executableWithZero = new Executable() {
            @Override
            public void execute() {
                builder.setQueuesLength(0);
            }
        };

        Executable executableWithNegative = new Executable() {
            @Override
            public void execute() {
                builder.setQueuesLength(-1);
            }
        };

        Assertions.assertThrows(EventBusException.class, executableWithZero);
        Assertions.assertThrows(EventBusException.class, executableWithNegative);
    }

    @Test
    public void should_ThrowException_When_NumberOfWorkersIsNull() {
        EventBusBuilder builder = new EventBusBuilder();

        Executable executable = new Executable() {
            @Override
            public void execute() {
                builder.setNumberOfWorkers(null);
            }
        };

        Assertions.assertThrows(EventBusException.class, executable);
    }

    @Test
    public void should_ThrowException_When_NumberOfWorkersIsNotPositive() {
        EventBusBuilder builder = new EventBusBuilder();

        Executable executableWithZero = new Executable() {
            @Override
            public void execute() {
                builder.setNumberOfWorkers(0);
            }
        };

        Executable executableWithNegative = new Executable() {
            @Override
            public void execute() {
                builder.setNumberOfWorkers(-1);
            }
        };

        Assertions.assertThrows(EventBusException.class, executableWithZero);
        Assertions.assertThrows(EventBusException.class, executableWithNegative);
    }

    @Test
    public void should_BuildBus_When_NumericParametersArePositive() {
        EventBus bus = new EventBusBuilder()
                .setQueuesLength(128)
                .setNumberOfWorkers(2)
                .build();

        Assertions.assertNotNull(bus);
        bus.shutdownBus();
    }
}
