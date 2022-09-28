/*
 * Copyright (C) 2021 Andrea Paternesi Rebirth project
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
package it.rebirthproject.ufoeb.architecture.state;

import it.rebirthproject.ufoeb.architecture.messages.commands.PostEventMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.RemoveStickyEventMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.RegisterMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.PostStickyEventMessage;
import it.rebirthproject.ufoeb.architecture.messages.commands.UnregisterListenerMessage;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.architecture.executor.EventExecutor;
import it.rebirthproject.ufoeb.architecture.messages.interfaces.Message;
import it.rebirthproject.ufoeb.architecture.messages.query.IsListenerRegisteredMessage;
import it.rebirthproject.ufoeb.dto.BusEventKey;
import it.rebirthproject.ufoeb.dto.registrations.maps.interfaces.EventsRegistrationsMap;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import it.rebirthproject.ufoeb.services.ListenerMethodFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * The bus memory state manager which stores and manages events and listeners
 */
public class BusMemoryStateManager implements Runnable {

    /**
     * The logger used by this class
     */
    private static final Logger logger = LoggerFactory.getLogger(BusMemoryStateManager.class);

    /**
     * The message queue is used by the {@link BusMemoryStateManager} to receive
     * internal system messages.
     * <pre>example: (REGISTER_MESSAGE, UNREGISTER_MESSAGE, SHUTDOWN_STATE_MANAGER, etc... )</pre>
     * Once one of those messages is received, it will be handled accordingly.
     */
    private final BlockingQueue<Message> commandQueryMessageQueue;
    /**
     * This is the thread pool cache that handles thread
     * parallelism to obtain bus scalability.
     */   
    private final ExecutorService workersPoolExecutor;

    /**
     * A {@link CountDownLatch} used during the shutdown phase to notify when
     * the {@link BusMemoryStateManager} is shutting down.
     */
    private final CountDownLatch countDownLatch;

    /**
     * The {@link MemoryState} of the {@link EventBus}
     */
    private final MemoryState memoryState;

    /**
     * The {@link ListenerMethodFinder} is a service used to retrieve registered
     * listeners methods annotated with {@link Listen} and to store them inside
     * the {@link MemoryState}
     */
    private final ListenerMethodFinder listenerMethodFinder;

    /**
     * A boolean flag used to determine if a throwNoRegistrationsWarning should
     * be raised or not. If the flag is false no warning be will be thrown when
     * an event is posted and no listeners are registered to listen to that
     * precise event.
     */
    private final boolean throwNoRegistrationsWarning;

    /**
     * The constructor used to build the {@link BusMemoryStateManager}
     *
     * @param commandQueryMessageQueue The message queue is used by the
     * {@link BusMemoryStateManager} to receive internal system messages.
     * <pre>example: (REGISTER_MESSAGE, UNREGISTER_MESSAGE, SHUTDOWN_STATE_MANAGER, etc... )</pre>
     * Once one of those messages is received, it will be handled accordingly.          
     * @param workersPoolExecutor This is the thread pool cache that handles thread
     * parallelism to obtain bus scalability.
     * @param countDownLatch A countDownLatch used during the shutdown phase to
     * notify when the {@link BusMemoryStateManager} is shutting down.
     * @param memoryState The {@link MemoryState} of the {@link EventBus}
     * @param listenerMethodFinder The {@link ListenerMethodFinder} is a service
     * used to retrieve registered listeners methods annotated with
     * {@link Listen} and to store them inside the {@link MemoryState}
     * @param throwNoRegistrationsWarning A boolean flag used to determine if a
     * throwNoRegistrationsWarning should be raised or not. If the flag is false
     * no warning be will be thrown when an event is posted and no listeners are
     * registered to listen to that precise event.
     */
    public BusMemoryStateManager(BlockingQueue<Message> commandQueryMessageQueue, ExecutorService workersPoolExecutor, CountDownLatch countDownLatch, MemoryState memoryState, ListenerMethodFinder listenerMethodFinder, boolean throwNoRegistrationsWarning) {
        this.commandQueryMessageQueue = commandQueryMessageQueue;       
        this.workersPoolExecutor = workersPoolExecutor;
        this.countDownLatch = countDownLatch;
        this.memoryState = memoryState;
        this.listenerMethodFinder = listenerMethodFinder;
        this.throwNoRegistrationsWarning = throwNoRegistrationsWarning;
    }

