/*
 * Copyright (C) 2021/2026 Matteo Veroni Rebirth project
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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClassProcessableServiceTest {

    private static final String EMPTY_INHERITANCE_PACKAGE_FRONTIER_PATH = "";
    private final String thisClassPackage;
    private ClassProcessableService classProcessableService;

    public ClassProcessableServiceTest() {
        thisClassPackage = this.getClass().getPackageName();
    }

    @ParameterizedTest
    @ValueSource(strings = {"it.rebirthproject.ufoeb.services", "it.rebirthproject.ufoeb", "it.rebirthproject", "it", ""})
    public void test_this_class_is_processable_by_frontier_path(String processableFrontierPath) {
        classProcessableService = new ClassProcessableService(processableFrontierPath);

        boolean isClassProcessableByPackage = classProcessableService.isClassProcessableByPackage(thisClassPackage);

        assertTrue(isClassProcessableByPackage, "Error this class should be processable but it isn't");
    }

    @ParameterizedTest
    @ValueSource(strings = {"java.", "javax.", "android.", "java.util.List", "javax.lang.model.util.ElementFilter", "android.view.View", "it.rebirthproject.ufoeb.services.xyz", "abc"})
    public void test_this_class_is_not_processable_by_frontier_path(String notProcessableFrontierPath) {
        classProcessableService = new ClassProcessableService(notProcessableFrontierPath);

        boolean isClassProcessableByPackage = classProcessableService.isClassProcessableByPackage(thisClassPackage);

        assertFalse(isClassProcessableByPackage, "Error this class should not be processable but it is");
    }

    @ParameterizedTest
    @ValueSource(strings = {"java.util.List", "javax.lang.model.util.ElementFilter", "android.view.View"})
    public void test_internal_java_classes_not_processable_if_package_frontier_path_is_empty(String internalJavaClass) {
        classProcessableService = new ClassProcessableService(EMPTY_INHERITANCE_PACKAGE_FRONTIER_PATH);

        boolean isClassProcessableByPackage = classProcessableService.isClassProcessableByPackage(internalJavaClass);

        assertFalse(isClassProcessableByPackage, "Error the internal java class " + internalJavaClass + " should not be processable but it is");
    }

    @Test
    public void should_NotProcessClassEndingWithObject_When_FrontierPathIsSet() {
        classProcessableService = new ClassProcessableService("it.rebirthproject.ufoeb");

        boolean isClassProcessableByPackage = classProcessableService.isClassProcessableByPackage("it.rebirthproject.ufoeb.Object");

        assertFalse(isClassProcessableByPackage, "Error class ending with .Object should not be processable when a frontier path is set");
    }
}
