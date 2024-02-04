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
import it.rebirthproject.ufoeb.architecture.state.MemoryState;

/**
 * An internal message sent from the bus infrastructure to the {@link EventBus}' {@link MemoryState} when a new listener is registered to an event.
 * The {@link RegisterMessage} wraps the listener to register.
 * This message is sent from the bus infrastructure to the message queue used by the bus state manager.
 * The bus state manager will consume each {@link RegisterMessage} and populate his internal data structures using the listenerToRegister
 * object (actual listener) wrapped in the message.
 *
 * @see EventBus
 * @see MemoryState
 */
public class RegisterMessage extends AbstractCommandMessage {

    /**
     * The listener to register
     */
    private final Object listenerToRegister;

    /**
     * The constructor to build a {@link RegisterMessage}
     *
     * @param listenerToRegister The listener to register
     */
    public RegisterMessage(Object listenerToRegister) {
        this.listenerToRegister = listenerToRegister;
    }

    /**
     * Getter for the message type
     *
     * @return The message type
     */
    @Override
    public MessageType getMessageType() {
        return MessageType.REGISTER_LISTENER_MESSAGE;
    }

    /**
     * Getter for the wrapped listener to register
     *
     * @return The listener to register wrapped in the RegisterEventMessage
     */
    public Object getListenerToRegister() {
        return listenerToRegister;
    }
}
