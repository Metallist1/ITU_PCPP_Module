// For week 2
// sestoft@itu.dk * 2015-10-29
package exercises02;

import javax.annotation.concurrent.GuardedBy;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TestLocking0 {
    public static void main(String[] args) {

        final int count = 1_000_000;

        Mystery m = new Mystery();

        Thread t1 = new Thread(() -> {

            for (int i=0; i<count; i++){
                m.addInstance(1);
            }

        });
        Thread t2 = new Thread(() -> {

            for (int i=0; i<count; i++){
                m.addStatic(1);
            }

        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException exn) { }

        System.out.printf("Sum is %f and should be %f%n", m.sum(), 2.0 * count);

    }
}

class Mystery {
    private static double sum = 0;
    static ReadWriteLock lock = new ReentrantReadWriteLock();
    static Lock writeLock = lock.writeLock();
    static Lock readLock = lock.readLock();

    public static synchronized void addStatic(double x) {
        writeLock.lock();
        try {
            sum += x;
        } finally {
            writeLock.unlock();
        }
    }

    public synchronized void addInstance(double x) {
        writeLock.lock();
        try {
            sum += x;
        } finally {
            writeLock.unlock();
        }
    }

    public static synchronized double sum() {
        try {
            readLock.lock();
            return sum;
        } finally {
            readLock.unlock();
        }
    }
}