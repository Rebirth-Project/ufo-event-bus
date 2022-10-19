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
package it.rebirthproject.ufoeb.architecture.state.mock;

import it.rebirthproject.ufoeb.architecture.executor.EventExecutor;
import it.rebirthproject.ufoeb.architecture.messages.interfaces.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FakePoolExecutor extends ThreadPoolExecutor {
    
    private final List<Message> messagesList = new ArrayList<>();

    public FakePoolExecutor() {
        super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public List<Message> getReceivedMessageList() throws Exception {       
        return messagesList;
    }

    @Override
    public void execute(Runnable runnable) {
        EventExecutor executor = ((EventExecutor)runnable);
        messagesList.add(new FakeMessage(executor.getRegistrationList(),executor.getEventToPost()));
    }
}
