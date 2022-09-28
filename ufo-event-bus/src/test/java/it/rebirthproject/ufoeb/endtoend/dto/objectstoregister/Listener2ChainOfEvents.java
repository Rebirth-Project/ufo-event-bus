package it.rebirthproject.ufoeb.endtoend.dto.objectstoregister;

import it.rebirthproject.ufoeb.endtoend.dto.events.Event2;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import it.rebirthproject.ufoeb.exceptions.EventBusException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Listener2ChainOfEvents {
    private final CountDownLatch countDownLatch;
    private final List<Object> events;

    public Listener2ChainOfEvents(CountDownLatch countDownLatch, List<Object> events) {
        this.countDownLatch = countDownLatch;
        this.events = events;
    }

    @Listen
    public void onEvent(Event2 event2) throws EventBusException {
        events.add(event2);
        countDownLatch.countDown();
    }
}
