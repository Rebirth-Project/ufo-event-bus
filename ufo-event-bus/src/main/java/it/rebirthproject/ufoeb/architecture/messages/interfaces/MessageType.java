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
package it.rebirthproject.ufoeb.architecture.messages.interfaces;

/**
 * An enum which contains all the possible messages types used by the eventbus architecture.
 */
public enum MessageType {
    REGISTER_LISTENER_MESSAGE,
    UNREGISTER_LISTENER_MESSAGE,
    SHUTDOWN_EXECUTOR,
    SHUTDOWN_STATE_MANAGER,
    POST_EVENT_MESSAGE,
    POST_STICKY_EVENT_MESSAGE,
    EXECUTE_MESSAGE,
    PRINT_STATE,
    REMOVE_STICKY_EVENT_MESSAGE,
    CLEAR_ALL_STICKY_EVENTS_MESSAGE,
    IS_LISTENER_REGISTERED_MESSAGE;
}
