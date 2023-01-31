package exercises05;

class Measurement {
    public static void main(String[] args) {
        new Measurement();
    }

    public Measurement() {
        long start = System.nanoTime();
        multiply(126465);
        long end = System.nanoTime();
        System.out.println(end - start + " ns");
    }

    private static int multiply(int i) {
        return i * i;
    }
}