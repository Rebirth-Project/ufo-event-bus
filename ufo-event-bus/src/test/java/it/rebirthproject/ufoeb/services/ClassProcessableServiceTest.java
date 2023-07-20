package it.rebirthproject.ufoeb.services;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ClassProcessableServiceTest {

    private static final String REBIRTH_INHERITANCE_PACKAGE_FRONTIER_PATH = "it.rebirthproject";
    private static final String EMPTY_INHERITANCE_PACKAGE_FRONTIER_PATH = "";

    private ClassProcessableService classProcessableService;

    @ParameterizedTest
    @ValueSource(strings = {"java.", "javax.", "android.", "java.util.List", "android.view.View"})
    public void test_not_processable_classes_by_package_with_rebirth_inheritance_package_frontier_path(String notProcessablePackage) {
        boolean isClassProcessableByPackage = is_class_processable_by_package(REBIRTH_INHERITANCE_PACKAGE_FRONTIER_PATH, notProcessablePackage);

        assertFalse(isClassProcessableByPackage, "Error the class should not be processable but it is");
    }

    @ParameterizedTest
    @ValueSource(strings = {"java.", "javax.", "android.", "java.util.List", "android.view.View"})
    public void test_not_processable_classes_by_package_with_empty_inheritance_package_frontier_path(String notProcessablePackage) {
        boolean isClassProcessableByPackage = is_class_processable_by_package(EMPTY_INHERITANCE_PACKAGE_FRONTIER_PATH, notProcessablePackage);

        assertFalse(isClassProcessableByPackage, "Error the class should not be processable but it is");
    }

    private boolean is_class_processable_by_package(String inheritancePackageFrontierPath, String packageName) {
        classProcessableService = new ClassProcessableService(inheritancePackageFrontierPath);

        return classProcessableService.isClassProcessableByPackage(packageName);
    }

}