/*
 * Copyright (C) 2021/2022 Andrea Paternesi Rebirth project
 * Modifications copyright (C) 2021/2022 Matteo Veroni Rebirth project
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.BlockingQueue;

public class FakeMessageEmitter {

    private static final Logger logger = LoggerFactory.getLogger(FakeMessageEmitter.class);

    private final BlockingQueue<Message> messageQueue;

    public FakeMessageEmitter(BlockingQueue<Message> messageQueue) {
        this.messageQueue = messageQueue;
    }

    public FakeMessageEmitter sendMessage(Message message) throws InterruptedException {
        logger.info("Add message => {}", message);
        messageQueue.put(message);
        logger.info("Add message done");
        return this;
    }

    public FakeMessageEmitter delay(long timeInMillis) {
        logger.info("delay method called ({}ms)", timeInMillis);
        try {
            Thread.sleep(timeInMillis);
        } catch (InterruptedException ex) {
            logger.error("Error", ex);
        }
        logger.info("delay method done ({}ms)", timeInMillis);
        return this;
    }
}
