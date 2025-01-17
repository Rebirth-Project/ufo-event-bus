/*
 * Copyright (C) 2021/2025 Matteo Veroni Rebirth project
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

public class ListVerifier<E, V extends Validator<E>> {

    public final void assertAsExpected(List<E> list, List<V> elementValidators) throws Exception {       
        assertEquals(elementValidators.size(), list.size(), "The number of elements was different from expectations.");

        for (int i = 0; i < list.size(); i++) {
            E element = list.get(i);
            V elementValidator = elementValidators.get(i);
            elementValidator.assertValid(element);
        }
    }
}