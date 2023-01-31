// For week 2
// sestoft@itu.dk * 2015-10-29
package exercises02;

public class ReadWriteTest{
    public static void main(String[] args) {

        ReadWriteMonitor readWriteMonitor = new ReadWriteMonitor();
        for (int i = 0; i < 10; i++) {

            // start a reader
            new Thread(() -> {

                    readWriteMonitor.readLock();
                    System.out.println(" Reader " + Thread.currentThread().getId() + " started reading");
                    System.out.println(" Reader " + Thread.currentThread().getId() + " stopped reading");
                    readWriteMonitor.readUnlock();

            }).start();

// start a writer
            new Thread(() -> {

                    readWriteMonitor.writeLock();
                    System.out.println(" Writer " + Thread.currentThread().getId() + " started writing");
                    System.out.println(" Writer " + Thread.currentThread().getId() + " stopped writing");
                    readWriteMonitor.writeUnlock();

            }).start();
        }

    }


}
