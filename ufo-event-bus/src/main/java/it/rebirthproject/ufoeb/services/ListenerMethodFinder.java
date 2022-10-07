/*
 * Copyright (C) 2012-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 * Modifications copyright (C) 2021 Andrea Paternesi Rebirth project
 * Modifications copyright (C) 2021 Matteo Veroni Rebirth project
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
import it.rebirthproject.ufoeb.architecture.state.MemoryState;
import it.rebirthproject.ufoeb.dto.BusEventKey;
import it.rebirthproject.ufoeb.dto.registrations.Registration;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.dto.registrations.RegistrationMethodHandler;
import it.rebirthproject.ufoeb.dto.registrations.RegistrationStandardReflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * The {@link ListenerMethodFinder} is a service used to retrieve registered
 * listeners methods annotated with {@link Listen} and to store them inside the
 * {@link MemoryState}
 */
public class ListenerMethodFinder {

    /**
     * In newer class files, compilers may add methods. Those are called bridge
     * or synthetic methods. UfoEventBus must ignore both. There modifiers are
     * not public but defined in the Java class file format:
     *
     * @see
     * <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.6-200-A.1">The
     * related javadoc</a>
     */
    private static final int BRIDGE = 0x40;
    /**
     * In newer class files, compilers may add methods. Those are called bridge
     * or synthetic methods. UfoEventBus must ignore both. There modifiers are
     * not public but defined in the Java class file format:
     *
     * @see
     * <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.6-200-A.1">The
     * related javadoc</a>
     */
    private static final int SYNTHETIC = 0x1000;
    /**
     * In newer class files, compilers may add methods. Those are called bridge
     * or synthetic methods. UfoEventBus must ignore both. There modifiers are
     * not public but defined in the Java class file format:
     *
     * @see
     * <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.6-200-A.1">The
     * related javadoc</a>
     */
    private static final int MODIFIERS_IGNORE = Modifier.ABSTRACT | Modifier.STATIC | BRIDGE | SYNTHETIC;
    /**
     * A default empty inheritance package frontier path
     */
    private static final String EMPTY_INHERITANCE_PACKAGE_FRONTIER_PATH = "";
    /**
     * This parameter should be used when you want to use inheritance over a
     * listener and all its superclasses. Enabling it will let the bus look for
     * all listeners' methods considering also all their superclasses methods.
     * The default value is to look for only the listener's class methods.
     *
     * @see EventBusBuilder#setListenerSuperclassInheritance()
     */
    private final boolean listenerSuperclassInheritance;
    /**
     * if set then an {@link EventBusException} is thrown when an invalid
     * {@link Listen} annotated method is found in a Listener.
     *
     * @see EventBusBuilder#setThrowNotValidMethodException()
     */
    private final boolean throwNotValidMethodException;
    /**
     * If a registering listener does not have any {@link Listen} annotated
     * method or, in case of event inheritance enabled, also its super classes
     * or interfaces does not have any {@link Listen} annotated method, then an
     * {@link EventBusException} is thrown.
     *
     * @see EventBusBuilder#setThrowNoListenerAnnotationException()
     */
    private final boolean throwNoListenerAnnotationException;
    /**
     * This set the usage of Lambdafactory instead of standard java reflection
     *
     * @see EventBusBuilder#setUseLambdaFactoryInsteadStandardReflection()
     */
    private final boolean useLambdaFactoryInsteadStandardReflection;
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
     * @param listenerSuperclassInheritance Parameter used to initialize the
     * attribute {@link #listenerSuperclassInheritance}
     * @param throwNotValidMethodException Parameter used to initialize the
     * attribute {@link #throwNotValidMethodException}
     * @param throwNoListenerAnnotationException Parameter used to initialize
     * the attribute {@link #throwNoListenerAnnotationException}
     * @param useLambdaFactoryInsteadStandardReflection Parameter used to
     * initialize the attribute
     * {@link #useLambdaFactoryInsteadStandardReflection}
     * @param inheritancePackageFrontierPath Parameter used to initialize the
     * attribute {@link #inheritancePackageFrontierPath}
     */
    public ListenerMethodFinder(boolean listenerSuperclassInheritance, boolean throwNotValidMethodException, boolean throwNoListenerAnnotationException, boolean useLambdaFactoryInsteadStandardReflection, String inheritancePackageFrontierPath) {
        this.listenerSuperclassInheritance = listenerSuperclassInheritance;
        this.throwNotValidMethodException = throwNotValidMethodException;
        this.throwNoListenerAnnotationException = throwNoListenerAnnotationException;
        this.useLambdaFactoryInsteadStandardReflection = useLambdaFactoryInsteadStandardReflection;
        this.inheritancePackageFrontierPath = (inheritancePackageFrontierPath == null) ? EMPTY_INHERITANCE_PACKAGE_FRONTIER_PATH : inheritancePackageFrontierPath;
    }

    /**
     * Method used to retrieve all the listener's methods annotated with the
     * {@link Listen} annotation and to update the {@link MemoryState}
     * accordingly.
     *
     * @param listenerToRegister The {@link Listen} annotated methods' listener.
     * @param memoryState The eventbus {@link MemoryState}
     * @throws EventBusException is thrown if some exception occurs during the
     * execution of this method
     */
    public void findListenerMethods(final Object listenerToRegister, final MemoryState memoryState) throws EventBusException {
        final Map<BusEventKey, Set<Method>> eventsMethodsMap = new HashMap<>();

        Class<?> clazz = listenerToRegister.getClass();
        String clazzName = clazz.getName();

        // need to iterate through hierarchy in order to retrieve methods from above the current instance
        while (isClassProcessableByPackage(clazzName)) {
            // iterate through the list of methods declared in the class represented by clazz variable, and add those annotated with the specified annotation
            for (final Method method : findAllMethods(clazz)) {
                int modifiers = method.getModifiers();
                if (method.isAnnotationPresent(Listen.class)) {
                    if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & MODIFIERS_IGNORE) == 0) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (parameterTypes.length == 1) {
                            //this is a valid method to scan
                            Listen listenerAnnotation = method.getAnnotation(Listen.class);
                            BusEventKey eventKey = new BusEventKey(parameterTypes[0]);

                            eventsMethodsMap.computeIfAbsent(eventKey, evtKey -> new HashSet<>());

                            Registration registration;
                            try {
                                if (useLambdaFactoryInsteadStandardReflection) {
                                    registration = new RegistrationStandardReflection(listenerToRegister, method, listenerAnnotation.priority());
                                } else {
                                    registration = new RegistrationMethodHandler(listenerToRegister, method, listenerAnnotation.priority());
                                }
                            } catch (Throwable ex) {
                                String methodName = method.getDeclaringClass().getName() + "." + method.getName();
                                throw new EventBusException("Could not create method handler for public method " + methodName + ".", ex);
                            }
                            memoryState.registerListener(eventKey, registration);
                        } else if (throwNotValidMethodException) {
                            String methodName = method.getDeclaringClass().getName() + "." + method.getName();
                            throw new EventBusException(methodName + " (@Listen) annotated method must have exactly 1 parameter but has " + parameterTypes.length + ".");
                        }
                    } else if (throwNotValidMethodException) {
                        String methodName = method.getDeclaringClass().getName() + "." + method.getName();
                        throw new EventBusException(methodName + " (@Listen) annotated method is not valid : must be public, non-static, and non-abstract.");
                    }
                }
            }

            if (listenerSuperclassInheritance) {
                // move to the upper class in the hierarchy in search for more methods
                clazz = clazz.getSuperclass();
                clazzName = clazz.getName();
            } else {
                break;
            }
        }

        if (throwNoListenerAnnotationException && eventsMethodsMap.isEmpty()) {
            throw new EventBusException("Listener " + listenerToRegister.getClass().getName() + " and its super classes have no public methods with the @Listen annotation.");
        }
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
    private boolean isClassProcessableByPackage(String className) {
        if (inheritancePackageFrontierPath.trim().isEmpty()) {
            return !className.startsWith("java.") && !className.startsWith("javax.") && !className.startsWith("android.") && !className.startsWith("androidx.");
        } else {
            return className.startsWith(inheritancePackageFrontierPath);
        }
    }

    /**
     * This method finds all methods defined in a given class.
     *
     * @param clazz The given class to search for methods.
     * @return The methods found in the given class.
     * @throws EventBusException If the reflection raises an exception, this
     * will be wrapped into an {@link EventBusException}
     */
    private Method[] findAllMethods(Class<?> clazz) throws EventBusException {
        Method[] methods;
        try {
            // This is faster than getMethods, especially when listeners are fat classes
            methods = clazz.getDeclaredMethods();
        } catch (SecurityException th) {
            // Workaround for java.lang.NoClassDefFoundError
            try {
                methods = clazz.getMethods();
            } catch (LinkageError error) { // super class of NoClassDefFoundError to be a bit more broad...
                throw new EventBusException("Could not inspect methods of " + clazz.getName(), error);
            }
        }
        return methods;
    }
}
