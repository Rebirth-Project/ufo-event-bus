/*
 * Copyright (C) 2021/2025 Andrea Paternesi Rebirth project
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
package it.rebirthproject.eventbusdemo.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import it.rebirthproject.eventbusdemo.core.screen.MainWindowScreen;
import it.rebirthproject.eventbusdemo.core.eventemitter.EventEmitterRunnable;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBusBuilder;

public class EventBusDemo extends Game implements Disposable {

    private EventBus eventBus;

    @Override
    public void create() {
        eventBus = new EventBusBuilder()
                .setNumberOfWorkers(1)
                .build();
        EventEmitterRunnable eventEmitterRunnable = new EventEmitterRunnable(eventBus);
        setScreen(new MainWindowScreen(eventBus, eventEmitterRunnable));
        Thread eventEmitterThread = new Thread(eventEmitterRunnable);
        eventEmitterThread.start();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        eventBus.shutdownBus();
        Gdx.app.exit();
        System.exit(0);
    }
}
