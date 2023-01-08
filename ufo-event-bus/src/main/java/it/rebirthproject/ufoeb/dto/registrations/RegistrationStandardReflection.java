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
package it.rebirthproject.ufoeb.dto.registrations;

import java.lang.reflect.Method;

/**
 * DTO Representing all the data of a listener to register in the system.
 */
public class RegistrationStandardReflection extends Registration {
    
    /**
     * @param listener The listener of the {@link Registration}
     * @param method   The callback method to invoke when a new notification for the listener needs to be delivered
     * @param priority The priority of the {@link Registration}
     */
    public RegistrationStandardReflection(Object listener, Method method, int priority) {
        this.listener = listener;
        this.method = method;       
        this.priority = priority;
    }
    
    @Override
    public void process(Object event) throws Exception {
        method.invoke(listener, event);
    }
}
