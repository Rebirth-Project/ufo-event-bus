package it.rebirthproject.ufoeb.endtoend.dto.objectstoregister;

import it.rebirthproject.ufoeb.endtoend.dto.events.Event1;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Listener2OfEvent1 {

    private final CountDownLatch countDownLatch;
    private final List<Class> receivers;

    public Listener2OfEvent1(CountDownLatch countDownLatch, List<Class> receivers) {
        this.countDownLatch = countDownLatch;
        this.receivers = receivers;
    }

    @Listen
    public void onEvent(Event1 event1) {
        receivers.add(getClass());
        countDownLatch.countDown();
    }
}
