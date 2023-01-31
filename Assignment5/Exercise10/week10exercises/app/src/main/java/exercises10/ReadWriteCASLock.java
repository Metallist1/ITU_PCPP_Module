// For week 10
// raup@itu.dk * 10/10/2021

package exercises10;

// Very likely you will need some imports here

import java.util.concurrent.atomic.AtomicReference;

class ReadWriteCASLock implements SimpleRWTryLockInterface {

    private final AtomicReference<Holders> holders = new AtomicReference<>();

    private static Writer writer_instance = new Writer(Thread.currentThread());

    public boolean readerTryLock() {
        ReaderList newNode = new ReaderList(Thread.currentThread(), null);
        ReaderList oldHead;
        do {
            if (holders.get() instanceof Writer) {
                return false;
            }
            oldHead = (ReaderList) holders.get();
            newNode.next = oldHead;
        } while (!holders.compareAndSet(oldHead, newNode));

        return true;
    }

    public void readerUnlock() {
        ReaderList newHead;
        ReaderList oldHead;
        do {
            //If holders is null or does not refer to a ReaderList or the current thread is
            //not on the reader list, then it must throw an exception.
            if (holders.get() instanceof Writer || holders.get() == null ) {
                throw new RuntimeException("Not lock holder");
            }
            oldHead = (ReaderList) holders.get();
            if(oldHead.thread != Thread.currentThread()){
                throw new RuntimeException("Not lock holder");
            }
            newHead = oldHead.next;

        } while (!holders.compareAndSet(oldHead, newHead));
    }

    //Method writerTryLock is called by a thread that tries to obtain a write lock. It must succeed and return true if
    //the lock is not already held by any thread, and return false if the lock is held by at least one reader or by a writer.

    public boolean writerTryLock() {
        Writer newNode = new Writer(Thread.currentThread());
        if(holders.compareAndSet(null, newNode)){
            writer_instance = newNode;
            return true;
        }else{
            return false;
        }
    }

    //Method writerUnlock is called to release the write lock, and must throw an exception if the calling thread
    //does not hold a write lock.
    public void writerUnlock() {
        if(writer_instance.thread == Thread.currentThread()){
            if (!holders.compareAndSet(writer_instance, null))
                throw new RuntimeException("Not lock holder");
        }else{
            throw new RuntimeException("Not lock holder");
        }
    }


    // Challenging 7.2.7: You may add new methods


    private static abstract class Holders {
    }

    private static class ReaderList extends Holders {
        private final Thread thread;
        private ReaderList next;

        private ReaderList(Thread thread, ReaderList next) {
            this.thread = thread;
            this.next = next;
        }

        // TODO: Constructor

        // TODO: contains

        // TODO: remove
    }

    private static class Writer extends Holders {
        private Thread thread;

        private Writer(Thread thread) {
            this.thread = thread;
        }

        // TODO: Constructor

    }
}
