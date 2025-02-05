/*
 * Copyright (C) 2021/2025 Andrea Paternesi Rebirth project
 * Modifications copyright (C) 2021/2025 Matteo Veroni Rebirth project
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
package it.rebirthproject.ufoeb.dto;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import java.util.Objects;

/**
 * A key for events used by the {@link EventBus}' maps internally
 *
 * @see EventBus
 */
public final class BusEventKey {

    /**
     * The class of the event
     */
    private final Class<?> eventClass;

    /**
     * The constructor to create a BusEventKey
     *
     * @param eventClass The class of the event
     */
    public BusEventKey(Class<?> eventClass) {
        this.eventClass = eventClass;
    }

    /**
     * Get the class of the event
     *
     * @return The class of the event
     */
    public Class<?> getEventClass() {
        return eventClass;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.eventClass);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BusEventKey other = (BusEventKey) obj;
        return Objects.equals(this.eventClass, other.eventClass);
    }
}
