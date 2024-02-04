/*
 * Copyright (C) 2021/2024 Andrea Paternesi Rebirth project
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * The abstract class defining a query message that can be used by the bus
 * infrastructure.
 * @param <T> The value passed to the complete action
 */
public abstract class AbstractQueryMessage<T> implements Message {

    /**
     * A completable future used to return some query value, for example a
     * boolean stating if a listener is registered to the bus.
     */
    private final CompletableFuture<T> futureResponse = new CompletableFuture<>();

    /**
     * Getter for the future response
     *
     * @return The future containing the query response value.
     */
    public Future<T> getResponse() {
        return futureResponse;
    }

    /**
     * A method useful to notify that an action is completed in the future
     * without exception
     * @param response The response value of the completed action
     */
    public void complete(T response) {
        futureResponse.complete(response);
    }

    /**
     * A method useful to notify that an action thrown exception while
     * completing.
     * @param e The exception thrown while completing the action
     */
    public void completeWithException(Exception e) {
        futureResponse.completeExceptionally(e);
    }

    /**
     * A method that serializes the message class name.
     */
    @Override
    public String toString() {
        return "Message: " + this.getClass().getName();
    }
}
