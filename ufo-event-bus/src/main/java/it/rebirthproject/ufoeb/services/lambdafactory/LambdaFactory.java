/*
 * Copyright (C) 2021 Andrea Paternesi Rebirth project
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
package it.rebirthproject.ufoeb.services.lambdafactory;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class LambdaFactory {

    public static <T, V> Handler<T, V> create(Method method) throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle passedMethodHandle = lookup.unreflect(method);
        MethodType functionMethodType = MethodType.methodType(void.class, Object.class, Object.class);

        final CallSite site = LambdaMetafactory.metafactory(
                lookup,
                "invokeMethod",
                MethodType.methodType(Handler.class),
                functionMethodType,
                passedMethodHandle,
                passedMethodHandle.type());

        return (Handler<T, V>) site.getTarget().invokeExact();
    }
}
