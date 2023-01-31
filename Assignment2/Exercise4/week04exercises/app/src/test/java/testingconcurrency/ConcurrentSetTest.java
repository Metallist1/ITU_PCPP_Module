package testingconcurrency;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ConcurrentSetTest {

    // Variable with set under test
    private ConcurrentIntegerSet setBug;

    private ConcurrentIntegerSet setFixed;

    private ConcurrentIntegerSet setWorking;

    // Number of threads // Expected result // Number of adds
    static class AddParams implements ArgumentsProvider {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                    Arguments.of(1, 1, 1), //Small tests
                    Arguments.of(10, 10, 1), //Increase thread count
                    Arguments.of(1, 10, 10), //Increase execution count
                    Arguments.of(100, 10000, 100), //Medium test
                    Arguments.of(10000, 10000, 1),
                    Arguments.of(1, 10000, 10000),
                    Arguments.of(1000, 1000000, 1000), //Big test
                    Arguments.of(1, 1000000, 1000000)
            );
        }
    }

    static class RemoveParams implements ArgumentsProvider {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                    Arguments.of(1, 0, 1), //Small tests
                    Arguments.of(10, 0, 1), //Increase thread count
                    Arguments.of(1, 0, 10), //Increase execution count
                    Arguments.of(100, 0, 100), //Medium test
                    Arguments.of(10000, 0, 1),
                    Arguments.of(1, 0, 10000),
                    Arguments.of(1000, 0, 1000), //Big test
                    Arguments.of(1, 0, 1000000)
            );
        }
    }

    @BeforeEach
    public void initialize() {
        setBug = new ConcurrentIntegerSetBuggy();
        setFixed = new ConcurrentIntegerSetSync();
        setWorking = new ConcurrentIntegerSetLibrary();
    }

    @Nested
    @DisplayName("Base case")
    class BaseCase {

        @ParameterizedTest
        @DisplayName("Base case - Add Test")
        @ArgumentsSource(AddParams.class)
        public void test_set_library_add(int number_of_threads, int expected_result, int number_off_adds) throws InterruptedException {
            ExecutorService es = Executors.newCachedThreadPool();
            AtomicInteger actualSize = new AtomicInteger();
            for (int i = 0; i < number_of_threads; i++) {
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int x = 0; x < number_off_adds; x++) {
                            setWorking.add(actualSize.incrementAndGet());
                        }
                    }
                });

            }
            es.shutdown();
            boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
            if(finished){
                assertEquals(expected_result, setWorking.size());
            }else{
                fail("Task failed. Threads timed out");
            }
        }

        @ParameterizedTest
        @DisplayName("Base case - Add and Remove Test")
        @ArgumentsSource(RemoveParams.class)
        public void test_set_library_add_remove(int number_of_threads, int expected_result, int number_off_actions) throws InterruptedException {
            ExecutorService es = Executors.newCachedThreadPool();
            CyclicBarrier barrier = new CyclicBarrier(number_of_threads);
            AtomicInteger actualSize = new AtomicInteger();
            for (int i = 0; i < number_of_threads; i++) {
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int x = 0; x < number_off_actions; x++) {
                            setWorking.add(actualSize.incrementAndGet());
                        }
                        try {
                            barrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            fail();
                            throw new RuntimeException(e);
                        }
                        for (int x = 0; x < number_off_actions; x++) {
                            setWorking.remove(actualSize.getAndDecrement());
                        }
                    }
                });

            }
            es.shutdown();
            boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
            if(finished){
                assertEquals(expected_result, setWorking.size());
            }else{
                fail("Task failed. Threads timed out");
            }
        }

        @ParameterizedTest
        @DisplayName("Base case - Remove Test")
        @ArgumentsSource(RemoveParams.class)
        public void test_set_library_remove(int number_of_threads, int expected_result, int number_off_actions) throws InterruptedException {
            ExecutorService es = Executors.newCachedThreadPool();
            AtomicInteger actualSize = new AtomicInteger();
            for (int x = 0; x < number_off_actions*number_of_threads; x++) {
                setWorking.add(actualSize.incrementAndGet());
            }
            for (int i = 0; i < number_of_threads; i++) {
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int x = 0; x < number_off_actions; x++) {
                            setWorking.remove(actualSize.getAndDecrement());
                        }
                    }
                });

            }
            es.shutdown();
            boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
            if(finished){
                assertEquals(expected_result, setWorking.size());
            }else{
                fail("Task failed. Threads timed out");
            }
        }
    }

    @Nested
    @DisplayName("Buggy Set")
    class BuggySet {

        @ParameterizedTest
        @DisplayName("ConcurrentIntegerSetBuggy - Add Test - Expected Failure test")
        @ArgumentsSource(AddParams.class)
        public void test_set_buggy_add(int number_of_threads, int expected_result, int number_off_adds) throws InterruptedException {
            ExecutorService es = Executors.newCachedThreadPool();
            //Create x amount of new threads
            AtomicInteger actualSize = new AtomicInteger();
            for (int i = 0; i < number_of_threads; i++) {
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int x = 0; x < number_off_adds; x++) {
                            setBug.add(actualSize.incrementAndGet());
                        }
                    }
                });

            }
            es.shutdown();
            boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
            if(finished){
                assertEquals(expected_result, setBug.size());
            }else{
                fail("Task failed. Threads timed out");
            }
        }

        @ParameterizedTest
        @DisplayName("ConcurrentIntegerSetBuggy - Add and Remove Test - Expected Failure test")
        @ArgumentsSource(RemoveParams.class)
        public void test_set_buggy_add_remove(int number_of_threads, int expected_result, int number_off_actions) throws InterruptedException {
            ExecutorService es = Executors.newCachedThreadPool();
            CyclicBarrier barrier = new CyclicBarrier(number_of_threads);
            AtomicInteger actualSize = new AtomicInteger();
            for (int i = 0; i < number_of_threads; i++) {
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int x = 0; x < number_off_actions; x++) {
                            setBug.add(actualSize.incrementAndGet());
                        }
                        try {
                            barrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            fail();
                            throw new RuntimeException(e);
                        }
                        for (int x = 0; x < number_off_actions; x++) {
                            setBug.remove(actualSize.getAndDecrement());
                        }
                    }
                });

            }
            es.shutdown();
            boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
            if(finished){
                assertEquals(expected_result, setBug.size());
            }else{
                fail("Task failed. Threads timed out");
            }
        }


        @ParameterizedTest
        @DisplayName("ConcurrentIntegerSetBuggy - Remove Test - Expected Failure test")
        @ArgumentsSource(RemoveParams.class)
        public void test_set_buggy_remove(int number_of_threads, int expected_result, int number_off_actions) throws InterruptedException {
            ExecutorService es = Executors.newCachedThreadPool();
            AtomicInteger actualSize = new AtomicInteger();
            for (int x = 0; x < number_off_actions*number_of_threads; x++) {
                setBug.add(actualSize.incrementAndGet());
            }
            for (int i = 0; i < number_of_threads; i++) {
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int x = 0; x < number_off_actions; x++) {
                            setBug.remove(actualSize.getAndDecrement());
                        }
                    }
                });

            }
            es.shutdown();
            boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
            if(finished){
                assertEquals(expected_result, setBug.size());
            }else{
                fail("Task failed. Threads timed out");
            }
        }
    }


    @Nested
    @DisplayName("Fixed Set")
    class FixedSet {

        @ParameterizedTest
        @DisplayName("ConcurrentIntegerSetSync - Add Test")
        @ArgumentsSource(AddParams.class)
        public void test_set_sync_add(int number_of_threads, int expected_result, int number_off_adds) throws InterruptedException {
            ExecutorService es = Executors.newCachedThreadPool();
            AtomicInteger actualSize = new AtomicInteger();
            for (int i = 0; i < number_of_threads; i++) {
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int x = 0; x < number_off_adds; x++) {
                            setFixed.add(actualSize.incrementAndGet());
                        }
                    }
                });

            }
            es.shutdown();
            boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
            if(finished){
                assertEquals(expected_result, setFixed.size());
            }else{
                fail("Task failed. Threads timed out");
            }
        }

        @ParameterizedTest
        @DisplayName("ConcurrentIntegerSetSync -Add and Remove Test")
        @ArgumentsSource(RemoveParams.class)
        public void test_set_sync_add_remove(int number_of_threads, int expected_result, int number_off_actions) throws InterruptedException {
            ExecutorService es = Executors.newCachedThreadPool();
            CyclicBarrier barrier = new CyclicBarrier(number_of_threads);
            AtomicInteger actualSize = new AtomicInteger();
            for (int i = 0; i < number_of_threads; i++) {
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int x = 0; x < number_off_actions; x++) {
                            setFixed.add(actualSize.incrementAndGet());
                        }
                        try {
                            barrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            fail();
                            throw new RuntimeException(e);
                        }
                        for (int x = 0; x < number_off_actions; x++) {
                            setFixed.remove(actualSize.getAndDecrement());
                        }
                    }
                });

            }
            es.shutdown();
            boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
            if(finished){
                assertEquals(expected_result, setFixed.size());
            }else{
                fail("Task failed. Threads timed out");
            }
        }

        @ParameterizedTest
        @DisplayName("ConcurrentIntegerSetSync - Remove Test")
        @ArgumentsSource(RemoveParams.class)
        public void test_set_sync_remove(int number_of_threads, int expected_result, int number_off_actions) throws InterruptedException {
            ExecutorService es = Executors.newCachedThreadPool();
            AtomicInteger actualSize = new AtomicInteger();
            for (int x = 0; x < number_off_actions*number_of_threads; x++) {
                setFixed.add(actualSize.incrementAndGet());
            }
            for (int i = 0; i < number_of_threads; i++) {
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int x = 0; x < number_off_actions; x++) {
                            setFixed.remove(actualSize.getAndDecrement());
                        }
                    }
                });

            }
            es.shutdown();
            boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
            if(finished){
                assertEquals(expected_result, setFixed.size());
            }else{
                fail("Task failed. Threads timed out");
            }
        }
    }

}
