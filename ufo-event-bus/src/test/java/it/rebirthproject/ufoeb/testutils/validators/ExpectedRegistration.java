/*
 * Copyright (C) 2021/2023 Matteo Veroni Rebirth project
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
package it.rebirthproject.ufoeb.testutils.validators;

import it.rebirthproject.ufoeb.dto.enums.EventPriority;
import it.rebirthproject.ufoeb.dto.registrations.Registration;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpectedRegistration implements Validator<Registration> {

    private final Object expectedRegisteredObject;
    private final int expectedPriority;
    private final String expectedRegisteredMethod;
    private final Class<?> expectedEventClass;

    public ExpectedRegistration(Object expectedRegisteredObject, EventPriority eventPriority, String expectedRegisteredMethod, Class<?> expectedEventClass) {
        this.expectedRegisteredObject = expectedRegisteredObject;
        this.expectedPriority = eventPriority.getValue();
        this.expectedRegisteredMethod = expectedRegisteredMethod;
        this.expectedEventClass = expectedEventClass;
    }

    @Override
    public void assertValid(Registration registration) throws NoSuchMethodException {
        assertEquals(expectedRegisteredObject, registration.getListener(), "The registered object was not the expected one.");
        assertEquals(expectedPriority, registration.getPriority(), "The priority of the event was wrong.");
        assertEquals(expectedRegisteredObject.getClass().getMethod(expectedRegisteredMethod, expectedEventClass), registration.getMethod(), "The method to call was wrong.");
    }
}