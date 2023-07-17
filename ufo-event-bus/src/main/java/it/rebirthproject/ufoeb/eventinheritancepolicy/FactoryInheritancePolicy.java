/*
 * Copyright (C) 2021/2023 Andrea Paternesi Rebirth project
 * Modifications copyright (C) 2021/2023 Matteo Veroni Rebirth project
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
package it.rebirthproject.ufoeb.eventinheritancepolicy;

import it.rebirthproject.ufoeb.eventinheritancepolicy.base.InheritancePolicyType;
import it.rebirthproject.ufoeb.eventinheritancepolicy.policies.InterfaceEventInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.policies.NoEventInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.policies.ClassEventInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.policies.CompleteEventInheritancePolicy;
import it.rebirthproject.ufoeb.eventinheritancepolicy.base.InheritancePolicy;
import it.rebirthproject.ufoeb.services.ClassProcessableService;

/**
 * The factory used to create an {@link InheritancePolicy}. There are several
 * {@link InheritancePolicyType} to choose.
 *
 * @see InheritancePolicy
 * @see InheritancePolicyType
 * @see NoEventInheritancePolicy
 * @see InterfaceEventInheritancePolicy
 * @see ClassEventInheritancePolicy
 * @see CompleteEventInheritancePolicy
 */
public class FactoryInheritancePolicy {

    /**
     * The factory method used to create an {@link InheritancePolicy}
     *
     * @param inheritancePolicyType The chosen {@link InheritancePolicyType} to
     * create
     * @param classProcessableService The service that checks if you are extending or implementing a forbidden type
     * @return The created {@link InheritancePolicy} corresponding to the
     * specified {@link InheritancePolicyType}
     */
    public static InheritancePolicy createInheritancePolicy(InheritancePolicyType inheritancePolicyType, ClassProcessableService classProcessableService) {
        switch (inheritancePolicyType) {
            case COMPLETE_EVENT_INHERITANCE:
                return new CompleteEventInheritancePolicy(classProcessableService);
            case CLASS_EVENT_INHERITANCE:
                return new ClassEventInheritancePolicy(classProcessableService);
            case INTERFACE_EVENT_INHERITANCE:
                return new InterfaceEventInheritancePolicy(classProcessableService);
            case NO_EVENT_INHERITANCE:
            default:
                return new NoEventInheritancePolicy();
        }
    }
}
