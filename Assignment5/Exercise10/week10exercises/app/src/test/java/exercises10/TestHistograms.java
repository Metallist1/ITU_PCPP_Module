package exercises10;

// JUnit testing imports

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
// Concurrency imports


public class TestHistograms {
    // The imports above are just for convenience, feel free add or remove imports


    // Variable with set under test
    private Histogram histogramBasic;

    private Histogram histogramCAS;

    private final int maxPrimeRange = 4_999_999;
    private final int maxNThreads = 4;
    private final Function<Integer, Integer> numberOfThreads = (x) ->  2 << x;

    // Range // 2 pow (n) threads
    static class AddParams implements ArgumentsProvider {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                    Arguments.of(5_000_000, 0), //1 threads
                    Arguments.of(5_000_000, 1), //2 threads
                    Arguments.of(5_000_000, 2), //4 threads
                    Arguments.of(5_000_000, 3), //8 threads
                    Arguments.of(5_000_000, 4)  //16 threads
            );
        }
    }

    @BeforeEach
    public void initialize() {
        histogramBasic = new Histogram1(25);
        histogramCAS = new CASHistogram(25);
    }


    @Nested
    @DisplayName("Base case")
    class BaseCase {
        // Function to count the prime factors of a number `p`
        private int countFactors(int p) {
            if (p < 2) return 0;
            int factorCount = 1, k = 2;
            while (p >= k * k) {
                if (p % k == 0) {
                    factorCount++;
                    p = p / k;
                } else
                    k = k + 1;
            }
            return factorCount;
        }
        public boolean countParallelN(Histogram his, int range, int threadCount) throws Exception {
            ExecutorService es = Executors.newFixedThreadPool(threadCount);
            final int perThread = range / threadCount;
            for (int t = 0; t < threadCount; t++) {
                final int from = perThread * t,
                        to = (t + 1 == threadCount) ? range : perThread * (t + 1);
                es.execute(() -> {
                    for (int i = from; i < to; i++) {
                        his.increment(countFactors(i));
                    }
                });
            }

            es.shutdown();

            boolean finished = es.awaitTermination(10, TimeUnit.MINUTES);

            if (finished) {
                return true;
            } else {
                throw new Exception("ES failed");
            }
        }


        @Test
        public void baseTest_incrementOneBinShouldIncrementOnce(){
            // Arrange
            int span = maxPrimeRange;
            int reducedPrimeRange = 100;
            histogramCAS = new CASHistogram(span);
            histogramBasic = new Histogram1(span);

            // Act
            try {
                countParallelN(histogramCAS, reducedPrimeRange, 1);
                countParallelN(histogramBasic, reducedPrimeRange, 1);
            } catch(Exception e) {
                System.out.println(e);
            }

            // Assert
            assertHistogram(histogramCAS, histogramBasic, 0, maxPrimeRange-1);
        }

        private void assertHistogram(Histogram sut, Histogram control, int min , int max ){

            for (int i = min; i <= max; i++){
                int expectedValue = control.getCount(i);
                int actualValue = sut.getCount(i);
                // Assert
                assertEquals(expectedValue, actualValue);

            }
        }

        @RepeatedTest(10)
        public void incrementOneBinMultipleThreadsShouldIncrementOnce(){
            // Arrange
            int span = maxPrimeRange;

            // Act
            for(int t = 0; t <= maxNThreads; t++){
                histogramCAS = new CASHistogram(span);
                histogramBasic = new Histogram1(span);

                int threadsCount = numberOfThreads.apply(t);
                try {
                    countParallelN(histogramCAS, maxPrimeRange, threadsCount);
                    countParallelN(histogramBasic, maxPrimeRange, 1);
                } catch(Exception e) {
                    System.out.println(e);
                }

                // Assert
                assertHistogram(histogramCAS, histogramBasic, 0, maxPrimeRange-1);
            }

        }
        @ParameterizedTest
        @DisplayName("Base case - Add Test")
        @ArgumentsSource(AddParams.class)
        public void test_set_library_add(int range, int number_of_threads) throws InterruptedException {

            for (int e = 0; e < range; e++) histogramBasic.increment(countFactors(e)); // Sequential

            int total_threads = (int) Math.pow(2,number_of_threads);
            ExecutorService es = Executors.newFixedThreadPool(total_threads);
            final int perThread = range / total_threads;

            for (int t = 0; t < total_threads; t++) {
                final int from = perThread * t,
                        to = (t + 1 == total_threads) ? range : perThread * (t + 1);
                es.execute(() -> {
                    for (int i = from; i < to; i++) {
                        histogramCAS.increment(countFactors(i));
                    }
                });
            }

            es.shutdown();

            boolean finished = es.awaitTermination(10, TimeUnit.MINUTES);

            if (finished) {
                Assertions.assertAll(
                        IntStream.range(0, histogramCAS.getSpan())
                                .mapToObj(i -> () ->{
                                    int val1 = histogramBasic.getCount(i);
                                    int val2 = histogramCAS.getCount(i);
                                            assertEquals(
                                                    histogramBasic.getAndClear(i),
                                                    histogramCAS.getAndClear(i),
                                                    String.format("name: %s, expected %s, actual %s", i, val1, val2)
                                            );
                                        }
                                )
                );
            } else {
                fail("Task failed. Threads timed out");
            }
        }

        }
}

