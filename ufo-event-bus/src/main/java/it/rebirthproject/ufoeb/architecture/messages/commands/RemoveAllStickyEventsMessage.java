/*
 * Copyright (C) 2021/2024 Andrea Paternesi Rebirth project
 * Modifications copyright (C) 2021/2024 Matteo Veroni Rebirth project
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
 * An internal message sent when all sticky events must to be removed from the {@link EventBus}'
 * {@link MemoryState}. The {@link RemoveAllStickyEventsMessage} is sent to the
 * {@link BusMemoryStateManager} which will consume it and remove all sticky
 * events.
 *
 * @see EventBus
 * @see BusMemoryStateManager
 * @see MemoryState
 */
public class RemoveAllStickyEventsMessage extends AbstractCommandMessage {

    /**
     * The constructor to build a {@link RemoveAllStickyEventsMessage}
     */
    public RemoveAllStickyEventsMessage() {
    }

    /**
     * Getter for the message type
     *
     * @return The message type
     */
    @Override
    public MessageType getMessageType() {
        return MessageType.CLEAR_ALL_STICKY_EVENTS_MESSAGE;
    }
}
