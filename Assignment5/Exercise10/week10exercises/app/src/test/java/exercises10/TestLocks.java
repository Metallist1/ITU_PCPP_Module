package exercises10;

// JUnit testing imports

import org.junit.jupiter.api.*;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.*;

// Data structures imports
import java.util.concurrent.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// Concurrency imports
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestLocks {
    // The imports above are just for convenience, feel free add or remove imports

    static class Retest implements ArgumentsProvider {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                    Arguments.of(2, 1),
                    Arguments.of(10, 5),
                    Arguments.of(15, 5),
                    Arguments.of(35, 5),
                    Arguments.of(105, 5),
                    Arguments.of(1005, 5),
                    Arguments.of(10005, 5)
            );
        }
    }

    private ReadWriteCASLock readWriteCASLock;

    @BeforeEach
    public void initialize() {
        readWriteCASLock = new ReadWriteCASLock();
    }

    @DisplayName("Take a read lock while holding a write lock")
    @Test
    public void read_lock_while_holding_write_lock() throws InterruptedException {
        readWriteCASLock.writerTryLock();
        assertFalse(readWriteCASLock.readerTryLock());
    }

    @DisplayName("Not possible to unlock a lock that you do not hold read")
    @Test
    public void write_lock_while_holding_read_lock() throws InterruptedException {
        readWriteCASLock.readerTryLock();
        assertFalse(readWriteCASLock.writerTryLock());
    }



    @Test
    public void readerUnlock_shouldthrowExceptionWhenUnlockingNotHeldLock() {
        // Act
        Exception exception = assertThrows(RuntimeException.class, () -> {
            readWriteCASLock.readerUnlock();
        });

        // Act
        assertTrue(exception.getMessage().contains("Not lock holder"));
    }


    @Test
    public void writerUnlock_shouldthrowExceptionWhenUnlockingNotHeldLock() {
        // Act
        Exception exception = assertThrows(RuntimeException.class, () -> {
            readWriteCASLock.writerUnlock();
        });

        // Act
        assertTrue(exception.getMessage().contains("Not lock holder"));
    }

    @Test
    public void ReaderLock_shouldNotLockWhenWriterLocksArePresent(){
        // Arrange
        boolean expectedResult = false;


        // Act
        readWriteCASLock.writerTryLock();
        boolean actualResult = readWriteCASLock.readerTryLock();


        // Assert
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void writerLock_ShouldNotLockWhenReaderLocksArePresent(){
        // Arrange
        boolean expectedResult = false;


        // Act
        readWriteCASLock.readerTryLock();
        boolean actualResult = readWriteCASLock.writerTryLock();


        // Assert
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName("It is not possible to unlock a lock that you do not hold (Write)")
    @Test
    public void impossible_unlock_for_not_hold_write() throws InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(new Runnable() {
            @Override
            public void run() {
                readWriteCASLock.writerTryLock();
            }
        });


        es.shutdown();
        boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
        if (finished) {
            assertThrows(RuntimeException.class, () -> {
                readWriteCASLock.writerUnlock();
            });
        } else {
            fail("Task failed. Threads timed out");
        }
    }

    @DisplayName("It is not possible to unlock a lock that you do not hold (Read)")
    @Test
    public void impossible_unlock_for_not_hold_read() throws InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(new Runnable() {
            @Override
            public void run() {
                readWriteCASLock.readerTryLock();
            }
        });


        es.shutdown();
        boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
        if (finished) {
            assertThrows(RuntimeException.class, () -> {
                readWriteCASLock.readerUnlock();
            });
        } else {
            fail("Task failed. Threads timed out");
        }
    }

    @ParameterizedTest
    @DisplayName("Two writers cant get lock.")
    @ArgumentsSource(Retest.class)
    public void two_writers_dont_hold_thread(int number_of_threads) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(number_of_threads);
        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
        for (int i = 0; i < number_of_threads; i++) {
            tasks.add(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    readWriteCASLock.writerTryLock();
                    return null;
                }
            });

        }
        try {
            es.invokeAll(tasks);

        } catch (Throwable e) {
            System.out.println("Error " + e.getMessage());
            e.printStackTrace();
        }

        es.shutdown();
        boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
        if (finished) {
            assertFalse(readWriteCASLock.writerTryLock());
        } else {
            fail("Task failed. Threads timed out");
        }
    }

    @ParameterizedTest
    @DisplayName("Two writers cannot acquire the lock at the same time.")
    @ArgumentsSource(Retest.class)
    public void both_writers_dont_hold_thread(int number_of_threads) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(number_of_threads);
        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
        for (int i = 0; i < number_of_threads; i++) {
            tasks.add(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    while (true) {
                        if (readWriteCASLock.writerTryLock())
                            break;
                    }
                    readWriteCASLock.writerUnlock();
                    return null;
                }
            });

        }
        try {
            es.invokeAll(tasks);

        } catch (Throwable e) {
            System.out.println("Error " + e.getMessage());
            e.printStackTrace();
        }

        es.shutdown();
        boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
        if (finished) {
            assertTrue(readWriteCASLock.writerTryLock());
        } else {
            fail("Task failed. Threads timed out");
        }
    }

}



