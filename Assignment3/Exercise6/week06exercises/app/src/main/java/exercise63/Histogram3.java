package exercise63;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Histogram3 implements Histogram {
    private final int nrLocks;
    private final Object[] locks;
    private final int[] counts;
    private final AtomicInteger total = new AtomicInteger(0);


    public Histogram3(int span , int nrLocks) {
        this.nrLocks = nrLocks;
        locks = new Object[nrLocks];
        for (int i = 0; i < nrLocks; i++)
            locks[i] = new Object();

        this.counts = new int [span];
    }

    private final int hash(Object key) {
        return Math.abs(key.hashCode() % counts.length);
    }
    @Override
    public void increment(int bin) {
        synchronized (locks[hash(bin) % nrLocks]) {
            counts[bin] = counts[bin] + 1;
            total.incrementAndGet();
        }
    }

    @Override
    public int getCount(int bin) {
        synchronized (locks[hash(bin) % nrLocks]) {
            return counts[bin];
        }
    }

    @Override
    public float getPercentage(int bin) {
        return (float) getCount(bin) / (float) getTotal() * 100;
    }

    @Override
    public int getSpan() {
        return counts.length;
    }

    @Override
    public int getTotal() {
        return total.get();
    }
}
