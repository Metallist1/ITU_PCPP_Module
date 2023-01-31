// For week 1
// sestoft@itu.dk * 2014-08-21
// raup@itu.dk * 2021-08-27
package exercises01;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PrinterExcercise {

    Printer p = new Printer();
    public PrinterExcercise() {

		Thread t1 = new Thread(() -> {
			while (true){
				p.print();
			}
		});

		Thread t2 = new Thread(() -> {
			while (true){
				p.print();
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

    }
    
    public static void main(String[] args) {
		new PrinterExcercise();
    }

    class Printer {
		ReadWriteLock lock = new ReentrantReadWriteLock();
		Lock writeLock = lock.writeLock();

		public void print() {
			writeLock.lock();
			try {
				System.out.print("-");
				try {
					Thread.sleep(50);
				} catch (InterruptedException exn) {
					System.out.println(exn);
				}
				System.out.print("|");
			} finally {
				writeLock.unlock();
			}
		}
    }
}


