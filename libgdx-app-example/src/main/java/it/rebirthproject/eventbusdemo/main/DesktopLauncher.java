/*
 * Copyright (C) 2021/2023 Andrea Paternesi Rebirth project
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
package it.rebirthproject.eventbusdemo.main;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import it.rebirthproject.eventbusdemo.core.EventBusDemo;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
        public static void main (String[] arg) {
                Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
                config.setForegroundFPS(60);
                config.setTitle("UFO EventBus Demo");
                new Lwjgl3Application(new EventBusDemo(), config);
        }
}

