/*
 * Copyright (C) 2022 Andrea Paternesi Rebirth project
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
package it.rebirthproject.ufoeb.testutils.verifiers;

import it.rebirthproject.ufoeb.testutils.validators.Validator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnorderedListVerifier<E, V extends Validator<E>> {

    public final void assertAsExpected(List<E> list, List<V> elementValidators) throws Exception {
        assertEquals(elementValidators.size(), list.size(), "The number of elements was different from expectations.");

        for (E element : list) {
            int numberOfEceptionsSearchingElement = 0;
            for (V elementValidator : elementValidators) {
                try {
                    elementValidator.assertValid(element);
                } catch (Exception ex) {
                    numberOfEceptionsSearchingElement++;
                }
            }
            if (numberOfEceptionsSearchingElement == elementValidators.size()) {
                throw new Exception("I did not find this element in the list as expected." + element);
            }
        }
    }
}
