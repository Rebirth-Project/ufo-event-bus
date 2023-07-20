package it.rebirthproject.ufoeb.services;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassProcessableServiceTest {

    private static final String EMPTY_INHERITANCE_PACKAGE_FRONTIER_PATH = "";

    private ClassProcessableService classProcessableService;

    @ParameterizedTest
    @ValueSource(strings = {"it.rebirthproject.ufoeb.services", "it.rebirthproject.ufoeb", "it.rebirthproject", "it", ""})
    public void test_this_class_is_processable_by_frontier_path(String processableFrontierPath) {
        classProcessableService = new ClassProcessableService(processableFrontierPath);

        boolean isClassProcessableByPackage = classProcessableService.isClassProcessableByPackage(this.getClass().getPackageName());

        assertTrue(isClassProcessableByPackage, "Error this class should be processable but it isn't");
    }

    @ParameterizedTest
    @ValueSource(strings = {"java.util.List", "java.", "javax.", "android.", "java.util.List", "javax.lang.model.util.ElementFilter", "android.view.View", "it.rebirthproject.ufoeb.services.xyz", "abc"})
    public void test_this_class_is_not_processable_by_frontier_path(String notProcessableFrontierPath) {
        classProcessableService = new ClassProcessableService(notProcessableFrontierPath);

        boolean isClassProcessableByPackage = classProcessableService.isClassProcessableByPackage(this.getClass().getPackageName());

        assertFalse(isClassProcessableByPackage, "Error this class should not be processable but it is");
    }

    @ParameterizedTest
    @ValueSource(strings = {"java.util.List", "java.util.List", "javax.lang.model.util.ElementFilter", "android.view.View"})
    public void test_internal_java_classes_not_processable_if_package_frontier_path_is_empty(String internalJavaClass) {
        classProcessableService = new ClassProcessableService(EMPTY_INHERITANCE_PACKAGE_FRONTIER_PATH);

        boolean isClassProcessableByPackage = classProcessableService.isClassProcessableByPackage(internalJavaClass);

        assertFalse(isClassProcessableByPackage, "Error the internal java class " + internalJavaClass + " should not be processable but it is");
    }
}