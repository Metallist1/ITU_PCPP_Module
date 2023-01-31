// sestoft@itu.dk * 2014-09-04
// thdy@itu.dk * 2019
// kasper@itu.dk * 2020
// raup@itu.dk * 05/10/2022
package exercise63;


import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleHistogram {

    public static void main(String[] args) throws Exception {
        final int threadCount = 10;
        final int element_count = 30;
        final Histogram histogram = new Histogram3(30,16);
        //final Histogram histogram = new Histogram3(30,30);
        Random random = new Random();

        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            es.execute(() -> {
                for (int x = 0; x < element_count; x++) {
                    histogram.increment(x);
                }
                for (int x = 0; x < element_count; x++) {
                    int bin = x;
                    System.out.println(bin +" Count: "+ histogram.getCount(bin) +" Procentage: "+ histogram.getPercentage(bin) +
                            " Span: "+  histogram.getSpan() +" Total: "+ histogram.getTotal());
                }
            });
        }

        es.shutdown();

        boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);

        if (finished) {
            dump(histogram);
        } else {
            throw new Exception("ES failed");
        }
    }

    public static void dump(Histogram histogram) {

        for (int bin = 0; bin < histogram.getSpan(); bin++) {
            System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
        }

        System.out.printf("      %9d%n", histogram.getTotal());

    }
}


