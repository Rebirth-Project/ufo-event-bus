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
package it.rebirthproject.ufoeb.architecture.eventbus;

import it.rebirthproject.ufoeb.eventannotation.Listen;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import java.util.concurrent.Future;

/**
 * UFO EventBus is a publish/subscribe event system.
 * Events are posted to the bus ({@link #post(Object)}), which delivers them to subscribers (listeners) that have a matching handler
 * method for the event type.
 * To receive events, listeners must register themselves to the bus using {@link #register(Object)}.
 * Once registered, listeners receive events until {@link #unregister(Object)} is called.
 * Event handling methods of listeners must be annotated by {@link Listen}, must be public, return nothing (void),
 * and have exactly one parameter (the event).
 *
 * <p>
 * Example:
 * <pre>
 * Class MagazineReader wants to receive MagazineNews events, so this could be a stub of the class:
 *
 * public class MagazineReader {
 *      {@literal @}Listen
 *      public void listenOnMagazineNews(MagazineNewsEvent event) {
 *          System.out.println("A news arrived: " + event.getNews());
 *      }
 * }
 *
 * Class MagazineNewsEvent can be a simple POJO used to carry event's metadata, like this one:
 *
 * public class MagazineNewsEvent {
 *
 *      private final String newsMessage;
 *
 *      public MagazineNewsEvent(String newsMessage) {
 *          this.newsMessage = newsMessage;
 *      }
 *
 *      public String getNews() {
 *          return newsMessage;
 *      }
 * }
 *
 * The MagazineReader instance must be registered to {@link EventBus} to receive news in his listenOnMagazineNews method.
 *
 * public static void main(String ... args) {
 *     MagazineReader magazineReader = new MagazineReader();
 *     eventBus.register(magazineReader);
 *     ...
 *     eventBus.post(new MagazineNewsEvent("Hello Readers!!! Something new just happened..."));
 *     ...
 * }
 * </pre>
 * </p>
 *
 * @see Listen
 *
 * @author Andrea Paternesi
 * @author Matteo Veroni
 */
public interface EventBus {

    // Commands

    /**
     * Registers a listener to the eventbus to receive specific events. Listeners must call {@link #unregister(Object)}
     * once they are no longer interested in receiving events.
     * Listeners event handling methods must be annotated with the {@link Listen} annotation.
     *
     * @param listenerToRegister The listener to register
     * @throws EventBusException If the listener is null or something fails while registering
     */
    public void register(Object listenerToRegister) throws EventBusException;

    /**
     * Unregisters the given listener from the eventbus.
     *
     * @param listenerToUnregister The listener to unregister
     * @throws EventBusException If the given listener is null or something fails while unregistering
     * @see #register(Object)
     */
    public void unregister(Object listenerToUnregister) throws EventBusException;

    /**
     * Posts the given event to the eventbus. This event will always be notified to and listened by registered listeners.
     * Depending on which inheritance policy is chosen during bus initialization and on which event class/interface is listened by the listeners,
     * listeners could get other events too. For example if an event extends a superclass or implements an interface.
     * For a detailed explanation about inheritance policies refers to {@link EventBusBuilder EventBusBuilder documentation}
     *
     * @param event The event to post
     * @throws EventBusException If the event is null or some internal error occurs while posting the event
     */
    public void post(Object event) throws EventBusException;

    /**
     * Posts the given event to the eventbus that saves it (because it's sticky). Once new listeners register to
     * the same sticky event they will be notified about the sticky event on registration. In this way it's possible to not lose
     * events which are being already sent before the listener is being registered.
     *
     * @param event The 'sticky' event to post
     * @throws EventBusException If the sticky event is null or some internal error occurs while posting the sticky event
     */
    public void postSticky(Object event) throws EventBusException;

    /**
     * Removes a sticky event from the eventbus.
     *
     * @param event The sticky event to remove
     * @throws EventBusException If the sticky event is null or some internal error occurs while posting the sticky event
     * @see #postSticky(Object)
     */
    public void removeSticky(Object event) throws EventBusException;

    /**
     * Removes a sticky event from the eventbus given the event type.
     *
     * @param eventClass The class of a sticky event to remove
     * @throws EventBusException If the class of the sticky event to remove is null or some internal error occurs while posting the sticky event
     * @see #postSticky(Object)
     */
    public void removeSticky(Class<?> eventClass) throws EventBusException;

    /**
     * Removes all the sticky events from eventbus.
     *
     * @throws EventBusException If some error occurs while removing all the sticky events
     */
    public void removeAllSticky() throws EventBusException;

    /**
     * Tells the bus to log its memory state for debug pourpose.
     *      
     * @throws EventBusException 
     */
    public void printBusState() throws EventBusException;
    
    /**
     * Shutdown the eventbus infrastructure. This is needed to free memory and to stop background workers behind the scenes.
     * Note: Make sure to call this method when there is no more need for an eventbus in your application, so this can be
     * usually done before the application shutdown.
     */
    public void shutdownBus();

    // Queries

    /**
     * Checks if the passed object is already registered in the eventbus for listening to events.
     *
     * @param possibleRegisteredListener An object which represents a possible registered listener
     * @return A future wrapping a boolean value which will be true if the object passed to the method is a listener for some events or false otherwise
     * @throws EventBusException If the possibleRegisteredListener is null or some error occurs during isRegistered method execution
     * @see #register(Object)
     */
    public Future<Boolean> isRegistered(Object possibleRegisteredListener) throws EventBusException;

}
