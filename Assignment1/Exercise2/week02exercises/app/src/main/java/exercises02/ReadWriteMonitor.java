// For week 2
// sestoft@itu.dk * 2015-10-29
package exercises02;

import java.util.concurrent.locks.*;

public class ReadWriteMonitor {
    private int readers;
    private boolean hasWriter;
    private Object lock;

    public ReadWriteMonitor(){
        readers = 0;
        hasWriter = false;
        lock = new Object();

    }

    public void readLock(){
            synchronized(lock) {
                try {
                    while(hasWriter){
                        lock.wait();
                    }
                    readers++;
                } catch(InterruptedException e) {}
            }
        }

    public void readUnlock(){
        synchronized(lock) {
            readers--;
            if (readers == 0){
                lock.notifyAll();
            }
        }
    }

    public void writeLock(){
        synchronized(lock){
                while(hasWriter)
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                hasWriter=true;
                while(readers != 0)
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
            }
    }

    public void writeUnlock(){
        synchronized(lock) {
            hasWriter = false;
            lock.notifyAll();
        }
    }
}
