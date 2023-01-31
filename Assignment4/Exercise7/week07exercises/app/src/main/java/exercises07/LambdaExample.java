package exercises07;
// Simple Lambda example
// jst@itu.dk * 2022-10-10 Simple example to illustrate use of Lambda

import java.util.function.Function;
import java.util.function.IntUnaryOperator;

class LambdaExample {
    public static void main(String[] args) {
        new LambdaExample();
    }

    public LambdaExample() {
        System.out.println("I: " + increment(f));
        System.out.println("I: " + multiply(f_m));

        Mark6Int("Increment Mark 6", i -> increment(f));

    }

    Function<Integer, Integer> f = (x) -> x + 1;
    Function<Integer, Integer> f_m = (x) -> x * 5;

    private static int increment(Function<Integer, Integer> add1) {
        return add1.apply(8);
    }

    private static int multiply(Function<Integer, Integer> multi) {
        return multi.apply(2);
    }


    public static double Mark6Int(String msg, IntUnaryOperator f) {
        int n = 10, count = 1, totalCount = 0;
        double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
        do {
            count *= 2;
            st = sst = 0.0;
            for (int j = 0; j < n; j++) {
                Timer t = new Timer();
                for (int i = 0; i < count; i++)
                    dummy += f.applyAsInt(i);
                runningTime = t.check();
                double time = runningTime * 1e9 / count;
                st += time;
                sst += time * time;
                totalCount += count;
            }
            double mean = st / n, sdev = Math.sqrt((sst - mean * mean * n) / (n - 1));
            System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev, count);
        } while (runningTime < 0.25 && count < Integer.MAX_VALUE / 2);
        return dummy / totalCount;
    }
}