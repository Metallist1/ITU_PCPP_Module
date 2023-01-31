package exercises03;

public class BoundedBufferTestMain {

    public BoundedBufferTestMain() {

        //Establish buffer max count
        final int buffer_size = 10;

        //Create BoundedBuffer
        BoundedBuffer buffer = new BoundedBuffer(buffer_size);

        //Create x amount of new threads
        final int numReadersWriters = 50;

        for (int i = 0; i < numReadersWriters; i++) {

            // start a consumer
            new Thread(() -> {
                try {
                   buffer.take();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();

            // start a producer
            new Thread(() -> {
                try {
                    buffer.insert(new Object());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();

        }
    }

    public static void main(String[] args) {
        new BoundedBufferTestMain();
    }

}
