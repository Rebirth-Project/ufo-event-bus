package it.rebirthproject.ufoeb.architecture.eventbus;

import it.rebirthproject.ufoeb.architecture.eventbus.dto.ListenerForBuilderDefaultsTest;
import it.rebirthproject.ufoeb.dto.events.TestEvent1;
import it.rebirthproject.ufoeb.dto.objectstoregister.services.TestClassToRegisterWithMethodWithTooManyParameters;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventBusBuilderDefaultsTest {

    @Test
    public void should_KeepWorking_When_ListenerWithoutListenMethodsIsRegisteredWithDefaultBuilderConfiguration() throws Exception {
        EventBus eventBus = new EventBusBuilder()
                .setNumberOfWorkers(1)
                .build();

        ListenerForBuilderDefaultsTest validListener = new ListenerForBuilderDefaultsTest();

        eventBus.register(new Object());
        eventBus.register(validListener);
        eventBus.post(new TestEvent1());

        boolean eventDelivered = validListener.awaitEvent(2, TimeUnit.SECONDS);
        eventBus.shutdownBus();

        assertTrue(eventDelivered, "Default EventBusBuilder configuration should not stop the bus when a listener without @Listen methods is registered.");
    }

    @Test
    public void should_KeepWorking_When_ListenerWithInvalidListenMethodIsRegisteredWithDefaultBuilderConfiguration() throws Exception {
        EventBus eventBus = new EventBusBuilder()
                .setNumberOfWorkers(1)
                .build();

        ListenerForBuilderDefaultsTest validListener = new ListenerForBuilderDefaultsTest();

        eventBus.register(new TestClassToRegisterWithMethodWithTooManyParameters());
        eventBus.register(validListener);
        eventBus.post(new TestEvent1());

        boolean eventDelivered = validListener.awaitEvent(2, TimeUnit.SECONDS);
        eventBus.shutdownBus();

        assertTrue(eventDelivered, "Default EventBusBuilder configuration should not stop the bus when a listener has an invalid @Listen method signature.");
    }
}
