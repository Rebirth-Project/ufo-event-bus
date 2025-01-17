/*
 * Copyright (C) 2012-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 * Modifications copyright (C) 2021/2025 Andrea Paternesi Rebirth project
 * Modifications copyright (C) 2021/2025 Matteo Veroni Rebirth project
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
package it.rebirthproject.ufoeb.eventannotation;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation must be applied to methods of registered listeners to {@link EventBus}. Those methods will be used by
 * the bus system as callbacks to notify listeners about new events of the requested type.
 * Method annotated with {@link Listen} must be public, return nothing (void), and have exactly one parameter (the event).
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
 * @see EventBus
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Listen {

    /**
     * Listener priority to influence the order of event delivery. Higher
     * priority listeners will receive events before others with a lower
     * priority. The default priority is 0 (lowest priority).
     *
     * @return the listener priority.
     */
    public int priority() default 0;
}
