package it.rebirthproject.ufoeb.endtoend.dto.objectstoregister;

import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.endtoend.dto.events.Event1;
import it.rebirthproject.ufoeb.endtoend.dto.events.Event2;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Listener1ChainOfEvents {
    private final CountDownLatch countDownLatch;
    private final EventBus eventBus;
    private final List<Object> events;

    private Event2 event2;

    public Listener1ChainOfEvents(CountDownLatch countDownLatch, EventBus eventBus, List<Object> events) {
        this.countDownLatch = countDownLatch;
        this.eventBus = eventBus;
        this.events = events;
    }

    @Listen
    public void onEvent(Event1 event1) throws EventBusException {
        events.add(event1);
        event2 = new Event2();
        eventBus.post(event2);
        countDownLatch.countDown();
    }

    public Event2 getEvent2() {
        return event2;
    }
}