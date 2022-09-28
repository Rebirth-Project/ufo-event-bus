package it.rebirthproject.ufoeb.endtoend.dto.objectstoregister;

import it.rebirthproject.ufoeb.endtoend.dto.events.Event1;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ListenerOfEvent1WithLowPriority {

    private final CountDownLatch countDownLatch;
    private final List<Class> receivers;

    public ListenerOfEvent1WithLowPriority(CountDownLatch countDownLatch, List<Class> receivers) {
        this.countDownLatch = countDownLatch;
        this.receivers = receivers;
    }

    @Listen(priority = 1)
    public void onEvent(Event1 event1) {
        receivers.add(getClass());
        countDownLatch.countDown();
    }
}
