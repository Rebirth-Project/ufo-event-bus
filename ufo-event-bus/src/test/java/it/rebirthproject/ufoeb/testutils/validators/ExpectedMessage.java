/*
 * Copyright (C) 2021/2023 Andrea Paternesi Rebirth project
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

import it.rebirthproject.ufoeb.architecture.messages.interfaces.Message;
import it.rebirthproject.ufoeb.architecture.state.mock.FakeMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpectedMessage implements Validator<Message> {

    private final Object expectedEvent;

    public ExpectedMessage(Object expectedEvent) {        
        this.expectedEvent = expectedEvent;
    }

    @Override
    public void assertValid(Message message) {
        assertEquals(expectedEvent.getClass(), ((FakeMessage)message).getEventToPost().getClass(), "Event class was wrong.");
        assertEquals(expectedEvent, ((FakeMessage)message).getEventToPost(), "Event was wrong");
    }
}
