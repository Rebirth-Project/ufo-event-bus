/*
 * Copyright (C) 2021 Andrea Paternesi Rebirth project
 * Modifications copyright (C) 2021 Matteo Veroni Rebirth project
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
package it.rebirthproject.ufoeb.architecture.messages.commands;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.architecture.messages.interfaces.AbstractCommandMessage;
import it.rebirthproject.ufoeb.architecture.messages.interfaces.MessageType;
import it.rebirthproject.ufoeb.architecture.state.BusMemoryStateManager;
import it.rebirthproject.ufoeb.architecture.state.MemoryState;

/**
 * An internal message sent from the event bus infrastructure to the {@link EventBus}'
 * {@link MemoryState} when an event is posted to the bus. The
 * {@link PostEventMessage} wraps the posted event. The
 * {@link BusMemoryStateManager} will consume each {@link PostEventMessage} and send it
 * to the workers queue to process the event.
 *
 * @see EventBus
 * @see BusMemoryStateManager
 * @see MemoryState
 */
public class PostEventMessage extends AbstractCommandMessage {

    /**
     * The event to post
     */
    private final Object eventToPost;

    /**
     * The constructor to build a {@link PostEventMessage}
     *
     * @param eventToPost The event to post, which will be eventually notified
     * to listeners.
     */
    public PostEventMessage(Object eventToPost) {
        this.eventToPost = eventToPost;
    }

    /**
     * Getter for the message type
     *
     * @return The message type
     */
    @Override
    public MessageType getMessageType() {
        return MessageType.POST_EVENT_MESSAGE;
    }

    /**
     * Getter for the wrapped event to post
     *
     * @return The event to post wrapped in the PostedEventMessage
     */
    public Object getEventToPost() {
        return eventToPost;
    }
}
