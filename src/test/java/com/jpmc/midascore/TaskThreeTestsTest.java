import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

package com.jpmc.midascore;




public class TaskThreeTestsTest {
    @Test
    void task_three_verifier_sendsAllTransactions_and_can_be_interrupted() throws Exception {
        // prepare mocks
        KafkaProducer kafkaProducer = mock(KafkaProducer.class);
        UserPopulator userPopulator = mock(UserPopulator.class);
        FileLoader fileLoader = mock(FileLoader.class);

        String[] transactions = new String[] { "t1", "t2", "t3", "t4" };
        when(fileLoader.loadStrings("/test_data/mnbvcxz.vbnm")).thenReturn(transactions);

        CountDownLatch latch = new CountDownLatch(transactions.length);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(kafkaProducer).send(anyString());

        // create instance and inject mocks via reflection
        TaskThreeTests subject = new TaskThreeTests();
        setField(subject, "kafkaProducer", kafkaProducer);
        setField(subject, "userPopulator", userPopulator);
        setField(subject, "fileLoader", fileLoader);

        // run the verifier in a separate thread so we can interrupt to stop the infinite loop
        Thread t = new Thread(() -> {
            try {
                subject.task_three_verifier();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "task-three-runner");
        t.start();

        // wait for all sends to be invoked
        boolean allSent = latch.await(5, TimeUnit.SECONDS);
        assertTrue(allSent, "Expected all transactions to be sent");

        // verify populate was called
        verify(userPopulator, atLeastOnce()).populate();

        // stop the test by interrupting the thread (task_three_verifier propagates InterruptedException)
        t.interrupt();
        t.join(2000);
        assertFalse(t.isAlive(), "Thread should terminate after interrupt");
    }

    // helper to inject private fields
    private static void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }
}


package com.jpmc.midascore;




public class TaskThreeTestsTest {

    @Test
    void task_three_verifier_sendsAllTransactions_and_can_be_interrupted() throws Exception {
        // prepare mocks
        KafkaProducer kafkaProducer = mock(KafkaProducer.class);
        UserPopulator userPopulator = mock(UserPopulator.class);
        FileLoader fileLoader = mock(FileLoader.class);

        String[] transactions = new String[] { "t1", "t2", "t3", "t4" };
        when(fileLoader.loadStrings("/test_data/mnbvcxz.vbnm")).thenReturn(transactions);

        CountDownLatch latch = new CountDownLatch(transactions.length);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(kafkaProducer).send(anyString());

        // create instance and inject mocks via reflection
        TaskThreeTests subject = new TaskThreeTests();
        setField(subject, "kafkaProducer", kafkaProducer);
        setField(subject, "userPopulator", userPopulator);
        setField(subject, "fileLoader", fileLoader);

        // run the verifier in a separate thread so we can interrupt to stop the infinite loop
        Thread t = new Thread(() -> {
            try {
                subject.task_three_verifier();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "task-three-runner");
        t.start();

        // wait for all sends to be invoked
        boolean allSent = latch.await(5, TimeUnit.SECONDS);
        assertTrue(allSent, "Expected all transactions to be sent");

        // verify populate and loadStrings were called
        verify(userPopulator, atLeastOnce()).populate();
        verify(fileLoader, atLeastOnce()).loadStrings("/test_data/mnbvcxz.vbnm");

        // stop the test by interrupting the thread (task_three_verifier propagates InterruptedException)
        t.interrupt();
        t.join(2000);
        assertFalse(t.isAlive(), "Thread should terminate after interrupt");
    }

    @Test
    void task_three_verifier_callsSend_exactNumberOfTimes() throws Exception {
        KafkaProducer kafkaProducer = mock(KafkaProducer.class);
        UserPopulator userPopulator = mock(UserPopulator.class);
        FileLoader fileLoader = mock(FileLoader.class);

        String[] transactions = new String[] { "a", "b", "c" };
        when(fileLoader.loadStrings("/test_data/mnbvcxz.vbnm")).thenReturn(transactions);

        CountDownLatch latch = new CountDownLatch(transactions.length);
        doAnswer(invocation -> { latch.countDown(); return null; }).when(kafkaProducer).send(anyString());

        TaskThreeTests subject = new TaskThreeTests();
        setField(subject, "kafkaProducer", kafkaProducer);
        setField(subject, "userPopulator", userPopulator);
        setField(subject, "fileLoader", fileLoader);

        Thread t = new Thread(() -> {
            try {
                subject.task_three_verifier();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        t.start();

        assertTrue(latch.await(3, TimeUnit.SECONDS), "All sends should occur");
        verify(kafkaProducer, times(transactions.length)).send(anyString());

        t.interrupt();
        t.join(2000);
        assertFalse(t.isAlive());
    }

    // helper to inject private fields
    private static void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }
}
