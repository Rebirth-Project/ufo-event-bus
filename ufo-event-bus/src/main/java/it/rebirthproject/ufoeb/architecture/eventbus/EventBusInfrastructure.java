/*
 * Copyright (C) 2021/2022 Matteo Veroni Rebirth project
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
package it.rebirthproject.ufoeb.architecture.eventbus;

import it.rebirthproject.ufoeb.architecture.executor.EventExecutor;
import it.rebirthproject.ufoeb.architecture.messages.commands.ShutdownStateManagerMessage;
import it.rebirthproject.ufoeb.architecture.messages.interfaces.Message;
import it.rebirthproject.ufoeb.architecture.state.BusMemoryStateManager;
import it.rebirthproject.ufoeb.architecture.state.MemoryState;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import it.rebirthproject.ufoeb.eventinheritancepolicy.base.InheritancePolicy;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import it.rebirthproject.ufoeb.services.ListenerMethodFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;

/**
 * The data structures composing the infrastructure of the {@link EventBus}.
 */
final class EventBusInfrastructure {

    /**
     * The logger used by this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(EventBusInfrastructure.class);
    /**
     * An {@link ExecutorService} used as a thread pool of {@link EventExecutor}
     * workers.
     */
    private final ExecutorService workersPoolExecutor;
    /**
     * A {@link Thread} used by the {@link BusMemoryStateManager} to work
     * asynchronously.
     */
    private final Thread busMemoryStateManagerThread;    
    /**
     * A blocking queue used to communicate internal messages to other
     * {@link EventBus}'s components.
     */
    private final BlockingQueue<Message> messageQueue;
    /**
     * The number of workers ({@link EventExecutor}s) used by the bus behind the
     * scenes to deliver events.
     */
    private final int numberOfWorkers;

    /**
     * The constructor used to build the {@link EventBusInfrastructure}
     *
     * @param listenerMethodFinder The {@link ListenerMethodFinder} is a service
     * used to retrieve registered listeners methods annotated with
     * {@link Listen} and to store them inside the {@link MemoryState}
     * @param inheritancePolicy The event {@link InheritancePolicy} used by the
     * bus
     * @param numberOfWorkers The number of workers ({@link EventExecutor}s)
     * used by the bus behind the scenes to deliver events.
     * @param safeRegistrationsListNeeded is used when you want to use
     * inheritance over a listener and all its superclasses. Enabling it will
     * let the bus look for all listeners' methods considering also all their
     * superclasses methods.
     * @param throwNoRegistrationsWarning A boolean which defines if it's needed
     * to throw warnings when no registrations are found for a specific event
     * @param verboseLogging Boolean parameter set to true if a more verbose
     * logging is needed
     * @see EventBusBuilder
     * @see ListenerMethodFinder
     * @see InheritancePolicy
     */
    EventBusInfrastructure(ListenerMethodFinder listenerMethodFinder, InheritancePolicy inheritancePolicy, int queueLength, int numberOfWorkers, boolean safeRegistrationsListNeeded, boolean throwNoRegistrationsWarning, boolean verboseLogging) {
        this.messageQueue = new LinkedBlockingQueue<>(queueLength);
        this.numberOfWorkers = numberOfWorkers;
        this.workersPoolExecutor = Executors.newFixedThreadPool(numberOfWorkers);
        MemoryState memoryState = new MemoryState(safeRegistrationsListNeeded, inheritancePolicy, verboseLogging);
        BusMemoryStateManager busMemoryStateManager = new BusMemoryStateManager(messageQueue, workersPoolExecutor, memoryState, listenerMethodFinder, throwNoRegistrationsWarning);
        this.busMemoryStateManagerThread = new Thread(busMemoryStateManager);
    }

    /**
     * The method used to start up the {@link EventBusInfrastructure}
     */
    void startup() {
        logger.debug("nr workers {}", numberOfWorkers);
        busMemoryStateManagerThread.start();
    }

    /**
     * The method used to send messages to other internal components
     *
     * @param message The command/query message to send to other internal
     * components
     * @throws EventBusException if something goes wrong sending an internal
     * message
     */
    void sendMessage(Message message) throws EventBusException {
        try {
            messageQueue.put(message);
        } catch (InterruptedException ex) {
            throw new EventBusException("Error", ex);
        }
    }

    /**
     * The method used to shut down the {@link EventBus}. The
     * {@link EventBusInfrastructure} will dispose his data structures and send
     * a message to all the others internal components (like
     * {@link EventExecutor}s workers and {@link BusMemoryStateManager}) in
     * order to shut down.
     */
    void shutdown() {
        logger.debug("Shutting down command for the bus system");
        try {
            sendShutdownStateManagerMessage();
            //countDownLatch.await();
            busMemoryStateManagerThread.join();
            shutdownAndAwaitTermination(workersPoolExecutor);
//            workersPoolExecutor.shutdownNow();
            
        } catch (InterruptedException ex) {
            logger.error("Error during the shutdown", ex);
        }

    }

    void shutdownAndAwaitTermination(ExecutorService pool) {
        // Disable new tasks from being submitted
        pool.shutdown();
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                // Cancel currently executing tasks forcefully
                pool.shutdownNow();
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ex) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Private utility method to send a {@link ShutdownStateManagerMessage} to
     * the {@link BusMemoryStateManager}
     */
    private void sendShutdownStateManagerMessage() {
        try {
            ShutdownStateManagerMessage message = new ShutdownStateManagerMessage();
            messageQueue.put(message);
            logger.debug("Put into eventsQueue: {}", message);
        } catch (InterruptedException e) {
            logger.error("Error", e);
        }
    }
}