    /**
     * This method is called when the {@link BusMemoryStateManager} (which
     * implements the {@link Runnable} java interface) starts his work after
     * being executed in a separated thread.
     */
    @Override
    public void run() {
        while (true) {
            try {
                Message message = commandQueryMessageQueue.take();
                switch (message.getMessageType()) {
                    case SHUTDOWN_STATE_MANAGER: {
                        logger.debug("Shutdown bus memory state manager");
                        countDownLatch.countDown();
                        return;
                    }
                    case REGISTER_LISTENER_MESSAGE: {
                        logger.debug("A new Registration arrived!!");
                        RegisterMessage registerMessage = (RegisterMessage) message;
                        listenerMethodFinder.findListenerMethods(registerMessage.getListenerToRegister(), memoryState);
                        EventsRegistrationsMap foundStickyEventsRegistrations = memoryState.getFoundListenerStickyEventsRegistrations();
                        for (BusEventKey busEventKey : foundStickyEventsRegistrations.keySet()) {
                            workersPoolExecutor.execute(new EventExecutor(foundStickyEventsRegistrations.get(busEventKey),memoryState.getStickyEvent(busEventKey)));
                        }
                        break;
                    }
                    case UNREGISTER_LISTENER_MESSAGE: {
                        UnregisterListenerMessage unregisterListenerMessage = (UnregisterListenerMessage) message;
                        memoryState.unregisterListener(unregisterListenerMessage.getListenerToUnregister());
                        break;
                    }
                    case POST_EVENT_MESSAGE: {
                        logger.debug("A new Post Arrived!!");
                        manageEventToPost(((PostEventMessage) message).getEventToPost());
                        break;
                    }
                    case POST_STICKY_EVENT_MESSAGE: {
                        Object eventObjectToPost = ((PostStickyEventMessage) message).getEventToPost();
                        memoryState.registerStickyEvent(new BusEventKey(eventObjectToPost.getClass()), eventObjectToPost);
                        manageEventToPost(eventObjectToPost);
                        break;
                    }
                    case REMOVE_STICKY_EVENT_MESSAGE: {
                        RemoveStickyEventMessage removeStickyMessage = (RemoveStickyEventMessage) message;
                        BusEventKey eventToRemove = new BusEventKey(removeStickyMessage.getEventClass());
                        memoryState.unregisterStickyEvent(eventToRemove);
                        logger.debug("Removed Sticky Event {}", eventToRemove.getEventClass());
                        break;
                    }
                    case CLEAR_ALL_STICKY_EVENTS_MESSAGE: {
                        memoryState.removeAllStickyEvents();
                        break;
                    }
                    case IS_LISTENER_REGISTERED_MESSAGE: {
                        IsListenerRegisteredMessage isListenerRegisteredMessage = (IsListenerRegisteredMessage) message;
                        if (memoryState.isListenerRegistered(isListenerRegisteredMessage.getListenerToCheck())) {
                            isListenerRegisteredMessage.complete(true);
                        } else {
                            isListenerRegisteredMessage.complete(false);
                        }
                        break;
                        //isListenerRegisteredMessage.completeWithException(new IllegalArgumentException("Eccezione!"));
                    }
                    case PRINT_STATE: {
                        memoryState.printState();
                        break;
                    }
                    default: {
                        //logger.debug("A command for the executor arrived. Sending to the executor!!");
                        //workerMessageQueue.put(message);
                        break;
                    }
                }
            } catch (EventBusException | InterruptedException ex) {
                logger.error("Something went wrong while elaborating bus messages", ex);
            }
        }
    }

    /**
     * Private method used to handle a message to post to {@link EventExecutor}s
     * workers. This method will save data into {@link #memoryState} if needed
     * and then call the {@link #postEvent(Object, Class)} method to notify an
     * event to {@link EventExecutor}s
     *
     * @param eventObjectToPost The event to post {@link EventExecutor}s
     * @throws InterruptedException If there is an exception trying to send an
     * event to workers.
     */
    private void manageEventToPost(Object eventObjectToPost) throws InterruptedException {
        Set<Class<?>> eventSuperClassesAndInterfacesList = memoryState.getEventSuperClassesAndInterfaces(eventObjectToPost);
        if (eventSuperClassesAndInterfacesList != null) {
            for (Class<?> eventClass : eventSuperClassesAndInterfacesList) {
                postEvent(eventObjectToPost, eventClass);
            }
        }
    }

    /**
     * Private method used to send an event to {@link EventExecutor}s workers
     *
     * @param eventObjectToPost The event to post to {@link EventExecutor}s
     * @param eventClass The event class
     * @throws InterruptedException If there is an exception trying to send an
     * event to workers.
     */
    private void postEvent(Object eventObjectToPost, Class<?> eventClass) throws InterruptedException {
        BusEventKey busEventKey = new BusEventKey(eventClass);
        if (memoryState.registrationMapContainsKey(busEventKey)) {
            workersPoolExecutor.execute(new EventExecutor(memoryState.getRegistrations(busEventKey),eventObjectToPost));
        } else {
            //we are in the case of a sticky event so if a sticky event is posted before any registrations we do nothing.
            //We can also be in the case of inheritance where superclasses or interfaces are not listened by anyone
            if (throwNoRegistrationsWarning) {
                logger.warn("No registrations found for this event: {}. Perhaps a sticky event was posted before a registration or event inheritance is active?", eventClass.getName());
            }
        }
    }
}
