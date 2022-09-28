/*
 * Copyright (C) 2021 Andrea Paternesi Rebirth project
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
package it.rebirthproject.ufoeb.architecture.messages.query;

import it.rebirthproject.ufoeb.architecture.messages.interfaces.MessageType;
import it.rebirthproject.ufoeb.architecture.messages.interfaces.AbstractQueryMessage;

/**
 * An internal message sent from the event bus infrastructure to the bus memory
 * state when a query regarding a listener registration is sent. The
 * IsListenerRegisteredMessage wraps the listener to check if registered. The bus state
 * manager will consume each PostEventMessage and send it to the workers queue
 * to process the event.
 */
public class IsListenerRegisteredMessage extends AbstractQueryMessage<Boolean> {

     /**
     * The listener to check
     */
    private final Object listenerToCheck;

    /**
     * The constructor to build an IsListenerRegisteredMessage
     *
     * @param listenerToCheck The listener to check     
     */
    public IsListenerRegisteredMessage(Object listenerToCheck) {
        this.listenerToCheck = listenerToCheck;
    }

    /**
     * Getter for the message type
     *
     * @return The message type
     */
    @Override
    public MessageType getMessageType() {
        return MessageType.IS_LISTENER_REGISTERED_MESSAGE;
    }

    /**
     * Getter for the wrapped listener to check
     *
     * @return The listener to check
     */
    public Object getListenerToCheck() {
        return listenerToCheck;
    }
}
