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
package it.rebirthproject.ufoeb.services;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBusBuilder;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;

/**
 * The {@link ClassProcessableService} is a service used to tell is a class should 
 * be processable by reflection. 
 */
public class ClassProcessableService {

    /**
     * A default empty inheritance package frontier path
     */
    private static final String EMPTY_INHERITANCE_PACKAGE_FRONTIER_PATH = "";

    /**
     * Sets the package used to stop the iteration over classes while using
     * event inheritance. If a class belongs to the set package then the
     * iteration stops. This parameter must be used only when event inheritance
     * policy is enabled otherwise it is useless even if set.
     *
     * @see EventBusBuilder#setInheritancePackageFrontierPath(String)
     */
    private final String inheritancePackageFrontierPath;

    /**
     * Class constructor used to build a ListenerMethodFinder
     *
     * @param inheritancePackageFrontierPath Parameter used to initialize the
     * attribute {@link #inheritancePackageFrontierPath}
     */
    public ClassProcessableService(String inheritancePackageFrontierPath) {
        this.inheritancePackageFrontierPath = (inheritancePackageFrontierPath == null) ? EMPTY_INHERITANCE_PACKAGE_FRONTIER_PATH : inheritancePackageFrontierPath;
    }

    /**
     * This method is used to check if the listener class is processable
     * analyzing its package. There are two methods: - the check over a given
     * inheritance frontier path package (for example the package of the
     * application that uses the {@link EventBus}). - the check over hardcoded
     * basic java packages (that can depends even on Android platform). This
     * method is used only when the frontier path is not specified.
     *
     * @param className The listener class
     * @return A boolean that states if the loop must continue or not.
     */
    public boolean isClassProcessableByPackage(String className) {
        if (inheritancePackageFrontierPath.trim().isEmpty()) {
            return !className.startsWith("java.") && !className.startsWith("javax.") && !className.startsWith("android.") && !className.startsWith("androidx.");
        } else {
            return className.startsWith(inheritancePackageFrontierPath);
        }
    }
}
