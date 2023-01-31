package exercises05;

class LongCounter {
    private long count = 0;

    public synchronized void increment() {
        count = count + 1;
    }

    public synchronized long get() {
        return count;
    }

    public synchronized void add(long c) {
       count += c;
    }

    public synchronized void reset() {
        count = 0;
    }
}