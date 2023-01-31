package exercise63;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Histogram2 implements Histogram {

    ReadWriteLock lock = new ReentrantReadWriteLock();
    Lock readLock = lock.readLock();
    Lock writeLock = lock.writeLock();

    private final int[] counts;
    private int total = 0;


    public Histogram2(int span) {
        this.counts = new int[span];
    }

    @Override
    public void increment(int bin) {
        writeLock.lock();
        try {
            counts[bin] = counts[bin] + 1;
            total++ ;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public int getCount(int bin) {
        readLock.lock();
        try {
            return counts[bin];
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
        return counts.length;
    }

    @Override
    public int getTotal() {
        readLock.lock();
        try {
            return total;
        } finally {
            readLock.unlock();
        }
    }
}
