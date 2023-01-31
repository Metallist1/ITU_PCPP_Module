// For week 1
// sestoft@itu.dk * 2014-08-21
// raup@itu.dk * 2021-08-27
package exercises01;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TestLongCounterExperiments {

    LongCounter lc = new LongCounter();
    int counts = 10_000_000;
    public TestLongCounterExperiments() {

		Thread t1 = new Thread(() -> {
			for (int i=0; i<counts; i++) {
					lc.increment();
			}
		});

		Thread t2 = new Thread(() -> {
			for (int i=0; i<counts; i++) {
				lc.increment();
			}
		});

		t1.start();
		t2.start();

		try {
			t1.join();
			t2.join();
		}
		catch (InterruptedException exn) {
			System.out.println("Some thread was interrupted");
		}

		System.out.println("Count is " + lc.get() + " and should be " + 2*counts);
    }
    
    public static void main(String[] args) {
		new TestLongCounterExperiments();
    }

    class LongCounter {
		ReadWriteLock lock = new ReentrantReadWriteLock();
		Lock readLock = lock.readLock();
		Lock writeLock = lock.writeLock();
		private long count = 0;
    
		public void increment() {
			writeLock.lock();
			try {
				count++;
			} finally {
				writeLock.unlock();
			}
		}
    
		public long get() {
			readLock.lock();
			try {
				return count;
			} finally {
				readLock.unlock();
			}
		}
    }
}


