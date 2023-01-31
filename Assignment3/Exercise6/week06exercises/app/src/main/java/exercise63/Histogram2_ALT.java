package exercise63;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Histogram2_ALT implements Histogram {

    ReadWriteLock lock = new ReentrantReadWriteLock();
    Lock readLock = lock.readLock();
    Lock writeLock = lock.writeLock();

    private final ConcurrentHashMap<Integer, AtomicInteger> counts;
    private final AtomicInteger total = new AtomicInteger(0);


    public Histogram2_ALT(int span) {
        this.counts = new ConcurrentHashMap<>(span) {{
            for (int i = 0; i < span; i++) {
                put(i, new AtomicInteger(0));
            }
        }};
    }

    @Override
    public void increment(int bin) {
        writeLock.lock();
        try {
            counts.get(bin).incrementAndGet();
            total.incrementAndGet();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public int getCount(int bin) {
        readLock.lock();
        try {
            return counts.get(bin).intValue();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public float getPercentage(int bin) {
        readLock.lock();
        try {
            return (float) getCount(bin) / (float) getTotal() * 100;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int getSpan() {
        return counts.size();
    }

    @Override
    public int getTotal() {
        readLock.lock();
        try {
            return total.get();
        } finally {
            readLock.unlock();
        }
    }
}
