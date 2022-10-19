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
package it.rebirthproject.ufoeb.architecture.messages.commands;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.architecture.messages.interfaces.AbstractCommandMessage;
import it.rebirthproject.ufoeb.architecture.messages.interfaces.MessageType;
import it.rebirthproject.ufoeb.architecture.state.BusMemoryStateManager;
import it.rebirthproject.ufoeb.architecture.state.MemoryState;

//TODO this could be a query instead of a command...pheraps
/**
 * An internal message sent from the event bus infrastructure to the {@link EventBus}'
 * {@link MemoryState} when you want to print the bus internal state.
 * 
 * @see EventBus
 * @see BusMemoryStateManager
 * @see MemoryState
 */
public class PrintStateMessage extends AbstractCommandMessage {   
    /**
     * Getter for the message type
     *
     * @return The message type
     */
    @Override
    public MessageType getMessageType() {
        return MessageType.PRINT_STATE;
    }
}
