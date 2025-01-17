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
package it.rebirthproject.eventbusdemo.core.listener;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import it.rebirthproject.eventbusdemo.core.events.EventMessage;
import it.rebirthproject.ufoeb.eventannotation.Listen;

public class LabelListener extends Label {
    
    public LabelListener(CharSequence text, LabelStyle style) {
        super(text, style);
    }
    
    @Listen
    public void printEvent(EventMessage event) {
        setText("Received message number: " + event.getMessage());
    }
}
