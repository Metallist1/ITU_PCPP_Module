package exercises03;

import java.util.*;
import java.util.concurrent.Semaphore;

public class BoundedBuffer<T> implements BoundedBufferInteface<T>{

    private final Queue<T> linkedListQueue = new LinkedList<T>(); // Queue is FIFO (First In, First Out). Its is not necessary to declare

    private final Semaphore mutex = new Semaphore(1); // This is a lock. It will always have 1 permit.
    private final Semaphore freeBuffers ; // This is used to check if the list is not full. 0 means its full.

    private final Semaphore loadedBuffers = new Semaphore(0); // This is used to check if there are any values inside the list. It starts at 0 because we want to allow a thread to insert values first.

    public BoundedBuffer(int arraySize) {
        freeBuffers = new Semaphore(arraySize);
    }

    public T take() throws Exception {
        //Take a loaded buffer. If there are no loaded buffers. It means the list is empty and is blocked.
        loadedBuffers.acquire();
        //Lock the method
        mutex.acquire();
        //Critical section.
        T element = linkedListQueue.remove();

        System.out.println("Consumer consumed item : " + element);
        //End of Critical section
        mutex.release();
        //Releases lock. And releases free buffers. Thus signaling that the list is no longer full.
        freeBuffers.release();

        return element;
    }

    public void insert(T elem) throws Exception {

        //Consume free buffer permission. This signifies that the list is begging to fill up. 0 permits means that the list is full
        freeBuffers.acquire();

        //Lock the method
        mutex.acquire();

        // Critical section
        linkedListQueue.add(elem);
        System.out.println("Producer produced item : " + elem);
        // End of critical section

        //Unlock the method
        mutex.release();
        //Tell the buffer that there are values inside the list.
        loadedBuffers.release();
    }
}
