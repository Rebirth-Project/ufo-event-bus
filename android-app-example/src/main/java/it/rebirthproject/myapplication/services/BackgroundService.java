/*
 * Copyright (C) 2021/2022 Andrea Paternesi Rebirth project
 * Copyright (C) 2021/2022 Matteo Veroni Rebirth project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.* See the License for the specific language governing permissions and* limitations under the License.
 */

package it.rebirthproject.myapplication.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import it.rebirthproject.myapplication.eventemitters.EventEmitterRunnable;
import it.rebirthproject.ufoeb.architecture.eventbus.GlobalEventBus;

public class BackgroundService extends Service {

    private EventEmitterRunnable eventEmitterRunnable;
    private Thread eventEmitterThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ClearService", "Service Started");
        if (eventEmitterRunnable == null && eventEmitterThread == null) {
            startEventEmitter();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopEventEmitterAndShutdownEventBus();
        Log.d("ClearService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i("ClearService", "END");
        stopSelf();
    }

    private void startEventEmitter() {
        try {
            eventEmitterRunnable = new EventEmitterRunnable(GlobalEventBus.getInstance());
            eventEmitterThread = new Thread(eventEmitterRunnable);
            eventEmitterThread.start();
        } catch (Exception ex) {
            Log.e("ERROR", ex.getMessage());
        }
    }

    private void stopEventEmitterAndShutdownEventBus() {
        try {
            eventEmitterRunnable.stop();
            eventEmitterThread.join();
            GlobalEventBus.getInstance().shutdownBus();
        } catch (Exception ex) {
            Log.e("ERROR", ex.getMessage());
        }
    }
}
