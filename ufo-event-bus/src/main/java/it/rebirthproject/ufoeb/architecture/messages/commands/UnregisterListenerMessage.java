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
 * An internal message sent when the unregister method of the {@link EventBus}
 * is invoked to unregister a listener. The {@link UnregisterListenerMessage} is
 * sent to the {@link BusMemoryStateManager} which will consume it and
 * unregister the listener from the {@link MemoryState}.
 * The {@link UnregisterListenerMessage} wraps the listener to unregister.
 *
 * @see EventBus
 * @see BusMemoryStateManager
 * @see MemoryState
 */
public class UnregisterListenerMessage extends AbstractCommandMessage {

    /**
     * The listener to unregister
     */
    private final Object listenerToUnregister;

    /**
     * The constructor to build a {@link UnregisterListenerMessage}
     *
     * @param listenerToUnregister The listener to unregister
     */
    public UnregisterListenerMessage(Object listenerToUnregister) {
        this.listenerToUnregister = listenerToUnregister;
    }

    /**
     * Getter for the message type
     *
     * @return The message type
     */
    @Override
    public MessageType getMessageType() {
        return MessageType.UNREGISTER_LISTENER_MESSAGE;
    }

    /**
     * Getter for the wrapped listener to unregister
     *
     * @return The listener to unregister wrapped in the
     * {@link UnregisterListenerMessage}
     */
    public Object getListenerToUnregister() {
        return listenerToUnregister;
    }
}
