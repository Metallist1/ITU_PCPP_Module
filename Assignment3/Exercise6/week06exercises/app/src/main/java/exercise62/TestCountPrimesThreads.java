package exercise62;
// Counting primes, using multiple threads for better performance.
// (Much simplified from CountprimesMany.java)
// sestoft@itu.dk * 2014-08-31, 2015-09-15
// modified rikj@itu.dk 2017-09-20
// modified jst@itu.dk 2021-09-24
// raup@itu.dk * 05/10/2022

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.IntToDoubleFunction;
import java.util.concurrent.atomic.AtomicLong;

import benchmarking.Benchmark;

public class TestCountPrimesThreads {

    public static void main(String[] args) {
        new TestCountPrimesThreads();
    }

    public TestCountPrimesThreads() {
        final int range = 100_000;

        Benchmark.Mark7("countSequential", i -> countSequential(range));

        for (int c = 1; c <= 32; c++) {
            final int threadCount = c;

            Benchmark.Mark7(String.format("countParallelN %2d", threadCount),
                    i -> {
                        try {
                            return countParallelN(range, threadCount);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

            Benchmark.Mark7(String.format("countParallelNLocal %2d", threadCount),
                    i -> countParallelNLocal(range, threadCount));
        }
    }

    private static boolean isPrime(int n) {
        int k = 2;

        while (k * k <= n && n % k != 0) {
            k++;
        }

        return n >= 2 && k * k > n;
    }

    // Sequential solution
    private static long countSequential(int range) {
        long count = 0;
        final int from = 0;

        for (int i = from; i < range; i++) {
            if (isPrime(i)) {
                count++;
            }
        }

        return count;
    }

    private static long countParallelN(int range, int threadCount) throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        final int perThread = range / threadCount;

        final AtomicLong lc = new AtomicLong(0);

        for (int t = 0; t < threadCount; t++) {
            final int from = perThread * t,
                    to = (t + 1 == threadCount) ? range : perThread * (t + 1);
            es.execute(() -> {
                for (int i = from; i < to; i++) {
                    if (isPrime(i)) {
                        lc.incrementAndGet();
                    }
                }
            });
        }

        es.shutdown();

        boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);

        if (finished) {
            return lc.get();
        } else {
            throw new Exception("ES failed");
        }
    }

    // General parallel solution, using multiple threads
    private static long countParallelNLocal(int range, int threadCount) {
        final int perThread = range / threadCount;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);

        List<Callable<Long>> tasks = new ArrayList<>();

        for (int t = 0; t < threadCount; t++) {
            final int from = perThread * t,
                    to = (t + 1 == threadCount) ? range : perThread * (t + 1);

            tasks.add(new Callable<Long>() {
                @Override
                public Long call() {
                    long count = 0;

                    for (int i = from; i < to; i++) {
                        if (isPrime(i)) {
                            count++;
                        }
                    }

                    return count;
                }

            });

        }

        long result = 0;

        try {
            // Add all futures to the execution pool at once
            List<Future<Long>> futures = pool.invokeAll(tasks);
            for (Future<Long> f : futures) {
                result += f.get(); // Wait for each future to be executed and add partial result
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        pool.shutdown(); // We are sure to be done, so we shut down the pool

        return result;
    }
}
