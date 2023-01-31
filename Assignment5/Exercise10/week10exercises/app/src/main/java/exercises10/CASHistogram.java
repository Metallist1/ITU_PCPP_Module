package exercises10;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class CASHistogram implements Histogram{

    //The implementation must use AtomicInteger (instead of locks), and only use the methods compareAndSet
    //and get; no other methods provided in the class AtomicInteger are allowed.
    //The method getAndClear returns the current value in the bin and sets it to 0.

    private final AtomicIntegerArray counts;

    public CASHistogram(int span) {
        this.counts = new AtomicIntegerArray(span);
    }

    @Override
    public void increment(int bin) {
        int v;
        do {
            v = counts.get(bin);
        }
        while (!counts.compareAndSet(bin,v, v + 1));
    }

    @Override
    public int getCount(int bin) {
        return counts.get(bin);
    }

    @Override
    public int getSpan() {
        return counts.length();
    }

    @Override
    public int getAndClear(int bin) {
        int v;
        do {
            v = counts.get(bin);
        }
        while (!counts.compareAndSet(bin, v, 0));
        return v;
    }
}
