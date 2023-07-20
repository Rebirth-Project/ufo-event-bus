package it.rebirthproject.ufoeb.services;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ClassProcessableServiceTest {

    private ClassProcessableService classProcessableService;

    @ParameterizedTest
    @ValueSource(strings = {"java.", "javax.", "android."})
    public void x(String packageName) {
        classProcessableService = new ClassProcessableService("it.rebirthproject");
        boolean classProcessableByPackage = classProcessableService.isClassProcessableByPackage(packageName);

        assertFalse(classProcessableByPackage);
    }

}