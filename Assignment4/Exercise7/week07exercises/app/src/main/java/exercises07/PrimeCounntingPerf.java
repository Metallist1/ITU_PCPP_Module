//Exercise 7.2
//JSt vers Oct 10, 2022

package exercises07;

import java.util.*;
import java.util.stream.*;
import java.util.function.IntToDoubleFunction;

class PrimeCountingPerf {
    public static void main(String[] args) {
        new PrimeCountingPerf();
    }

    static final int range = 100000;

    //Test whether n is a prime number
    private static boolean isPrime(int n) {
        int k = 2;
        while (k * k <= n && n % k != 0)
            k++;
        return n >= 2 && k * k > n;
    }

    // Sequential solution
    private static long countSequential(int range) {
        long count = 0;
        final int from = 0, to = range;
        for (int i = from; i < to; i++)
            if (isPrime(i)) count++;
        return count;
    }

    // IntStream solution
    private static long countIntStream(int range) {
        return IntStream.range(2, range).filter(n -> isPrime(n)).count();
    }

    // IntStream solution with system.out.print
    private static long displayIntStream(int range) {
        List<Integer> primeList = IntStream.range(2, range).filter(n -> isPrime(n)).boxed().collect(Collectors.toList());
        primeList.forEach(System.out::println);
        return primeList.size();
    }

    // Parallel Stream solution
    private static long countParallel(int range) {
        return IntStream.range(2, range).parallel().filter(n -> isPrime(n)).count();
    }

    // parallelStream solution
    private static long countparallelStream(List<Integer> list) {
        return list.parallelStream().filter(n -> isPrime(n)).count();
    }

    // --- Benchmarking infrastructure ---
    public static double Mark7(String msg, IntToDoubleFunction f) {
        int n = 10, count = 1, totalCount = 0;
        double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
        do {
            count *= 2;
            st = sst = 0.0;
            for (int j = 0; j < n; j++) {
                Timer t = new Timer();
                for (int i = 0; i < count; i++)
                    dummy += f.applyAsDouble(i);
                runningTime = t.check();
                double time = runningTime * 1e9 / count;
                st += time;
                sst += time * time;
                totalCount += count;
            }
        } while (runningTime < 0.25 && count < Integer.MAX_VALUE / 2);
        double mean = st / n, sdev = Math.sqrt((sst - mean * mean * n) / (n - 1));
        System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev, count);
        return dummy / totalCount;
    }

    public PrimeCountingPerf() {

        // displayIntStream(1000);

        Mark7("Sequential", i -> countSequential(range));

        Mark7("IntStream", i -> countIntStream(range));

        Mark7("Parallel", i -> countParallel(range));

        List<Integer> list = new ArrayList<Integer>();
        for (int i = 2; i < range; i++) {
            list.add(i);
        }
        Mark7("ParallelStream", i -> countparallelStream(list));
    }
}
