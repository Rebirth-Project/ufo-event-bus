/*
 * Copyright (C) 2012-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 * Modifications copyright (C) 2021/2025 Andrea Paternesi Rebirth project
 * Modifications copyright (C) 2021/2025 Matteo Veroni Rebirth project
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
package it.rebirthproject.ufoeb.exceptions;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;

/**
 * An {@link RuntimeException} thrown if something goes wrong using the {@link EventBus}.
 */
public class EventBusException extends RuntimeException {

    /**
     * Constructor for creating an EventBusException, passing a message for the exception
     *
     * @param detailMessage The message of the exception
     */
    public EventBusException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor for creating an {@link EventBusException}, passing a message for the exception and the concrete exception occurred behind the scenes
     *
     * @param detailMessage The message of the exception
     * @param throwable     The concrete exception which triggered the {@link EventBusException}
     */
    public EventBusException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
