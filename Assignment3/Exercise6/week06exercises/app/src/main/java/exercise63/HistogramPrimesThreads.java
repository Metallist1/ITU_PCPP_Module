package exercise63;

// first version by Kasper modified by jst@itu.dk 24-09-2021
// raup@itu.dk * 05/10/2022

import benchmarking.Benchmark;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HistogramPrimesThreads {

    public static void main(String[] args) throws Exception {
        new HistogramPrimesThreads();
    }

    public HistogramPrimesThreads() throws Exception {

        final int range = 5_000_000;

        Benchmark.Mark7("Histogram2 ", i -> {
            try {
                Histogram histogram = new Histogram2(25); // 25 bins sufficient for a range of 0..4_999_999
                return countParallelN(histogram, range, 20);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        /*
           final Histogram saneHis = new Histogram2(25); // 25 bins sufficient for a range of 0..4_999_999
           countParallelN(saneHis, range, 20);
        // Finally we plot the result to ensure that it looks as expected (see example output in the exercise script)
            dump(saneHis);
         */

        for (int c = 1; c <= 32; c++) {
            final int threadCount = c;

            Benchmark.Mark7(String.format("Histogram3 %2d", threadCount),
                    i -> {
                        try {
                             Histogram histogram = new Histogram3(25,threadCount); // 25 bins sufficient for a range of 0..4_999_999
                            return countParallelN(histogram, range, 20);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    // Returns the number of prime factors of `p`
    public static int countFactors(int p) {

        if (p < 2) return 0;

        int factorCount = 1, k = 2;

        while (p >= k * k) {
            if (p % k == 0) {
                factorCount++;
                p = p / k;
            } else {
				k = k + 1;
			}
        }

        return factorCount;
    }

    private static double countParallelN(Histogram his, int range, int threadCount) throws Exception {
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
            return (double) his.getTotal();
        } else {
            throw new Exception("ES failed");
        }
    }

    public static void dump(Histogram histogram) {
        for (int bin = 0; bin < histogram.getSpan(); bin = bin + 1) {
            System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
        }
        System.out.printf("      %9d%n", histogram.getTotal());
    }
}

