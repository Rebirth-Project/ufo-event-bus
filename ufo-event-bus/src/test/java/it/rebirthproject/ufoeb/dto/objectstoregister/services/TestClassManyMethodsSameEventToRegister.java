/*
 * Copyright (C) 2021 Andrea Paternesi Rebirth project
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
package it.rebirthproject.ufoeb.dto.objectstoregister.services;

import it.rebirthproject.ufoeb.dto.events.TestEvent1;
import it.rebirthproject.ufoeb.eventannotation.Listen;

public class TestClassManyMethodsSameEventToRegister {

    @Listen()
    public void methodToRegister1(TestEvent1 testEvent) {
    }

    @Listen()
    public void methodToRegister2(TestEvent1 testEvent) {
    }

    @Listen()
    public void methodToRegister3(TestEvent1 testEvent) {
    }

    @Listen()
    public void methodToRegister4(TestEvent1 testEvent) {
    }

    @Listen()
    public void methodToRegister5(TestEvent1 testEvent) {
    }
}
