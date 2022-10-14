/*
 * Copyright (C) 2021/2022 Matteo Veroni Rebirth project
 * Modifications copyright (C) 2021/2022 Andrea Paternesi Rebirth project
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
package it.rebirthproject.ufoeb.architecture.state.dto.inheritancetest.objectstoregister;

import it.rebirthproject.ufoeb.architecture.state.dto.inheritancetest.events.*;
import it.rebirthproject.ufoeb.eventannotation.Listen;

public class Listener {

    @Listen
    public void method1(EventInterface1 event) {
    }

    @Listen
    public void method2(Event1 event) {
    }

    @Listen
    public void method3(Event2 event) {
    }

    @Listen
    public void method4(Event3 event) {
    }

    @Listen
    public void method5(Event4 event) {
    }
}
