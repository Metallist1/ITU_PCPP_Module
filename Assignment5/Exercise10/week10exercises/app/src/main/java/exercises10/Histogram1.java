package exercises10;

class Histogram1 implements Histogram {
    private final int[] counts;

    public Histogram1(int span) {
        this.counts = new int[span];
    }

    public void increment(int bin) {
        counts[bin] = counts[bin] + 1;
    }

    public int getCount(int bin) {
        return counts[bin];
    }

    public int getSpan() {
        return counts.length;
    }

    @Override
    public int getAndClear(int bin) {
        int finalVal = counts[bin];
        counts[bin] = 0;
        return finalVal;
    }

}
