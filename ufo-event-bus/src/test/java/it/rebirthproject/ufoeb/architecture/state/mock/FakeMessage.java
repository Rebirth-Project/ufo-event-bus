/*
 * Copyright (C) 2021/2022 Andrea Paternesi Rebirth project
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
package it.rebirthproject.ufoeb.architecture.state.mock;

import it.rebirthproject.ufoeb.architecture.messages.interfaces.Message;
import it.rebirthproject.ufoeb.architecture.messages.interfaces.MessageType;
import it.rebirthproject.ufoeb.dto.registrations.Registration;
import java.util.List;

public class FakeMessage implements Message {

    private final List<Registration> registrationList;
    private final Object eventToPost;

    public FakeMessage(List<Registration> registrationList, Object eventToPost) {
        this.registrationList = registrationList;
        this.eventToPost = eventToPost;
    }

    public List<Registration> getRegistrationsList() {
        return registrationList;
    }

    public Object getEventToPost() {
        return eventToPost;
    }

    @Override
    public MessageType getMessageType() {
        return null;
    }
}
