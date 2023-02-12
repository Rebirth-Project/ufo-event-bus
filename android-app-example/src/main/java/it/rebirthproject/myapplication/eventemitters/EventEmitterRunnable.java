/*
 * Copyright (C) 2021/2023 Andrea Paternesi Rebirth project
 * Copyright (C) 2021/2023 Matteo Veroni Rebirth project
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

package it.rebirthproject.myapplication.eventemitters;

import android.util.Log;
import it.rebirthproject.myapplication.events.EventMessage;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;

import java.util.UUID;

public class EventEmitterRunnable implements Runnable {

    private final EventBus eventBus;
    private boolean loop = true;

    public EventEmitterRunnable(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void stop() {
        loop = false;
    }

    @Override
    public void run() {
        while (loop) {
            try {
                Thread.sleep(3000);
                eventBus.post(new EventMessage(String.format("%s", UUID.randomUUID())));
            } catch (Exception e) {
                Log.i("ERROR", e.getMessage());
            }
        }
    }
}
